package com.bluelay.damda

import android.content.Intent
import android.util.Log
import android.widget.RemoteViewsService

class ToDoRemoteViewsService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory {
        Log.d("goeun", "service")
        //var toDoList = arrayListOf<ToDo>()
        var appWidgetId = -1
        if (intent != null) {
          //  toDoList = intent.extras?.get("toDoList") as ArrayList<ToDo>
            //Log.d("goeun", "toDoList size" + toDoList.size)
            appWidgetId = intent.extras?.get("widgetId") as Int
        }
       // Log.d("goeun", "toDoList size " + toDoList.size)
        return ToDoRemoteViewsFactory(this.applicationContext, appWidgetId)
    }
}