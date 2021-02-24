package com.bluelay.damda

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import android.widget.TextView
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

    private val TAG = "MovieSearchActivity"
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
                    MoviesDB.getSearchMovies(1, query, ::onSearchMoviesFetched)
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


    // interface API
    interface Api {
        @GET("search/movie")
        fun getSearchMovies(
            @Query("api_key") apiKey: String = "407931f34a3bdd3d9b53426058d5049e",
            @Query("page") page: Int,
            @Query("language") language: String = "ko-KR",
            @Query("query") query: String
        ): Call<GetMoviesResponse>
    }

    // create object
    object MoviesDB {
        private val api: Api //인터페이스 구현

        init {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://api.themoviedb.org/3/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            api = retrofit.create(Api::class.java)
        }

        // add method
        fun getSearchMovies(page: Int = 1, query : String, onSuccess: (movies: List<Movie>) -> Unit) {
            api.getSearchMovies(page = page, query = query)
                .enqueue(object : Callback<GetMoviesResponse> {
                    override fun onResponse(
                        call: Call<GetMoviesResponse>,
                        response: Response<GetMoviesResponse>
                    ) {
                        if (response.isSuccessful) {
                            val responseBody = response.body()
                            if (responseBody != null) {
                                onSuccess.invoke(responseBody.movies)
                            } else {
                                Log.d("Repository", "Failed to get response")
                            }
                        }
                    }

                    override fun onFailure(call: Call<GetMoviesResponse>, t: Throwable) {
                        Log.e("Repository", "onFailure", t)
                    }
                })
        }
    }
}