package com.bluelay.damda

import android.app.AlertDialog
import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_bucket.*
import kotlinx.android.synthetic.main.activity_recipe.*
import kotlinx.android.synthetic.main.activity_recipe.btnSettings
import kotlinx.android.synthetic.main.activity_recipe.settingLayout
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

        btnChangeColor.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val view = LayoutInflater.from(this).inflate(R.layout.dialog_change_color, null)
            builder.setView(view)
            val dialog = builder.create()
            val ivColor0 = view.findViewById<ImageView>(R.id.ivColor0)
            val ivColor1 = view.findViewById<ImageView>(R.id.ivColor1)
            val ivColor2 = view.findViewById<ImageView>(R.id.ivColor2)
            val ivColor3 = view.findViewById<ImageView>(R.id.ivColor3)
            val ivColor4 = view.findViewById<ImageView>(R.id.ivColor4)
            val ivColor5 = view.findViewById<ImageView>(R.id.ivColor5)
            val ivColor6 = view.findViewById<ImageView>(R.id.ivColor6)

            val colorClickListener = View.OnClickListener { v ->
                when (v) {
                    ivColor0 -> {
                        color = 0
                    }
                    ivColor1 -> {
                        color = 1
                    }
                    ivColor2 -> {
                        color = 2
                    }
                    ivColor3 -> {
                        color = 3
                    }
                    ivColor4 -> {
                        color = 4
                    }
                    ivColor5 -> {
                        color = 5
                    }
                    ivColor6 -> {
                        color = 6
                    }
                }
                setColor(this, color, activity_bucket)
                dialog.dismiss()
            }

            ivColor0!!.setOnClickListener(colorClickListener)
            ivColor1!!.setOnClickListener(colorClickListener)
            ivColor2!!.setOnClickListener(colorClickListener)
            ivColor3!!.setOnClickListener(colorClickListener)
            ivColor4!!.setOnClickListener(colorClickListener)
            ivColor5!!.setOnClickListener(colorClickListener)
            ivColor6!!.setOnClickListener(colorClickListener)

            dialog.show()
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
        c.close()
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

    override fun onDestroy() {
        super.onDestroy()
        dbHelper.close()
    }
}