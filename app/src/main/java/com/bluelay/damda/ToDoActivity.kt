package com.bluelay.damda

import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class ToDoActivity : AppCompatActivity()  {

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
}