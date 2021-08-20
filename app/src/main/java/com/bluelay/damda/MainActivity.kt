package com.bluelay.damda

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bluelay.damda.DBHelper.Companion.MEM_COL_CONTENT
import com.bluelay.damda.DBHelper.Companion.MEM_TABLE_NAME
import com.bluelay.damda.DBHelper.Companion.MOV_COL_TITLE
import com.bluelay.damda.DBHelper.Companion.MOV_TABLE_NAME
import com.bluelay.damda.DBHelper.Companion.REC_COL_NAME
import com.bluelay.damda.DBHelper.Companion.REC_TABLE_NAME
import com.bluelay.damda.DBHelper.Companion.TODL_COL_DATE
import com.bluelay.damda.DBHelper.Companion.TODL_TABLE_NAME
import com.bluelay.damda.DBHelper.Companion.WEE_COL_DATE
import com.bluelay.damda.DBHelper.Companion.WEE_TABLE_NAME
import com.bluelay.damda.DBHelper.Companion.WISL_COL_CATEGORY
import com.bluelay.damda.DBHelper.Companion.WISL_TABLE_NAME
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.adapter_view_main_memo.*
import kotlinx.android.synthetic.main.adapter_view_main_memo.view.*
import kotlinx.android.synthetic.main.layout_memo_settings.*


class MainActivity : AppCompatActivity() {
    private lateinit var dbHelper : DBHelper
    private lateinit var database : SQLiteDatabase
    private lateinit var cursor : Cursor

    var tabTableName = ""
    private lateinit var mainMemoAdapter : MainMemoAdapter
    val mmList = arrayListOf<MemoInfo>()

    private lateinit var bkmrMemoAdapter : BkmrMemoAdapter
    val bmList = arrayListOf<MemoInfo>()

    var nextIntent : Intent? = null

