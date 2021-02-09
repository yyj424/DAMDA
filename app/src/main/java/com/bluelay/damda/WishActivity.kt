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
import kotlinx.android.synthetic.main.activity_wish.*

class WishActivity : AppCompatActivity() {
    var wishList = arrayListOf<Wish>()
    lateinit var dbHelper : DBHelper
    lateinit var database : SQLiteDatabase
    var wid = -1
    var total = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wish)
        val lvWish = findViewById<ListView>(R.id.lvWish)
        val tvWishTotal = findViewById<TextView>(R.id.tvWishTotal)
        val etWishCategory = findViewById<EditText>(R.id.etWishCategory)
        dbHelper = DBHelper(this)
        database = dbHelper.writableDatabase

        val wishAdapter = WishAdapter(this, wishList)

        //if (wid != -1) {  } else {}
        //getWishList()
        tvWishTotal.setText(total.toString())//total 어찌 해결?
        for (i in 1.. 10) {
            wishList.add(Wish("", 0, 0, ""))//price 0 없애기
        }
        lvWish.adapter = wishAdapter
    }

    fun getWishList() {
        wid = 1
        var columns = arrayOf(DBHelper.WIS_COL_ID, DBHelper.WIS_COL_ITEM, DBHelper.WIS_COL_PRICE, DBHelper.WIS_COL_CHECKED, DBHelper.WIS_COL_LINK)
        var selection = "wid=?"
        var selectArgs = arrayOf(wid.toString())
        var c : Cursor = database.query(DBHelper.WIS_TABLE_NAME, columns, selection, selectArgs, null, null, null)
        wishList.clear()
        for (i in 1.. 10) {
            if (c.moveToNext()) {
                wishList.add(Wish(c.getString(c.getColumnIndex(DBHelper.WIS_COL_ITEM)), c.getInt(c.getColumnIndex(DBHelper.WIS_COL_PRICE)), c.getInt(c.getColumnIndex(DBHelper.WIS_COL_CHECKED)), c.getString(c.getColumnIndex(DBHelper.WIS_COL_LINK))))
            }
            else {
                wishList.add(Wish("", 0, 0, ""))
            }
        }
    }

    override fun onBackPressed() {
        Log.d("yyj", "BackPressed")
        /*var contentValues = ContentValues()
        contentValues.put(DBHelper.WISL_COL_WDATE , System.currentTimeMillis());
        contentValues.put(DBHelper.WISL_COL_COLOR , 0);

        if (wid != -1) {
            var whereCluase = "_id=?"
            var whereArgs = arrayOf(wid.toString())
            database.update(DBHelper.WISL_TABLE_NAME, contentValues, whereCluase, whereArgs)

            whereCluase = "wid=?"
            database.delete(DBHelper.WIS_TABLE_NAME, whereCluase, whereArgs)
        }
        //else  {
        // wid = database.insert(DBHelper.WISL_TABLE_NAME, null, contentValues)
        // }
        for(wish in wishList){
            contentValues.clear()
            if (wish.content != "") {
                contentValues.put(DBHelper.WIS_COL_wid, wid)
                contentValues.put(DBHelper.WIS_COL_DATE, "2020.01.28")
                contentValues.put(DBHelper.WIS_COL_CHECKED, wish.checked)
                contentValues.put(DBHelper.WIS_COL_CONTENT, wish.content)
                database.insert(DBHelper.WIS_TABLE_NAME, null, contentValues)
            }
        }
        //dbHelper.close() ondestroy
        //startActivity(Intent(this, MainActivity::class.java))
        //finish()*/
    }
}