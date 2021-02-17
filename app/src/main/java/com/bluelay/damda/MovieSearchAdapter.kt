package com.bluelay.damda

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MovieSearchAdapter (val context : Context, val movieList : ArrayList<Movie>) : BaseAdapter() {

    private val networkManager = NetworkManager(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view : View = LayoutInflater.from(context).inflate(R.layout.adapter_view_movie, null)

        val tvMovieTitle : TextView = view.findViewById(R.id.tvMovieTitle)
        val tvMoviePubDate : TextView = view.findViewById(R.id.tvMoviePubDate)
        val ivSearchPoster : ImageView = view.findViewById(R.id.ivSearchPoster)

        val movie = movieList[position]
        tvMoviePubDate.text = movie.date
        tvMovieTitle.text = movie.title
        if (movie.image != "") {
            CoroutineScope(Dispatchers.IO).launch {
                val result = networkManager.downloadImage(movie.image)
                withContext(Dispatchers.Main) {
                    ivSearchPoster.setImageBitmap(result)
                }
            }
        }
        return view
    }

    override fun getCount(): Int {
        return movieList.size
    }

    override fun getItem(position: Int): Any {
        return movieList[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }
}