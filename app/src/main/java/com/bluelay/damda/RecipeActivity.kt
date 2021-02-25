package com.bluelay.damda

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_recipe.*
import kotlinx.android.synthetic.main.layout_memo_settings.*

class RecipeActivity : AppCompatActivity(), SetMemo{

    lateinit var dbHelper : DBHelper
    lateinit var database : SQLiteDatabase

    // TODO: 메인 만든 후에 ID 수정!!!!!! -1 로 초기화, putExtra 있으면 그 값 넣기
    private var recipeId = -1

    var lock = 0
    var bkmr = 0
    var color = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe)

        dbHelper = DBHelper(this)

        var intent = getIntent()
        color = intent.getIntExtra("color", 0)
        setColor(this, color, activity_recipe)

        if (recipeId != -1) {
            selectRecipe()
        }

        settingLayout.visibility = View.INVISIBLE
        btnSettings.setOnClickListener {
            settingLayout.visibility = if (settingLayout.visibility == View.INVISIBLE) View.VISIBLE  else View.INVISIBLE
        }

        cbLock.setOnCheckedChangeListener { _, isChecked ->
            lock =  if(isChecked) 1 else 0
        }
        cbBkmr.setOnCheckedChangeListener { _, isChecked ->
            bkmr = if(isChecked) 1 else 0
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
        finish()
    }

    private fun selectRecipe() {
        Log.d("RecipeActivity", "recipeSelect")
        database = dbHelper.readableDatabase

        var c : Cursor = database.rawQuery("SELECT * FROM ${DBHelper.REC_TABLE_NAME} WHERE ${DBHelper.REC_COL_ID} = ?", arrayOf(recipeId.toString()))
        while(c.moveToNext()){
            etRecipeName.setText(c.getString(c.getColumnIndex(DBHelper.REC_COL_NAME)))
            etIngredients.setText(c.getString(c.getColumnIndex(DBHelper.REC_COL_INGREDIENTS)))
            etRecipeContent.setText(c.getString(c.getColumnIndex(DBHelper.REC_COL_CONTENT)))
        }
    }

    private fun insertRecipe() {
        database = dbHelper.writableDatabase
        var contentValues = ContentValues()

        contentValues.put(DBHelper.REC_COL_WDATE, System.currentTimeMillis()/1000L)
        contentValues.put(DBHelper.REC_COL_NAME, etRecipeName.text.toString())
        contentValues.put(DBHelper.REC_COL_INGREDIENTS, etIngredients.text.toString())
        contentValues.put(DBHelper.REC_COL_CONTENT, etRecipeContent.text.toString())
        contentValues.put(DBHelper.REC_COL_COLOR, color)
        contentValues.put(DBHelper.REC_COL_BKMR, bkmr)
        contentValues.put(DBHelper.REC_COL_LOCK, lock)

        database.insert(DBHelper.REC_TABLE_NAME, null, contentValues)

        Log.d("RecipeActivity", "recipeInsert")
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

        Log.d("RecipeActivity", "recipeUpdate")
    }


}