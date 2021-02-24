package com.bluelay.damda

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_wish.*
import kotlinx.android.synthetic.main.layout_memo_settings.*

class WishActivity : AppCompatActivity(), CalTotal, SetMemo {
    var wishList = arrayListOf<Wish>()
    lateinit var dbHelper : DBHelper
    lateinit var database : SQLiteDatabase
    var wid = -1
    var lock = -1
    var bkmr = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wish)
        dbHelper = DBHelper(this)
        database = dbHelper.writableDatabase
        val wishAdapter = WishAdapter(this, this, wishList)

        //if (wid != -1) {  } else {}
        //getWishList()
        for (i in 1.. 10) {
            wishList.add(Wish("", null, 0, ""))
        }
        lvWish.adapter = wishAdapter

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

    fun getWishList() {
        wid = 1

        var columns = arrayOf(DBHelper.WISL_COL_ID, DBHelper.WISL_COL_COLOR, DBHelper.WISL_COL_CATEGORY, DBHelper.WISL_COL_BKMR, DBHelper.WISL_COL_LOCK)
        var selection = "_id=?"
        var selectArgs = arrayOf(wid.toString())
        var c : Cursor = database.query(DBHelper.WISL_TABLE_NAME, columns, selection, selectArgs, null, null, null)
        c.moveToNext()
        etWishCategory.setText(c.getString(c.getColumnIndex(DBHelper.WISL_COL_CATEGORY)))
        var view = findViewById<ConstraintLayout>(R.id.activity_wish)
        setColor(this, c.getInt(c.getColumnIndex(DBHelper.WISL_COL_COLOR)), view)
        lock = c.getInt(c.getColumnIndex(DBHelper.WISL_COL_LOCK))
        bkmr = c.getInt(c.getColumnIndex(DBHelper.WISL_COL_BKMR))
        if (lock == 1) {
            cbLock.isChecked = true
        }
        if (bkmr == 1) {
            cbBkmr.isChecked = true
        }

        columns = arrayOf(DBHelper.WIS_COL_ID, DBHelper.WIS_COL_ITEM, DBHelper.WIS_COL_PRICE, DBHelper.WIS_COL_CHECKED, DBHelper.WIS_COL_LINK)
        selection = "wid=?"
        selectArgs = arrayOf(wid.toString())
        c = database.query(DBHelper.WIS_TABLE_NAME, columns, selection, selectArgs, null, null, null)
        wishList.clear()
        for (i in 1.. 10) {
            if (c.moveToNext()) {
                wishList.add(Wish(c.getString(c.getColumnIndex(DBHelper.WIS_COL_ITEM)), c.getInt(c.getColumnIndex(DBHelper.WIS_COL_PRICE)), c.getInt(c.getColumnIndex(DBHelper.WIS_COL_CHECKED)), c.getString(c.getColumnIndex(DBHelper.WIS_COL_LINK))))
            }
            else {
                wishList.add(Wish("", null, 0, ""))
            }
        }
    }

    override fun onBackPressed() {
        Log.d("yyj", "wish_BackPressed")
        var contentValues = ContentValues()
        contentValues.put(DBHelper.WISL_COL_WDATE, System.currentTimeMillis() / 1000L)
        contentValues.put(DBHelper.WISL_COL_COLOR, 3)
        contentValues.put(DBHelper.WISL_COL_CATEGORY, etWishCategory.text.toString())
        contentValues.put(DBHelper.WISL_COL_LOCK, lock)
        contentValues.put(DBHelper.WISL_COL_BKMR, bkmr)

       if (wid != -1) {
            var whereCluase = "_id=?"
            var whereArgs = arrayOf(wid.toString())
            database.update(DBHelper.WISL_TABLE_NAME, contentValues, whereCluase, whereArgs)

            whereCluase = "wid=?"
            database.delete(DBHelper.WIS_TABLE_NAME, whereCluase, whereArgs)
        }
       else {
           wid = database.insert(DBHelper.WISL_TABLE_NAME, null, contentValues).toInt() //int 변환???
       }
        for(wish in wishList){
            contentValues.clear()
            if (!wish.item.replace(" ", "").equals("")) {//null 확인 (어댑터에도)
                contentValues.put(DBHelper.WIS_COL_WID, wid)
                contentValues.put(DBHelper.WIS_COL_ITEM, wish.item)
                contentValues.put(DBHelper.WIS_COL_PRICE, wish.price)
                contentValues.put(DBHelper.WIS_COL_CHECKED, wish.checked)
                contentValues.put(DBHelper.WIS_COL_LINK, wish.link)
                database.insert(DBHelper.WIS_TABLE_NAME, null, contentValues)
            }
        }
        //dbHelper.close() ondestroy
        //startActivity(Intent(this, MainActivity::class.java))
        //finish()
    }

    override fun cal(total: String) {
        tvWishTotal.text = total
    }
}