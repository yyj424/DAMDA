package com.bluelay.damda

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import androidx.core.database.getIntOrNull
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.AppWidgetTarget

/**
 * Implementation of App Widget functionality.
 */
class BaseWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }

    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    companion object {

        lateinit var dbHelper : DBHelper
        lateinit var database : SQLiteDatabase

        internal fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            dbHelper = DBHelper(context)
            database = dbHelper.readableDatabase

            val sharedPref = context.getSharedPreferences("widget", Context.MODE_PRIVATE)
            val memoType = sharedPref.getString("type$appWidgetId", "")
            val memoId = sharedPref.getInt("id$appWidgetId", -1)

            var remoteView = RemoteViews(context.packageName, R.layout.widget_movie)

            when (memoType) {
                "Memo" -> {
                    remoteView = RemoteViews(context.packageName, R.layout.widget_memo)
                    setMemoWidget(memoId, context, remoteView, appWidgetId)
                }
                "TodoList" -> {
                    remoteView = RemoteViews(context.packageName, R.layout.widget_todo)
                    val serviceIntent = Intent(context, ToDoRemoteViewsService::class.java)
                    serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                    serviceIntent.data = Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME))
                    serviceIntent.putExtra("widgetId", appWidgetId)
                    remoteView.setRemoteAdapter(R.id.lvWidgetToDo, serviceIntent)
                    setToDoWidget(memoId, remoteView)
                    appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.lvWidgetToDo)
                }
                "WishList" -> {
                    remoteView = RemoteViews(context.packageName, R.layout.widget_wish)
                    val serviceIntent = Intent(context, WishWidgetService::class.java)
                    serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                    serviceIntent.data = Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME))
                    serviceIntent.putExtra("widgetId", appWidgetId)
                    remoteView.setRemoteAdapter(R.id.lvWidgetWish, serviceIntent)
                    setWishWidget(memoId, context, remoteView)
                    appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.lvWidgetWish)
                }
                "Weekly" -> {
                    remoteView = RemoteViews(context.packageName, R.layout.widget_weekly)
                }
                "Recipe" -> {
                    remoteView = RemoteViews(context.packageName, R.layout.widget_recipe)
                }
                "Movie" -> {
                    remoteView = RemoteViews(context.packageName, R.layout.widget_movie)
                    setMovieWidget(memoId, remoteView, appWidgetId, context)
                }
            }
            appWidgetManager.updateAppWidget(appWidgetId, remoteView)
        }

        private fun setToDoWidget(memoId : Int, remoteView : RemoteViews) {

            val cursor: Cursor = database.rawQuery(
                "SELECT * FROM ${DBHelper.TODL_TABLE_NAME} WHERE ${DBHelper.TODL_COL_ID}=?", arrayOf(
                    memoId.toString()
                )
            )

            if (cursor.moveToNext()) {
                val date = cursor.getString(cursor.getColumnIndex(DBHelper.TODL_COL_DATE))
                val color = cursor.getInt(cursor.getColumnIndex(DBHelper.TODL_COL_COLOR))
                remoteView.setTextViewText(R.id.tvWidgetTodoDate, date)
                setColor(R.id.llWidgetToDo, color, remoteView)
            }

            cursor.close()
        }

        private fun setMemoWidget(memoId : Int, context: Context, remoteView : RemoteViews, appWidgetId: Int) {
            val dbHelper = DBHelper(context)
            val database = dbHelper.readableDatabase
            val cursor: Cursor = database.rawQuery(
                "SELECT * FROM ${DBHelper.MEM_TABLE_NAME} WHERE ${DBHelper.MEM_COL_ID}=?", arrayOf(
                    memoId.toString()
                )
            )
            
             val content = cursor.getString(cursor.getColumnIndex(DBHelper.MEM_COL_CONTENT))
                remoteView.setCharSequence(R.id.tvWidgetMemo, "setText", content)
                if (cursor.getString(cursor.getColumnIndex(DBHelper.MEM_COL_PHOTO)) != null) {
                    try {
                        val savedPhotoPath = cursor.getString(cursor.getColumnIndex(DBHelper.MEM_COL_PHOTO))
                        remoteView.setViewVisibility(R.id.ivWidgetMemo, View.VISIBLE)
                        val ivWidgetMemo = AppWidgetTarget(context, R.id.ivWidgetMemo, remoteView, appWidgetId)
                        Glide.with(context).asBitmap().load(savedPhotoPath).into(ivWidgetMemo)
                    } catch (e: java.lang.Exception) {
                        remoteView.setViewVisibility(R.id.ivWidgetMemo, View.GONE)
                    }
                }
                val color = cursor.getInt(cursor.getColumnIndex(DBHelper.MEM_COL_COLOR))
                setColor(R.id.llWidgetMemo, color, remoteView)
            }
            cursor.close()
        }
            
        private fun setMovieWidget(memoId : Int, remoteView : RemoteViews, appWidgetId : Int, context: Context) {
            val cursor: Cursor = database.rawQuery(
                "SELECT * FROM ${DBHelper.MOV_TABLE_NAME} WHERE ${DBHelper.MOV_COL_ID}=?", arrayOf(
            if (cursor.moveToNext()) {

                val date = cursor.getString(cursor.getColumnIndex(DBHelper.MOV_COL_DATE))
                val score = cursor.getDouble(cursor.getColumnIndex(DBHelper.MOV_COL_SCORE))
                val image = cursor.getString(cursor.getColumnIndex(DBHelper.MOV_COL_POSTERPIC))
                val content = cursor.getString(cursor.getColumnIndex(DBHelper.MOV_COL_CONTENT))
                val title = cursor.getString(cursor.getColumnIndex(DBHelper.MOV_COL_TITLE))
                val color = cursor.getInt(cursor.getColumnIndex(DBHelper.MOV_COL_COLOR))

                remoteView.setCharSequence(R.id.tvWidgetMovieDate, "setText", date)
                remoteView.setCharSequence(R.id.tvWidgetMovieReview, "setText", content)
                remoteView.setCharSequence(R.id.tvWidgetMovieTitle, "setText", title)
                val ivWidgetMoviePoster = AppWidgetTarget(context, R.id.ivWidgetMoviePoster, remoteView, appWidgetId)
                Glide.with(context.applicationContext).asBitmap().fitCenter().load(image).into(ivWidgetMoviePoster)
                setColor(R.id.llWidgetMovie, color, remoteView)

                val ratingStars = arrayOf(R.id.ivRatingStar1, R.id.ivRatingStar2, R.id.ivRatingStar3, R.id.ivRatingStar4, R.id.ivRatingStar5)
                var i = 0
                while (i < score.toInt()) {
                    remoteView.setImageViewResource(ratingStars[i++], R.drawable.star_fill)
                }
                if (score - score.toInt() == 0.5) {
                    remoteView.setImageViewResource(ratingStars[i++], R.drawable.star_half)
                }
                for (j in i until 5) {
                    remoteView.setImageViewResource(ratingStars[j], R.drawable.star_empty)
                }
            }
            cursor.close()
        }

        private fun setWishWidget(memoId : Int, context: Context, remoteView : RemoteViews) {
            val dbHelper = DBHelper(context)
            val database = dbHelper.readableDatabase
            val wishList = arrayListOf<Wish>()
            var cursor: Cursor = database.rawQuery(
                "SELECT * FROM ${DBHelper.WISL_TABLE_NAME} WHERE ${DBHelper.WISL_COL_ID}=?", arrayOf(
                    memoId.toString()
                )
            )
            if (cursor.moveToNext()) {
                val category = cursor.getString(cursor.getColumnIndex(DBHelper.WISL_COL_CATEGORY))
                remoteView.setTextViewText(R.id.tvWidgetWishCatgory, category)
                val color = cursor.getInt(cursor.getColumnIndex(DBHelper.WISL_COL_COLOR))
                setColor(R.id.llWidgetWish, color, remoteView)

                cursor = database.rawQuery(
                    "SELECT * FROM ${DBHelper.WIS_TABLE_NAME} WHERE ${DBHelper.WIS_COL_WID}=?", arrayOf(
                        memoId.toString()
                    )
                )
                var total = 0
                while (cursor.moveToNext()) {
                    var price: Int? = null
                    if (cursor.getIntOrNull(cursor.getColumnIndex(DBHelper.WIS_COL_PRICE)) != null) {
                        price = cursor.getInt(cursor.getColumnIndex(DBHelper.WIS_COL_PRICE))
                        total += price
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
                remoteView.setTextViewText(R.id.tvWidgetWishTotal, total.toString())
            }


        private fun setColor(layoutId : Int, color : Int, remoteView : RemoteViews) {
            when (color) {
                0 -> remoteView.setInt(layoutId, "setBackgroundResource", R.color.white)
                1 -> remoteView.setInt(layoutId, "setBackgroundResource", R.color.pastel_red)
                2 -> remoteView.setInt(layoutId, "setBackgroundResource", R.color.pastel_yellow)
                3 -> remoteView.setInt(layoutId, "setBackgroundResource", R.color.pastel_green)
                4 -> remoteView.setInt(layoutId, "setBackgroundResource", R.color.pastel_blue)
                5 -> remoteView.setInt(layoutId, "setBackgroundResource", R.color.pastel_purple)
                6 -> remoteView.setInt(layoutId, "setBackgroundResource", R.color.pastel_pink)
            }
        }
    }
}



