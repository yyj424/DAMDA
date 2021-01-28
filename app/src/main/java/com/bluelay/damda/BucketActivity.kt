package com.bluelay.damda

import android.content.ContentValues
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class BucketActivity : AppCompatActivity() {
    var bucketList = arrayListOf<Bucket>()
    lateinit var dbHelper : DBHelper
    lateinit var database : SQLiteDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bucket)

        val lvBucket = findViewById<ListView>(R.id.lv_bucket)
        val bucketAdapter = BucketAdapter(this, bucketList)
        for (i in 1.. 10) {
            bucketList.add(Bucket("", 0))
        }
        lvBucket.adapter = bucketAdapter

        dbHelper = DBHelper(this)
        database = dbHelper.writableDatabase
    }

    override fun onBackPressed() {
        var contentValues = ContentValues()
        contentValues.put("wdate" , System.currentTimeMillis());
        contentValues.put("color" , 0);
        val bid = database.insert("BucketList", null, contentValues)

        for(bucket in bucketList){
            contentValues.clear()
            if (bucket.content != "") {
            contentValues.put("bid", bid)
            contentValues.put("date", "2020.01.28")
            contentValues.put("checked", bucket.checked)
            contentValues.put("content", bucket.content)
            database.insert("Bucket", null, contentValues) }
        }
        Log.d("yyj", "insert")
        //dbHelper.close() ondestroy
        //startActivity(Intent(this, MainActivity::class.java))
        //finish()
    }
}