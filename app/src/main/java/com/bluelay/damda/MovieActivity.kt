package com.bluelay.damda

import android.app.Activity
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.ContentValues
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.activity_movie.*
import kotlinx.android.synthetic.main.activity_todo.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class MovieActivity : AppCompatActivity()  {

    private lateinit var dbHelper : DBHelper
    private lateinit var database : SQLiteDatabase
    private val calendar = Calendar.getInstance()
    private val dateFormat = "yyyy.MM.dd"
    private val sdf = SimpleDateFormat(dateFormat, Locale.KOREA)
    private val networkManager = NetworkManager(this)

    // TODO: 2021-02-09  메인 만든 후에 ID 수정!!!!!! 작성 시 -1, 수정 시 1 이상
    private var movieId = 1
    private var color = 0
    private var score = 0.0F
    private var date = ""
    private var title = ""
    private var image = ""
    private var content = ""

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
            rbMovieScore.rating = score
            etMovieDate.setText(date)
            etMovieTitle.setText(title)
            etMovieReview.setText(content)
            if (image != "") {
                Glide.with(this)
                    .load(image)
                    .apply(RequestOptions.fitCenterTransform())
                    .into(ivMoviePoster)
            }
        }

        ivMoviePoster.setOnClickListener {
            val intent = Intent(this, MovieSearchActivity::class.java)
            startActivityForResult(intent, 100)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                etMovieTitle.setText(data?.getStringExtra("title"))
                image = data?.getStringExtra("image").toString()
                if (image != "") {
                    Glide.with(this)
                            .load(image)
                            .apply(RequestOptions.fitCenterTransform())
                            .into(ivMoviePoster)
                }
            }
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
        value.put(DBHelper.MOV_COL_WDATE, System.currentTimeMillis())
        value.put(DBHelper.MOV_COL_DATE, etMovieDate.text.toString())
        value.put(DBHelper.MOV_COL_COLOR, color)
        value.put(DBHelper.MOV_COL_CONTENT, etMovieReview.text.toString())
        value.put(DBHelper.MOV_COL_TITLE, etMovieTitle.text.toString())
        value.put(DBHelper.MOV_COL_POSTERPIC, image)
        value.put(DBHelper.MOV_COL_SCORE, rbMovieScore.rating)
        database.insert(DBHelper.MOV_TABLE_NAME, null, value)
    }

    private fun selectMovie() {
        database = dbHelper.readableDatabase

        var cursor: Cursor = database.rawQuery("SELECT * FROM ${DBHelper.MOV_TABLE_NAME} WHERE ${DBHelper.MOV_COL_ID}=?", arrayOf(movieId.toString()))
        cursor.moveToNext()
        date = cursor.getString(cursor.getColumnIndex(DBHelper.MOV_COL_DATE))
        color = cursor.getInt(cursor.getColumnIndex(DBHelper.MOV_COL_COLOR))
        score = cursor.getFloat(cursor.getColumnIndex(DBHelper.MOV_COL_SCORE))
        title = cursor.getString(cursor.getColumnIndex(DBHelper.MOV_COL_TITLE))
        image = cursor.getString(cursor.getColumnIndex(DBHelper.MOV_COL_POSTERPIC))
        content = cursor.getString(cursor.getColumnIndex(DBHelper.MOV_COL_CONTENT))
    }

    private fun updateMovie() {
        database = dbHelper.writableDatabase

        val value = ContentValues()
        value.put(DBHelper.MOV_COL_WDATE, System.currentTimeMillis())
        value.put(DBHelper.MOV_COL_DATE, etMovieDate.text.toString())
        value.put(DBHelper.MOV_COL_COLOR, color)
        value.put(DBHelper.MOV_COL_CONTENT, etMovieReview.text.toString())
        value.put(DBHelper.MOV_COL_TITLE, etMovieTitle.text.toString())
        value.put(DBHelper.MOV_COL_POSTERPIC, image)
        value.put(DBHelper.MOV_COL_SCORE, rbMovieScore.rating)
        database.update(DBHelper.MOV_TABLE_NAME, value, "${DBHelper.MOV_COL_ID}=?", arrayOf(movieId.toString()))
    }

    private fun View.hideKeyboard() {
        val imm = context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }
}