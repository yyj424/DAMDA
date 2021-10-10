package com.bluelay.damda

import android.Manifest
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.appwidget.AppWidgetManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.activity_movie.*
import kotlinx.android.synthetic.main.activity_movie.fabMemoSetting
import kotlinx.android.synthetic.main.activity_movie.settingLayout
import kotlinx.android.synthetic.main.layout_memo_settings.*
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*


class MovieActivity : AppCompatActivity(), SetMemo  {

    private lateinit var dbHelper : DBHelper
    private lateinit var database : SQLiteDatabase
    private val calendar = Calendar.getInstance()
    private val dateFormat = "yyyy.MM.dd"
    private val sdf = SimpleDateFormat(dateFormat, Locale.KOREA)

    private var movieId = -1
    private var color = 0
    private var score = 0.0F
    private var date = ""
    private var title = ""
    private var image : String? = null
    private var oldImage = ""
    private var content = ""
    private var lock = 0
    private var bkmr = 0
    private var photo = ""
    private var saveFile: File? = null
    private var photoUri : Uri? = null

    private lateinit var memo : MemoInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie)

        etMovieDate.hideKeyboard()
        dbHelper = DBHelper(this)
        color = intent.getIntExtra("color", 0)

        if (intent.hasExtra("memo")) {
            btnDeleteMemo.visibility = View.VISIBLE
            memo = intent.getSerializableExtra("memo") as MemoInfo
            color = memo.color
            movieId = memo.id
            lock = memo.lock
            bkmr = memo.bkmr

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
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .dontAnimate()
                    .into(ivMoviePoster)
            }
        }

        setColor(this, color, clMovie)

        val datePicker = OnDateSetListener { view, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            etMovieDate.setText(sdf.format(calendar.time))
        }
        etMovieDate.setOnClickListener {
            DatePickerDialog(
                this,
                datePicker,
                calendar[Calendar.YEAR],
                calendar[Calendar.MONTH],
                calendar[Calendar.DAY_OF_MONTH]
            ).show()
        }

        fabMemoSetting.setOnClickListener {
            it.startAnimation(
                AnimationUtils.loadAnimation(
                applicationContext, R.anim.fade_out
            ))
            btnCloseSetting.startAnimation(
                AnimationUtils.loadAnimation(
                applicationContext, R.anim.fade_in
            ))
            it.visibility = View.INVISIBLE
            settingLayout.visibility = View.VISIBLE
        }
        btnCloseSetting.setOnClickListener {
            settingLayout.startAnimation(
                AnimationUtils.loadAnimation(
                applicationContext, R.anim.fade_out
            ))
            fabMemoSetting.startAnimation(
                AnimationUtils.loadAnimation(
                applicationContext, R.anim.fade_in
            ))
            settingLayout.visibility = View.INVISIBLE
            fabMemoSetting.visibility = View.VISIBLE
        }


        cbLock.setOnCheckedChangeListener { _, isChecked ->
            if (checkExistPassword(this) && isChecked) {
                lock = 1
            }
            else {
                lock = 0
                cbLock.isChecked = false
            }
        }
        cbBkmr.setOnCheckedChangeListener { _, isChecked ->
            bkmr = if(isChecked) 1 else 0
        }

        btnChangeColor.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val view = LayoutInflater.from(this).inflate(R.layout.dialog_change_color, null)
            builder.setView(view)
            val dialog = builder.create()
            val ivColor0 = view.findViewById<ImageView>(R.id.ivColor0)
            val ivColor1 = view.findViewById<ImageView>(R.id.ivColor1)
            val ivColor2 = view.findViewById<ImageView>(R.id.ivColor2)
            val ivColor3 = view.findViewById<ImageView>(R.id.ivColor3)
            val ivColor4 = view.findViewById<ImageView>(R.id.ivColor4)
            val ivColor5 = view.findViewById<ImageView>(R.id.ivColor5)
            val ivColor6 = view.findViewById<ImageView>(R.id.ivColor6)

            val colorClickListener = View.OnClickListener { v ->
                color = when (v) {
                    ivColor0 -> 0
                    ivColor1 -> 1
                    ivColor2 -> 2
                    ivColor3 -> 3
                    ivColor4 -> 4
                    ivColor5 -> 5
                    ivColor6 -> 6
                    else -> 0
                }
                setColor(this, color, clMovie)
                dialog.dismiss()
            }
            ivColor0!!.setOnClickListener(colorClickListener)
            ivColor1!!.setOnClickListener(colorClickListener)
            ivColor2!!.setOnClickListener(colorClickListener)
            ivColor3!!.setOnClickListener(colorClickListener)
            ivColor4!!.setOnClickListener(colorClickListener)
            ivColor5!!.setOnClickListener(colorClickListener)
            ivColor6!!.setOnClickListener(colorClickListener)
            dialog.show()
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
                alertDialog.dismiss()
                if (isInternetConnection(this)) {
                    val intent = Intent(this, MovieSearchActivity::class.java)
                    startActivityForResult(intent, 100)
                }
                else
                    Toast.makeText(this, "인터넷에 연결되어 있어야 검색이 가능합니다.", Toast.LENGTH_SHORT).show()
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

        btnSaveMovie.setOnClickListener {
            saveMemo()
        }

        btnDeleteMemo.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val view = LayoutInflater.from(this).inflate(R.layout.dialog_delete_memo, null)
            builder.setView(view)
            val btnDelConfirm = view.findViewById<Button>(R.id.btnDelConfirm)
            val btnDelCancel = view.findViewById<Button>(R.id.btnDelCancel)
            val dialog = builder.create()
            btnDelConfirm.setOnClickListener{
                dialog.dismiss()
                deleteMovie()
            }
            btnDelCancel.setOnClickListener{
                dialog.dismiss()
            }
            dialog.show()
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
                            .fitCenter()
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .dontAnimate()
                            .into(ivMoviePoster)
                }
            }
        }
        else if (requestCode == 200) {
            if (resultCode == RESULT_OK) {
                photoUri = data?.data!!
                if (photoUri.toString().contains("/gallery/picker")) {
                    Toast.makeText(this,"클라우드 사진은 사용할 수 없습니다.", Toast.LENGTH_SHORT).show()
                    return
                }
                var cursor: Cursor? = null
                try {
                    cursor = contentResolver.query(photoUri!!, null, null, null, null)
                    if (cursor != null) {
                        if (cursor.moveToFirst()) {
                            photo = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
                        }
                        cursor.close()
                    }
                } finally {
                    cursor?.close()
                }

                Glide.with(this)
                    .load(photo)
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .dontAnimate()
                    .into(ivMoviePoster)
            }
        }
    }

    private fun savePhoto(imgBitmap: Bitmap) {
        if (photo != "") {
            saveFile = File(this.filesDir, photo.substring(photo.lastIndexOf("/")+1))
        }
        else {
            photoUri = null
            Toast.makeText(applicationContext, "사진 첨부를 실패하였습니다", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            saveFile!!.createNewFile();
            val out = FileOutputStream(saveFile)
            imgBitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            out.close()
            image = saveFile!!.path
        } catch (e: Exception) {
            image = null
            Toast.makeText(applicationContext, "사진 첨부를 실패하였습니다", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBackPressed() {
        saveMemo()
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
        }
        oldImage = image.toString()
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

    private fun saveMemo() {
        if (photoUri != null) {
            val instream: InputStream? = contentResolver.openInputStream(photoUri!!)
            val imgBitmap = BitmapFactory.decodeStream(instream)
            instream?.close()
            savePhoto(imgBitmap)
        }
        if (movieId == -1)
            insertMovie()
        else
            if (checkUpdate()) {
                updateMovie()
                updateWidget()
            }
        finish()
    }

    private fun checkUpdate() : Boolean {
        if (color != memo.color) return true
        if (bkmr != memo.bkmr) return true
        if (lock != memo.lock) return true
        if (date != etMovieDate.text.toString()) return true
        if (content != etMovieReview.text.toString()) return true
        if (title != etMovieTitle.text.toString()) return true
        if (score != rbMovieScore.rating) return true
        if (oldImage != image) return true
        return false
    }

    private fun deleteMovie() {
        database.execSQL("DELETE FROM ${DBHelper.MOV_TABLE_NAME} WHERE _id = $movieId")
        finish()
    }

    private fun View.hideKeyboard() {
        val imm = context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    override fun onDestroy() {
        super.onDestroy()
        dbHelper.close()
    }

    private fun isInternetConnection(context : Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val nw      = connectivityManager.activeNetwork ?: return false
            val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
            return when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            return connectivityManager.activeNetworkInfo?.isConnected ?: false
        }
    }

    private fun updateWidget() {
        val largeWidgetIntent = Intent(this, LargeWidget::class.java)
        largeWidgetIntent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        this.sendBroadcast(largeWidgetIntent)

        val smallWidgetIntent = Intent(this, SmallWidget::class.java)
        smallWidgetIntent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        this.sendBroadcast(smallWidgetIntent)
    }
}