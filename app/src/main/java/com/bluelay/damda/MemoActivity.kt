package com.bluelay.damda

import android.Manifest
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.activity_memo.*
import kotlinx.android.synthetic.main.layout_memo_settings.*
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream


class MemoActivity : AppCompatActivity(), SetMemo, KeyEvent.Callback {
    private lateinit var dbHelper: DBHelper
    private lateinit var database: SQLiteDatabase
    private lateinit var memo : MemoInfo
    private var saveFile: File? = null
    private var photoPath: String? = null
    private var savedPhotoPath: String? = null
    private var photoUri : Uri? = null
    private var mid = -1
    private var lock = 0
    private var bkmr = 0
    private var color = -1
    private var photo = ""
    private var content = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memo)

        btnAddPhoto.visibility = View.VISIBLE

        dbHelper = DBHelper(this)
        database = dbHelper.writableDatabase

        if (intent.hasExtra("memo")) {
            btnDeleteMemo.visibility = View.VISIBLE
            memo = intent.getSerializableExtra("memo") as MemoInfo
            mid = memo.id
            color = memo.color
            lock = memo.lock
            bkmr = memo.bkmr
            getMemo()
        } else {
            color = intent.getIntExtra("color", 0)
        }

        setColor(this, color, activity_memo)
        etMemo.setSelection(etMemo.text.length)

        fabMemoSetting.setOnClickListener {
            it.startAnimation(AnimationUtils.loadAnimation(
                applicationContext, R.anim.fade_out
            ))
            btnCloseSetting.startAnimation(AnimationUtils.loadAnimation(
                applicationContext, R.anim.fade_in
            ))
            it.visibility = View.INVISIBLE
            settingLayout.visibility = View.VISIBLE
        }
        btnCloseSetting.setOnClickListener {
            settingLayout.startAnimation(AnimationUtils.loadAnimation(
                applicationContext, R.anim.fade_out
            ))
            fabMemoSetting.startAnimation(AnimationUtils.loadAnimation(
                applicationContext, R.anim.fade_in
            ))
            settingLayout.visibility = View.INVISIBLE
            fabMemoSetting.visibility = View.VISIBLE
        }

        cbLock.setOnCheckedChangeListener { _, isChecked ->
            if (checkExistPassword(this) && isChecked) {
                lock = 1
            } else {
                lock = 0
                cbLock.isChecked = false
            }
        }
        cbBkmr.setOnCheckedChangeListener { _, isChecked ->
            bkmr = if (isChecked) 1 else 0
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
                setColor(this, color, activity_memo)
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

        btnSaveMemo.setOnClickListener {
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
                deleteMemo()
            }
            btnDelCancel.setOnClickListener{
                dialog.dismiss()
            }
            dialog.show()
        }

        btnAddPhoto.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(
                    this, arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ), 200
                )
            } else {
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                startActivityForResult(intent, 100)
            }
        }

        ivDelPhoto.setOnClickListener {
            memoPhotoLayout.visibility = View.GONE
            photo = ""
            photoUri = null
            photoPath = null
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100) {
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
                            memoPhotoLayout.visibility = View.VISIBLE
                        }
                        cursor.close()
                    }
                } finally {
                    cursor?.close()
                }

                Glide.with(this)
                    .load(photo)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .dontAnimate()
                    .into(ivMemo)
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
            photoPath = saveFile!!.path
            val savedPhoto = BitmapFactory.decodeFile(photo)
            if (savedPhoto.width > 1080 && savedPhoto.height > 2220) {
                val options = BitmapFactory.Options()
                options.inSampleSize = 2
                val src = BitmapFactory.decodeFile(photo, options)
                val resized = Bitmap.createScaledBitmap(src, src.width, src.height, true)
                resized.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            else {
                imgBitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            out.close()
        } catch (e: Exception) {
            photoPath = null
            Toast.makeText(applicationContext, "사진 첨부를 실패하였습니다", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getMemo() {
        val columns = arrayOf(DBHelper.MEM_COL_ID, DBHelper.MEM_COL_COLOR, DBHelper.MEM_COL_CONTENT, DBHelper.MEM_COL_PHOTO)
        val selection = "_id=?"
        val selectArgs = arrayOf(mid.toString())
        val c: Cursor = database.query(
            DBHelper.MEM_TABLE_NAME,
            columns,
            selection,
            selectArgs,
            null,
            null,
            null
        )
        c.moveToNext()
        content = c.getString(c.getColumnIndex(DBHelper.MEM_COL_CONTENT))
        etMemo.setText(content)
        if (c.getString(c.getColumnIndex(DBHelper.MEM_COL_PHOTO)) != null) {
            try {
                savedPhotoPath = c.getString(c.getColumnIndex(DBHelper.MEM_COL_PHOTO))
                photoPath = savedPhotoPath
                val bm = BitmapFactory.decodeFile(savedPhotoPath)
                memoPhotoLayout.visibility = View.VISIBLE
                Glide.with(this)
                    .load(bm)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .dontAnimate()
                    .into(ivMemo)
            } catch (e: java.lang.Exception) {
                memoPhotoLayout.visibility = View.GONE
                Toast.makeText(applicationContext, "사진을 가져오지 못했습니다", Toast.LENGTH_SHORT).show()
            }
        }

        if (lock == 1) {
            cbLock.isChecked = true
        }
        if (bkmr == 1) {
            cbBkmr.isChecked = true
        }
        c.close()
    }

    override fun onBackPressed() {
        saveMemo()
    }

    private fun saveMemo() {
        if (photoUri != null) {
            val inStream: InputStream? = contentResolver.openInputStream(photoUri!!)
            val imgBitmap = BitmapFactory.decodeStream(inStream)
            inStream?.close()
            savePhoto(imgBitmap)
        }

        val contentValues = ContentValues()
        contentValues.put(DBHelper.MEM_COL_WDATE, System.currentTimeMillis() / 1000L)
        contentValues.put(DBHelper.MEM_COL_COLOR, color)
        contentValues.put(DBHelper.MEM_COL_CONTENT, etMemo.text.toString())
        contentValues.put(DBHelper.MEM_COL_PHOTO, photoPath)
        contentValues.put(DBHelper.MEM_COL_LOCK, lock)
        contentValues.put(DBHelper.MEM_COL_BKMR, bkmr)

        if (mid != -1) {
            if (checkUpdate()) {
                val whereCluase = "_id=?"
                val whereArgs = arrayOf(mid.toString())
                database.update(DBHelper.MEM_TABLE_NAME, contentValues, whereCluase, whereArgs)
            }
        }
        else {
            database.insert(DBHelper.MEM_TABLE_NAME, null, contentValues)
        }
        finish()
    }

    private fun checkUpdate() : Boolean {
        if (color != memo.color) return true
        if (bkmr != memo.bkmr) return true
        if (lock != memo.lock) return true
        if (savedPhotoPath != photoPath) {
            delSavedPhoto()
            return true
        }
        if (content != etMemo.text.toString()) return true
        return false
    }

    private fun delSavedPhoto() {
        try {
            val file = this.filesDir
            val fileList = file.listFiles()
            for (i in fileList.indices) {
                if (fileList[i].path == savedPhotoPath) {
                    fileList[i].delete()
                }
            }
        } catch (e: java.lang.Exception) {
            Toast.makeText(applicationContext, "기존 사진 파일을 삭제하지 못했습니다", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteMemo() {
        delSavedPhoto()
        database.execSQL("DELETE FROM ${DBHelper.MEM_TABLE_NAME} WHERE _id = $mid")
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        dbHelper.close()
    }
}