package com.bluelay.damda

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.EditText

class ToDoAdapter (val context : Context, val toDoList : ArrayList<ToDo>) : BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view : View = LayoutInflater.from(context).inflate(R.layout.adapter_view_todo, null)

        val cbToDo = view.findViewById<CheckBox>(R.id.cbToDo)
        val etToDoContent = view.findViewById<EditText>(R.id.etToDoContent)

        val toDo = toDoList[position]
        cbToDo.isChecked = toDo.checked == 1
        etToDoContent.setText(toDo.content)

        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                toDoList[position].content = s.toString()
            }
        }
        etToDoContent.addTextChangedListener(textWatcher)

        cbToDo.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                toDoList[position].checked = 1
            }
            else {
                toDoList[position].checked = 0
            }
        }

        return view
    }
    override fun getCount(): Int {
        return toDoList.size
    }

    override fun getItem(position: Int): Any {
        return toDoList[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

}