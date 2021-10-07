package com.bluelay.damda

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.AppWidgetTarget
import kotlinx.android.synthetic.main.activity_app_widget_configure.*

class AppWidgetConfigure : AppCompatActivity() {
    private lateinit var remoteView : RemoteViews
    private lateinit var appWidgetManager : AppWidgetManager
    private var mAppWidgetId : Int = 0

    private val memoList = arrayListOf<MemoInfo>()
    private lateinit var unLockedMemo : MemoInfo

    private lateinit var dbHelper : DBHelper
    private lateinit var database : SQLiteDatabase

    private lateinit var sharedPref : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_widget_configure)

        dbHelper = DBHelper(this)
        database = dbHelper.readableDatabase
        sharedPref = getSharedPreferences("widget", Context.MODE_PRIVATE)

        getAllMemo()
        val mmLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvAllMemo.layoutManager = mmLayoutManager
        val memoAdapter = WidgetSelectAdapter(this, memoList)
        rvAllMemo.adapter = memoAdapter

        val getResult_unLock = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                getWidget(unLockedMemo)
            }
        }
        var nextIntent : Intent
        val allMemoItemClickListener = object: WidgetSelectAdapter.ItemClickListener{
            override fun onClick(view: View, position: Int) {
                if (memoList[position].lock == 1) {
                    nextIntent = Intent(this@AppWidgetConfigure, UnlockPWActivity::class.java)
                    getResult_unLock.launch(nextIntent)
                    unLockedMemo = memoList[position]
                }
                else {
                    getWidget(memoList[position])
                }
            }
        }
        memoAdapter.setItemClickListener(allMemoItemClickListener)



    }

    private fun getAllMemo(){
        val types = mutableMapOf(
            DBHelper.MEM_TABLE_NAME to DBHelper.MEM_COL_CONTENT,
            DBHelper.TODL_TABLE_NAME to DBHelper.TODL_COL_DATE,
            DBHelper.WISL_TABLE_NAME to DBHelper.WISL_COL_CATEGORY,
            DBHelper.WEE_TABLE_NAME to DBHelper.WEE_COL_DATE,
            DBHelper.REC_TABLE_NAME to DBHelper.REC_COL_NAME,
            DBHelper.MOV_TABLE_NAME to DBHelper.MOV_COL_TITLE
        )

        var query : String?
        for (t in types) {
            query = "SELECT * " +
                    "FROM ${t.key}"
            val cursor = database.rawQuery(query, null)

            while(cursor.moveToNext()){
                val id = cursor.getInt(cursor.getColumnIndex("_id"))
                val wdate =cursor.getInt(cursor.getColumnIndex("wdate")) * 1000L
                val color = cursor.getInt(cursor.getColumnIndex("color"))
                val lock = cursor.getInt(cursor.getColumnIndex("lock"))
                val bkmr = cursor.getInt(cursor.getColumnIndex("bkmr"))
                val title = cursor.getString(cursor.getColumnIndex(t.value))

                memoList.add(MemoInfo(id, t.key, wdate, color, lock, bkmr, false, title))
            }
        }

        memoList.sortByDescending { memoInfo -> memoInfo.wdate }
    }

    fun getWidget(memo : MemoInfo){
        val intent = intent
        val extras = intent.extras
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        appWidgetManager = AppWidgetManager.getInstance(this)
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
        }
        sharedPref.edit().apply {
            putString("type$mAppWidgetId", memo.type)
            putInt("id$mAppWidgetId", memo.id)
            apply()
        }
        when (memo.type) {
            "Memo" -> {
                remoteView = RemoteViews(this.packageName, R.layout.widget_memo)
                setMemoWidget(memo)
            }
            "TodoList" -> {
                remoteView = RemoteViews(this.packageName, R.layout.widget_todo)
            }
            "WishList" -> {
                remoteView = RemoteViews(this.packageName, R.layout.widget_movie)
            }
            "Weekly" -> {
                remoteView = RemoteViews(this.packageName, R.layout.widget_weekly)
                setWeeklyWidget(memo)
            }
            "Recipe" -> {
                remoteView = RemoteViews(this.packageName, R.layout.widget_recipe)
                setRecipeWidget(memo)
            }
            "Movie" -> {
                remoteView = RemoteViews(this.packageName, R.layout.widget_movie)
            }
        }
        BaseWidget.updateAppWidget(this, appWidgetManager, mAppWidgetId)

        val resultValue = Intent()
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId)
        setResult(RESULT_OK, resultValue)
        finish()
    }





    private fun setWeeklyWidget(memo: MemoInfo){
        var c : Cursor = database.rawQuery(
            "SELECT * FROM ${DBHelper.WEE_TABLE_NAME} WHERE ${DBHelper.WEE_COL_ID} = ?", arrayOf(memo.id.toString()))
        c.moveToFirst()
        val date = c.getString(c.getColumnIndex(DBHelper.WEE_COL_DATE))
        remoteView.setCharSequence(R.id.tvWidgetWeeklyDate, "setText", date)

        c = database.rawQuery("SELECT * FROM ${DBHelper.DIA_TABLE_NAME} WHERE ${DBHelper.DIA_COL_DID} = ?", arrayOf(memo.id.toString()))
        for (i in 1.. 7) {
            c.moveToNext()
            val moodPic = c.getString(c.getColumnIndex(DBHelper.DIA_COL_MOODPIC))
            val weather = c.getString(c.getColumnIndex(DBHelper.DIA_COL_WEATHER))
            val content = c.getString(c.getColumnIndex(DBHelper.DIA_COL_CONTENT))

            when(i){
                1 -> {
                    remoteView.setImageViewUri(R.id.ivWidgetWeeklyMoodPic_mon, moodPic.toUri())
                    remoteView.setImageViewUri(R.id.ivWidgetWeeklyWeather_mon, weather.toUri())
                    remoteView.setCharSequence(R.id.tvWidgetWeeklyContent_mon, "setText", content)
                }
                2 -> {
                    remoteView.setImageViewUri(R.id.ivWidgetWeeklyMoodPic_tue, moodPic.toUri())
                    remoteView.setImageViewUri(R.id.ivWidgetWeeklyWeather_tue, weather.toUri())
                    remoteView.setCharSequence(R.id.tvWidgetWeeklyContent_tue, "setText", content)
                }
                3 -> {
                    remoteView.setImageViewUri(R.id.ivWidgetWeeklyMoodPic_wed, moodPic.toUri())
                    remoteView.setImageViewUri(R.id.ivWidgetWeeklyWeather_wed, weather.toUri())
                    remoteView.setCharSequence(R.id.tvWidgetWeeklyContent_wed, "setText", content)
                }
                4 -> {
                    remoteView.setImageViewUri(R.id.ivWidgetWeeklyMoodPic_thr, moodPic.toUri())
                    remoteView.setImageViewUri(R.id.ivWidgetWeeklyWeather_thr, weather.toUri())
                    remoteView.setCharSequence(R.id.tvWidgetWeeklyContent_thr, "setText", content)
                }
                5 -> {
                    remoteView.setImageViewUri(R.id.ivWidgetWeeklyMoodPic_fri, moodPic.toUri())
                    remoteView.setImageViewUri(R.id.ivWidgetWeeklyWeather_fri, weather.toUri())
                    remoteView.setCharSequence(R.id.tvWidgetWeeklyContent_fri, "setText", content)
                }
                6 -> {
                    remoteView.setImageViewUri(R.id.ivWidgetWeeklyMoodPic_sat, moodPic.toUri())
                    remoteView.setImageViewUri(R.id.ivWidgetWeeklyWeather_sat, weather.toUri())
                    remoteView.setCharSequence(R.id.tvWidgetWeeklyContent_sat, "setText", content)
                }
                7 -> {
                    remoteView.setImageViewUri(R.id.ivWidgetWeeklyMoodPic_sun, moodPic.toUri())
                    remoteView.setImageViewUri(R.id.ivWidgetWeeklyWeather_sun, weather.toUri())
                    remoteView.setCharSequence(R.id.tvWidgetWeeklyContent_sun, "setText", content)
                }
            }
            setColor(R.id.llWidgetWeekly, memo.color)
        }
        c.close()
    }

    private fun setRecipeWidget(memo: MemoInfo){
        val c : Cursor = database.rawQuery(
            "SELECT * FROM ${DBHelper.REC_TABLE_NAME} WHERE ${DBHelper.REC_COL_ID} = ?", arrayOf(memo.id.toString()))

        remoteView.setCharSequence(R.id.tvWidgetRecipeName, "setText", memo.title)
        if(c.moveToNext()){
            val name = c.getString(c.getColumnIndex(DBHelper.REC_COL_NAME))
            val ingredients = c.getString(c.getColumnIndex(DBHelper.REC_COL_INGREDIENTS))
            val content = c.getString(c.getColumnIndex(DBHelper.REC_COL_CONTENT))

            remoteView.setCharSequence(R.id.tvWidgetRecipeName, "setText", name)
            remoteView.setCharSequence(R.id.tvWidgetRecipeIngredients, "setText", ingredients)
            remoteView.setCharSequence(R.id.tvWidgetRecipeContent, "setText", content)
            setColor(R.id.llWidgetRecipe, memo.color)
        }
        c.close()
    }



    private fun setMemoWidget(memo: MemoInfo) {
        val cursor: Cursor = database.rawQuery(
            "SELECT * FROM ${DBHelper.MEM_TABLE_NAME} WHERE ${DBHelper.MEM_COL_ID}=?", arrayOf(
                memo.id.toString()
            )
        )

        if (cursor.moveToNext()) {
            val content = cursor.getString(cursor.getColumnIndex(DBHelper.MEM_COL_CONTENT))
            remoteView.setCharSequence(R.id.tvWidgetMemo, "setText", content)
            if (cursor.getString(cursor.getColumnIndex(DBHelper.MEM_COL_PHOTO)) != null) {
                try {
                    val savedPhotoPath = cursor.getString(cursor.getColumnIndex(DBHelper.MEM_COL_PHOTO))
                    remoteView.setViewVisibility(R.id.ivWidgetMemo, View.VISIBLE)
                    val ivWidgetMemo = AppWidgetTarget(this, R.id.ivWidgetMemo, remoteView, mAppWidgetId)
                    Glide.with(this.applicationContext).asBitmap().load(savedPhotoPath).into(ivWidgetMemo)
                } catch (e: java.lang.Exception) {
                    remoteView.setViewVisibility(R.id.ivWidgetMemo, View.GONE)
                }
            }
            setColor(R.id.llWidgetMemo, memo.color)
        }
        cursor.close()
    }

    private fun setWishWidget(memo: MemoInfo) {

    }

    private fun setColor(layoutId : Int, color : Int) {
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