package com.bluelay.damda

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_setting_bg.*

class SettingBGActivity : AppCompatActivity() {
    private var selColorView : View? = null
    private lateinit var sharedPref : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting_bg)

        sharedPref = getSharedPreferences("memoColor", Context.MODE_PRIVATE)
        selColorView = when(sharedPref.getInt("color", -1)) {
            1 -> ivSetRed
            2 -> ivSetYellow
            3 -> ivSetGreen
            4 -> ivSetBlue
            5 -> ivSetPurple
            6 -> ivSetPink
            else -> null
        }
        selColorView?.setBackgroundResource(R.drawable.border)

        ivSetRed!!.setOnClickListener(colorClickListener)
        ivSetYellow!!.setOnClickListener(colorClickListener)
        ivSetGreen!!.setOnClickListener(colorClickListener)
        ivSetBlue!!.setOnClickListener(colorClickListener)
        ivSetPurple!!.setOnClickListener(colorClickListener)
        ivSetPink!!.setOnClickListener(colorClickListener)
    }

    private val colorClickListener = View.OnClickListener { v ->
        when(selColorView) {
            null -> {
                v.setBackgroundResource(R.drawable.border)
                selColorView = v
            }
            v -> {
                selColorView!!.background = null
                selColorView = null
            }
            else -> {
                v.setBackgroundResource(R.drawable.border)
                selColorView!!.background = null
                selColorView = v
            }
        }
    }

    override fun onBackPressed() {
        sharedPref.edit().apply{
            when(selColorView?.contentDescription.toString()) {
                "null" -> putInt("color", 0)
                "red" -> putInt("color", 1)
                "yellow" -> putInt("color", 2)
                "green" -> putInt("color", 3)
                "blue" -> putInt("color", 4)
                "purple" -> putInt("color", 5)
                "pink" -> putInt("color", 6)
            }
            apply()
        }
        finish()
    }
}