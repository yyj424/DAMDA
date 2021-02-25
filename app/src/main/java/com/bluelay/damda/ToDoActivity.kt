package com.bluelay.damda

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_movie.*
import kotlinx.android.synthetic.main.activity_todo.*
import java.text.SimpleDateFormat
import java.util.*

class ToDoActivity : AppCompatActivity(), SetMemo  {

    private lateinit var dbHelper : DBHelper
    private lateinit var database : SQLiteDatabase
    private var toDoList = arrayListOf<ToDo>()
    private val calendar = Calendar.getInstance()
    private val dateFormat = "yyyy.MM.dd"
    private var sdf = SimpleDateFormat(dateFormat, Locale.KOREA)

    // TODO: 2021-01-30 메인 만든 후에 ID 수정!!!!!! -1 로 초기화, putExtra 있으면 그 값 넣기
    private var toDoId = 1
    private var date : String = ""
    private var color = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_todo)
        setColor(this, color, clToDo)

        etTodoDate.hideKeyboard()
        dbHelper = DBHelper(this)
        val toDoAdapter = ToDoAdapter(this, toDoList)

        var recordDatePicker = OnDateSetListener { view, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            etTodoDate.setText(sdf.format(calendar.time))
        }
        etTodoDate.setOnClickListener {
            DatePickerDialog(this, R.style.DialogTheme, recordDatePicker, calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH]).show()
        }

        if (toDoId != -1) {
            selectToDo()
            etTodoDate.setText(date)
        }

        val listSize = toDoList.size
        for (i in 1.. 10-listSize) {
            toDoList.add(ToDo("", 0))
        }
       lvToDo.adapter = toDoAdapter
    }

    override fun onBackPressed() {
        // TODO: 2021-01-29 main 만들고 finish
        // TODO: 2021-01-30 아무것도 안썼을 경우 체크하기
        if (toDoId == -1) {
            insertToDo()
        }
       else {
           updateToDo()
        }
    }

    private fun insertToDo() {
        database = dbHelper.writableDatabase

        val value = ContentValues()
        value.put(DBHelper.TODL_COL_WDATE, System.currentTimeMillis())
        value.put(DBHelper.TODL_COL_DATE, etTodoDate.text.toString())
        value.put(DBHelper.TODL_COL_COLOR, color)
        val id =  database.insert(DBHelper.TODL_TABLE_NAME, null, value)
        value.clear()
        for (toDo in toDoList) {
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
        color = cursor.getInt(cursor.getColumnIndex(DBHelper.TODL_COL_COLOR))

        cursor = database.rawQuery("SELECT * FROM ${DBHelper.TOD_TABLE_NAME} WHERE ${DBHelper.TOD_COL_TID}=?", arrayOf(toDoId.toString()))
        while(cursor.moveToNext()) {
            toDoList.add(ToDo(cursor.getString(cursor.getColumnIndex(DBHelper.TOD_COL_CONTENT)), cursor.getInt(cursor.getColumnIndex(DBHelper.TOD_COL_CHECKED))))
        }
    }

    private fun updateToDo() {
        database = dbHelper.writableDatabase

        val value = ContentValues()
        value.put(DBHelper.TODL_COL_WDATE, System.currentTimeMillis())
        value.put(DBHelper.TODL_COL_DATE, etTodoDate.text.toString())
        value.put(DBHelper.TODL_COL_COLOR, color)
        database.update(DBHelper.TODL_TABLE_NAME, value, "${DBHelper.TODL_COL_ID}=?", arrayOf(toDoId.toString()))

        database.delete(DBHelper.TOD_TABLE_NAME, "${DBHelper.TOD_COL_TID}=?",  arrayOf(toDoId.toString()))
        value.clear()
        for (toDo in toDoList) {
            if (toDo.content != "")  {
                value.put(DBHelper.TOD_COL_CHECKED, toDo.checked)
                value.put(DBHelper.TOD_COL_CONTENT, toDo.content)
                value.put(DBHelper.TOD_COL_TID, toDoId)
                database.insert(DBHelper.TOD_TABLE_NAME, null, value)
            }
        }
    }

    private fun View.hideKeyboard() {
        val imm = context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }
}