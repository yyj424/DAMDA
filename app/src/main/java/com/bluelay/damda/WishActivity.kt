package com.bluelay.damda

import android.app.AlertDialog
import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.database.getIntOrNull
import kotlinx.android.synthetic.main.activity_wish.*
import kotlinx.android.synthetic.main.activity_wish.btnSaveMemo
import kotlinx.android.synthetic.main.activity_wish.fabMemoSetting
import kotlinx.android.synthetic.main.activity_wish.settingLayout
import kotlinx.android.synthetic.main.layout_memo_settings.*

class WishActivity : AppCompatActivity(), CalTotal, SetMemo {
    private var oldWishList = arrayListOf<Wish>()
    private var newWishList = arrayListOf<Wish>()
    private lateinit var dbHelper : DBHelper
    private lateinit var database : SQLiteDatabase
    private lateinit var memo : MemoInfo
    private var wid = -1
    private var lock = 0
    private var bkmr = 0
    private var color = -1
    private var category = ""
    private var total = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wish)

        dbHelper = DBHelper(this)
        database = dbHelper.writableDatabase
        val wishAdapter = WishAdapter(this, this, newWishList)

        if (intent.hasExtra("memo")) {
            btnDeleteMemo.visibility = View.VISIBLE
            memo = intent.getSerializableExtra("memo") as MemoInfo
            wid = memo.id
            color = memo.color
            lock = memo.lock
            bkmr = memo.bkmr
            getWishList()
        }
        else {
            color = intent.getIntExtra("color", 0)
            for (i in 0.. 9) {
                newWishList.add(Wish("", null, 0, ""))
            }
        }

        setColor(this, color, activity_wish)
        lvWish.adapter = wishAdapter
        lvWish.itemsCanFocus = true

        fabMemoSetting.setOnClickListener {
            it.startAnimation(
                AnimationUtils.loadAnimation(
                    applicationContext, R.anim.fade_out
                ))
            btnCloseSetting.startAnimation(
                AnimationUtils.loadAnimation(
                    applicationContext, R.anim.fade_in
                ))
            it.visibility = View.INVISIBLE
            settingLayout.visibility = View.VISIBLE
        }
        btnCloseSetting.setOnClickListener {
            settingLayout.startAnimation(
                AnimationUtils.loadAnimation(
                    applicationContext, R.anim.fade_out
                ))
            fabMemoSetting.startAnimation(
                AnimationUtils.loadAnimation(
                    applicationContext, R.anim.fade_in
                ))
            settingLayout.visibility = View.INVISIBLE
            fabMemoSetting.visibility = View.VISIBLE
        }

        cbLock.setOnCheckedChangeListener { _, isChecked ->
            if (checkExistPassword(this) && isChecked) {
                lock = 1
            }
            else {
                lock = 0
                cbLock.isChecked = false
            }
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
                color = when (v) {
                    ivColor0 -> 0
                    ivColor1 -> 1
                    ivColor2 -> 2
                    ivColor3 -> 3
                    ivColor4 -> 4
                    ivColor5 -> 5
                    ivColor6 -> 6
                    else -> 0
                }
                setColor(this, color, activity_wish)
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

        btnSaveMemo.setOnClickListener {
            saveMemo()
        }

        btnDeleteMemo.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val view = LayoutInflater.from(this).inflate(R.layout.dialog_delete_memo, null)
            builder.setView(view)
            val btnDelConfirm = view.findViewById<Button>(R.id.btnDelConfirm)
            val btnDelCancel = view.findViewById<Button>(R.id.btnDelCancel)
            val dialog = builder.create()
            btnDelConfirm.setOnClickListener{
                dialog.dismiss()
                deleteMemo()
            }
            btnDelCancel.setOnClickListener{
                dialog.dismiss()
            }
            dialog.show()
        }
    }

    private fun getWishList() {
        var columns = arrayOf(DBHelper.WISL_COL_ID, DBHelper.WISL_COL_COLOR, DBHelper.WISL_COL_CATEGORY)
        var selection = "_id=?"
        var selectArgs = arrayOf(wid.toString())
        var c : Cursor = database.query(DBHelper.WISL_TABLE_NAME, columns, selection, selectArgs, null, null, null)
        c.moveToNext()
        category = c.getString(c.getColumnIndex(DBHelper.WISL_COL_CATEGORY))
        etWishCategory.setText(category)

        if (lock == 1) {
            cbLock.isChecked = true
        }
        if (bkmr == 1) {
            cbBkmr.isChecked = true
        }

        columns = arrayOf(DBHelper.WIS_COL_ID, DBHelper.WIS_COL_ITEM, DBHelper.WIS_COL_PRICE, DBHelper.WIS_COL_CHECKED, DBHelper.WIS_COL_LINK)
        selection = "wid=?"
        selectArgs = arrayOf(wid.toString())
        c = database.query(DBHelper.WIS_TABLE_NAME, columns, selection, selectArgs, null, null, null)
        newWishList.clear()
        oldWishList.clear()
        for (i in 0.. 9) {
            if (c.moveToNext()) {
                var price: Int? = null
                if (c.getIntOrNull(c.getColumnIndex(DBHelper.WIS_COL_PRICE)) != null) {
                    price = c.getInt(c.getColumnIndex(DBHelper.WIS_COL_PRICE))
                    total += price
                }
                newWishList.add(Wish(c.getString(c.getColumnIndex(DBHelper.WIS_COL_ITEM)), price, c.getInt(c.getColumnIndex(DBHelper.WIS_COL_CHECKED)), c.getString(c.getColumnIndex(DBHelper.WIS_COL_LINK))))
                oldWishList.add(Wish(c.getString(c.getColumnIndex(DBHelper.WIS_COL_ITEM)), price, c.getInt(c.getColumnIndex(DBHelper.WIS_COL_CHECKED)), c.getString(c.getColumnIndex(DBHelper.WIS_COL_LINK))))
            }
            else {
                newWishList.add(Wish("", null, 0, ""))
                oldWishList.add(Wish("", null, 0, ""))
            }
        }

        tvWishTotal.text = total.toString()
        c.close()
    }

    override fun onBackPressed() {
        saveMemo()
    }

    private fun saveMemo() {
        val contentValues = ContentValues()
        contentValues.put(DBHelper.WISL_COL_WDATE, System.currentTimeMillis() / 1000L)
        contentValues.put(DBHelper.WISL_COL_COLOR, color)
        contentValues.put(DBHelper.WISL_COL_CATEGORY, etWishCategory.text.toString())
        contentValues.put(DBHelper.WISL_COL_LOCK, lock)
        contentValues.put(DBHelper.WISL_COL_BKMR, bkmr)

        var dbChange = false
        if (wid != -1) {
            if (checkUpdate()) {
                dbChange = true
                var whereClause = "_id=?"
                val whereArgs = arrayOf(wid.toString())
                database.update(DBHelper.WISL_TABLE_NAME, contentValues, whereClause, whereArgs)

                whereClause = "wid=?"
                database.delete(DBHelper.WIS_TABLE_NAME, whereClause, whereArgs)
            }
        }
        else {
            dbChange = true
            wid = database.insert(DBHelper.WISL_TABLE_NAME, null, contentValues).toInt()
        }

        if (dbChange) {
            for (wish in newWishList) {
                contentValues.clear()
                if (wish.item.replace(" ", "") != "" || wish.price != null || wish.link.replace(" ", "") != "") {
                    contentValues.put(DBHelper.WIS_COL_WID, wid)
                    contentValues.put(DBHelper.WIS_COL_ITEM, wish.item)
                    contentValues.put(DBHelper.WIS_COL_PRICE, wish.price)
                    contentValues.put(DBHelper.WIS_COL_CHECKED, wish.checked)
                    contentValues.put(DBHelper.WIS_COL_LINK, wish.link)
                    database.insert(DBHelper.WIS_TABLE_NAME, null, contentValues)
                }
            }
        }

        finish()
    }

    private fun checkUpdate() : Boolean {
        if (color != memo.color) return true
        if (bkmr != memo.bkmr) return true
        if (lock != memo.lock) return true
        if (category != etWishCategory.text.toString()) return true
        newWishList.forEachIndexed { i, newWish ->
            var oldWish = oldWishList[i]
            if (newWish.item != oldWish.item) return true
            if (newWish.price != oldWish.price) return true
            if (newWish.checked != oldWish.checked) return true
            if (newWish.link != oldWish.link) return true
        }
        return false
    }

    private fun deleteMemo() {
        database.execSQL("DELETE FROM ${DBHelper.WISL_TABLE_NAME} WHERE _id = $wid")
        finish()
    }

    override fun cal(total: String) {
        tvWishTotal.text = total
    }

    override fun onDestroy() {
        super.onDestroy()
        dbHelper.close()
    }
}