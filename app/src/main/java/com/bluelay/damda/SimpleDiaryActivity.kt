package com.bluelay.damda

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_simple_diary.*


class SimpleDiaryActivity : AppCompatActivity(){

    lateinit var dbHelper : DBHelper
    lateinit var database : SQLiteDatabase
    var did = -1

    var diaryList = arrayListOf<SimpleDiary>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simple_diary)

        dbHelper = DBHelper(this)
        database = dbHelper.writableDatabase

        val diaryAdapter = SimpleDiaryAdapter(this, diaryList)
        lvDiary.adapter = diaryAdapter

        for (i in 1.. 7) {
            diaryList.add(
                SimpleDiary("날짜", "", getURLForResource(R.drawable.select_emoji).toString(),
                getURLForResource(R.drawable.select_weather).toString())
            )
        }
        lvDiary.adapter = diaryAdapter
    }

    override fun onBackPressed() {
        Log.d("aty", "onBackPressed")
        var contentValues = ContentValues()
        contentValues.put(DBHelper.WEE_COL_WDATE , System.currentTimeMillis());
        contentValues.put(DBHelper.WEE_COL_COLOR , 0);

        if (did != -1) {
            var whereCluase = "_id=?"
            var whereArgs = arrayOf(did.toString())
            database.update(DBHelper.WEE_TABLE_NAME, contentValues, whereCluase, whereArgs)

            whereCluase = "did=?"
            database.delete(DBHelper.DIA_TABLE_NAME, whereCluase, whereArgs)
        }
        else  {
            did = database.insert(DBHelper.WEE_TABLE_NAME, null, contentValues).toInt()
        }
        for(diary in diaryList){
            contentValues.clear()
            if (diary.content != "") {
                contentValues.put(DBHelper.DIA_COL_DID, did)
                contentValues.put(DBHelper.DIA_COL_DATE, diary.date)
                contentValues.put(DBHelper.DIA_COL_WEATHER, diary.weather)
                contentValues.put(DBHelper.DIA_COL_MOODPIC, diary.moodPic)
                contentValues.put(DBHelper.DIA_COL_CONTENT, diary.content)
                database.insert(DBHelper.DIA_TABLE_NAME, null, contentValues)
            }
        }
        //dbHelper.close() ondestroy
        //startActivity(Intent(this, MainActivity::class.java))
        //finish()
    }

    private fun getURLForResource(resId: Int): String? {
        return Uri.parse("android.resource://" + R::class.java.getPackage().name + "/" + resId)
            .toString()
    }
}