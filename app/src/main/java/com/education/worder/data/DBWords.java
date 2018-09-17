package com.education.worder.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
;import java.util.Locale;

public class DBWords extends SQLiteOpenHelper {

    private static final String TAG = DBWords.class.getSimpleName();
    private static final String NAME_DB = "worder.db";
    private static final String MAIN_TABLE = "main";
    private static final String DICT_TABLE = "dictionaries";
    private static final int VERSION = 4;


    public DBWords(Context context) {
        super(context, NAME_DB, null, VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_DICT_CREATE);
        db.execSQL(SQL_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE);
        db.execSQL(SQL_DICT_DELETE);
        onCreate(db);
    }

    /*
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion){
        onUpgrade(db, oldVersion, newVersion);
    }
    */

    public Cursor loadWordsFromDictionary(long dict){
        SQLiteDatabase db = getReadableDatabase();
        String commad = DBEntryWord.DICTIONARY + "=?";
        String[] args = {String.valueOf(dict)};
        Cursor cursor = db.query(MAIN_TABLE,null,commad,args,null,null,null);
        return cursor;
    }

    public long insert(String name, Locale langFrom, Locale langTo){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DBDictEntry.NAME, name);
        cv.put(DBDictEntry.LANG_FROM, langFrom.toString());
        cv.put(DBDictEntry.LANG_TO, langTo.toString());
        long id = db.insertOrThrow(DICT_TABLE,null, cv);
        return id;
    }

    public Cursor loadDictionaries(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(DICT_TABLE,null,null,null,null,null,null);
        return cursor;
    }

    public long insert(long dict, String word, String translate){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DBEntryWord.DICTIONARY, dict);
        cv.put(DBEntryWord.WORD, word);
        cv.put(DBEntryWord.TRANSLATE, translate);
        long result = db.insertOrThrow(MAIN_TABLE,null, cv);
        return result;
    }

    public int delete(long id){
        String command = DBEntryWord.ID + " = ?";
        String[] args = {String.valueOf(id)};
        SQLiteDatabase db = getWritableDatabase();
        int result = db.delete(MAIN_TABLE,command, args);
        return result;
    }

    public int update(long id, String word, String translate, boolean selected){
        String command = DBEntryWord.ID + " = ?";
        String[] args = {String.valueOf(id)};
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DBEntryWord.WORD,word);
        cv.put(DBEntryWord.TRANSLATE, translate);
        cv.put(DBEntryWord.SELECTED, selected?1:0);
        int result = db.update(MAIN_TABLE,cv,command, args);
        return result;
    }

    private static final String SQL_DICT_CREATE =
            "CREATE TABLE " + DICT_TABLE + " ("+
            DBDictEntry.ID + " INTEGER PRIMARY KEY, "+
            DBDictEntry.NAME + " TEXT, "+
            DBDictEntry.LANG_FROM + " TEXT, "+
            DBDictEntry.LANG_TO + " TEXT"+");";

    private static final String SQL_CREATE =
            "CREATE TABLE " + MAIN_TABLE + " ("+
            DBEntryWord.ID  + " INTEGER PRIMARY KEY, "+
            DBEntryWord.DICTIONARY + " INTEGER, "+
            DBEntryWord.WORD + " TEXT, "+
            DBEntryWord.TRANSLATE + " TEXT, "+
            DBEntryWord.SELECTED + " INTEGER"+");";

    private static final String SQL_DICT_DELETE =
            "DROP TABLE IF EXISTS " + DICT_TABLE;

    private static final String SQL_DELETE =
            "DROP TABLE IF EXISTS " + MAIN_TABLE;

    public static class DBEntryWord{
        public static final String ID = "_id";
        public static final String DICTIONARY = "dict";
        public static final String WORD = "word";
        public static final String TRANSLATE = "translate";
        public static final String SELECTED = "selected";
    }
    public static class DBDictEntry{
        public static final String ID = "_id";
        public static final String NAME = "name";
        public static final String LANG_FROM = "lang_from";
        public static final String LANG_TO = "lang_to";
    }
}