    private val titles = mutableMapOf(
        MEM_TABLE_NAME to MEM_COL_CONTENT,
        TODL_TABLE_NAME to TODL_COL_DATE,
        WISL_TABLE_NAME to WISL_COL_CATEGORY,
        WEE_TABLE_NAME to WEE_COL_DATE,
        REC_TABLE_NAME to REC_COL_NAME,
        MOV_TABLE_NAME to MOV_COL_TITLE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = DBHelper(this)
        database = dbHelper.writableDatabase

        editBar.visibility = View.GONE

        val itemCheckListener = object : MainMemoAdapter.ItemClickListener{
            override fun onClick(view: View, position: Int) {
                val checkBox = view.findViewById<CheckBox>(R.id.ck_mainMemo)
                checkBox.isChecked = !checkBox.isChecked
            }
        }

        val itemCheckListener2 = object : BkmrMemoAdapter.ItemClickListener{
            override fun onClick(view: View, position: Int) {
                val checkBox = view.findViewById<CheckBox>(R.id.ck_bkmrMemo)
                checkBox.isChecked = !checkBox.isChecked
            }
        }

        val mainMemoItemClickListener = object: MainMemoAdapter.ItemClickListener{
            override fun onClick(view: View, position: Int) {
                if (mmList[position].lock == 1) {
                    nextIntent = Intent(this@MainActivity, UnlockPWActivity::class.java)
                }
                else {
                    when (mmList[position].type) {
                        "Memo" -> nextIntent = Intent(this@MainActivity, MemoActivity::class.java)
                        "TodoList" -> nextIntent = Intent(this@MainActivity, ToDoActivity::class.java)
                        "WishList" -> nextIntent = Intent(this@MainActivity, WishActivity::class.java)
                        "Weekly" -> nextIntent = Intent(this@MainActivity, WeeklyActivity::class.java)
                        "Recipe" -> nextIntent = Intent(this@MainActivity, RecipeActivity::class.java)
                        "Movie" -> nextIntent = Intent(this@MainActivity, MovieActivity::class.java)
                    }
                }
                nextIntent?.putExtra("memo", mmList[position])
                startActivity(nextIntent)
            }
        }

        val bkmrMemoItemClickListener = object: BkmrMemoAdapter.ItemClickListener{
            override fun onClick(view: View, position: Int) {
                if (bmList[position].lock == 1) {
                    nextIntent = Intent(this@MainActivity, UnlockPWActivity::class.java)
                }
                else {
                    when (bmList[position].type) {
                        "Memo" -> nextIntent = Intent(this@MainActivity, MemoActivity::class.java)
                        "TodoList" -> nextIntent = Intent(this@MainActivity, ToDoActivity::class.java)
                        "WishList" -> nextIntent = Intent(this@MainActivity, WishActivity::class.java)
                        "Weekly" -> nextIntent = Intent(this@MainActivity, WeeklyActivity::class.java)
                        "Recipe" ->  nextIntent = Intent(this@MainActivity, RecipeActivity::class.java)
                        "Movie" -> nextIntent = Intent(this@MainActivity, MovieActivity::class.java)
                    }
                }
                nextIntent?.putExtra("memo", bmList[position])
                startActivity(nextIntent)
            }
        }

        val bmLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvMain_memo.layoutManager = bmLayoutManager
        bkmrMemoAdapter = BkmrMemoAdapter(this, bmList, false)
        bkmrMemoAdapter.setItemClickListener(bkmrMemoItemClickListener)
        rvBKMR_memo.adapter = bkmrMemoAdapter

        val mmLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvMain_memo.layoutManager = mmLayoutManager
        mainMemoAdapter = MainMemoAdapter(this, mmList, false)
        mainMemoAdapter.setItemClickListener(mainMemoItemClickListener)
        rvMain_memo.adapter = mainMemoAdapter

        selectTab()

        btnAddMemo.setOnClickListener {
            when (tabLayout.selectedTabPosition) {
                0 -> addMemoDialog()
                1 -> moveMemoActivity(MemoActivity::class.java)
                2 -> moveMemoActivity(ToDoActivity::class.java)
                3 -> moveMemoActivity(WishActivity::class.java)
                4 -> moveMemoActivity(WeeklyActivity::class.java)
                5 -> moveMemoActivity(RecipeActivity::class.java)
                6 -> moveMemoActivity(MovieActivity::class.java)
            }
        }

        btnMenu.setOnClickListener {
            val pop = PopupMenu(this, btnMenu)
            menuInflater.inflate(R.menu.setting_menu, pop.menu)
            pop.setOnMenuItemClickListener { item->
                when (item.itemId) {
                    R.id.optionEdit ->  {
                        editBar.visibility = View.VISIBLE
                        tvCancel.setOnClickListener {
                            editBar.visibility = View.GONE
                            mainMemoAdapter = MainMemoAdapter(this, mmList, false)
                            mainMemoAdapter.setItemClickListener(mainMemoItemClickListener)
                            rvMain_memo.adapter = mainMemoAdapter
                            mainMemoAdapter.notifyDataSetChanged()
                            bkmrMemoAdapter = BkmrMemoAdapter(this, bmList, false)
                            bkmrMemoAdapter.setItemClickListener(bkmrMemoItemClickListener)
                            rvBKMR_memo.adapter = bkmrMemoAdapter
                            bkmrMemoAdapter.notifyDataSetChanged()
                        }
                        tvDelete.setOnClickListener {
                            val iterator = mmList.iterator()
                            while(iterator.hasNext()) {
                                val m = iterator.next()
                                if (m.check) {
                                    database.execSQL("DELETE FROM ${m.type} WHERE _id = ${m.id}")
                                    iterator.remove()
                                }
                            }
                            val iterator2 = bmList.iterator()
                            while(iterator2.hasNext()) {
                                val m = iterator2.next()
                                if (m.check) {
                                    database.execSQL("DELETE FROM ${m.type} WHERE _id = ${m.id}")
                                    iterator2.remove()
                                }
                            }
                            if(bmList.size == 0) {
                                imgBkmr.visibility = View.GONE
                            }
                            else {
                                imgBkmr.visibility = View.VISIBLE
                            }
                            editBar.visibility = View.GONE
                            mainMemoAdapter = MainMemoAdapter(this, mmList, false)
                            mainMemoAdapter.setItemClickListener(mainMemoItemClickListener)
                            rvMain_memo.adapter = mainMemoAdapter
                            mainMemoAdapter.notifyDataSetChanged()
                            bkmrMemoAdapter = BkmrMemoAdapter(this, bmList, false)
                            bkmrMemoAdapter.setItemClickListener(bkmrMemoItemClickListener)
                            rvBKMR_memo.adapter = bkmrMemoAdapter
                            bkmrMemoAdapter.notifyDataSetChanged()
                        }
                        mainMemoAdapter = MainMemoAdapter(this, mmList, true)
                        mainMemoAdapter.setItemClickListener(itemCheckListener)
                        rvMain_memo.adapter = mainMemoAdapter
                        mainMemoAdapter.notifyDataSetChanged()
                        bkmrMemoAdapter = BkmrMemoAdapter(this, bmList, true)
                        bkmrMemoAdapter.setItemClickListener(itemCheckListener2)
                        rvBKMR_memo.adapter = bkmrMemoAdapter
                        bkmrMemoAdapter.notifyDataSetChanged()
                    }
                    R.id.optionSetBG -> {
                        nextIntent = Intent(this, SettingBGActivity::class.java)
                        startActivity(nextIntent)
                    }
                    R.id.optionSetPW -> {
                        nextIntent = Intent(this, SettingPWActivity::class.java)
                        startActivity(nextIntent)
                    }
                    R.id.optionLicense -> {
                        nextIntent = Intent(this, LicenseActivity::class.java)
                        startActivity(nextIntent)
                    }
                }
                false
            }
            pop.show()
        }
    }

