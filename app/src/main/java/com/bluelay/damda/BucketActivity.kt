package com.bluelay.damda

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_bucket.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.adapter_view_bucket.*
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter

class BucketActivity : AppCompatActivity() {
    var bucketList = arrayListOf<Bucket>()
    lateinit var dbHelper : DBHelper
    lateinit var database : SQLiteDatabase
    var bid = -1
    var color = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bucket)

        dbHelper = DBHelper(this)
        database = dbHelper.writableDatabase

        val bucketAdapter = BucketAdapter(this, bucketList)

        //if (bid != -1) {  } else {}
        //color =
        getBucketList()
        /*for (i in 1.. 10) {
            bucketList.add(Bucket("", 0, ""))
        }*/
        lvBucket.adapter = bucketAdapter
    }

    fun getBucketList() {
        bid = 1 //임시 bid

        var columns = arrayOf(DBHelper.BUCL_COL_ID, DBHelper.BUCL_COL_COLOR)
        var selection = "_id=?"
        var selectArgs = arrayOf(bid.toString())
        var c : Cursor = database.query(DBHelper.BUCL_TABLE_NAME, columns, selection, selectArgs, null, null, null)
        c.moveToNext()
        var view = findViewById<ConstraintLayout>(R.id.activity_bucket)
        when (c.getInt(c.getColumnIndex(DBHelper.BUCL_COL_COLOR))) {
            0 -> view.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
            1 -> view.setBackgroundColor(ContextCompat.getColor(this, R.color.pastel_red))
            2 -> view.setBackgroundColor(ContextCompat.getColor(this, R.color.pastel_yellow))
            3 -> view.setBackgroundColor(ContextCompat.getColor(this, R.color.pastel_green))
            4 -> view.setBackgroundColor(ContextCompat.getColor(this, R.color.pastel_blue))
            5 -> view.setBackgroundColor(ContextCompat.getColor(this, R.color.pastel_purple))
            6 -> view.setBackgroundColor(ContextCompat.getColor(this, R.color.pastel_pink))
        }

        columns = arrayOf(DBHelper.BUC_COL_ID, DBHelper.BUC_COL_DATE, DBHelper.BUC_COL_CHECKED, DBHelper.BUC_COL_CONTENT)
        selection = "bid=?"
        selectArgs = arrayOf(bid.toString())
        c = database.query(DBHelper.BUC_TABLE_NAME, columns, selection, selectArgs, null, null, null)
        bucketList.clear()
        for (i in 1.. 10) {
            if (c.moveToNext()) {
                bucketList.add(Bucket(c.getString(c.getColumnIndex(DBHelper.BUC_COL_CONTENT)), c.getInt(c.getColumnIndex(DBHelper.BUC_COL_CHECKED)), c.getString(c.getColumnIndex(DBHelper.BUC_COL_DATE))))
            }
            else {
                bucketList.add(Bucket("", 0, ""))
            }
        }
    }

    override fun onBackPressed() {
        Log.d("yyj", "BackPressed")
        var contentValues = ContentValues()
        contentValues.put(DBHelper.BUCL_COL_WDATE, System.currentTimeMillis())
        //contentValues.put(DBHelper.BUCL_COL_COLOR, 4)
        contentValues.put(DBHelper.BUCL_COL_LOCK, 0)

        if (bid != -1) {
            var whereCluase = "_id=?"
            var whereArgs = arrayOf(bid.toString())
            database.update(DBHelper.BUCL_TABLE_NAME, contentValues, whereCluase, whereArgs)

            whereCluase = "bid=?"
            database.delete(DBHelper.BUC_TABLE_NAME, whereCluase, whereArgs)
        }
        else  {
            bid = database.insert(DBHelper.BUCL_TABLE_NAME, null, contentValues).toInt()
        }

        for(bucket in bucketList){
            contentValues.clear()
            if (bucket.content != "") {
                contentValues.put(DBHelper.BUC_COL_BID, bid)
                contentValues.put(DBHelper.BUC_COL_DATE, bucket.date)
                contentValues.put(DBHelper.BUC_COL_CHECKED, bucket.checked)
                contentValues.put(DBHelper.BUC_COL_CONTENT, bucket.content)
                database.insert(DBHelper.BUC_TABLE_NAME, null, contentValues)
            }
        }
        //dbHelper.close() ondestroy
        //startActivity(Intent(this, MainActivity::class.java))
        //finish()
    }
}