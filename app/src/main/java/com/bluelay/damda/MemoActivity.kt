package com.bluelay.damda

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_memo.*
import kotlinx.android.synthetic.main.activity_wish.*

class MemoActivity : AppCompatActivity() {
    lateinit var dbHelper : DBHelper
    lateinit var database : SQLiteDatabase
    var mid = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memo)
        dbHelper = DBHelper(this)
        database = dbHelper.writableDatabase

        //if (mid != -1) {  } else {}
        //getMemo()
    }

    fun getMemo() {
        mid = 1

        var columns = arrayOf(DBHelper.MEM_COL_ID, DBHelper.MEM_COL_COLOR, DBHelper.MEM_COL_CONTENT)
        var selection = "_id=?"
        var selectArgs = arrayOf(mid.toString())
        var c : Cursor = database.query(DBHelper.MEM_TABLE_NAME, columns, selection, selectArgs, null, null, null)
        c.moveToNext()
        var view = findViewById<ConstraintLayout>(R.id.activity_memo)
        when (c.getInt(c.getColumnIndex(DBHelper.MEM_COL_COLOR))) {
            0 -> view.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
            1 -> view.setBackgroundColor(ContextCompat.getColor(this, R.color.pastel_red))
            2 -> view.setBackgroundColor(ContextCompat.getColor(this, R.color.pastel_yellow))
            3 -> view.setBackgroundColor(ContextCompat.getColor(this, R.color.pastel_green))
            4 -> view.setBackgroundColor(ContextCompat.getColor(this, R.color.pastel_blue))
            5 -> view.setBackgroundColor(ContextCompat.getColor(this, R.color.pastel_purple))
            6 -> view.setBackgroundColor(ContextCompat.getColor(this, R.color.pastel_pink))
        }
        etMemo.setText(c.getString(c.getColumnIndex(DBHelper.MEM_COL_CONTENT)))
    }

    override fun onBackPressed() {
        Log.d("yyj", "mem_BackPressed")
        var contentValues = ContentValues()
        contentValues.put(DBHelper.MEM_COL_WDATE, System.currentTimeMillis())
        contentValues.put(DBHelper.MEM_COL_COLOR, 6)
        contentValues.put(DBHelper.MEM_COL_CONTENT, etMemo.text.toString())
        contentValues.put(DBHelper.MEM_COL_LOCK, 0)

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