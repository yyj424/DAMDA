package com.bluelay.damda

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context): SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        private val DB_NAME = "DAMDA.db"
        private val DB_VERSION = 1

        private val CON_TABLE_NAME = "Connect"
        private val CON_COL_ID = "_id"
        private val CON_COL_FOLDER_ID = "folder_id"
        private val CON_COL_TYPE = "type"
        private val CON_COL_TYPE_ID = "type_id"

        private val FOL_TABLE_NAME = "Folder"
        private val FOL_COL_ID = "_id"
        private val FOL_COL_NAME = "name"

        private val TOD_TABLE_NAME = "Todo"
        private val TOD_COL_ID = "_id"
        private val TOD_COL_WDATE = "wdate"
        private val TOD_COL_DATE = "date"
        private val TOD_COL_CONTENT = "content"
        private val TOD_COL_CHECKED = "checked"
        private val TOD_COL_COLOR = "color"

        private val REC_TABLE_NAME = "Recipe"
        private val REC_COL_ID = "_id"
        private val REC_COL_WDATE = "wdate"
        private val REC_COL_CONTENT = "content"
        private val REC_COL_NAME = "name"
        private val REC_COL_COLOR = "color"
        private val REC_COL_INGREDIENTS = "ingredients"

        private val MEM_TABLE_NAME = "Memo"
        private val MEM_COL_ID = "_id"
        private val MEM_COL_WDATE = "wdate"
        private val MEM_COL_CONTENT = "content"
        private val MEM_COL_COLOR = "color"

        private val BUC_TABLE_NAME = "Bucket"
        private val BUC_COL_ID = "_id"
        private val BUC_COL_WDATE = "wdate"
        private val BUC_COL_DATE = "date"
        private val BUC_COL_CONTENT = "content"
        private val BUC_COL_CHECKED = "checked"
        private val BUC_COL_COLOR = "color"

        private val DIA_TABLE_NAME = "Diary"
        private val DIA_COL_ID = "_id"
        private val DIA_COL_WDATE = "wdate"
        private val DIA_COL_DATE = "date"
        private val DIA_COL_CONTENT = "content"
        private val DIA_COL_MOODPIC = "moodpic"
        private val DIA_COL_WEATHER = "weather"
        private val DIA_COL_COLOR = "color"

        private val WIS_TABLE_NAME = "Wish"
        private val WIS_COL_ID = "_id"
        private val WIS_COL_WDATE = "wdate"
        private val WIS_COL_CATEGORY = "category"
        private val WIS_COL_CHECKED = "checked"
        private val WIS_COL_PRICE = "price"
        private val WIS_COL_ITEM = "item"
        private val WIS_COL_LINK = "link"
        private val WIS_COL_COLOR = "color"

        private val MOV_TABLE_NAME = "Movie"
        private val MOV_COL_ID = "_id"
        private val MOV_COL_WDATE = "wdate"
        private val MOV_COL_DATE = "date"
        private val MOV_COL_CONTENT = "content"
        private val MOV_COL_TITLE = "title"
        private val MOV_COL_SCORE = "score"
        private val MOV_COL_POSTERPIC = "posterpic"
        private val MOV_COL_COLOR = "color"
    }

    override fun onCreate(db: SQLiteDatabase?) {
       var createTable =
                "CREATE TABLE $CON_TABLE_NAME" +
                        "($CON_COL_ID Integer PRIMARY KEY, " +
                        "$CON_COL_TYPE_ID Integer, " +
                        "$CON_COL_TYPE Integer, " +
                        "$CON_COL_FOLDER_ID Integer);"
        db?.execSQL(createTable)

        createTable =
                "CREATE TABLE $FOL_TABLE_NAME" +
                        "($FOL_COL_ID Integer PRIMARY KEY," +
                        "$FOL_COL_NAME TEXT)"
        db?.execSQL(createTable)

        createTable =
                "CREATE TABLE $TOD_TABLE_NAME" +
                        "($TOD_COL_ID Integer PRIMARY KEY," +
                        "$TOD_COL_WDATE TEXT," +
                        "$TOD_COL_DATE TEXT," +
                        "$TOD_COL_CONTENT TEXT," +
                        "$TOD_COL_CHECKED Integer," +
                        "$TOD_COL_COLOR TEXT)"
        db?.execSQL(createTable)

        createTable =
                "CREATE TABLE $REC_TABLE_NAME" +
                        "($REC_COL_ID Integer PRIMARY KEY," +
                        "$REC_COL_WDATE TEXT," +
                        "$REC_COL_INGREDIENTS TEXT," +
                        "$REC_COL_CONTENT TEXT," +
                        "$REC_COL_NAME TEXT" +
                        "$REC_COL_COLOR TEXT)"
        db?.execSQL(createTable)

        createTable =
                "CREATE TABLE $MEM_TABLE_NAME" +
                        "($MEM_COL_ID Integer PRIMARY KEY," +
                        "$MEM_COL_WDATE TEXT," +
                        "$MEM_COL_CONTENT TEXT," +
                        "$MEM_COL_COLOR TEXT)"
        db?.execSQL(createTable)

        createTable =
                "CREATE TABLE $BUC_TABLE_NAME" +
                        "($BUC_COL_ID Integer PRIMARY KEY," +
                        "$BUC_COL_WDATE TEXT," +
                        "$BUC_COL_DATE TEXT," +
                        "$BUC_COL_CONTENT TEXT," +
                        "$BUC_COL_CHECKED Integer," +
                        "$BUC_COL_COLOR TEXT)"
        db?.execSQL(createTable)

        createTable =
                "CREATE TABLE $DIA_TABLE_NAME" +
                        "($DIA_COL_ID Integer PRIMARY KEY," +
                        "$DIA_COL_WDATE TEXT," +
                        "$DIA_COL_DATE TEXT," +
                        "$DIA_COL_WEATHER TEXT," +
                        "$DIA_COL_MOODPIC TEXT," +
                        "$DIA_COL_CONTENT TEXT," +
                        "$DIA_COL_COLOR TEXT)"
        db?.execSQL(createTable)

        createTable =
                "CREATE TABLE $WIS_TABLE_NAME" +
                        "($WIS_COL_ID Integer PRIMARY KEY," +
                        "$WIS_COL_WDATE TEXT," +
                        "$WIS_COL_CATEGORY TEXT," +
                        "$WIS_COL_ITEM TEXT," +
                        "$WIS_COL_PRICE Integer," +
                        "$WIS_COL_LINK TEXT," +
                        "$WIS_COL_CHECKED Integer," +
                        "$WIS_COL_COLOR TEXT)"
        db?.execSQL(createTable)

        createTable =
                "CREATE TABLE $MOV_TABLE_NAME" +
                        "($MOV_COL_ID Integer PRIMARY KEY," +
                        "$MOV_COL_WDATE TEXT," +
                        "$MOV_COL_DATE TEXT," +
                        "$MOV_COL_TITLE TEXT," +
                        "$MOV_COL_POSTERPIC TEXT," +
                        "$MOV_COL_SCORE real," +
                        "$MOV_COL_CONTENT Integer," +
                        "$MOV_COL_COLOR TEXT)"
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE if exists $CON_TABLE_NAME")
        db.execSQL("DROP TABLE if exists $FOL_TABLE_NAME")
        db.execSQL("DROP TABLE if exists $TOD_TABLE_NAME")
        db.execSQL("DROP TABLE if exists $REC_TABLE_NAME")
        db.execSQL("DROP TABLE if exists $MEM_TABLE_NAME")
        db.execSQL("DROP TABLE if exists $BUC_TABLE_NAME")
        db.execSQL("DROP TABLE if exists $DIA_TABLE_NAME")
        db.execSQL("DROP TABLE if exists $WIS_TABLE_NAME")
        db.execSQL("DROP TABLE if exists $MOV_TABLE_NAME")
        onCreate(db)
    }
}