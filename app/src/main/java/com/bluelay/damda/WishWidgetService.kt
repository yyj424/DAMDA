package com.bluelay.damda

import android.content.Intent
import android.widget.RemoteViewsService

class WishWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(p0: Intent?): RemoteViewsFactory {
        return WishWidgetFactory(this.applicationContext)
    }
}