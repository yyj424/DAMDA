package com.bluelay.damda

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_todo.*
import kotlinx.android.synthetic.main.layout_memo_settings.*
import java.text.SimpleDateFormat
import java.util.*

class ToDoActivity : AppCompatActivity(), SetMemo  {

    private lateinit var database : SQLiteDatabase
    private var dbHelper = DBHelper(this)
    private var oldToDoList = arrayListOf<ToDo>()
    private var newToDoList = arrayListOf<ToDo>()
    private val calendar = Calendar.getInstance()
    private val dateFormat = "yyyy.MM.dd"
    private var sdf = SimpleDateFormat(dateFormat, Locale.KOREA)

    private var toDoId = -1
    private var date  = ""
    private var color = 0
    private var lock = 0
    private var bkmr = 0

    private lateinit var memo : MemoInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_todo)

        etTodoDate.hideKeyboard()
        val toDoAdapter = ToDoAdapter(this, newToDoList)

        color = intent.getIntExtra("color", 0)
        if (intent.hasExtra("memo")) {
            btnDeleteMemo.visibility = View.VISIBLE
            memo = intent.getSerializableExtra("memo") as MemoInfo
            color = memo.color
            toDoId = memo.id
            lock = memo.lock
            bkmr = memo.bkmr

            selectToDo()
            etTodoDate.setText(date)
            if (lock == 1) cbLock.isChecked = true
            if (bkmr == 1) cbBkmr.isChecked = true
        }
        setColor(this, color, clToDo)

        val recordDatePicker = OnDateSetListener { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            etTodoDate.setText(sdf.format(calendar.time))
        }
        etTodoDate.setOnClickListener {
            DatePickerDialog(this, recordDatePicker, calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH]).show()
        }

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

        val listSize = newToDoList.size
        for (i in 1.. 10-listSize) {
            newToDoList.add(ToDo("", 0))
            oldToDoList.add(ToDo("", 0))
        }

        lvToDo.adapter = toDoAdapter

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
                setColor(this, color, clToDo)
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

        btnAddItem.visibility = View.VISIBLE
        btnAddItem.setOnClickListener {
            newToDoList.add(ToDo("", 0))
            toDoAdapter.notifyDataSetChanged()
        }
    }

    override fun onBackPressed() {
        saveMemo()
    }

    private fun insertToDo() {
        database = dbHelper.writableDatabase

        val value = ContentValues()
        value.put(DBHelper.TODL_COL_WDATE, System.currentTimeMillis() / 1000L)
        value.put(DBHelper.TODL_COL_DATE, etTodoDate.text.toString())
        value.put(DBHelper.TODL_COL_COLOR, color)
        value.put(DBHelper.TODL_COL_BKMR, bkmr)
        value.put(DBHelper.TODL_COL_LOCK, lock)
        val id =  database.insert(DBHelper.TODL_TABLE_NAME, null, value)
        value.clear()

        for (toDo in newToDoList) {
            if (toDo.content != "")  {
                value.put(DBHelper.TOD_COL_CHECKED, toDo.checked)
                value.put(DBHelper.TOD_COL_CONTENT, toDo.content)
                value.put(DBHelper.TOD_COL_TID, id)
                database.insert(DBHelper.TOD_TABLE_NAME, null, value)
            }
        }
    }

    private fun selectToDo() {
        database = dbHelper.readableDatabase

        var cursor: Cursor = database.rawQuery("SELECT * FROM ${DBHelper.TODL_TABLE_NAME} WHERE ${DBHelper.TODL_COL_ID}=?", arrayOf(toDoId.toString()))
        cursor.moveToNext()
        date = cursor.getString(cursor.getColumnIndex(DBHelper.TODL_COL_DATE))

        cursor = database.rawQuery("SELECT * FROM ${DBHelper.TOD_TABLE_NAME} WHERE ${DBHelper.TOD_COL_TID}=?", arrayOf(toDoId.toString()))
        while(cursor.moveToNext()) {
            newToDoList.add(ToDo(cursor.getString(cursor.getColumnIndex(DBHelper.TOD_COL_CONTENT)), cursor.getInt(cursor.getColumnIndex(DBHelper.TOD_COL_CHECKED))))
            oldToDoList.add(ToDo(cursor.getString(cursor.getColumnIndex(DBHelper.TOD_COL_CONTENT)), cursor.getInt(cursor.getColumnIndex(DBHelper.TOD_COL_CHECKED))))
        }

        cursor.close()
    }

    private fun updateToDo() {
        database = dbHelper.writableDatabase

        val value = ContentValues()
        value.put(DBHelper.TODL_COL_WDATE, System.currentTimeMillis() / 1000L)
        value.put(DBHelper.TODL_COL_DATE, etTodoDate.text.toString())
        value.put(DBHelper.TODL_COL_COLOR, color)
        value.put(DBHelper.TODL_COL_BKMR, bkmr)
        value.put(DBHelper.TODL_COL_LOCK, lock)
        database.update(DBHelper.TODL_TABLE_NAME, value, "${DBHelper.TODL_COL_ID}=?", arrayOf(toDoId.toString()))

        database.delete(DBHelper.TOD_TABLE_NAME, "${DBHelper.TOD_COL_TID}=?",  arrayOf(toDoId.toString()))
        value.clear()
        for (toDo in newToDoList) {
            if (toDo.content != "")  {
                value.put(DBHelper.TOD_COL_CHECKED, toDo.checked)
                value.put(DBHelper.TOD_COL_CONTENT, toDo.content)
                value.put(DBHelper.TOD_COL_TID, toDoId)
                database.insert(DBHelper.TOD_TABLE_NAME, null, value)
            }
        }
    }

    private fun saveMemo() {
        if (toDoId == -1)
            insertToDo()
        else
            if (checkUpdate())
                updateToDo()
        finish()
    }

    private fun checkUpdate() : Boolean {
        if (color != memo.color) return true
        if (bkmr != memo.bkmr) return true
        if (lock != memo.lock) return true
        if (date != etTodoDate.text.toString()) return true
        newToDoList.forEachIndexed { i, newToDo ->
            var oldTodo = oldToDoList[i]
            Log.d("goeun", "new $newToDo , old $oldTodo")
            if (newToDo.content != oldTodo.content) return true
            if (newToDo.checked != oldTodo.checked) return true
        }
        return false
    }

    private fun deleteMemo() {
        database.execSQL("DELETE FROM ${DBHelper.TODL_TABLE_NAME} WHERE _id = $toDoId")
        finish()
    }

    private fun View.hideKeyboard() {
        val imm = context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    override fun onDestroy() {
        super.onDestroy()
        dbHelper.close()
    }
}