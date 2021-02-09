package com.bluelay.damda

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_movie.*
import kotlinx.android.synthetic.main.activity_todo.*
import java.text.SimpleDateFormat
import java.util.*

class MovieActivity : AppCompatActivity()  {

    private lateinit var dbHelper : DBHelper
    private lateinit var database : SQLiteDatabase
    private val calendar = Calendar.getInstance()
    private val dateFormat = "yyyy.MM.dd"
    private var sdf = SimpleDateFormat(dateFormat, Locale.KOREA)

    // TODO: 2021-02-09  메인 만든 후에 ID 수정!!!!!! 작성 시 -1, 수정 시 1 이상
    private var movieId = -1
    private var date : String = ""
    private var color = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie)

        etMovieDate.hideKeyboard()
        dbHelper = DBHelper(this)

        var datePicker = OnDateSetListener { view, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            etMovieDate.setText(sdf.format(calendar.time))
        }
        etMovieDate.setOnClickListener {
            DatePickerDialog(this, R.style.DialogTheme, datePicker, calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH]).show()
        }

        if (movieId != -1) {
            selectMovie()
            etMovieDate.setText(date)
        }

        ivMoviePoster.setOnClickListener {

        }
    }

    override fun onBackPressed() {
        // TODO: 2021-01-29 main 만들고 finish
        // TODO: 2021-01-30 아무것도 안썼을 경우 체크하기
        if (movieId == -1) {
            insertMovie()
        }
        else {
            updateMovie()
        }
    }

    private fun insertMovie() {
        database = dbHelper.writableDatabase

        val value = ContentValues()
        value.put(DBHelper.TODL_COL_WDATE, System.currentTimeMillis())
        value.put(DBHelper.TODL_COL_DATE, etTodoDate.text.toString())
        value.put(DBHelper.TODL_COL_COLOR, color)
        val id =  database.insert(DBHelper.TODL_TABLE_NAME, null, value)
    }

    private fun selectMovie() {
        database = dbHelper.readableDatabase

        var cursor: Cursor = database.rawQuery("SELECT * FROM ${DBHelper.TODL_TABLE_NAME} WHERE ${DBHelper.TODL_COL_ID}=?", arrayOf(movieId.toString()))
        cursor.moveToNext()
        date = cursor.getString(cursor.getColumnIndex(DBHelper.TODL_COL_DATE))
        color = cursor.getInt(cursor.getColumnIndex(DBHelper.TODL_COL_COLOR))
    }

    private fun updateMovie() {
        database = dbHelper.writableDatabase

        val value = ContentValues()
        value.put(DBHelper.TODL_COL_WDATE, System.currentTimeMillis())
        value.put(DBHelper.TODL_COL_DATE, etTodoDate.text.toString())
        value.put(DBHelper.TODL_COL_COLOR, color)
        database.update(DBHelper.TODL_TABLE_NAME, value, "${DBHelper.TODL_COL_ID}=?", arrayOf(movieId.toString()))
    }

    private fun View.hideKeyboard() {
        val imm = context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }
}