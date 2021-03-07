package com.bluelay.damda

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bluelay.damda.DBHelper.Companion.MEM_TABLE_NAME
import com.bluelay.damda.DBHelper.Companion.MOV_TABLE_NAME
import com.bluelay.damda.DBHelper.Companion.REC_TABLE_NAME
import com.bluelay.damda.DBHelper.Companion.TODL_TABLE_NAME
import com.bluelay.damda.DBHelper.Companion.WEE_TABLE_NAME
import com.bluelay.damda.DBHelper.Companion.WISL_TABLE_NAME
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.adapter_view_main_memo.*
import kotlinx.android.synthetic.main.adapter_view_main_memo.view.*
import kotlinx.android.synthetic.main.layout_memo_settings.*
import java.text.SimpleDateFormat


class MainActivity : AppCompatActivity() {
    lateinit var dbHelper : DBHelper
    lateinit var database : SQLiteDatabase
    lateinit var cursor : Cursor

    var tabTableName = ""
    lateinit var mainMemoAdapter : MainMemoAdapter
    val mmList = arrayListOf<MemoInfo>()

    lateinit var bkmrMemoAdapter : BkmrMemoAdapter
    val bmList = arrayListOf<MemoInfo>()

    val formatWdate = SimpleDateFormat("yy-MM-dd HH:mm")
    lateinit var nextIntent : Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = DBHelper(this)
        database = dbHelper.writableDatabase

        editBar.visibility = View.GONE

        val itemCheckListener = object : MainMemoAdapter.ItemClickListener{
            override fun onClick(view: View, position: Int) {
                var checkBox = view.findViewById<CheckBox>(R.id.ck_mainMemo)
                checkBox.isChecked = !checkBox.isChecked
            }
        }

        val mainMemoitemClickListener = object: MainMemoAdapter.ItemClickListener{
            override fun onClick(view: View, position: Int) {
                if (mmList[position].lock == 1) {
                    nextIntent = Intent(this@MainActivity, UnlockPWActivity::class.java)
                }
                else {
                    when (mmList[position].type) {
                        "Memo" -> {
                            nextIntent = Intent(this@MainActivity, MemoActivity::class.java)
                        }
                        "TodoList" -> {
                            nextIntent = Intent(this@MainActivity, ToDoActivity::class.java)
                        }
                        "WishList" -> {
                            nextIntent = Intent(this@MainActivity, WishActivity::class.java)
                        }
                        "Weekly" -> {
                            nextIntent = Intent(this@MainActivity, SimpleDiaryActivity::class.java)
                        }
                        "Recipe" -> {
                            nextIntent = Intent(this@MainActivity, RecipeActivity::class.java)
                        }
                        "Movie" -> {
                            nextIntent = Intent(this@MainActivity, MovieActivity::class.java)
                        }
                    }
                }
                nextIntent.putExtra("memo", mmList[position])
                startActivity(nextIntent)
            }
        }

        val bmLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvMain_memo.layoutManager = bmLayoutManager
        bkmrMemoAdapter = BkmrMemoAdapter(this, bmList)
        bkmrMemoAdapter.setItemClickListener(object: BkmrMemoAdapter.ItemClickListener{
            override fun onClick(view: View, position: Int) {
                if (bmList[position].lock == 1) {
                    nextIntent = Intent(this@MainActivity, UnlockPWActivity::class.java)
                }
                else {
                    when (bmList[position].type) {
                        "Memo" -> {
                            nextIntent = Intent(this@MainActivity, MemoActivity::class.java)
                        }
                        "TodoList" -> {
                            nextIntent = Intent(this@MainActivity, ToDoActivity::class.java)
                        }
                        "WishList" -> {
                            nextIntent = Intent(this@MainActivity, WishActivity::class.java)
                        }
                        "Weekly" -> {
                            nextIntent = Intent(this@MainActivity, SimpleDiaryActivity::class.java)
                        }
                        "Recipe" -> {
                            nextIntent = Intent(this@MainActivity, RecipeActivity::class.java)
                        }
                        "Movie" -> {
                            nextIntent = Intent(this@MainActivity, MovieActivity::class.java)
                        }
                    }
                }
                nextIntent.putExtra("memo", bmList[position])
                startActivity(nextIntent)
            }
        })
        rvBKMR_memo.adapter = bkmrMemoAdapter

        val mmLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvMain_memo.layoutManager = mmLayoutManager
        mainMemoAdapter = MainMemoAdapter(this, mmList, false)
        mainMemoAdapter.setItemClickListener(mainMemoitemClickListener)
        rvMain_memo.adapter = mainMemoAdapter