    private fun moveMemoActivity(activity: Class<out AppCompatActivity>) {
        val sharedPref = this.getSharedPreferences("memoColor", Context.MODE_PRIVATE)
        var  selectedColor = sharedPref.getInt("color", 0)

        nextIntent = Intent(this, activity)
        nextIntent?.putExtra("color", selectedColor)
        startActivity(nextIntent)
    }

    override fun onResume() {
        super.onResume()
        if(tabTableName != "All" && tabTableName != "") {
            titles[tabTableName]?.let { getTypeMemo(tabTableName, it) }
        }
        else {
            getAllMemo()
        }
    }

    private fun addMemoDialog() {
        val builder = AlertDialog.Builder(this)
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_select_memo, null)
        val llMemo = view.findViewById<LinearLayout>(R.id.llMemo)
        val llTodo = view.findViewById<LinearLayout>(R.id.llTodo)
        val llDiary = view.findViewById<LinearLayout>(R.id.llDiary)
        val llWish = view.findViewById<LinearLayout>(R.id.llWish)
        val llRecipe = view.findViewById<LinearLayout>(R.id.llRecipe)
        val llMovie = view.findViewById<LinearLayout>(R.id.llMovie)
        val ivColor0 = view.findViewById<ImageView>(R.id.ivColor0)
        val ivColor1 = view.findViewById<ImageView>(R.id.ivColor1)
        val ivColor2 = view.findViewById<ImageView>(R.id.ivColor2)
        val ivColor3 = view.findViewById<ImageView>(R.id.ivColor3)
        val ivColor4 = view.findViewById<ImageView>(R.id.ivColor4)
        val ivColor5 = view.findViewById<ImageView>(R.id.ivColor5)
        val ivColor6 = view.findViewById<ImageView>(R.id.ivColor6)
        val btnOk = view.findViewById<ImageView>(R.id.btnOk)
        val tvSelect = view.findViewById<TextView>(R.id.tvSelect)
        tvSelect.visibility = View.INVISIBLE

