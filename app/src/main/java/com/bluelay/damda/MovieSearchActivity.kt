package com.bluelay.damda

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.google.gson.annotations.SerializedName
import kotlinx.android.synthetic.main.activity_search_movie.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class MovieSearchActivity : AppCompatActivity() {
    private var movieList = arrayListOf<Movie>()
    private val movieAdapter = MovieSearchAdapter(this, movieList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_movie)

        lvSearchMovie.adapter = movieAdapter

        val id: Int = svMovie.context.resources.getIdentifier(
            "android:id/search_src_text",
            null,
            null
        )
        val searchText: TextView = svMovie.findViewById(id)
        val myCustomFont = ResourcesCompat.getFont(this, R.font.nanumsquareroundb)
        searchText.typeface = myCustomFont

        svMovie.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    if (isInternetConnection(this@MovieSearchActivity))
                        MoviesDB.getSearchMovies(getString(R.string.api_key),1, query, ::onSearchMoviesFetched)
                    else
                        Toast.makeText(this@MovieSearchActivity, "인터넷에 연결되어 있어야 검색이 가능합니다.", Toast.LENGTH_SHORT).show()
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        lvSearchMovie.setOnItemClickListener { parent, view, position, id ->
            val resultIntent = Intent()
            resultIntent.putExtra("title", movieList[position].title)
            resultIntent.putExtra("image", movieList[position].poster_path)
            setResult(RESULT_OK, resultIntent)
            finish()
        }
    }

    private fun onSearchMoviesFetched(movies: List<Movie>) {
        movieList.clear()
        movieList.addAll(movies)
        for (movie in movieList) {
            movie.poster_path = "https://image.tmdb.org/t/p/original${movie.poster_path}"
        }
        movieAdapter.notifyDataSetChanged()
    }

    data class GetMoviesResponse(
        @SerializedName("page") val page: Int,
        @SerializedName("results") val movies: List<Movie>,
        @SerializedName("total_pages") val pages: Int
    )

    interface Api {
        @GET("search/movie")
        fun getSearchMovies(
            @Query("api_key") api_key: String,
            @Query("page") page: Int,
            @Query("language") language: String = "ko-KR",
            @Query("query") query: String
        ): Call<GetMoviesResponse>
    }

    object MoviesDB {
        private val api: Api

        init {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://api.themoviedb.org/3/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            api = retrofit.create(Api::class.java)
        }

        fun getSearchMovies(api_key : String, page: Int = 1, query : String, onSuccess: (movies: List<Movie>) -> Unit) {
            api.getSearchMovies(api_key = api_key, page = page, query = query)
                .enqueue(object : Callback<GetMoviesResponse> {
                    override fun onResponse(
                        call: Call<GetMoviesResponse>,
                        response: Response<GetMoviesResponse>
                    ) {
                        if (response.isSuccessful) {
                            val responseBody = response.body()
                            if (responseBody != null) {
                                onSuccess.invoke(responseBody.movies)
                            }
                        }
                    }

                    override fun onFailure(call: Call<GetMoviesResponse>, t: Throwable) {}
                })
        }
    }

    private fun isInternetConnection(context : Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val nw      = connectivityManager.activeNetwork ?: return false
            val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
            return when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            return connectivityManager.activeNetworkInfo?.isConnected ?: false
        }
    }
}