package com.bluelay.damda

import android.content.Intent
import android.util.Log
import android.widget.RemoteViewsService

class WishWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory {
        var appWidgetId = -1
        if (intent != null) {
            appWidgetId = intent.extras?.get("widgetId") as Int
        }
        return WishWidgetFactory(this.applicationContext, appWidgetId)
    }
}