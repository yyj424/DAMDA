package com.bluelay.damda

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context): SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        val DB_NAME = "DAMDA.db"
        val DB_VERSION = 1

        val CON_TABLE_NAME = "Connect"
        val CON_COL_ID = "_id"
        val CON_COL_FOLDER_ID = "folder_id"
        val CON_COL_TYPE = "type"
        val CON_COL_TYPE_ID = "type_id"

        val FOL_TABLE_NAME = "Folder"
        val FOL_COL_ID = "_id"
        val FOL_COL_NAME = "name"

        val TODL_TABLE_NAME = "TodoList"
        val TODL_COL_ID = "_id"
        val TODL_COL_WDATE = "wdate"
        val TODL_COL_DATE = "date"
        val TODL_COL_COLOR = "color"
        val TODL_COL_LOCK = "lock"

        val TOD_TABLE_NAME = "Todo"
        val TOD_COL_ID = "_id"
        val TOD_COL_TID = "tid"
        val TOD_COL_CONTENT = "content"
        val TOD_COL_CHECKED = "checked"

        val REC_TABLE_NAME = "Recipe"
        val REC_COL_ID = "_id"
        val REC_COL_WDATE = "wdate"
        val REC_COL_CONTENT = "content"
        val REC_COL_NAME = "name"
        val REC_COL_COLOR = "color"
        val REC_COL_INGREDIENTS = "ingredients"
        val REC_COL_LOCK = "lock"

        val MEM_TABLE_NAME = "Memo"
        val MEM_COL_ID = "_id"
        val MEM_COL_WDATE = "wdate"
        val MEM_COL_CONTENT = "content"
        val MEM_COL_COLOR = "color"
        val MEM_COL_LOCK = "lock"

        val BUCL_TABLE_NAME = "BucketList"
        val BUCL_COL_ID = "_id"
        val BUCL_COL_WDATE = "wdate"
        val BUCL_COL_COLOR = "color"
        val BUCL_COL_LOCK = "lock"

        val BUC_TABLE_NAME = "Bucket"
        val BUC_COL_ID = "_id"
        val BUC_COL_BID = "bid"
        val BUC_COL_DATE = "date"
        val BUC_COL_CONTENT = "content"
        val BUC_COL_CHECKED = "checked"

        val WEE_TABLE_NAME = "Weekly"
        val WEE_COL_ID = "_id"
        val WEE_COL_WDATE = "wdate"
        val WEE_COL_DATE = "date"
        val WEE_COL_COLOR = "color"
        val WEE_COL_LOCK = "lock"

        val DIA_TABLE_NAME = "Diary"
        val DIA_COL_ID = "_id"
        val DIA_COL_DID = "did"
        val DIA_COL_DATE = "date"
        val DIA_COL_CONTENT = "content"
        val DIA_COL_MOODPIC = "moodpic"
        val DIA_COL_WEATHER = "weather"

        val WISL_TABLE_NAME = "WishList"
        val WISL_COL_ID = "_id"
        val WISL_COL_WDATE = "wdate"
        val WISL_COL_CATEGORY = "category"
        val WISL_COL_COLOR = "color"
        val WISL_COL_LOCK = "lock"

        val WIS_TABLE_NAME = "Wish"
        val WIS_COL_ID = "_id"
        val WIS_COL_WID = "wid"
        val WIS_COL_CHECKED = "checked"
        val WIS_COL_PRICE = "price"
        val WIS_COL_ITEM = "item"
        val WIS_COL_LINK = "link"

        val MOV_TABLE_NAME = "Movie"
        val MOV_COL_ID = "_id"
        val MOV_COL_WDATE = "wdate"
        val MOV_COL_DATE = "date"
        val MOV_COL_CONTENT = "content"
        val MOV_COL_TITLE = "title"
        val MOV_COL_SCORE = "score"
        val MOV_COL_POSTERPIC = "posterpic"
        val MOV_COL_COLOR = "color"
        val MOV_COL_LOCK = "lock"
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
                "CREATE TABLE $TODL_TABLE_NAME" +
                        "($TODL_COL_ID Integer PRIMARY KEY," +
                        "$TODL_COL_WDATE Integer," +
                        "$TODL_COL_DATE TEXT," +
                        "$TODL_COL_LOCK Integer," +
                        "$TODL_COL_COLOR Integer)"
        db?.execSQL(createTable)

        createTable =
                "CREATE TABLE $TOD_TABLE_NAME" +
                        "($TOD_COL_ID Integer PRIMARY KEY," +
                        "$TOD_COL_TID Integer," +
                        "$TOD_COL_CONTENT TEXT," +
                        "$TOD_COL_CHECKED Integer," +
                        "FOREIGN KEY($TOD_COL_TID) REFERENCES $TODL_TABLE_NAME ($TODL_COL_ID) ON DELETE CASCADE)"
        db?.execSQL(createTable)

        createTable =
                "CREATE TABLE $REC_TABLE_NAME" +
                        "($REC_COL_ID Integer PRIMARY KEY," +
                        "$REC_COL_WDATE Integer," +
                        "$REC_COL_INGREDIENTS TEXT," +
                        "$REC_COL_CONTENT TEXT," +
                        "$REC_COL_NAME TEXT," +
                        "$REC_COL_LOCK Integer," +
                        "$REC_COL_COLOR Integer)"
        db?.execSQL(createTable)

        createTable =
                "CREATE TABLE $MEM_TABLE_NAME" +
                        "($MEM_COL_ID Integer PRIMARY KEY," +
                        "$MEM_COL_WDATE Integer," +
                        "$MEM_COL_CONTENT TEXT," +
                        "$MEM_COL_LOCK Integer," +
                        "$MEM_COL_COLOR Integer)"
        db?.execSQL(createTable)

        createTable =
                "CREATE TABLE $BUCL_TABLE_NAME" +
                        "($BUCL_COL_ID Integer PRIMARY KEY," +
                        "$BUCL_COL_WDATE Integer," +
                        "$BUCL_COL_LOCK Integer," +
                        "$BUCL_COL_COLOR Integer)"
        db?.execSQL(createTable)

        createTable =
                "CREATE TABLE $BUC_TABLE_NAME" +
                        "($BUC_COL_ID Integer PRIMARY KEY," +
                        "$BUC_COL_BID Integer," +
                        "$BUC_COL_DATE TEXT," +
                        "$BUC_COL_CONTENT Integer," +
                        "$BUC_COL_CHECKED TEXT," +
                        "FOREIGN KEY($BUC_COL_BID) REFERENCES $BUCL_TABLE_NAME ($BUCL_COL_ID) ON DELETE CASCADE)"
        db?.execSQL(createTable)

        createTable =
                "CREATE TABLE $WEE_TABLE_NAME" +
                        "($WEE_COL_ID Integer PRIMARY KEY," +
                        "$WEE_COL_WDATE Integer," +
                        "$WEE_COL_DATE TEXT," +
                        "$WEE_COL_LOCK Integer," +
                        "$WEE_COL_COLOR Integer)"
        db?.execSQL(createTable)

        createTable =
                "CREATE TABLE $DIA_TABLE_NAME" +
                        "($DIA_COL_ID Integer PRIMARY KEY," +
                        "$DIA_COL_DID Integer," +
                        "$DIA_COL_DATE TEXT," +
                        "$DIA_COL_WEATHER TEXT," +
                        "$DIA_COL_MOODPIC TEXT," +
                        "$DIA_COL_CONTENT TEXT," +
                        "FOREIGN KEY($DIA_COL_DID) REFERENCES $WEE_TABLE_NAME ($WEE_COL_ID) ON DELETE CASCADE)"
        db?.execSQL(createTable)

        createTable =
                "CREATE TABLE $WISL_TABLE_NAME" +
                        "($WISL_COL_ID Integer PRIMARY KEY," +
                        "$WISL_COL_WDATE Integer," +
                        "$WISL_COL_CATEGORY TEXT," +
                        "$WISL_COL_LOCK Integer," +
                        "$WISL_COL_COLOR Integer)"
        db?.execSQL(createTable)

        createTable =
                "CREATE TABLE $WIS_TABLE_NAME" +
                        "($WIS_COL_ID Integer PRIMARY KEY," +
                        "$WIS_COL_WID Integer," +
                        "$WIS_COL_ITEM TEXT," +
                        "$WIS_COL_PRICE Integer," +
                        "$WIS_COL_LINK TEXT," +
                        "$WIS_COL_CHECKED Integer," +
                        "FOREIGN KEY($WIS_COL_WID) REFERENCES $WISL_TABLE_NAME ($WISL_COL_ID) ON DELETE CASCADE)"
        db?.execSQL(createTable)

        createTable =
                "CREATE TABLE $MOV_TABLE_NAME" +
                        "($MOV_COL_ID Integer PRIMARY KEY," +
                        "$MOV_COL_WDATE Integer," +
                        "$MOV_COL_DATE TEXT," +
                        "$MOV_COL_TITLE TEXT," +
                        "$MOV_COL_POSTERPIC TEXT," +
                        "$MOV_COL_SCORE real," +
                        "$MOV_COL_CONTENT Integer," +
                        "$MOV_COL_LOCK Integer," +
                        "$MOV_COL_COLOR Integer)"
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE if exists $CON_TABLE_NAME")
        db.execSQL("DROP TABLE if exists $FOL_TABLE_NAME")
        db.execSQL("DROP TABLE if exists $TODL_TABLE_NAME")
        db.execSQL("DROP TABLE if exists $TOD_TABLE_NAME")
        db.execSQL("DROP TABLE if exists $REC_TABLE_NAME")
        db.execSQL("DROP TABLE if exists $MEM_TABLE_NAME")
        db.execSQL("DROP TABLE if exists $BUCL_TABLE_NAME")
        db.execSQL("DROP TABLE if exists $BUC_TABLE_NAME")
        db.execSQL("DROP TABLE if exists $WEE_TABLE_NAME")
        db.execSQL("DROP TABLE if exists $DIA_TABLE_NAME")
        db.execSQL("DROP TABLE if exists $WISL_TABLE_NAME")
        db.execSQL("DROP TABLE if exists $WIS_TABLE_NAME")
        db.execSQL("DROP TABLE if exists $MOV_TABLE_NAME")
        onCreate(db)
    }

    override fun onOpen(db: SQLiteDatabase) {
        super.onOpen(db)
        if (!db.isReadOnly) {
            db.execSQL("PRAGMA foreign_keys=ON;")
        }
    }
}