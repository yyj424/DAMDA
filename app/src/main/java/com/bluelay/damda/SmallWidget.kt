package com.bluelay.damda

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.view.View
import android.widget.RemoteViews
import androidx.core.database.getIntOrNull
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.AppWidgetTarget

/**
 * Implementation of App Widget functionality.
 */
class SmallWidget : AppWidgetProvider() {

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
    }

    override fun onDisabled(context: Context) {
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val appWidgetManager = AppWidgetManager.getInstance(context);
        val widget =
            context?.let { ComponentName(it.packageName, SmallWidget::class.java.name) }

        val widgetIds = appWidgetManager.getAppWidgetIds(widget)
        val action = intent?.action

        if(action.equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)){
            if(widgetIds != null && widgetIds.isNotEmpty()){
                if (context != null) {
                    this.onUpdate(context, AppWidgetManager.getInstance(context), widgetIds)
                }
            }
        }
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

            var remoteView: RemoteViews? = null
            var memo : MemoInfo? = null
            if (memoType != "") {
                memo = getMemo(memoId, memoType)
            }
            var intent: Intent?

            when (memoType) {
                "Memo" -> {
                    remoteView = RemoteViews(context.packageName, R.layout.widget_memo_small)
                    setMemoWidget(memoId, context, remoteView, appWidgetId)
                    if (memoType != "") {
                        intent = Intent(context, MemoActivity::class.java)
                        intent.addCategory(Intent.CATEGORY_LAUNCHER)
                        intent.putExtra("memo", memo)
                        val pi = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                        remoteView.setOnClickPendingIntent(R.id.llWidgetMemo, pi)
                    }
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
                    if (memoType != "") {
                        intent = Intent(context, ToDoActivity::class.java)
                        intent.addCategory(Intent.CATEGORY_LAUNCHER)
                        intent.putExtra("memo", memo)
                        val pi = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                        remoteView.setOnClickPendingIntent(R.id.llWidgetToDo, pi)
                    }
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
                    if (memoType != "") {
                        intent = Intent(context, WishActivity::class.java)
                        intent.addCategory(Intent.CATEGORY_LAUNCHER)
                        intent.putExtra("memo", memo)
                        val pi = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                        remoteView.setOnClickPendingIntent(R.id.llWidgetWish, pi)
                    }
                }
                "Weekly" -> {
                    remoteView = RemoteViews(context.packageName, R.layout.widget_weekly_small)
                    setWeeklyWidget(memoId, remoteView)
                    if (memoType != "") {
                        intent = Intent(context, WeeklyActivity::class.java)
                        intent.addCategory(Intent.CATEGORY_LAUNCHER)
                        intent.putExtra("memo", memo)
                        val pi = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                        remoteView.setOnClickPendingIntent(R.id.llWidgetWeekly_s, pi)
                    }
                }
                "Recipe" -> {
                    remoteView = RemoteViews(context.packageName, R.layout.widget_recipe_small)
                    setRecipeWidget(memoId, remoteView)
                    if (memoType != "") {
                        intent = Intent(context, RecipeActivity::class.java)
                        intent.addCategory(Intent.CATEGORY_LAUNCHER)
                        intent.putExtra("memo", memo)
                        val pi = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                        remoteView.setOnClickPendingIntent(R.id.llWidgetRecipe_s, pi)
                    }
                }
                "Movie" -> {
                    remoteView = RemoteViews(context.packageName, R.layout.widget_movie_small)
                    setMovieWidget(memoId, remoteView, appWidgetId, context)
                    if (memoType != "") {
                        intent = Intent(context, MovieActivity::class.java)
                        intent.addCategory(Intent.CATEGORY_LAUNCHER)
                        intent.putExtra("memo", memo)
                        val pi = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                        remoteView.setOnClickPendingIntent(R.id.llWidgetMovie, pi)
                    }
                }
            }
            appWidgetManager.updateAppWidget(appWidgetId, remoteView)
        }

        private fun getMemo(memoId : Int, memoType : String?) : MemoInfo {
            val titles = mutableMapOf(
                DBHelper.MEM_TABLE_NAME to DBHelper.MEM_COL_CONTENT,
                DBHelper.TODL_TABLE_NAME to DBHelper.TODL_COL_DATE,
                DBHelper.WISL_TABLE_NAME to DBHelper.WISL_COL_CATEGORY,
                DBHelper.WEE_TABLE_NAME to DBHelper.WEE_COL_DATE,
                DBHelper.REC_TABLE_NAME to DBHelper.REC_COL_NAME,
                DBHelper.MOV_TABLE_NAME to DBHelper.MOV_COL_TITLE
            )

            val cursor: Cursor = database.rawQuery(
                "SELECT * FROM $memoType WHERE _id = ?", arrayOf(
                    memoId.toString()
                )
            )

            cursor.moveToNext()
            val id = cursor.getInt(cursor.getColumnIndex("_id"))
            val wdate = cursor.getInt(cursor.getColumnIndex("wdate")) * 1000L
            val color = cursor.getInt(cursor.getColumnIndex("color"))
            val lock = cursor.getInt(cursor.getColumnIndex("lock"))
            val bkmr = cursor.getInt(cursor.getColumnIndex("bkmr"))
            val title = cursor.getString(cursor.getColumnIndex(titles[memoType]))

            return MemoInfo(id, memoType, wdate, color, lock, bkmr, false, title)
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
            val cursor: Cursor = database.rawQuery(
                "SELECT * FROM ${DBHelper.MEM_TABLE_NAME} WHERE ${DBHelper.MEM_COL_ID}=?", arrayOf(
                    memoId.toString()
                )
            )
            if (cursor.moveToNext()) {
                val content = cursor.getString(cursor.getColumnIndex(DBHelper.MEM_COL_CONTENT))
                remoteView.setTextViewText(R.id.tvWidgetMemo, content)
                if (cursor.getString(cursor.getColumnIndex(DBHelper.MEM_COL_PHOTO)) != null) {
                    try {
                        val savedPhotoPath =
                            cursor.getString(cursor.getColumnIndex(DBHelper.MEM_COL_PHOTO))
                        remoteView.setViewVisibility(R.id.ivWidgetMemo, View.VISIBLE)
                        val ivWidgetMemo =
                            AppWidgetTarget(context, R.id.ivWidgetMemo, remoteView, appWidgetId)
                        Glide.with(context.applicationContext).asBitmap().fitCenter().load(savedPhotoPath)
                            .into(ivWidgetMemo)
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
                "SELECT * FROM ${DBHelper.MOV_TABLE_NAME} WHERE ${DBHelper.MOV_COL_ID}=?", arrayOf(memoId.toString()))

            if (cursor.moveToNext()) {

                val date = cursor.getString(cursor.getColumnIndex(DBHelper.MOV_COL_DATE))
                val score = cursor.getDouble(cursor.getColumnIndex(DBHelper.MOV_COL_SCORE))
                val image = cursor.getString(cursor.getColumnIndex(DBHelper.MOV_COL_POSTERPIC))
                val title = cursor.getString(cursor.getColumnIndex(DBHelper.MOV_COL_TITLE))
                val color = cursor.getInt(cursor.getColumnIndex(DBHelper.MOV_COL_COLOR))

                setVisibility(date, remoteView, R.id.tvWidgetMovieDate)
                setVisibility(title, remoteView, R.id.tvWidgetMovieTitle)
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

        private fun setVisibility(text : String, remoteView : RemoteViews, viewId : Int) {
            if (text != "")
                remoteView.setTextViewText(viewId, text)
            else
                remoteView.setViewVisibility(viewId, View.GONE)
        }

        private fun setWishWidget(memoId : Int, context: Context, remoteView : RemoteViews) {
            val wishList = arrayListOf<Wish>()
            var cursor: Cursor = database.rawQuery(
                "SELECT * FROM ${DBHelper.WISL_TABLE_NAME} WHERE ${DBHelper.WISL_COL_ID}=?",
                arrayOf(
                    memoId.toString()
                )
            )
            if (cursor.moveToNext()) {
                remoteView.setImageViewResource(R.id.ivWidgetWishBottomLine, R.drawable.thin_line)
                val category = cursor.getString(cursor.getColumnIndex(DBHelper.WISL_COL_CATEGORY))
                remoteView.setTextViewText(R.id.tvWidgetWishCatgory, category)
                val color = cursor.getInt(cursor.getColumnIndex(DBHelper.WISL_COL_COLOR))
                setColor(R.id.llWidgetWish, color, remoteView)

                cursor = database.rawQuery(
                    "SELECT * FROM ${DBHelper.WIS_TABLE_NAME} WHERE ${DBHelper.WIS_COL_WID}=?",
                    arrayOf(
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
        }

        private fun setWeeklyWidget(memoId: Int, remoteView : RemoteViews){
            var c : Cursor = database.rawQuery(
                "SELECT * FROM ${DBHelper.WEE_TABLE_NAME} WHERE ${DBHelper.WEE_COL_ID} = ?", arrayOf(memoId.toString()))
            c.moveToFirst()
            val date = c.getString(c.getColumnIndex(DBHelper.WEE_COL_DATE))
            val color = c.getInt(c.getColumnIndex(DBHelper.WEE_COL_COLOR))
            remoteView.setCharSequence(R.id.tvWidgetWeeklyDate, "setText", date)

            c = database.rawQuery("SELECT * FROM ${DBHelper.DIA_TABLE_NAME} WHERE ${DBHelper.DIA_COL_DID} = ?", arrayOf(memoId.toString()))
            for (i in 1.. 7) {
                c.moveToNext()
                val moodPic = c.getString(c.getColumnIndex(DBHelper.DIA_COL_MOODPIC))
                val weather = c.getString(c.getColumnIndex(DBHelper.DIA_COL_WEATHER))

                when(i){
                    1 -> {
                        remoteView.setImageViewUri(R.id.ivWidgetWeeklyMoodPic_mon_s, moodPic.toUri())
                        remoteView.setImageViewUri(R.id.ivWidgetWeeklyWeather_mon_s, weather.toUri())
                    }
                    2 -> {
                        remoteView.setImageViewUri(R.id.ivWidgetWeeklyMoodPic_tue_s, moodPic.toUri())
                        remoteView.setImageViewUri(R.id.ivWidgetWeeklyWeather_tue_s, weather.toUri())
                    }
                    3 -> {
                        remoteView.setImageViewUri(R.id.ivWidgetWeeklyMoodPic_wed_s, moodPic.toUri())
                        remoteView.setImageViewUri(R.id.ivWidgetWeeklyWeather_wed_s, weather.toUri())
                    }
                    4 -> {
                        remoteView.setImageViewUri(R.id.ivWidgetWeeklyMoodPic_thr_s, moodPic.toUri())
                        remoteView.setImageViewUri(R.id.ivWidgetWeeklyWeather_thr_s, weather.toUri())
                    }
                    5 -> {
                        remoteView.setImageViewUri(R.id.ivWidgetWeeklyMoodPic_fri_s, moodPic.toUri())
                        remoteView.setImageViewUri(R.id.ivWidgetWeeklyWeather_fri_s, weather.toUri())
                    }
                    6 -> {
                        remoteView.setImageViewUri(R.id.ivWidgetWeeklyMoodPic_sat_s, moodPic.toUri())
                        remoteView.setImageViewUri(R.id.ivWidgetWeeklyWeather_sat_s, weather.toUri())
                    }
                    7 -> {
                        remoteView.setImageViewUri(R.id.ivWidgetWeeklyMoodPic_sun_s, moodPic.toUri())
                        remoteView.setImageViewUri(R.id.ivWidgetWeeklyWeather_sun_s, weather.toUri())
                    }
                }
                setColor(R.id.llWidgetWeekly_s, color, remoteView)
            }
            c.close()
        }

        private fun setRecipeWidget(memoId: Int, remoteView : RemoteViews){
            val c : Cursor = database.rawQuery(
                "SELECT * FROM ${DBHelper.REC_TABLE_NAME} WHERE ${DBHelper.REC_COL_ID} = ?", arrayOf(memoId.toString()))

            if(c.moveToNext()){
                val name = c.getString(c.getColumnIndex(DBHelper.REC_COL_NAME))
                val ingredients = c.getString(c.getColumnIndex(DBHelper.REC_COL_INGREDIENTS))
                val content = c.getString(c.getColumnIndex(DBHelper.REC_COL_CONTENT))
                val color = c.getInt(c.getColumnIndex(DBHelper.REC_COL_COLOR))

                remoteView.setCharSequence(R.id.tvWidgetRecipeName, "setText", name)
                remoteView.setCharSequence(R.id.tvWidgetRecipeIngredients, "setText", ingredients)
                remoteView.setCharSequence(R.id.tvWidgetRecipeContentSmall, "setText", content)

                remoteView.setImageViewResource(R.id.ivIngredients, R.drawable.line)
                setColor(R.id.llWidgetRecipe_s, color, remoteView)
            }
            c.close()
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



