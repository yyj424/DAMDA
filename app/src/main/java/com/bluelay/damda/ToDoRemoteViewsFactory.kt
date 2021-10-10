package com.bluelay.damda

import android.content.Context
import android.database.Cursor
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService

class ToDoRemoteViewsFactory(context : Context, appWidgetId : Int)  : RemoteViewsService.RemoteViewsFactory {
    private var context : Context = context
    private lateinit var toDoList : ArrayList<ToDo>
    private lateinit var date : String
    private var color = -1
    private val appWidgetId = appWidgetId

    override fun onCreate() {
        toDoList = arrayListOf()
        setToDoWidget()
    }

    private fun setToDoWidget() {
        val dbHelper = DBHelper(context)
        val database = dbHelper.readableDatabase
        val sharedPref = context.getSharedPreferences("widget", Context.MODE_PRIVATE)
        val memoId = sharedPref.getInt("id$appWidgetId", -1)
        if (memoId != -1) {
            var cursor: Cursor = database.rawQuery(
                "SELECT * FROM ${DBHelper.TODL_TABLE_NAME} WHERE ${DBHelper.TODL_COL_ID}=?", arrayOf(memoId.toString()))
            if (cursor.moveToNext()) {
                date = cursor.getString(cursor.getColumnIndex(DBHelper.TODL_COL_DATE))
                color = cursor.getInt(cursor.getColumnIndex(DBHelper.TODL_COL_COLOR))
            }
            cursor.moveToNext()

            cursor = database.rawQuery("SELECT * FROM ${DBHelper.TOD_TABLE_NAME} WHERE ${DBHelper.TOD_COL_TID}=?", arrayOf(memoId.toString()))

            while(cursor.moveToNext())
                toDoList.add(ToDo(cursor.getString(cursor.getColumnIndex(DBHelper.TOD_COL_CONTENT)), cursor.getInt(cursor.getColumnIndex(DBHelper.TOD_COL_CHECKED))))

            cursor.close()
        }
    }

    override fun onDataSetChanged() {
    }

    override fun onDestroy() {
    }

    override fun getCount(): Int {
        return toDoList.size
    }

    override fun getViewAt(position: Int): RemoteViews {
        val toDoWidget = RemoteViews(context.packageName, R.layout.adapter_view_widget_todo)
        toDoWidget.setTextViewText(R.id.tvWidgetToDoContent, toDoList[position].content)
        if (toDoList[position].checked == 1){
            toDoWidget.setImageViewResource(R.id.ivWidgetToDoCheckBox, R.drawable.checkbox_checked)
        }
        else {
            toDoWidget.setImageViewResource(R.id.ivWidgetToDoCheckBox, R.drawable.checkbox_default)
        }
        return toDoWidget
    }

    override fun getLoadingView(): RemoteViews? {
        return null
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun hasStableIds(): Boolean {
        return false
    }

}