package com.bluelay.damda

import android.app.AlertDialog
import android.content.Context
import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import java.util.*

class WeeklyAdapter(val context: Context, val diaryList: ArrayList<Weekly>) : BaseAdapter(){
    override fun getCount(): Int { return diaryList.size }

    override fun getItem(position: Int): Any { return diaryList[position] }

    override fun getItemId(position: Int): Long { return 0 }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = LayoutInflater.from(context).inflate(R.layout.adapter_view_weekly, null)

        val ivMoodPic = view.findViewById<ImageView>(R.id.ivMoodPic)
        val ivWeather = view.findViewById<ImageView>(R.id.ivWeather)
        val tvDay = view.findViewById<TextView>(R.id.tvDay)
        val etDiaryContent = view.findViewById<TextView>(R.id.etDiaryContent)

        val diary = diaryList[position]

        ivMoodPic.setImageURI(diary.moodPic.toUri())
        ivWeather.setImageURI(diary.weather.toUri())
        tvDay.text = diary.day

        etDiaryContent.text = diary.content
        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                diaryList[position].content = s.toString()
            }
        }
        etDiaryContent.addTextChangedListener(textWatcher)

        ivMoodPic.setOnClickListener {
            val builder = AlertDialog.Builder(context)

            val view = LayoutInflater.from(context).inflate(R.layout.dialog_select_moodpic, null)
            val iv_soso = view.findViewById<ImageView>(R.id.iv_soso)
            val iv_happy = view.findViewById<ImageView>(R.id.iv_happy)
            val iv_angry = view.findViewById<ImageView>(R.id.iv_angry)
            val iv_sad = view.findViewById<ImageView>(R.id.iv_sad)
            val iv_congrats = view.findViewById<ImageView>(R.id.iv_congrats)

            builder.setView(view)
            val dialog = builder.create()
            dialog.show()

            iv_soso.setOnClickListener {
                ivMoodPic.setImageURI(getURLForResource(R.drawable.soso_emoji).toUri())
                diaryList[position].moodPic = getURLForResource(R.drawable.soso_emoji).toString()
                dialog.dismiss()
            }
            iv_happy.setOnClickListener {
                ivMoodPic.setImageURI(getURLForResource(R.drawable.happy_emoji).toUri())
                diaryList[position].moodPic = getURLForResource(R.drawable.happy_emoji).toString()
                dialog.dismiss()
            }
            iv_angry.setOnClickListener {
                ivMoodPic.setImageURI(getURLForResource(R.drawable.angry_emoji).toUri())
                diaryList[position].moodPic = getURLForResource(R.drawable.angry_emoji).toString()
                dialog.dismiss()
            }
            iv_sad.setOnClickListener {
                ivMoodPic.setImageURI(getURLForResource(R.drawable.sad_emoji).toUri())
                diaryList[position].moodPic = getURLForResource(R.drawable.sad_emoji).toString()
                dialog.dismiss()
            }
            iv_congrats.setOnClickListener {
                ivMoodPic.setImageURI(getURLForResource(R.drawable.congrats_emoji).toUri())
                diaryList[position].moodPic = getURLForResource(R.drawable.congrats_emoji).toString()
                dialog.dismiss()
            }


        }

        ivWeather.setOnClickListener {
            val builder = AlertDialog.Builder(context)

            val view = LayoutInflater.from(context).inflate(R.layout.dialog_select_weather, null)
            val iv_sun = view.findViewById<ImageView>(R.id.iv_sun)
            val iv_cloud = view.findViewById<ImageView>(R.id.iv_cloud)
            val iv_rain = view.findViewById<ImageView>(R.id.iv_rain)
            val iv_snow = view.findViewById<ImageView>(R.id.iv_snow)
            val iv_bolt = view.findViewById<ImageView>(R.id.iv_bolt)

            builder.setView(view)
            val dialog = builder.create()
            dialog.show()

            iv_sun.setOnClickListener {
                ivWeather.setImageURI(getURLForResource(R.drawable.sun_weather)?.toUri())
                diaryList[position].weather = getURLForResource(R.drawable.sun_weather).toString()
                dialog.dismiss()
            }
            iv_cloud.setOnClickListener {
                ivWeather.setImageURI(getURLForResource(R.drawable.cloud_weather)?.toUri())
                diaryList[position].weather = getURLForResource(R.drawable.cloud_weather).toString()
                dialog.dismiss()
            }
            iv_rain.setOnClickListener {
                ivWeather.setImageURI(getURLForResource(R.drawable.rain_weather)?.toUri())
                diaryList[position].weather = getURLForResource(R.drawable.rain_weather).toString()
                dialog.dismiss()
            }
            iv_snow.setOnClickListener {
                ivWeather.setImageURI(getURLForResource(R.drawable.snow_weather)?.toUri())
                diaryList[position].weather = getURLForResource(R.drawable.snow_weather).toString()
                dialog.dismiss()
            }
            iv_bolt.setOnClickListener {
                ivWeather.setImageURI(getURLForResource(R.drawable.bolt_weather)?.toUri())
                diaryList[position].weather = getURLForResource(R.drawable.bolt_weather).toString()
                dialog.dismiss()
            }
        }

        return view
    }

    private fun getURLForResource(resId: Int): String {
        return Uri.parse("android.resource://" + R::class.java.getPackage().name + "/" + resId)
            .toString()
    }
}