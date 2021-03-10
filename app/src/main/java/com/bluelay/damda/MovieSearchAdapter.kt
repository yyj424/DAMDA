package com.bluelay.damda

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class MovieSearchAdapter (val context : Context, val movieList : ArrayList<Movie>) : BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view : View = LayoutInflater.from(context).inflate(R.layout.adapter_view_movie, null)
       
        val tvMovieTitle : TextView = view.findViewById(R.id.tvMovieTitle)
        val tvMoviePubDate : TextView = view.findViewById(R.id.tvMoviePubDate)
        val ivSearchPoster : ImageView = view.findViewById(R.id.ivSearchPoster)

        val movie = movieList[position]
        tvMoviePubDate.text = movie.release_date
        tvMovieTitle.text = movie.title
        Glide.with(context)
            .load(movie.poster_path)
            .centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .dontAnimate()
            .into(ivSearchPoster)
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