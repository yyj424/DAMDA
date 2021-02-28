package com.bluelay.damda

import android.app.AlertDialog
import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import kotlinx.android.synthetic.main.activity_bucket.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.layout_memo_settings.*

class BucketActivity : AppCompatActivity(), SetMemo {
    var bucketList = arrayListOf<Bucket>()
    lateinit var dbHelper : DBHelper
    lateinit var database : SQLiteDatabase
    var bid = -1
    var color = -1
    var lock = 0
    var bkmr = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bucket)

        dbHelper = DBHelper(this)
        database = dbHelper.writableDatabase
        val bucketAdapter = BucketAdapter(this, bucketList)

        if (intent.hasExtra("memo")) {
            var memo = intent.getSerializableExtra("memo") as MemoInfo
            color = memo.color
            bid = memo.id
            getBucketList()
        }
        else {
            color = intent.getIntExtra("color", 0)
            for (i in 1.. 10) {
                bucketList.add(Bucket("", 0, ""))
            }
        }

        setColor(this, color, activity_bucket)
        lvBucket.adapter = bucketAdapter

        settingLayout.visibility = View.INVISIBLE
        btnSettings.setOnClickListener {
            settingLayout.visibility = if (settingLayout.visibility == View.INVISIBLE) View.VISIBLE else View.INVISIBLE
        }

        cbLock.setOnCheckedChangeListener { _, isChecked ->
            lock = if(isChecked) 1 else 0
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
                setColor(this, color, activity_bucket)
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

    fun getBucketList() {
        var columns = arrayOf(DBHelper.BUCL_COL_ID, DBHelper.BUCL_COL_COLOR, DBHelper.BUCL_COL_LOCK, DBHelper.BUCL_COL_BKMR)
        var selection = "_id=?"
        var selectArgs = arrayOf(bid.toString())
        var c : Cursor = database.query(DBHelper.BUCL_TABLE_NAME, columns, selection, selectArgs, null, null, null)
        c.moveToNext()
        setColor(this, c.getInt(c.getColumnIndex(DBHelper.BUCL_COL_COLOR)), activity_bucket)
        lock = c.getInt(c.getColumnIndex(DBHelper.BUCL_COL_LOCK))
        bkmr = c.getInt(c.getColumnIndex(DBHelper.BUCL_COL_BKMR))
        if (lock == 1) {
            cbLock.isChecked = true
        }
        if (bkmr == 1) {
            cbBkmr.isChecked = true
        }

        columns = arrayOf(DBHelper.BUC_COL_ID, DBHelper.BUC_COL_DATE, DBHelper.BUC_COL_CHECKED, DBHelper.BUC_COL_CONTENT)
        selection = "bid=?"
        selectArgs = arrayOf(bid.toString())
        c = database.query(DBHelper.BUC_TABLE_NAME, columns, selection, selectArgs, null, null, null)
        bucketList.clear()
        for (i in 1.. 10) {
            if (c.moveToNext()) {
                bucketList.add(Bucket(c.getString(c.getColumnIndex(DBHelper.BUC_COL_CONTENT)), c.getInt(c.getColumnIndex(DBHelper.BUC_COL_CHECKED)), c.getString(c.getColumnIndex(DBHelper.BUC_COL_DATE))))
            }
            else {
                bucketList.add(Bucket("", 0, ""))
            }
        }
        c.close()
    }

    override fun onBackPressed() {
        var contentValues = ContentValues()
        contentValues.put(DBHelper.BUCL_COL_WDATE, System.currentTimeMillis() / 1000L)
        contentValues.put(DBHelper.BUCL_COL_COLOR, color)
        contentValues.put(DBHelper.BUCL_COL_LOCK, lock)
        contentValues.put(DBHelper.BUCL_COL_BKMR, bkmr)

        if (bid != -1) {
            var whereCluase = "_id=?"
            var whereArgs = arrayOf(bid.toString())
            database.update(DBHelper.BUCL_TABLE_NAME, contentValues, whereCluase, whereArgs)

            whereCluase = "bid=?"
            database.delete(DBHelper.BUC_TABLE_NAME, whereCluase, whereArgs)
        }
        else  {
            bid = database.insert(DBHelper.BUCL_TABLE_NAME, null, contentValues).toInt()
        }

        for(bucket in bucketList){
            contentValues.clear()
            if (!bucket.content.replace(" ", "").equals("")) {
                contentValues.put(DBHelper.BUC_COL_BID, bid)
                contentValues.put(DBHelper.BUC_COL_DATE, bucket.date)
                contentValues.put(DBHelper.BUC_COL_CHECKED, bucket.checked)
                contentValues.put(DBHelper.BUC_COL_CONTENT, bucket.content)
                database.insert(DBHelper.BUC_TABLE_NAME, null, contentValues)
            }
        }

        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        dbHelper.close()
    }
}