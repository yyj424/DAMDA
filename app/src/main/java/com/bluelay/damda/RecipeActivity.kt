package com.bluelay.damda

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_recipe.*
import kotlinx.android.synthetic.main.activity_todo.*

class RecipeActivity : AppCompatActivity() {

    lateinit var dbHelper : DBHelper
    lateinit var database : SQLiteDatabase

    // TODO: 메인 만든 후에 ID 수정!!!!!! -1 로 초기화, putExtra 있으면 그 값 넣기
    private var recipeId = -1
    private var color = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe)

        dbHelper = DBHelper(this)

        if (recipeId != -1) {
            selectRecipe()
        }

    }

    override fun onBackPressed() {

        if (recipeId == -1) {
            insertRecipe()
        }
        else {
            updateRecipe()
        }

        //startActivity(Intent(this, MainActivity::class.java))
        //finish()
    }

    private fun selectRecipe() {
        database = dbHelper.readableDatabase

       var c : Cursor = database.query(DBHelper.REC_TABLE_NAME, null, null, null, null, null, null)
        while(c.moveToNext()){
            etRecipeName.setText(c.getString(c.getColumnIndex(DBHelper.REC_COL_NAME)))
            etIngredients.setText(c.getString(c.getColumnIndex(DBHelper.REC_COL_INGREDIENTS)))
            etRecipeContent.setText(c.getString(c.getColumnIndex(DBHelper.REC_COL_CONTENT)))
        }
        Log.d("aty", "recipeSelect")
    }

    private fun insertRecipe() {
        database = dbHelper.writableDatabase
        var contentValues = ContentValues()

        contentValues.put(DBHelper.REC_COL_WDATE, System.currentTimeMillis()/1000L)
        contentValues.put(DBHelper.REC_COL_NAME, etRecipeName.text.toString())
        contentValues.put(DBHelper.REC_COL_INGREDIENTS, etIngredients.text.toString())
        contentValues.put(DBHelper.REC_COL_CONTENT, etRecipeContent.text.toString())
        contentValues.put(DBHelper.REC_COL_COLOR, color)

        database.insert(DBHelper.REC_TABLE_NAME, null, contentValues)

        Log.d("aty", "recipeInsert")
    }

    private fun updateRecipe() {
        database = dbHelper.writableDatabase
        val contentValues = ContentValues()

        contentValues.put(DBHelper.REC_COL_WDATE, System.currentTimeMillis())
        contentValues.put(DBHelper.REC_COL_NAME, etRecipeName.text.toString())
        contentValues.put(DBHelper.REC_COL_INGREDIENTS, etIngredients.text.toString())
        contentValues.put(DBHelper.REC_COL_CONTENT, etRecipeContent.text.toString())
        contentValues.put(DBHelper.REC_COL_COLOR, color)

        database.update(DBHelper.REC_TABLE_NAME, contentValues, "${DBHelper.REC_COL_ID}=?", arrayOf(recipeId.toString()))

        Log.d("aty", "recipeUpdate")
    }


}