package com.bluelay.damda

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_bucket.*
import kotlinx.android.synthetic.main.activity_memo.*
import kotlinx.android.synthetic.main.activity_memo.btnSettings
import kotlinx.android.synthetic.main.activity_memo.settingLayout
import kotlinx.android.synthetic.main.activity_wish.*
import kotlinx.android.synthetic.main.layout_memo_settings.*

class MemoActivity : AppCompatActivity(), SetMemo {
    lateinit var dbHelper : DBHelper
    lateinit var database : SQLiteDatabase
    var mid = -1
    var lock = 0
    var bkmr = 0
    var color = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memo)
        dbHelper = DBHelper(this)
        database = dbHelper.writableDatabase

        var intent = getIntent()
        color = intent.getIntExtra("color", 0)
        setColor(this, color, activity_memo)
        //if (mid != -1) {  } else {}

        getMemo()
        etMemo.setFocusAndShowKeyboard()

        settingLayout.visibility = View.INVISIBLE
        btnSettings.setOnClickListener {
            if (settingLayout.visibility == View.INVISIBLE) {
                settingLayout.visibility = View.VISIBLE
            }
            else {
                settingLayout.visibility = View.INVISIBLE
            }
        }

        cbLock.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked) {
                lock = 1
            }
            else {
                lock = 0
            }
        }
        cbBkmr.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked) {
                bkmr = 1
            }
            else {
                bkmr = 0
            }
        }
    }

    private fun EditText.setFocusAndShowKeyboard() {
        setSelection(this.text.length)
        this.requestFocus()
        this.postDelayed({
            val inputMethodManager =
                context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.showSoftInput(this, InputMethodManager.SHOW_FORCED)
        }, 100)
    }

    fun getMemo() {
        mid = 1

        var columns = arrayOf(DBHelper.MEM_COL_ID, DBHelper.MEM_COL_COLOR, DBHelper.MEM_COL_CONTENT, DBHelper.MEM_COL_LOCK, DBHelper.MEM_COL_BKMR)
        var selection = "_id=?"
        var selectArgs = arrayOf(mid.toString())
        var c : Cursor = database.query(DBHelper.MEM_TABLE_NAME, columns, selection, selectArgs, null, null, null)
        c.moveToNext()
        setColor(this, c.getInt(c.getColumnIndex(DBHelper.MEM_COL_COLOR)), activity_memo)
        etMemo.setText(c.getString(c.getColumnIndex(DBHelper.MEM_COL_CONTENT)))
        lock = c.getInt(c.getColumnIndex(DBHelper.MEM_COL_LOCK))
        bkmr = c.getInt(c.getColumnIndex(DBHelper.MEM_COL_BKMR))
        if (lock == 1) {
            cbLock.isChecked = true
        }
        if (bkmr == 1) {
            cbBkmr.isChecked = true
        }
    }

    override fun onBackPressed() {
        Log.d("yyj", "mem_BackPressed")
        var contentValues = ContentValues()
        contentValues.put(DBHelper.MEM_COL_WDATE, System.currentTimeMillis() / 1000L)
        contentValues.put(DBHelper.MEM_COL_COLOR, 0)
        contentValues.put(DBHelper.MEM_COL_CONTENT, etMemo.text.toString())
        contentValues.put(DBHelper.MEM_COL_LOCK, lock)
        contentValues.put(DBHelper.MEM_COL_BKMR, bkmr)

        if (mid != -1) {
            var whereCluase = "_id=?"
            var whereArgs = arrayOf(mid.toString())
            database.update(DBHelper.MEM_TABLE_NAME, contentValues, whereCluase, whereArgs)
        }
        else {
            database.insert(DBHelper.MEM_TABLE_NAME, null, contentValues)
        }

        //dbHelper.close() ondestroy
        //startActivity(Intent(this, MainActivity::class.java))
        //finish()
    }
}