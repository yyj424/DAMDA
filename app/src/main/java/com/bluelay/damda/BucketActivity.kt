package com.bluelay.damda

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_bucket.*
import androidx.appcompat.app.AppCompatActivity

class BucketActivity : AppCompatActivity() {
    var bucketList = arrayListOf<Bucket>()
    lateinit var dbHelper : DBHelper
    lateinit var database : SQLiteDatabase
    var bid = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bucket)

        dbHelper = DBHelper(this)
        database = dbHelper.writableDatabase

        val bucketAdapter = BucketAdapter(this, bucketList)

        //if (bid != -1) {  } else {}
        getBucketList()
        /*for (i in 1.. 10) {
            bucketList.add(Bucket("", 0))
        }*/
        lvBucket.adapter = bucketAdapter
    }

    fun getBucketList() {
        bid = 1 //임시 bid
        var columns = arrayOf(DBHelper.BUC_COL_ID, DBHelper.BUC_COL_DATE, DBHelper.BUC_COL_CHECKED, DBHelper.BUC_COL_CONTENT)
        var selection = "bid=?"
        var selectArgs = arrayOf(bid.toString())
        var c : Cursor = database.query("Bucket", columns, selection, selectArgs, null, null, null)
        bucketList.clear()
        for (i in 1.. 10) {
            if (c.moveToNext()) {
                bucketList.add(Bucket(c.getString(c.getColumnIndex(DBHelper.BUC_COL_CONTENT)), c.getInt(c.getColumnIndex(DBHelper.BUC_COL_CHECKED))))
            }
            else {
                bucketList.add(Bucket("", 0))
            }
        }
    }
    override fun onBackPressed() {
        Log.d("yyj", "BackPressed")
        var contentValues = ContentValues()
        contentValues.put(DBHelper.BUCL_COL_WDATE , System.currentTimeMillis());
        contentValues.put(DBHelper.BUCL_COL_COLOR , 0);

        if (bid != -1) {
            var whereCluase = "_id=?"
            var whereArgs = arrayOf(bid.toString())
            database.update(DBHelper.BUCL_TABLE_NAME, contentValues, whereCluase, whereArgs)

            whereCluase = "bid=?"
            database.delete(DBHelper.BUC_TABLE_NAME, whereCluase, whereArgs)
        }
        //else  {
        // bid = database.insert(DBHelper.BUCL_TABLE_NAME, null, contentValues)
        // }
        for(bucket in bucketList){
            contentValues.clear()
            if (bucket.content != "") {
                contentValues.put(DBHelper.BUC_COL_BID, bid)
                contentValues.put(DBHelper.BUC_COL_DATE, "2020.01.28")
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