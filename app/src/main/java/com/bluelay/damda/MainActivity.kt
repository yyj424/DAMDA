package com.bluelay.damda

import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    lateinit var dbHelper : DBHelper
    lateinit var database : SQLiteDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //taeyeon 브랜치 만들어서 push 해보겠음
        //주석
        dbHelper = DBHelper(this)
        database = dbHelper.writableDatabase
    }
}