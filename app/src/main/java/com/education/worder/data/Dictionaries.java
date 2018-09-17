package com.education.worder.data;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.Locale;

public class Dictionaries {
    private Context context;
    private ArrayList<Dictionary> list;

    public Dictionaries(Context context){
        this.context = context;
        this.list = new ArrayList<Dictionary>();
    }

    public ArrayList<Dictionary> load() {
        DBWords helper = new DBWords(context);
        Cursor cursor = helper.loadDictionaries();
        if(cursor.moveToFirst()){
            do{
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(DBWords.DBDictEntry.ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DBWords.DBDictEntry.NAME));
                Locale langFrom = new Locale(cursor.getString(cursor.getColumnIndexOrThrow(DBWords.DBDictEntry.LANG_FROM)));
                Locale langTo = new Locale(cursor.getString(cursor.getColumnIndexOrThrow(DBWords.DBDictEntry.LANG_TO)));
                list.add(new Dictionary(id, name, langFrom, langTo));
            }while (cursor.moveToNext());
        }
        return list;
    }
}
