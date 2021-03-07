package com.bluelay.damda

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_simple_diary.*
import kotlinx.android.synthetic.main.activity_simple_diary.btnSettings
import kotlinx.android.synthetic.main.activity_simple_diary.settingLayout
import kotlinx.android.synthetic.main.activity_wish.*
import kotlinx.android.synthetic.main.layout_memo_settings.*
import java.text.SimpleDateFormat
import java.util.*


class SimpleDiaryActivity : AppCompatActivity(), SetMemo{

    lateinit var dbHelper : DBHelper
    lateinit var database : SQLiteDatabase

    var did = -1
    var lock = 0
    var bkmr = 0
    var color = -1

    private val calendar = Calendar.getInstance()
    private val dateFormat = "yyyy.MM.dd"
    private var sdf = SimpleDateFormat(dateFormat, Locale.KOREA)

    var diaryList = arrayListOf<SimpleDiary>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simple_diary)

        dbHelper = DBHelper(this)
        database = dbHelper.writableDatabase

        var recordDatePicker = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            etDiaryDate.setText(sdf.format(calendar.time))
        }
        etDiaryDate.setOnClickListener {
            DatePickerDialog(
                this,
                R.style.DialogTheme,
                recordDatePicker,
                calendar[Calendar.YEAR],
                calendar[Calendar.MONTH],
                calendar[Calendar.DAY_OF_MONTH]
            ).show()
        }

        val diaryAdapter = SimpleDiaryAdapter(this, diaryList)
        if (intent.hasExtra("memo")) {
            var memo = intent.getSerializableExtra("memo") as MemoInfo
            color = memo.color
            did = memo.id
            lock = memo.lock
            bkmr = memo.bkmr
            selectDiary()
        }
        else {
            color = intent.getIntExtra("color", 0)
            if (did != -1) {
                selectDiary()
            } else {
                var day = "day"
                for (i in 1.. 7) {
                    if(i == 1){
                        day = "Mon"
                    } else if(i == 2){
                        day = "Tue"
                    } else if(i == 3){
                        day = "Wed"
                    }else if(i == 4){
                        day = "Thu"
                    }else if(i == 5){
                        day = "Fri"
                    }else if(i == 6){
                        day = "Sat"
                    }else if(i == 7){
                        day = "Sun"
                    }

                    diaryList.add(SimpleDiary(day, "", getURLForResource(R.drawable.select_emoji).toString(),
                        getURLForResource(R.drawable.select_weather).toString()))
                }
            }
        }
        setColor(this, color, activity_simpe_diary)

        lvDiary.adapter = diaryAdapter

        settingLayout.visibility = View.INVISIBLE
        btnSettings.setOnClickListener {
            settingLayout.visibility = if (settingLayout.visibility == View.INVISIBLE) View.VISIBLE  else View.INVISIBLE
        }

        cbLock.setOnCheckedChangeListener { _, isChecked ->
            lock =  if(isChecked) 1 else 0
        }
        cbBkmr.setOnCheckedChangeListener { _, isChecked ->
            bkmr = if(isChecked) 1 else 0
        }

        btnChangeColor.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val view = LayoutInflater.from(this).inflate(R.layout.dialog_change_color, null)
            builder.setView(view)
            val dialog = builder.create()
            val ivColor0 = view.findViewById<ImageView>(R.id.ivColor0)
            val ivColor1 = view.findViewById<ImageView>(R.id.ivColor1)
            val ivColor2 = view.findViewById<ImageView>(R.id.ivColor2)
            val ivColor3 = view.findViewById<ImageView>(R.id.ivColor3)
            val ivColor4 = view.findViewById<ImageView>(R.id.ivColor4)
            val ivColor5 = view.findViewById<ImageView>(R.id.ivColor5)
            val ivColor6 = view.findViewById<ImageView>(R.id.ivColor6)

            val colorClickListener = View.OnClickListener { v ->
                when (v) {
                    ivColor0 -> {
                        color = 0
                    }
                    ivColor1 -> {
                        color = 1
                    }
                    ivColor2 -> {
                        color = 2
                    }
                    ivColor3 -> {
                        color = 3
                    }
                    ivColor4 -> {
                        color = 4
                    }
                    ivColor5 -> {
                        color = 5
                    }
                    ivColor6 -> {
                        color = 6
                    }
                }
                setColor(this, color, activity_simpe_diary)
                dialog.dismiss()
            }

            ivColor0!!.setOnClickListener(colorClickListener)
            ivColor1!!.setOnClickListener(colorClickListener)
            ivColor2!!.setOnClickListener(colorClickListener)
            ivColor3!!.setOnClickListener(colorClickListener)
            ivColor4!!.setOnClickListener(colorClickListener)
            ivColor5!!.setOnClickListener(colorClickListener)
            ivColor6!!.setOnClickListener(colorClickListener)

            dialog.show()
        }
    }

    override fun onBackPressed() {
        Log.d("SimpleDiaryActivity", "onBackPressed")
        var contentValues = ContentValues()
        contentValues.put(DBHelper.WEE_COL_WDATE , System.currentTimeMillis()/1000L)
        contentValues.put(DBHelper.WEE_COL_COLOR , color)
        contentValues.put(DBHelper.WEE_COL_BKMR, bkmr)
        contentValues.put(DBHelper.WEE_COL_LOCK, lock)
        contentValues.put(DBHelper.WEE_COL_DATE, etDiaryDate.text.toString())

        if (did != -1) {
            var whereCluase = "_id=?"
            var whereArgs = arrayOf(did.toString())
            database.update(DBHelper.WEE_TABLE_NAME, contentValues, whereCluase, whereArgs)

            whereCluase = "did=?"
            database.delete(DBHelper.DIA_TABLE_NAME, whereCluase, whereArgs)
        }
        else  {
            did = database.insert(DBHelper.WEE_TABLE_NAME, null, contentValues).toInt()
        }
        for(diary in diaryList){
            contentValues.clear()

            contentValues.put(DBHelper.DIA_COL_DID, did)
            contentValues.put(DBHelper.DIA_COL_WEATHER, diary.weather)
            contentValues.put(DBHelper.DIA_COL_MOODPIC, diary.moodPic)
            contentValues.put(DBHelper.DIA_COL_CONTENT, diary.content)
            database.insert(DBHelper.DIA_TABLE_NAME, null, contentValues)

        }
        //dbHelper.close() ondestroy
        //startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun selectDiary() {
        Log.d("SimpleDiaryActivity", "diarySelect")
        database = dbHelper.readableDatabase

        var c : Cursor = database.rawQuery("SELECT * FROM ${DBHelper.DIA_TABLE_NAME} WHERE ${DBHelper.DIA_COL_DID} = ?", arrayOf(did.toString()))

        if (lock == 1) {
            cbLock.isChecked = true
        }
        if (bkmr == 1) {
            cbBkmr.isChecked = true
        }

        var day = "day"
        var moodPic : String
        var weather : String
        var content : String

        for (i in 1.. 7) {
            c.moveToNext()
            moodPic = c.getString(c.getColumnIndex(DBHelper.DIA_COL_MOODPIC))
            weather = c.getString(c.getColumnIndex(DBHelper.DIA_COL_WEATHER))
            content = c.getString(c.getColumnIndex(DBHelper.DIA_COL_CONTENT))

            if (i == 1) {
                day = "Mon"
            } else if (i == 2) {
                day = "Tue"
            } else if (i == 3) {
                day = "Wed"
            } else if (i == 4) {
                day = "Thu"
            } else if (i == 5) {
                day = "Fri"
            } else if (i == 6) {
                day = "Sat"
            } else if (i == 7) {
                day = "Sun"
            }

            diaryList.add(SimpleDiary(day, content, moodPic, weather))
        }
        c.close()
    }

    private fun getURLForResource(resId: Int): String? {
        return Uri.parse("android.resource://" + R::class.java.getPackage().name + "/" + resId)
            .toString()
    }

    override fun onDestroy() {
        super.onDestroy()
        dbHelper.close()
    }
}