        var selMem: View? = null
        var selCol: View? = null
        val memoClickListener = View.OnClickListener { v ->
            when (selMem) {
                null -> {
                    tvSelect.visibility = View.INVISIBLE
                    v.setBackgroundColor(Color.parseColor("#EAE8DD"))
                    selMem = v
                }
                else -> {
                    v.setBackgroundColor(Color.parseColor("#EAE8DD"))
                    selMem!!.background = null
                    selMem = v
                }
            }
        }
        val colorClickListener = View.OnClickListener { v ->
            when (selCol) {
                null -> {
                    v.setBackgroundResource(R.drawable.border)
                    selCol = v
                }
                v -> {
                    selCol!!.background = null
                    selCol = null
                }
                else -> {
                    v.setBackgroundResource(R.drawable.border)
                    selCol!!.background = null
                    selCol = v
                }
            }
        }
        llMemo!!.setOnClickListener(memoClickListener)
        llTodo!!.setOnClickListener(memoClickListener)
        llDiary!!.setOnClickListener(memoClickListener)
        llWish!!.setOnClickListener(memoClickListener)
        llRecipe!!.setOnClickListener(memoClickListener)
        llMovie!!.setOnClickListener(memoClickListener)

        ivColor0!!.setOnClickListener(colorClickListener)
        ivColor1!!.setOnClickListener(colorClickListener)
        ivColor2!!.setOnClickListener(colorClickListener)
        ivColor3!!.setOnClickListener(colorClickListener)
        ivColor4!!.setOnClickListener(colorClickListener)
        ivColor5!!.setOnClickListener(colorClickListener)
        ivColor6!!.setOnClickListener(colorClickListener)

