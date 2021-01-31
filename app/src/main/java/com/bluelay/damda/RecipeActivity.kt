package com.bluelay.damda

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_recipe.*

class RecipeActivity : AppCompatActivity() {

    lateinit var dbHelper : DBHelper
    lateinit var database : SQLiteDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe)

        dbHelper = DBHelper(this)
        database = dbHelper.writableDatabase

    }

    override fun onBackPressed() {

        var contentValues = ContentValues()
        contentValues.put(DBHelper.REC_COL_WDATE, System.currentTimeMillis())
        contentValues.put(DBHelper.REC_COL_NAME, etRecipeName.text.toString())
        contentValues.put(DBHelper.REC_COL_INGREDIENTS, etIngredients.text.toString())
        contentValues.put(DBHelper.REC_COL_CONTENT, etRecipeContent.text.toString())
        contentValues.put(DBHelper.REC_COL_COLOR, 0)

        database.insert(DBHelper.REC_TABLE_NAME, null, contentValues)

        Log.d("aty", "insert")
        //startActivity(Intent(this, MainActivity::class.java))
        //finish()
    }
}