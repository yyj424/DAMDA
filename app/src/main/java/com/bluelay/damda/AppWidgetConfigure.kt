package com.bluelay.damda

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.AppWidgetTarget
import kotlinx.android.synthetic.main.activity_app_widget_configure.*

class AppWidgetConfigure : AppCompatActivity() {
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
        if (appWidgetManager.getAppWidgetInfo(mAppWidgetId).provider.toString().contains("Large")) {
            LargeWidget.updateAppWidget(this, appWidgetManager, mAppWidgetId)
        }
        else {
            SmallWidget.updateAppWidget(this, appWidgetManager, mAppWidgetId)
        }

        val resultValue = Intent()
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId)
        setResult(RESULT_OK, resultValue)
        finish()
    }
}