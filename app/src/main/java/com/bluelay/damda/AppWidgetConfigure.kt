package com.bluelay.damda

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_widget_configure)

        dbHelper = DBHelper(this)
        database = dbHelper.readableDatabase

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

        when (memo.type) {
            "Memo" -> {
                remoteView = RemoteViews(this.packageName, R.layout.widget_movie)
            }
            "TodoList" -> {
                remoteView = RemoteViews(this.packageName, R.layout.widget_todo)
            }
            "WishList" -> {
                remoteView = RemoteViews(this.packageName, R.layout.widget_movie)
            }
            "Weekly" -> {
                remoteView = RemoteViews(this.packageName, R.layout.widget_movie)
            }
            "Recipe" -> {
                remoteView = RemoteViews(this.packageName, R.layout.widget_movie)
            }
            "Movie" -> {
                remoteView = RemoteViews(this.packageName, R.layout.widget_movie)
                setMovieWidget(memo)
            }
        }
        appWidgetManager.updateAppWidget(mAppWidgetId, remoteView)


        val resultValue = Intent()
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId)
        setResult(RESULT_OK, resultValue)
        finish()
    }

    private fun setMovieWidget(memo: MemoInfo) {
        val cursor: Cursor = database.rawQuery(
            "SELECT * FROM ${DBHelper.MOV_TABLE_NAME} WHERE ${DBHelper.MOV_COL_ID}=?", arrayOf(
                memo.id.toString()
            )
        )

        remoteView.setCharSequence(R.id.tvWidgetMovieTitle, "setText", memo.title)
        if (cursor.moveToNext()) {
            val date = cursor.getString(cursor.getColumnIndex(DBHelper.MOV_COL_DATE))
            val score = cursor.getFloat(cursor.getColumnIndex(DBHelper.MOV_COL_SCORE))
            val image = cursor.getString(cursor.getColumnIndex(DBHelper.MOV_COL_POSTERPIC))
            val content = cursor.getString(cursor.getColumnIndex(DBHelper.MOV_COL_CONTENT))

            remoteView.setCharSequence(R.id.tvWidgetMovieDate, "setText", date)
            remoteView.setCharSequence(R.id.tvWidgetMovieReview, "setText", content)
            val ivWidgetMoviePoster = AppWidgetTarget(this, R.id.ivWidgetMoviePoster, remoteView, mAppWidgetId)
            Glide.with(this.applicationContext).asBitmap().fitCenter().load(image).into(ivWidgetMoviePoster)
            setColor(R.id.llWidgetMovie, memo.color)
        }

        cursor.close()
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