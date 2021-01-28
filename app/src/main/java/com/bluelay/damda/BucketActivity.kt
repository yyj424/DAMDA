package com.bluelay.damda

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
        for(bucket in bucketList){

            Log.d("yyj", "con: " +  bucket.content + " // ch: " + bucket.checked)
        }
        //startActivity(Intent(this, MainActivity::class.java))
        //finish()
    }
}