        selectTab()

        btnAddMemo.setOnClickListener {
            addMemoDialog()
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
                            mainMemoAdapter.setItemClickListener(mainMemoitemClickListener)
                            rvMain_memo.adapter = mainMemoAdapter
                            mainMemoAdapter.notifyDataSetChanged()
                        }
                        tvDelete.setOnClickListener {
                            val iterator = mmList.iterator()
                            while(iterator.hasNext()) {
                                var m = iterator.next()
                                if (m.check) {
                                    database.execSQL("DELETE FROM ${m.type} WHERE _id = ${m.id}")
                                    iterator.remove()
                                }
                            }
                            editBar.visibility = View.GONE
                            mainMemoAdapter = MainMemoAdapter(this, mmList, false)
                            mainMemoAdapter.setItemClickListener(mainMemoitemClickListener)
                            rvMain_memo.adapter = mainMemoAdapter
                            mainMemoAdapter.notifyDataSetChanged()
                        }
                        mainMemoAdapter = MainMemoAdapter(this, mmList, true)
                        mainMemoAdapter.setItemClickListener(itemCheckListener)
                        rvMain_memo.adapter = mainMemoAdapter
                        mainMemoAdapter.notifyDataSetChanged()
                    }
                    R.id.optionSetBG -> {
                        nextIntent = Intent(this, SettingBGActivity::class.java)
                        startActivity(nextIntent)
                    }
                    R.id.optionSetPW -> {
                        nextIntent = Intent(this, SettingPWActivity::class.java)
                        startActivity(nextIntent)
                    }
                }
                false
            }
            pop.show()
        }
    }

    override fun onResume() {
        super.onResume()
        if(tabTableName != "All" && tabTableName != "") {
            getTypeMemo(tabTableName)
        }
        else {
            getAllMemo()
        }
    }

    fun addMemoDialog() {
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
            val sharedPref = this.getSharedPreferences("color", Context.MODE_PRIVATE)
//            var intent : Intent? = null
            var selectedColor = 0
            when (selMem) {
                llMemo -> {
                    nextIntent = Intent(this, MemoActivity::class.java)
                }
                llTodo -> {
                    nextIntent = Intent(this, ToDoActivity::class.java)
                }
                llDiary -> {
                    nextIntent = Intent(this, SimpleDiaryActivity::class.java)
                }
                llWish -> {
                    nextIntent = Intent(this, WishActivity::class.java)
                }
                llRecipe -> {
                    nextIntent = Intent(this, RecipeActivity::class.java)
                }
                llMovie -> {
                    nextIntent = Intent(this, MovieActivity::class.java)
                }
            }
            when (selCol) {
                null -> {
                    selectedColor = sharedPref.getInt("color", 0)
                }
                ivColor0 -> {
                    selectedColor = 0
                }
                ivColor1 -> {
                    selectedColor = 1
                }
                ivColor2 -> {
                    selectedColor = 2
                }
                ivColor3 -> {
                    selectedColor = 3
                }
                ivColor4 -> {
                    selectedColor = 4
                }
                ivColor5 -> {
                    selectedColor = 5
                }
                ivColor6 -> {
                    selectedColor = 6
                }
            }
            nextIntent.putExtra("color", selectedColor)
            startActivity(nextIntent)
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun getAllMemo(){
        mmList.clear()
        bmList.clear()
        val tableList : ArrayList<String> = arrayListOf(
            MEM_TABLE_NAME,
            TODL_TABLE_NAME,
            WISL_TABLE_NAME,
            WEE_TABLE_NAME,
            REC_TABLE_NAME,
            MOV_TABLE_NAME
        )

        //일반 메모
        var query : String?
        for (t in tableList) {
            query = "SELECT * " +
                    "FROM $t " +
                    "WHERE bkmr = 0"
            cursor = database.rawQuery(query, null)

            while(cursor.moveToNext()){
                val id = cursor.getInt(cursor.getColumnIndex("_id"))
                val wdate = formatWdate.format(cursor.getInt(cursor.getColumnIndex("wdate")) * 1000L)
                val color = cursor.getInt(cursor.getColumnIndex("color"))
                val lock = cursor.getInt(cursor.getColumnIndex("lock"))
                val bkmr = cursor.getInt(cursor.getColumnIndex("bkmr"))

                mmList.add(MemoInfo(id, t, wdate, color, lock, bkmr, false))
            }
        }

        //BKMR 메모
        var query2 : String?
        for (t in tableList) {
            query2 = "SELECT * " +
                    "FROM $t " +
                    "WHERE bkmr = 1"
            cursor = database.rawQuery(query2, null)

            while(cursor.moveToNext()){
                val id = cursor.getInt(cursor.getColumnIndex("_id"))
                val wdate = formatWdate.format(cursor.getInt(cursor.getColumnIndex("wdate")) * 1000L)
                val color = cursor.getInt(cursor.getColumnIndex("color"))
                val lock = cursor.getInt(cursor.getColumnIndex("lock"))
                val bkmr = cursor.getInt(cursor.getColumnIndex("bkmr"))

                bmList.add(MemoInfo(id, t, wdate, color, lock, bkmr, false))
            }
        }
        if(bmList.size == 0) {
            imgBkmr.visibility = View.GONE
        }
        else {
            imgBkmr.visibility = View.VISIBLE
        }

        cursor.close()

        mmList.sortByDescending { memoInfo -> memoInfo.wdate }
        bmList.sortByDescending { memoInfo -> memoInfo.wdate }
        mainMemoAdapter.notifyDataSetChanged()
        bkmrMemoAdapter.notifyDataSetChanged()
    }

    private fun getTypeMemo(tabTableName: String){
        mmList.clear()
        bmList.clear()

        val query1 = "Select * From $tabTableName WHERE bkmr = 0"  //일반 메모
        Log.d("aty", "getMainMemo query: " + query1)
        cursor = database.rawQuery(query1, null)

        while(cursor.moveToNext()){
            val id = cursor.getInt(cursor.getColumnIndex("_id"))
            val wdate = formatWdate.format(cursor.getInt(cursor.getColumnIndex("wdate")) * 1000L)
            Log.d("aty", "wdate: " + wdate)
            val color = cursor.getInt(cursor.getColumnIndex("color"))
            Log.d("aty", "color: " + color)
            val lock = cursor.getInt(cursor.getColumnIndex("lock"))
            val bkmr = cursor.getInt(cursor.getColumnIndex("bkmr"))

            mmList.add(MemoInfo(id, tabTableName, wdate, color, lock, bkmr, false))
        }
        Log.d("aty", mmList.size.toString())

        val query2 = "Select * From $tabTableName WHERE bkmr = 1"   //BKMR 메모
        cursor = database.rawQuery(query2, null)
        while(cursor.moveToNext()){
            val id = cursor.getInt(cursor.getColumnIndex("_id"))
            val wdate = formatWdate.format(cursor.getInt(cursor.getColumnIndex("wdate")) * 1000L)
            val color = cursor.getInt(cursor.getColumnIndex("color"))
            val lock = cursor.getInt(cursor.getColumnIndex("lock"))
            val bkmr = cursor.getInt(cursor.getColumnIndex("bkmr"))

            bmList.add(MemoInfo(id, tabTableName, wdate, color, lock, bkmr, false))
        }
        if(bmList.size == 0) {
            imgBkmr.visibility = View.GONE
        }
        else {
            imgBkmr.visibility = View.VISIBLE
        }

        cursor.close()

        mmList.sortByDescending { memoInfo -> memoInfo.wdate }
        bmList.sortByDescending { memoInfo -> memoInfo.wdate }
        mainMemoAdapter.notifyDataSetChanged()
        bkmrMemoAdapter.notifyDataSetChanged()
    }

    private fun selectTab() {
        tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tabLayout.selectedTabPosition) {
                    0 -> {
                        Log.d("aty", "tabTableName = All")
                        tabTableName = "All"
                        getAllMemo()
                    }
                    1 -> {
                        Log.d("aty", "tabTableName = Memo")
                        tabTableName = "Memo"
                        getTypeMemo(tabTableName)

                    }
                    2 -> {
                        Log.d("aty", "tabTableName = TodoList")
                        tabTableName = "TodoList"
                        getTypeMemo(tabTableName)

                    }
                    3 -> {
                        Log.d("aty", "tabTableName = WishList")
                        tabTableName = "WishList"
                        getTypeMemo(tabTableName)

                    }
                    4 -> {
                        Log.d("aty", "tabTableName = Weekly")
                        tabTableName = "Weekly"
                        getTypeMemo(tabTableName)

                    }
                    5 -> {
                        Log.d("aty", "tabTableName = Recipe")
                        tabTableName = "Recipe"
                        getTypeMemo(tabTableName)

                    }
                    6 -> {
                        Log.d("aty", "tabTableName = Movie")
                        tabTableName = "Movie"
                        getTypeMemo(tabTableName)
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

}