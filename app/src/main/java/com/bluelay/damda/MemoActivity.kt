package com.bluelay.damda

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_memo.*
import kotlinx.android.synthetic.main.activity_memo.btnSettings
import kotlinx.android.synthetic.main.activity_memo.settingLayout
import kotlinx.android.synthetic.main.activity_wish.*
import kotlinx.android.synthetic.main.layout_memo_settings.*

class MemoActivity : AppCompatActivity() {
    lateinit var dbHelper : DBHelper
    lateinit var database : SQLiteDatabase
    var mid = -1
    var lock = -1
    var bkmr = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memo)
        dbHelper = DBHelper(this)
        database = dbHelper.writableDatabase

        //if (mid != -1) {  } else {}
       // getd()
        getMemo()
        etMemo.setFocusAndShowKeyboard()

        settingLayout.visibility = View.INVISIBLE
        btnSettings.setOnClickListener {
            if (settingLayout.visibility == View.INVISIBLE) {
                settingLayout.visibility = View.VISIBLE
            }
            else {
                settingLayout.visibility = View.INVISIBLE
            }
        }

        cbLock.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked) {
                lock = 1
            }
            else {
                lock = 0
            }
        }
        cbBkmr.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked) {
                bkmr = 1
            }
            else {
                bkmr = 0
            }
        }
    }

    fun getd() {
        val builder = AlertDialog.Builder(this)
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_select_memo, null)
        val llMemo = view.findViewById<LinearLayout>(R.id.llMemo)
        val llTodo = view.findViewById<LinearLayout>(R.id.llTodo)
        val llDiary = view.findViewById<LinearLayout>(R.id.llDiary)
        val llBucket = view.findViewById<LinearLayout>(R.id.llBucket)
        val llWish = view.findViewById<LinearLayout>(R.id.llWish)
        val llRecipe = view.findViewById<LinearLayout>(R.id.llRecipe)
        val llMovie = view.findViewById<LinearLayout>(R.id.llMovie)
        val ivColor0 = view.findViewById<ImageView>(R.id.ivColor0)
        val ivColor1 = view.findViewById<ImageView>(R.id.ivColor1)
        val ivColor2 = view.findViewById<ImageView>(R.id.ivColor2)
        val ivColor3 = view.findViewById<ImageView>(R.id.ivColor3)
        val ivColor4 = view.findViewById<ImageView>(R.id.ivColor4)
        val ivColor5 = view.findViewById<ImageView>(R.id.ivColor5)
        val ivColor6 = view.findViewById<ImageView>(R.id.ivColor6)

        var selMem: View? = null
        var selCol: View? = null
        val memoClickListener = View.OnClickListener { v ->
            when (selMem) {
                null -> {
                    v.setBackgroundColor(Color.parseColor("#DFDFDF"))
                    selMem = v
                }
                else -> {
                    v.setBackgroundColor(Color.parseColor("#DFDFDF"))
                    selMem!!.background = null
                    selMem = v
                }
            }
            Log.d("yyj", " // seleMem : " + selMem?.toString())
        }
        val colorClickListener = View.OnClickListener { v ->
            when (selCol) {
                null -> {
                    v.setBackgroundResource(R.drawable.border)
                    selCol = v
                }
                v -> {
                    selCol!!.background = null
                    selCol = null
                }
                else -> {
                    v.setBackgroundResource(R.drawable.border)
                    selCol!!.background = null
                    selCol = v
                }
            }
        }

        llMemo!!.setOnClickListener(memoClickListener)
        llTodo!!.setOnClickListener(memoClickListener)
        llDiary!!.setOnClickListener(memoClickListener)
        llBucket!!.setOnClickListener(memoClickListener)
        llWish!!.setOnClickListener(memoClickListener)
        llRecipe!!.setOnClickListener(memoClickListener)
        llMovie!!.setOnClickListener(memoClickListener)

        ivColor0!!.setOnClickListener(colorClickListener)
        ivColor1!!.setOnClickListener(colorClickListener)
        ivColor2!!.setOnClickListener(colorClickListener)
        ivColor3!!.setOnClickListener(colorClickListener)
        ivColor4!!.setOnClickListener(colorClickListener)
        ivColor5!!.setOnClickListener(colorClickListener)
        ivColor6!!.setOnClickListener(colorClickListener)

        var intent = null
        var selectedColor = 0
        when (selMem) {
            llMemo -> {
                //intent = Intent(this, MemoActivity::class.java)
            }
            llTodo -> {
                //  intent = Intent(this, ToDoActivity::class.java)
            }
            llDiary -> {
                // intent = Intent(this, SimpleDiaryActivity::class.java)
            }
            llBucket -> {
                //  intent = Intent(this, BucketActivity::class.java)
            }
            llWish -> {
                // intent = Intent(this, WishActivity::class.java)
            }
            llRecipe -> {
                //intent = Intent(this, RecipeActivity::class.java)
            }
            llMovie -> {
                //   intent = Intent(this, MovieActivity::class.java)
            }
        }
        when (selCol) {
            ivColor0 -> {
                selectedColor = 0
            }
            ivColor1 -> {
                selectedColor = 1
            }
            ivColor2 -> {
                selectedColor = 2
            }
            ivColor3 -> {
                selectedColor = 3
            }
            ivColor4 -> {
                selectedColor = 4
            }
            ivColor5 -> {
                selectedColor = 5
            }
            ivColor6 -> {
                selectedColor = 6
            }
        }
        Log.d("yyj", "color : " + selectedColor + " // seleMem : " + selMem?.id.toString())

        builder.setView(view)
            .setPositiveButton("OK") { dialogInterface, i ->
               // intent.putExtra("color", selectedColor)
                //startActivity(intent)
                //Log.d("yyj", "color : " + selectedColor + " // seleMem : " + selMem?.id.toString())
            }
            .show()
    }

    private fun EditText.setFocusAndShowKeyboard() {
        setSelection(this.text.length)
        this.requestFocus()
        this.postDelayed({
            val inputMethodManager =
                context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.showSoftInput(this, InputMethodManager.SHOW_FORCED)
        }, 100)
    }

    fun getMemo() {
        mid = 1

        var columns = arrayOf(DBHelper.MEM_COL_ID, DBHelper.MEM_COL_COLOR, DBHelper.MEM_COL_CONTENT, DBHelper.MEM_COL_LOCK, DBHelper.MEM_COL_BKMR)
        var selection = "_id=?"
        var selectArgs = arrayOf(mid.toString())
        var c : Cursor = database.query(DBHelper.MEM_TABLE_NAME, columns, selection, selectArgs, null, null, null)
        c.moveToNext()
        var view = findViewById<ConstraintLayout>(R.id.activity_memo)
        when (c.getInt(c.getColumnIndex(DBHelper.MEM_COL_COLOR))) {
            0 -> view.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
            1 -> view.setBackgroundColor(ContextCompat.getColor(this, R.color.pastel_red))
            2 -> view.setBackgroundColor(ContextCompat.getColor(this, R.color.pastel_yellow))
            3 -> view.setBackgroundColor(ContextCompat.getColor(this, R.color.pastel_green))
            4 -> view.setBackgroundColor(ContextCompat.getColor(this, R.color.pastel_blue))
            5 -> view.setBackgroundColor(ContextCompat.getColor(this, R.color.pastel_purple))
            6 -> view.setBackgroundColor(ContextCompat.getColor(this, R.color.pastel_pink))
        }
        etMemo.setText(c.getString(c.getColumnIndex(DBHelper.MEM_COL_CONTENT)))
        lock = c.getInt(c.getColumnIndex(DBHelper.MEM_COL_LOCK))
        bkmr = c.getInt(c.getColumnIndex(DBHelper.MEM_COL_BKMR))
        if (lock == 1) {
            cbLock.isChecked = true
        }
        if (bkmr == 1) {
            cbBkmr.isChecked = true
        }
    }

    override fun onBackPressed() {
        Log.d("yyj", "mem_BackPressed")
        var contentValues = ContentValues()
        contentValues.put(DBHelper.MEM_COL_WDATE, System.currentTimeMillis() / 1000L)
        contentValues.put(DBHelper.MEM_COL_COLOR, 0)
        contentValues.put(DBHelper.MEM_COL_CONTENT, etMemo.text.toString())
        contentValues.put(DBHelper.MEM_COL_LOCK, lock)
        contentValues.put(DBHelper.MEM_COL_BKMR, bkmr)

        if (mid != -1) {
            var whereCluase = "_id=?"
            var whereArgs = arrayOf(mid.toString())
            database.update(DBHelper.MEM_TABLE_NAME, contentValues, whereCluase, whereArgs)
        }
        else {
            database.insert(DBHelper.MEM_TABLE_NAME, null, contentValues)
        }

        //dbHelper.close() ondestroy
        //startActivity(Intent(this, MainActivity::class.java))
        //finish()
    }
}