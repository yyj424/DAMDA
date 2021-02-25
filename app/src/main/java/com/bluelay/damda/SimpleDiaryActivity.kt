package com.bluelay.damda

import android.app.DatePickerDialog
import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import kotlinx.android.synthetic.main.activity_simple_diary.*
import kotlinx.android.synthetic.main.adapter_view_simple_diary.*
import java.text.SimpleDateFormat
import java.util.*


class SimpleDiaryActivity : AppCompatActivity(){

    lateinit var dbHelper : DBHelper
    lateinit var database : SQLiteDatabase

    var did = 2

    private val calendar = Calendar.getInstance()
    private val dateFormat = "yyyy.MM.dd"
    private var sdf = SimpleDateFormat(dateFormat, Locale.KOREA)

    var diaryList = arrayListOf<SimpleDiary>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simple_diary)

        dbHelper = DBHelper(this)
        database = dbHelper.writableDatabase

        var recordDatePicker = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            etDiaryDate.setText(sdf.format(calendar.time))
        }
        etDiaryDate.setOnClickListener {
            DatePickerDialog(
                this,
                R.style.DialogTheme,
                recordDatePicker,
                calendar[Calendar.YEAR],
                calendar[Calendar.MONTH],
                calendar[Calendar.DAY_OF_MONTH]
            ).show()
        }


        val diaryAdapter = SimpleDiaryAdapter(this, diaryList)

        if (did != -1) {
            selectDiary()
        } else {
            var day = "day"
            for (i in 1.. 7) {
                if(i == 1){
                    day = "Mon"
                } else if(i == 2){
                    day = "Tue"
                } else if(i == 3){
                    day = "Wed"
                }else if(i == 4){
                    day = "Thu"
                }else if(i == 5){
                    day = "Fri"
                }else if(i == 6){
                    day = "Sat"
                }else if(i == 7){
                    day = "Sun"
                }

                diaryList.add(SimpleDiary(day, "", getURLForResource(R.drawable.select_emoji).toString(),
                    getURLForResource(R.drawable.select_weather).toString()))
            }
        }
        lvDiary.adapter = diaryAdapter
    }

    override fun onBackPressed() {
        Log.d("aty", "onBackPressed")
        var contentValues = ContentValues()
        contentValues.put(DBHelper.WEE_COL_WDATE , System.currentTimeMillis()/1000L);
        contentValues.put(DBHelper.WEE_COL_COLOR , 0);

        if (did != -1) {
            var whereCluase = "_id=?"
            var whereArgs = arrayOf(did.toString())
            database.update(DBHelper.WEE_TABLE_NAME, contentValues, whereCluase, whereArgs)

            whereCluase = "did=?"
            database.delete(DBHelper.DIA_TABLE_NAME, whereCluase, whereArgs)
        }
        else  {
            did = database.insert(DBHelper.WEE_TABLE_NAME, null, contentValues).toInt()
        }
        for(diary in diaryList){
            contentValues.clear()

            contentValues.put(DBHelper.DIA_COL_DID, did)
            contentValues.put(DBHelper.DIA_COL_WEATHER, diary.weather)
            contentValues.put(DBHelper.DIA_COL_MOODPIC, diary.moodPic)
            contentValues.put(DBHelper.DIA_COL_CONTENT, diary.content)
            database.insert(DBHelper.DIA_TABLE_NAME, null, contentValues)

        }
        //dbHelper.close() ondestroy
        //startActivity(Intent(this, MainActivity::class.java))
        //finish()
    }

    private fun selectDiary() {
        Log.d("aty", "diarySelect")
        database = dbHelper.readableDatabase

        var c : Cursor = database.rawQuery("SELECT * FROM ${DBHelper.DIA_TABLE_NAME} WHERE ${DBHelper.DIA_COL_DID} = ?", arrayOf(did.toString()))

        var day = "day"
        var moodPic : String
        var weather : String
        var content : String

        for (i in 1.. 7) {
            c.moveToNext()
            moodPic = c.getString(c.getColumnIndex(DBHelper.DIA_COL_MOODPIC))
            weather = c.getString(c.getColumnIndex(DBHelper.DIA_COL_WEATHER))
            content = c.getString(c.getColumnIndex(DBHelper.DIA_COL_CONTENT))

            if (i == 1) {
                day = "Mon"
            } else if (i == 2) {
                day = "Tue"
            } else if (i == 3) {
                day = "Wed"
            } else if (i == 4) {
                day = "Thu"
            } else if (i == 5) {
                day = "Fri"
            } else if (i == 6) {
                day = "Sat"
            } else if (i == 7) {
                day = "Sun"
            }

            diaryList.add(SimpleDiary(day, content, moodPic, weather))
        }
    }

    private fun getURLForResource(resId: Int): String? {
        return Uri.parse("android.resource://" + R::class.java.getPackage().name + "/" + resId)
            .toString()
    }
    private fun getURLForResource(resId: String): String? {
        return Uri.parse("android.resource://" + R::class.java.getPackage().name + "/" + resId)
            .toString()
    }
}