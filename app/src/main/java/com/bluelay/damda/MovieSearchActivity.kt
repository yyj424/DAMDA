package com.bluelay.damda

import android.os.Build
import android.os.Bundle
import android.widget.SearchView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import kotlinx.android.synthetic.main.activity_search_movie.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URLEncoder


class MovieSearchActivity : AppCompatActivity() {

    private var movieList = arrayListOf<Movie>()
    private val movieAdapter = MovieSearchAdapter(this, movieList)
    private val parser = MovieXmlParser()
    private val networkManager = NetworkManager(this)
    private var apiAddress : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_movie)

        lvSearchMovie.adapter = movieAdapter
        apiAddress = resources.getString(R.string.movie_api_url)
        networkManager.setClientId(resources.getString(R.string.client_id))
        networkManager.setClientSecret(resources.getString(R.string.client_secret))

        val id: Int = svMovie.context.resources.getIdentifier(
            "android:id/search_src_text",
            null,
            null
        )
        val searchText : TextView = svMovie.findViewById(id)
        val myCustomFont = ResourcesCompat.getFont(this, R.font.nanumsquareroundb)
        searchText.typeface = myCustomFont

        svMovie.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onQueryTextSubmit(query: String?): Boolean {
                val address = apiAddress + URLEncoder.encode(query, "UTF-8")
                CoroutineScope(IO).launch {
                    val result = networkManager.downloadContents(address)
                    withContext(Main) {
                        movieList.clear()
                        movieList.addAll(parser.parse(result))
                        movieAdapter.notifyDataSetChanged()
                    }
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }


}