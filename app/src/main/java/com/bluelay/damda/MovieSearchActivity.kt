package com.bluelay.damda

import android.os.Bundle
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_search_movie.*

class MovieSearchActivity : AppCompatActivity() {

    private lateinit var movieList : ArrayList<Movie>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_movie)

        svMovie.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }
}