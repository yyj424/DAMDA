package com.bluelay.damda

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class ToDoActivity : AppCompatActivity()  {

    lateinit var dbHelper : DBHelper
    lateinit var database : SQLiteDatabase
    var toDoList = arrayListOf<ToDo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_todo)

        val lvToDo = findViewById<ListView>(R.id.lvToDo)
        val toDoAdapter = ToDoAdapter(this, toDoList)
        for (i in 1.. 10) {
            toDoList.add(ToDo("", 0))
        }
       lvToDo.adapter = toDoAdapter
    }

    override fun onBackPressed() {
        dbHelper = DBHelper(this)
        database = dbHelper.writableDatabase

        val value = ContentValues()
        value.put(DBHelper.TODL_COL_WDATE, System.currentTimeMillis())
        value.put(DBHelper.TODL_COL_COLOR, 0)
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
}