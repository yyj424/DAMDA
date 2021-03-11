package com.bluelay.damda

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context): SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        const val DB_NAME = "DAMDA.db"
        const val DB_VERSION = 1

        const val CON_TABLE_NAME = "Connect"
        const val CON_COL_ID = "_id"
        const val CON_COL_FOLDER_ID = "folder_id"
        const val CON_COL_TYPE = "type"
        const val CON_COL_TYPE_ID = "type_id"

        const val FOL_TABLE_NAME = "Folder"
        const val FOL_COL_ID = "_id"
        const val FOL_COL_NAME = "name"

        const val TODL_TABLE_NAME = "TodoList"
        const val TODL_COL_ID = "_id"
        const val TODL_COL_WDATE = "wdate"
        const val TODL_COL_DATE = "date"
        const val TODL_COL_COLOR = "color"
        const val TODL_COL_LOCK = "lock"
        const val TODL_COL_BKMR = "bkmr"

        const val TOD_TABLE_NAME = "Todo"
        const val TOD_COL_ID = "_id"
        const val TOD_COL_TID = "tid"
        const val TOD_COL_CONTENT = "content"
        const val TOD_COL_CHECKED = "checked"

        const val REC_TABLE_NAME = "Recipe"
        const val REC_COL_ID = "_id"
        const val REC_COL_WDATE = "wdate"
        const val REC_COL_CONTENT = "content"
        const val REC_COL_NAME = "name"
        const val REC_COL_COLOR = "color"
        const val REC_COL_INGREDIENTS = "ingredients"
        const val REC_COL_LOCK = "lock"
        const val REC_COL_BKMR = "bkmr"

        const val MEM_TABLE_NAME = "Memo"
        const val MEM_COL_ID = "_id"
        const val MEM_COL_WDATE = "wdate"
        const val MEM_COL_CONTENT = "content"
        const val MEM_COL_COLOR = "color"
        const val MEM_COL_LOCK = "lock"
        const val MEM_COL_BKMR = "bkmr"

        const val WEE_TABLE_NAME = "Weekly"
        const val WEE_COL_ID = "_id"
        const val WEE_COL_WDATE = "wdate"
        const val WEE_COL_DATE = "date"
        const val WEE_COL_COLOR = "color"
        const val WEE_COL_LOCK = "lock"
        const val WEE_COL_BKMR = "bkmr"

        const val DIA_TABLE_NAME = "Diary"
        const val DIA_COL_ID = "_id"
        const val DIA_COL_DID = "did"
        const val DIA_COL_CONTENT = "content"
        const val DIA_COL_MOODPIC = "moodpic"
        const val DIA_COL_WEATHER = "weather"

        const val WISL_TABLE_NAME = "WishList"
        const val WISL_COL_ID = "_id"
        const val WISL_COL_WDATE = "wdate"
        const val WISL_COL_CATEGORY = "category"
        const val WISL_COL_COLOR = "color"
        const val WISL_COL_LOCK = "lock"
        const val WISL_COL_BKMR = "bkmr"

        const val WIS_TABLE_NAME = "Wish"
        const val WIS_COL_ID = "_id"
        const val WIS_COL_WID = "wid"
        const val WIS_COL_CHECKED = "checked"
        const val WIS_COL_PRICE = "price"
        const val WIS_COL_ITEM = "item"
        const val WIS_COL_LINK = "link"

        const val MOV_TABLE_NAME = "Movie"
        const val MOV_COL_ID = "_id"
        const val MOV_COL_WDATE = "wdate"
        const val MOV_COL_DATE = "date"
        const val MOV_COL_CONTENT = "content"
        const val MOV_COL_TITLE = "title"
        const val MOV_COL_SCORE = "score"
        const val MOV_COL_POSTERPIC = "posterpic"
        const val MOV_COL_COLOR = "color"
        const val MOV_COL_LOCK = "lock"
        const val MOV_COL_BKMR = "bkmr"
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
                        "$TODL_COL_BKMR Integer," +
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
                        "$REC_COL_BKMR Integer," +
                        "$REC_COL_COLOR Integer)"
        db?.execSQL(createTable)

        createTable =
                "CREATE TABLE $MEM_TABLE_NAME" +
                        "($MEM_COL_ID Integer PRIMARY KEY," +
                        "$MEM_COL_WDATE Integer," +
                        "$MEM_COL_CONTENT TEXT," +
                        "$MEM_COL_LOCK Integer," +
                        "$MEM_COL_BKMR Integer," +
                        "$MEM_COL_COLOR Integer)"
        db?.execSQL(createTable)

        createTable =
                "CREATE TABLE $WEE_TABLE_NAME" +
                        "($WEE_COL_ID Integer PRIMARY KEY," +
                        "$WEE_COL_WDATE Integer," +
                        "$WEE_COL_DATE TEXT," +
                        "$WEE_COL_LOCK Integer," +
                        "$WEE_COL_BKMR Integer," +
                        "$WEE_COL_COLOR Integer)"
        db?.execSQL(createTable)

        createTable =
                "CREATE TABLE $DIA_TABLE_NAME" +
                        "($DIA_COL_ID Integer PRIMARY KEY," +
                        "$DIA_COL_DID Integer," +
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
                        "$WISL_COL_BKMR Integer," +
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
                        "$MOV_COL_BKMR Integer," +
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