package com.bluelay.damda

import android.Manifest
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.activity_movie.*
import kotlinx.android.synthetic.main.activity_movie.btnSettings
import kotlinx.android.synthetic.main.activity_movie.settingLayout
import kotlinx.android.synthetic.main.activity_todo.*
import kotlinx.android.synthetic.main.layout_memo_settings.*
import java.text.SimpleDateFormat
import java.util.*

class MovieActivity : AppCompatActivity(), SetMemo  {

    private lateinit var dbHelper : DBHelper
    private lateinit var database : SQLiteDatabase
    private val calendar = Calendar.getInstance()
    private val dateFormat = "yyyy.MM.dd"
    private val sdf = SimpleDateFormat(dateFormat, Locale.KOREA)

        // TODO: 2021-02-09  메인 만든 후에 ID 수정!!!!!! 작성 시 -1, 수정 시 1 이상
    private var movieId = -1
    private var color = 5
    private var score = 0.0F
    private var date = ""
    private var title = ""
    private var image = ""
    private var content = ""
    private var lock = 0
    private var bkmr = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie)

        color = intent.getIntExtra("color", 0)
        movieId = intent.getIntExtra("id", -1)
        setColor(this, color, clMovie)

        etMovieDate.hideKeyboard()
        dbHelper = DBHelper(this)

        var datePicker = OnDateSetListener { view, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            etMovieDate.setText(sdf.format(calendar.time))
        }
        etMovieDate.setOnClickListener {
            DatePickerDialog(
                this,
                R.style.DialogTheme,
                datePicker,
                calendar[Calendar.YEAR],
                calendar[Calendar.MONTH],
                calendar[Calendar.DAY_OF_MONTH]
            ).show()
        }

        settingLayout.visibility = View.INVISIBLE
        btnSettings.setOnClickListener {
            settingLayout.visibility = if (settingLayout.visibility == View.INVISIBLE) View.VISIBLE  else View.INVISIBLE
        }

        cbLock.setOnCheckedChangeListener { _, isChecked ->
            lock = if(isChecked) 1 else 0
        }
        cbBkmr.setOnCheckedChangeListener { _, isChecked ->
            bkmr = if(isChecked) 1 else 0
        }

        if (movieId != -1) {
            selectMovie()
            rbMovieScore.rating = score
            etMovieDate.setText(date)
            etMovieTitle.setText(title)
            etMovieReview.setText(content)
            if (lock == 1) cbLock.isChecked = true
            if (bkmr == 1) cbBkmr.isChecked = true
            if (image != "") {
                Glide.with(this)
                    .load(image)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .dontAnimate()
                    .into(ivMoviePoster)
            }
        }

        ivMoviePoster.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val view = LayoutInflater.from(this).inflate(R.layout.dialog_select_movie_poster, null)
            val tvSelSearchMovie : TextView = view.findViewById(R.id.tvSelSearchMovie)
            val tvSelGallery : TextView = view.findViewById(R.id.tvSelGallery)

            builder.setView(view)
            val alertDialog = builder.create()
            alertDialog.show()

            tvSelSearchMovie.setOnClickListener {
                val intent = Intent(this, MovieSearchActivity::class.java)
                startActivityForResult(intent, 100)
                alertDialog.dismiss()
            }
            tvSelGallery.setOnClickListener {
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(
                        this, arrayOf(
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        ), 300
                    )
                } else {
                    val intent = Intent(Intent.ACTION_PICK)
                    intent.type = "image/*"
                    startActivityForResult(intent, 200)
                }
                alertDialog.dismiss()
            }
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
                            .centerCrop()
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .dontAnimate()
                            .into(ivMoviePoster)
                }
            }
        }
        else if (requestCode == 200) {
            if (resultCode == RESULT_OK) {
                val photoUri : Uri = data?.data!!
                var cursor: Cursor? = null
                try {
                    cursor = contentResolver.query(photoUri, null, null, null, null)
                    if (cursor != null) {
                        if (cursor.moveToFirst()) {
                            image = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
                        }
                        cursor.close()
                    }
                } finally {
                    cursor?.close()
                }

                Glide.with(this)
                    .load(image)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .dontAnimate()
                    .into(ivMoviePoster)
            }
        }
    }

    override fun onBackPressed() {
        if (movieId == -1) {
            insertMovie()
        }
        else {
            updateMovie()
        }
        finish()
    }

    private fun insertMovie() {
        database = dbHelper.writableDatabase

        val value = ContentValues()
        value.put(DBHelper.MOV_COL_WDATE, System.currentTimeMillis() / 1000L)
        value.put(DBHelper.MOV_COL_DATE, etMovieDate.text.toString())
        value.put(DBHelper.MOV_COL_COLOR, color)
        value.put(DBHelper.MOV_COL_CONTENT, etMovieReview.text.toString())
        value.put(DBHelper.MOV_COL_TITLE, etMovieTitle.text.toString())
        value.put(DBHelper.MOV_COL_POSTERPIC, image)
        value.put(DBHelper.MOV_COL_SCORE, rbMovieScore.rating)
        value.put(DBHelper.MOV_COL_LOCK, lock)
        value.put(DBHelper.MOV_COL_BKMR, bkmr)
        database.insert(DBHelper.MOV_TABLE_NAME, null, value)
    }

    private fun selectMovie() {
        database = dbHelper.readableDatabase

        val cursor: Cursor = database.rawQuery(
            "SELECT * FROM ${DBHelper.MOV_TABLE_NAME} WHERE ${DBHelper.MOV_COL_ID}=?", arrayOf(
                movieId.toString()
            )
        )
        if (cursor.moveToNext()) {
            date = cursor.getString(cursor.getColumnIndex(DBHelper.MOV_COL_DATE))
            color = cursor.getInt(cursor.getColumnIndex(DBHelper.MOV_COL_COLOR))
            score = cursor.getFloat(cursor.getColumnIndex(DBHelper.MOV_COL_SCORE))
            title = cursor.getString(cursor.getColumnIndex(DBHelper.MOV_COL_TITLE))
            image = cursor.getString(cursor.getColumnIndex(DBHelper.MOV_COL_POSTERPIC))
            content = cursor.getString(cursor.getColumnIndex(DBHelper.MOV_COL_CONTENT))
            lock = cursor.getInt(cursor.getColumnIndex(DBHelper.MOV_COL_LOCK))
            bkmr = cursor.getInt(cursor.getColumnIndex(DBHelper.MOV_COL_BKMR))
        }
        cursor.close()
    }

    private fun updateMovie() {
        database = dbHelper.writableDatabase

        val value = ContentValues()
        value.put(DBHelper.MOV_COL_WDATE, System.currentTimeMillis() / 1000L)
        value.put(DBHelper.MOV_COL_DATE, etMovieDate.text.toString())
        value.put(DBHelper.MOV_COL_COLOR, color)
        value.put(DBHelper.MOV_COL_CONTENT, etMovieReview.text.toString())
        value.put(DBHelper.MOV_COL_TITLE, etMovieTitle.text.toString())
        value.put(DBHelper.MOV_COL_POSTERPIC, image)
        value.put(DBHelper.MOV_COL_SCORE, rbMovieScore.rating)
        value.put(DBHelper.MOV_COL_LOCK, lock)
        value.put(DBHelper.MOV_COL_BKMR, bkmr)
        database.update(
            DBHelper.MOV_TABLE_NAME,
            value,
            "${DBHelper.MOV_COL_ID}=?",
            arrayOf(movieId.toString())
        )
    }

    private fun View.hideKeyboard() {
        val imm = context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    override fun onDestroy() {
        super.onDestroy()
        dbHelper.close()
    }
}