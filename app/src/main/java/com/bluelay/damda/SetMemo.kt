package com.bluelay.damda

import android.content.Context
import android.view.View
import android.widget.CheckBox
import androidx.core.content.ContextCompat

interface SetMemo {
    fun setColor(context : Context, color : Int, view : View) {
        when (color) {
            0 -> view.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
            1 -> view.setBackgroundColor(ContextCompat.getColor(context, R.color.pastel_red))
            2 -> view.setBackgroundColor(ContextCompat.getColor(context, R.color.pastel_yellow))
            3 -> view.setBackgroundColor(ContextCompat.getColor(context, R.color.pastel_green))
            4 -> view.setBackgroundColor(ContextCompat.getColor(context, R.color.pastel_blue))
            5 -> view.setBackgroundColor(ContextCompat.getColor(context, R.color.pastel_purple))
            6 -> view.setBackgroundColor(ContextCompat.getColor(context, R.color.pastel_pink))
        }
    }
}
