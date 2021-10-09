package com.bluelay.damda

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.core.database.getIntOrNull


class WishWidgetFactory(context: Context, appWidgetId: Int) : RemoteViewsService.RemoteViewsFactory {
    private lateinit var dbHelper : DBHelper
    private lateinit var database : SQLiteDatabase
    private var context: Context = context
    private val appWidgetId = appWidgetId
    private val wishList = arrayListOf<Wish>()
    private var memoId = -1

    override fun onCreate() {
        dbHelper = DBHelper(context)
        database = dbHelper.readableDatabase
        val sharedPref = context.getSharedPreferences("widget", Context.MODE_PRIVATE)
        memoId = sharedPref.getInt("id$appWidgetId", -1)
        if (memoId != -1) {
            getWishList()
        }
    }

    override fun onDataSetChanged() {
        getWishList()
    }

    override fun onDestroy() {
    }

    override fun getCount(): Int {
        return wishList.size
    }

    override fun getViewAt(position: Int): RemoteViews {
        val listviewWidget = RemoteViews(context.packageName, R.layout.adapter_view_widget_wish)
        listviewWidget.setImageViewResource(R.id.ivWidgetWish, R.drawable.heart_default)
        listviewWidget.setTextViewText(R.id.tvWidgetWishItem, wishList[position].item)
        if (wishList[position].price == null) {
            listviewWidget.setTextViewText(R.id.tvWidgetWishPrice, "")
        }
        else {
            listviewWidget.setTextViewText(R.id.tvWidgetWishPrice, wishList[position].price.toString())
        }
        listviewWidget.setImageViewResource(R.id.ivWidgetWishLink, R.drawable.link_default)
        listviewWidget.setImageViewResource(R.id.ivWidgetWishVerticalLine1, R.drawable.vertical_line)
        listviewWidget.setImageViewResource(R.id.ivWidgetWishVerticalLine2, R.drawable.vertical_line)
        listviewWidget.setImageViewResource(R.id.ivWidgetWishBottomLine, R.drawable.thin_line)

        if (wishList[position].checked == 1) {
            listviewWidget.setTextColor(R.id.tvWidgetWishItem, Color.parseColor("#969191"))
            listviewWidget.setTextColor(R.id.tvWidgetWishPrice, Color.parseColor("#969191"))
            listviewWidget.setImageViewResource(R.id.ivWidgetWish, R.drawable.heart_checked)
        }
        if (wishList[position].link.isNotEmpty()) {
            listviewWidget.setImageViewResource(R.id.ivWidgetWishLink, R.drawable.link_checked)
        }

        return listviewWidget
    }

    override fun getLoadingView(): RemoteViews? {
        return null
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    private fun getWishList() {
        wishList.clear()
        var cursor:Cursor = database.rawQuery(
            "SELECT * FROM ${DBHelper.WIS_TABLE_NAME} WHERE ${DBHelper.WIS_COL_WID}=?", arrayOf(
                memoId.toString()
            )
        )
        while (cursor.moveToNext()) {
            var price: Int? = null
            if (cursor.getIntOrNull(cursor.getColumnIndex(DBHelper.WIS_COL_PRICE)) != null) {
                price = cursor.getInt(cursor.getColumnIndex(DBHelper.WIS_COL_PRICE))
            }
            wishList.add(
                Wish(
                    cursor.getString(cursor.getColumnIndex(DBHelper.WIS_COL_ITEM)),
                    price,
                    cursor.getInt(cursor.getColumnIndex(DBHelper.WIS_COL_CHECKED)),
                    cursor.getString(cursor.getColumnIndex(DBHelper.WIS_COL_LINK))
                )
            )
        }
        cursor.close()
    }
}