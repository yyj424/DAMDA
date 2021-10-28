package com.bluelay.damda

import android.app.AlertDialog
import android.appwidget.AppWidgetManager
import android.content.ContentValues
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_recipe.*
import kotlinx.android.synthetic.main.layout_memo_settings.*

class RecipeActivity : AppCompatActivity(), SetMemo{
    private lateinit var dbHelper : DBHelper
    private lateinit var database : SQLiteDatabase

    private var recipeId = -1

    private var lock = 0
    private var bkmr = 0
    var color = -1

    private var name = ""
    private var ingredients = ""
    private var content = ""

    private lateinit var memo : MemoInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe)

        dbHelper = DBHelper(this)

        if (intent.hasExtra("memo")) {
            btnDeleteMemo.visibility = View.VISIBLE
            memo = intent.getSerializableExtra("memo") as MemoInfo
            color = memo.color
            recipeId = memo.id
            lock = memo.lock
            bkmr = memo.bkmr
            selectRecipe()
        }
        else {
            color = intent.getIntExtra("color", 0)
        }
        setColor(this, color, activity_recipe)

        fabMemoSetting.setOnClickListener {
            it.startAnimation(
                AnimationUtils.loadAnimation(
                applicationContext, R.anim.fade_out
            ))
            btnCloseSetting.startAnimation(
                AnimationUtils.loadAnimation(
                applicationContext, R.anim.fade_in
            ))
            it.visibility = View.INVISIBLE
            settingLayout.visibility = View.VISIBLE
        }
        btnCloseSetting.setOnClickListener {
            settingLayout.startAnimation(
                AnimationUtils.loadAnimation(
                applicationContext, R.anim.fade_out
            ))
            fabMemoSetting.startAnimation(
                AnimationUtils.loadAnimation(
                applicationContext, R.anim.fade_in
            ))
            settingLayout.visibility = View.INVISIBLE
            fabMemoSetting.visibility = View.VISIBLE
        }

        cbLock.setOnCheckedChangeListener { _, isChecked ->
            if (checkExistPassword(this) && isChecked) {
                lock = 1
            }
            else {
                lock = 0
                cbLock.isChecked = false
            }
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
                color = when (v) {
                    ivColor0 -> 0
                    ivColor1 -> 1
                    ivColor2 -> 2
                    ivColor3 -> 3
                    ivColor4 -> 4
                    ivColor5 -> 5
                    ivColor6 -> 6
                    else -> 0
                }
                setColor(this, color, activity_recipe)
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

        btnSaveMemo.setOnClickListener {
            saveMemo()
        }

        btnDeleteMemo.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val view = LayoutInflater.from(this).inflate(R.layout.dialog_delete_memo, null)
            builder.setView(view)
            val btnDelConfirm = view.findViewById<Button>(R.id.btnDelConfirm)
            val btnDelCancel = view.findViewById<Button>(R.id.btnDelCancel)
            val dialog = builder.create()
            btnDelConfirm.setOnClickListener{
                dialog.dismiss()
                deleteMemo()
            }
            btnDelCancel.setOnClickListener{
                dialog.dismiss()
            }
            dialog.show()
        }
    }

    override fun onBackPressed() {
        saveMemo()
    }

    private fun selectRecipe() {
        database = dbHelper.readableDatabase

        val c : Cursor = database.rawQuery("SELECT * FROM ${DBHelper.REC_TABLE_NAME} WHERE ${DBHelper.REC_COL_ID} = ?", arrayOf(recipeId.toString()))
        if (lock == 1) {
            cbLock.isChecked = true
        }
        if (bkmr == 1) {
            cbBkmr.isChecked = true
        }

        while(c.moveToNext()){
            name = c.getString(c.getColumnIndex(DBHelper.REC_COL_NAME))
            ingredients = c.getString(c.getColumnIndex(DBHelper.REC_COL_INGREDIENTS))
            content = c.getString(c.getColumnIndex(DBHelper.REC_COL_CONTENT))

            etRecipeName.setText(name)
            etIngredients.setText(ingredients)
            etRecipeContent.setText(content)
        }
        c.close()
    }

    private fun insertRecipe() {
        database = dbHelper.writableDatabase
        val contentValues = ContentValues()

        contentValues.put(DBHelper.REC_COL_WDATE, System.currentTimeMillis()/1000L)
        contentValues.put(DBHelper.REC_COL_NAME, etRecipeName.text.toString())
        contentValues.put(DBHelper.REC_COL_INGREDIENTS, etIngredients.text.toString())
        contentValues.put(DBHelper.REC_COL_CONTENT, etRecipeContent.text.toString())
        contentValues.put(DBHelper.REC_COL_COLOR, color)
        contentValues.put(DBHelper.REC_COL_BKMR, bkmr)
        contentValues.put(DBHelper.REC_COL_LOCK, lock)

        database.insert(DBHelper.REC_TABLE_NAME, null, contentValues)
    }

    private fun updateRecipe() {
        database = dbHelper.writableDatabase
        val contentValues = ContentValues()

        contentValues.put(DBHelper.REC_COL_WDATE, System.currentTimeMillis()/1000L)
        contentValues.put(DBHelper.REC_COL_NAME, etRecipeName.text.toString())
        contentValues.put(DBHelper.REC_COL_INGREDIENTS, etIngredients.text.toString())
        contentValues.put(DBHelper.REC_COL_CONTENT, etRecipeContent.text.toString())
        contentValues.put(DBHelper.REC_COL_COLOR, color)
        contentValues.put(DBHelper.REC_COL_BKMR, bkmr)
        contentValues.put(DBHelper.REC_COL_LOCK, lock)

        database.update(DBHelper.REC_TABLE_NAME, contentValues, "${DBHelper.REC_COL_ID}=?", arrayOf(recipeId.toString()))
    }

    private fun saveMemo() {
        if (recipeId == -1) {
            insertRecipe()
        }
        else {
            if(checkUpdate()) {
                updateRecipe()
                updateWidget()
            }
        }
        finish()
    }

    private fun checkUpdate() : Boolean {
        if (color != memo.color) return true
        if (bkmr != memo.bkmr) return true
        if (lock != memo.lock) return true
        if (name != etRecipeName.text.toString()) return true
        if (ingredients != etIngredients.text.toString()) return true
        if (content != etRecipeContent.text.toString()) return true
        return false
    }

    private fun deleteMemo() {
        database.execSQL("DELETE FROM ${DBHelper.REC_TABLE_NAME} WHERE _id = $recipeId")
        updateWidget()
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        dbHelper.close()
    }

    private fun updateWidget() {
        val largeWidgetIntent = Intent(this, LargeWidget::class.java)
        largeWidgetIntent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        this.sendBroadcast(largeWidgetIntent)

        val smallWidgetIntent = Intent(this, SmallWidget::class.java)
        smallWidgetIntent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        this.sendBroadcast(smallWidgetIntent)
    }
}