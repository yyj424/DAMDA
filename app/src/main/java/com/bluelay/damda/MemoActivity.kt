package com.bluelay.damda

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_memo.*
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

        color = intent.getIntExtra("color", 0)
        setColor(this, color, activity_memo)

        if (mid != -1) {
          getMemo()
        }

        etMemo.setFocusAndShowKeyboard()

        settingLayout.visibility = View.INVISIBLE
        btnSettings.setOnClickListener {
            settingLayout.visibility = if (settingLayout.visibility == View.INVISIBLE) View.VISIBLE else View.INVISIBLE
        }

        cbLock.setOnCheckedChangeListener { _, isChecked ->
            lock = if(isChecked) 1 else 0
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
                when (v) {
                    ivColor0 -> {
                        color = 0
                    }
                    ivColor1 -> {
                        color = 1
                    }
                    ivColor2 -> {
                        color = 2
                    }
                    ivColor3 -> {
                        color = 3
                    }
                    ivColor4 -> {
                        color = 4
                    }
                    ivColor5 -> {
                        color = 5
                    }
                    ivColor6 -> {
                        color = 6
                    }
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
        c.close()
    }

    override fun onBackPressed() {
        var contentValues = ContentValues()
        contentValues.put(DBHelper.MEM_COL_WDATE, System.currentTimeMillis() / 1000L)
        contentValues.put(DBHelper.MEM_COL_COLOR, color)
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

        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        dbHelper.close()
    }
}