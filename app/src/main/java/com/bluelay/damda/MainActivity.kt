package com.bluelay.damda

import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bluelay.damda.DBHelper.Companion.BUCL_TABLE_NAME
import com.bluelay.damda.DBHelper.Companion.MEM_TABLE_NAME
import com.bluelay.damda.DBHelper.Companion.MOV_TABLE_NAME
import com.bluelay.damda.DBHelper.Companion.REC_TABLE_NAME
import com.bluelay.damda.DBHelper.Companion.TODL_TABLE_NAME
import com.bluelay.damda.DBHelper.Companion.WEE_TABLE_NAME
import com.bluelay.damda.DBHelper.Companion.WISL_TABLE_NAME
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat


class MainActivity : AppCompatActivity() {
    lateinit var dbHelper : DBHelper
    lateinit var database : SQLiteDatabase

    var tabTableName = ""
    lateinit var mainMemoAdapter : MainMemoAdapter
    val mmList = arrayListOf<MainMemo>()

    lateinit var bkmrMemoAdapter : BkmrMemoAdapter
    val bmList = arrayListOf<BkmrMemo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = DBHelper(this)
        database = dbHelper.writableDatabase

        val bmLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvMain_memo.layoutManager = bmLayoutManager
        bkmrMemoAdapter = BkmrMemoAdapter(this, bmList)
        Log.d("aty", bkmrMemoAdapter.bmList.toString())
        rvBKMR_memo.adapter = bkmrMemoAdapter

        val mmLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvMain_memo.layoutManager = mmLayoutManager
        mainMemoAdapter = MainMemoAdapter(this, mmList)
        Log.d("aty", mainMemoAdapter.mmList.toString())
        rvMain_memo.adapter = mainMemoAdapter

        getAllMemo()
        selectTab()
    }

    private fun getAllMemo(){
        mmList.clear()
        bmList.clear()
        val tableList : ArrayList<String> = arrayListOf(MEM_TABLE_NAME, TODL_TABLE_NAME, WISL_TABLE_NAME, WEE_TABLE_NAME, REC_TABLE_NAME, BUCL_TABLE_NAME, MOV_TABLE_NAME)

        //일반 메모
        var query : String?
        for (t in tableList) {
            query = "SELECT wdate, color " +
                    "FROM $t " +
                    "WHERE bkmr = 0"
            val cursor = database.rawQuery(query, null)
            val formatWdate = SimpleDateFormat("yyyy-MM-dd HH:mm")

            while(cursor.moveToNext()){
                val wdate = formatWdate.format(cursor.getInt(cursor.getColumnIndex("wdate"))*1000L)
                val color = cursor.getInt(cursor.getColumnIndex("color"))

                mmList.add(MainMemo(t, wdate, color))
            }
        }

        //BKMR 메모
        var query2 : String?
        for (t in tableList) {
            query2 = "SELECT wdate, color " +
                    "FROM $t " +
                    "WHERE bkmr = 1"
            val cursor = database.rawQuery(query2, null)
            val formatWdate = SimpleDateFormat("yyyy-MM-dd HH:mm")

            while(cursor.moveToNext()){
                val wdate = formatWdate.format(cursor.getInt(cursor.getColumnIndex("wdate"))*1000L)
                val color = cursor.getInt(cursor.getColumnIndex("color"))

                mmList.add(MainMemo(t, wdate, color))
            }
        }

        // cursor.close()
//        database.close()

        mainMemoAdapter.notifyDataSetChanged()
        bkmrMemoAdapter.notifyDataSetChanged()
    }

    private fun getTypeMemo(tabTableName: String){
        mmList.clear()
        bmList.clear()

        val query1 = "Select wdate From $tabTableName"  //일반 메모
        Log.d("aty", "getMainMemo query: " + query1)
        val cursor = database.rawQuery(query1, null)
        val formatWdate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

        while(cursor.moveToNext()){
            val wdate = formatWdate.format(cursor.getInt(cursor.getColumnIndex("wdate"))*1000L)
            Log.d("aty", "wdate: " + wdate)
            val color = cursor.getInt(cursor.getColumnIndex("color"))
            Log.d("aty", "color: " + color)
            mmList.add(MainMemo(tabTableName, wdate, color))
        }
        Log.d("aty", mmList.size.toString())

        val query2 = "Select wdate From $tabTableName WHERE bkmr = 1"   //BKMR 메모
        val cursor2 = database.rawQuery(query2, null)
        while(cursor2.moveToNext()){
            val wdate = formatWdate.format(cursor2.getInt(cursor2.getColumnIndex("wdate"))*1000L)
            val color = cursor2.getInt(cursor2.getColumnIndex("color"))
            bmList.add(BkmrMemo(tabTableName, wdate, color))
        }

        // cursor.close()
//        database.close()

        mainMemoAdapter.notifyDataSetChanged()
        bkmrMemoAdapter.notifyDataSetChanged()
    }

    private fun selectTab() {
        tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                if (tabLayout.selectedTabPosition == 0) {
                    Log.d("aty", "tabTableName = All")
                    tabTableName = "All"
                    getAllMemo()
                } else if (tabLayout.selectedTabPosition == 1) {
                    Log.d("aty", "tabTableName = Memo")
                    tabTableName = "Memo"
                    getTypeMemo(tabTableName)

                } else if (tabLayout.selectedTabPosition == 2) {
                    Log.d("aty", "tabTableName = TodoList")
                    tabTableName = "TodoList"
                    getTypeMemo(tabTableName)

                } else if (tabLayout.selectedTabPosition == 3) {
                    Log.d("aty", "tabTableName = WishList")
                    tabTableName = "WishList"
                    getTypeMemo(tabTableName)

                } else if (tabLayout.selectedTabPosition == 4) {
                    Log.d("aty", "tabTableName = Weekly")
                    tabTableName = "Weekly"
                    getTypeMemo(tabTableName)

                } else if (tabLayout.selectedTabPosition == 5) {
                    Log.d("aty", "tabTableName = Recipe")
                    tabTableName = "Recipe"
                    getTypeMemo(tabTableName)

                } else if (tabLayout.selectedTabPosition == 6) {
                    Log.d("aty", "tabTableName = BucketList")
                    tabTableName = "BucketList"
                    getTypeMemo(tabTableName)

                } else if (tabLayout.selectedTabPosition == 7) {
                    Log.d("aty", "tabTableName = Movie")
                    tabTableName = "Movie"
                    getTypeMemo(tabTableName)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

}