package com.bluelay.damda

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_wish.*

class WishActivity : AppCompatActivity() {
    var wishList = arrayListOf<Wish>()
    lateinit var dbHelper : DBHelper
    lateinit var database : SQLiteDatabase
    var wid = -1
    var total = "0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wish)
        dbHelper = DBHelper(this)
        database = dbHelper.writableDatabase

        val wishAdapter = WishAdapter(this, wishList)

        //if (wid != -1) {  } else {}
       // getWishList()
        tvWishTotal.setText(total.toString())
        for (i in 1.. 10) {
            wishList.add(Wish("", null, 0, ""))
        }
        lvWish.adapter = wishAdapter
    }

    fun getWishList() {
        wid = 1

        var columns = arrayOf(DBHelper.WISL_COL_ID, DBHelper.WISL_COL_COLOR, DBHelper.WISL_COL_CATEGORY)
        var selection = "_id=?"
        var selectArgs = arrayOf(wid.toString())
        var c : Cursor = database.query(DBHelper.WISL_TABLE_NAME, columns, selection, selectArgs, null, null, null)
        c.moveToNext()
        etWishCategory.setText(c.getString(c.getColumnIndex(DBHelper.WISL_COL_CATEGORY)))
        var view = findViewById<ConstraintLayout>(R.id.activity_wish)
        when (c.getInt(c.getColumnIndex(DBHelper.WISL_COL_COLOR))) {
            0 -> view.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
            1 -> view.setBackgroundColor(ContextCompat.getColor(this, R.color.pastel_red))
            2 -> view.setBackgroundColor(ContextCompat.getColor(this, R.color.pastel_yellow))
            3 -> view.setBackgroundColor(ContextCompat.getColor(this, R.color.pastel_green))
            4 -> view.setBackgroundColor(ContextCompat.getColor(this, R.color.pastel_blue))
            5 -> view.setBackgroundColor(ContextCompat.getColor(this, R.color.pastel_purple))
            6 -> view.setBackgroundColor(ContextCompat.getColor(this, R.color.pastel_pink))
        }

        columns = arrayOf(DBHelper.WIS_COL_ID, DBHelper.WIS_COL_ITEM, DBHelper.WIS_COL_PRICE, DBHelper.WIS_COL_CHECKED, DBHelper.WIS_COL_LINK)
        selection = "wid=?"
        selectArgs = arrayOf(wid.toString())
        c = database.query(DBHelper.WIS_TABLE_NAME, columns, selection, selectArgs, null, null, null)
        wishList.clear()
        for (i in 1.. 10) {
            if (c.moveToNext()) {//null이면 null값 넣기
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
        contentValues.put(DBHelper.WISL_COL_WDATE, System.currentTimeMillis())
        contentValues.put(DBHelper.WISL_COL_COLOR, 6)
        contentValues.put(DBHelper.WISL_COL_CATEGORY, etWishCategory.text.toString())
        contentValues.put(DBHelper.WISL_COL_LOCK, 0)

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
}