        builder.setView(view)
        val dialog = builder.create()
        btnOk.setOnClickListener{
            val sharedPref = this.getSharedPreferences("memoColor", Context.MODE_PRIVATE)
            nextIntent = null
            var selectedColor = 0
            when (selMem) {
                llMemo -> nextIntent = Intent(this, MemoActivity::class.java)
                llTodo -> nextIntent = Intent(this, ToDoActivity::class.java)
                llDiary -> nextIntent = Intent(this, WeeklyActivity::class.java)
                llWish -> nextIntent = Intent(this, WishActivity::class.java)
                llRecipe -> nextIntent = Intent(this, RecipeActivity::class.java)
                llMovie -> nextIntent = Intent(this, MovieActivity::class.java)
            }
            when (selCol) {
                null -> selectedColor = sharedPref.getInt("color", 0)
                ivColor0 -> selectedColor = 0
                ivColor1 -> selectedColor = 1
                ivColor2 -> selectedColor = 2
                ivColor3 -> selectedColor = 3
                ivColor4 -> selectedColor = 4
                ivColor5 -> selectedColor = 5
                ivColor6 -> selectedColor = 6
            }
            nextIntent?.putExtra("color", selectedColor)
            if (nextIntent == null) {
                tvSelect.visibility = View.VISIBLE
            }
            else {
                startActivity(nextIntent)
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    private fun getAllMemo(){
        mmList.clear()
        bmList.clear()

        var query : String?
        for (t in titles) {
            query = "SELECT * " +
                    "FROM ${t.key} " +
                    "WHERE bkmr = 0"
            cursor = database.rawQuery(query, null)

            while(cursor.moveToNext()){
                val id = cursor.getInt(cursor.getColumnIndex("_id"))
                val wdate =cursor.getInt(cursor.getColumnIndex("wdate")) * 1000L
                val color = cursor.getInt(cursor.getColumnIndex("color"))
                val lock = cursor.getInt(cursor.getColumnIndex("lock"))
                val bkmr = cursor.getInt(cursor.getColumnIndex("bkmr"))
                val title = cursor.getString(cursor.getColumnIndex(t.value))

                mmList.add(MemoInfo(id, t.key, wdate, color, lock, bkmr, false, title))
            }
        }

        var query2 : String?
        for (t in titles) {
            query2 = "SELECT * " +
                    "FROM ${t.key} " +
                    "WHERE bkmr = 1"
            cursor = database.rawQuery(query2, null)

            while(cursor.moveToNext()){
                val id = cursor.getInt(cursor.getColumnIndex("_id"))
                val wdate = cursor.getInt(cursor.getColumnIndex("wdate")) * 1000L
                val color = cursor.getInt(cursor.getColumnIndex("color"))
                val lock = cursor.getInt(cursor.getColumnIndex("lock"))
                val bkmr = cursor.getInt(cursor.getColumnIndex("bkmr"))
                val title = cursor.getString(cursor.getColumnIndex(t.value))

                bmList.add(MemoInfo(id, t.key, wdate, color, lock, bkmr, false, title))
            }
        }
        cursor.close()
        mmList.sortByDescending { memoInfo -> memoInfo.wdate }
        bmList.sortByDescending { memoInfo -> memoInfo.wdate }
        mainMemoAdapter.notifyDataSetChanged()
        bkmrMemoAdapter.notifyDataSetChanged()
        if(bmList.size == 0) {
            imgBkmr.visibility = View.GONE
        }
        else {
            imgBkmr.visibility = View.VISIBLE
        }
    }

    private fun getTypeMemo(tabTableName: String, t : String){
        mmList.clear()
        bmList.clear()

        val query1 = "Select * From $tabTableName WHERE bkmr = 0"
        cursor = database.rawQuery(query1, null)
        while(cursor.moveToNext()){
            val id = cursor.getInt(cursor.getColumnIndex("_id"))
            val wdate = cursor.getInt(cursor.getColumnIndex("wdate")) * 1000L
            val color = cursor.getInt(cursor.getColumnIndex("color"))
            val lock = cursor.getInt(cursor.getColumnIndex("lock"))
            val bkmr = cursor.getInt(cursor.getColumnIndex("bkmr"))
            val title = cursor.getString(cursor.getColumnIndex(t))

            mmList.add(MemoInfo(id, tabTableName, wdate, color, lock, bkmr, false, title))
        }

        val query2 = "Select * From $tabTableName WHERE bkmr = 1"
        cursor = database.rawQuery(query2, null)
        while(cursor.moveToNext()){
            val id = cursor.getInt(cursor.getColumnIndex("_id"))
            val wdate = cursor.getInt(cursor.getColumnIndex("wdate")) * 1000L
            val color = cursor.getInt(cursor.getColumnIndex("color"))
            val lock = cursor.getInt(cursor.getColumnIndex("lock"))
            val bkmr = cursor.getInt(cursor.getColumnIndex("bkmr"))
            val title = cursor.getString(cursor.getColumnIndex(t))

            bmList.add(MemoInfo(id, tabTableName, wdate, color, lock, bkmr, false, title))
        }
        cursor.close()
        mmList.sortByDescending { memoInfo -> memoInfo.wdate }
        bmList.sortByDescending { memoInfo -> memoInfo.wdate }
        mainMemoAdapter.notifyDataSetChanged()
        bkmrMemoAdapter.notifyDataSetChanged()
        if(bmList.size == 0) {
            imgBkmr.visibility = View.GONE
        }
        else {
            imgBkmr.visibility = View.VISIBLE
        }
    }

    private fun selectTab() {
        tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tabLayout.selectedTabPosition) {
                    0 -> {
                        tabTableName = "All"
                        getAllMemo()
                    }
                    1 -> {
                        tabTableName = "Memo"
                        getTypeMemo(MEM_TABLE_NAME, MEM_COL_CONTENT)

                    }
                    2 -> {
                        tabTableName = "TodoList"
                        getTypeMemo(TODL_TABLE_NAME, TODL_COL_DATE)

                    }
                    3 -> {
                        tabTableName = "WishList"
                        getTypeMemo(WISL_TABLE_NAME, WISL_COL_CATEGORY)

                    }
                    4 -> {
                        tabTableName = "Weekly"
                        getTypeMemo(WEE_TABLE_NAME, WEE_COL_DATE)

                    }
                    5 -> {
                        tabTableName = "Recipe"
                        getTypeMemo(REC_TABLE_NAME, REC_COL_NAME)
                    }
                    6 -> {
                        tabTableName = "Movie"
                        getTypeMemo(MOV_TABLE_NAME, MOV_COL_TITLE)
                    }
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }
}