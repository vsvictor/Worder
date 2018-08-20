package com.education.worder.data;

import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Words{
    private static final String TAG = Words.class.getSimpleName();

    public interface OnWords{
        void onLoaded(Words words);
        void onInserted();
        void onDeleted();
        void onUpdated();
    }

    private Context context;
    private DBWords helper;
    private ArrayList<Word> data;
    private Handler handler;
    private OnWords onWords;


    public Words(Context context, Handler handler, ArrayList<Word> array){
        this.context = context;
        helper = new DBWords(this.context);
        data = array;
        this.handler = handler;
    }

    public Words(Context context, Handler handler){
        this(context, handler, new ArrayList<Word>());
    }

    public ArrayList<Word> getSelected(){
        ArrayList<Word> selected = new ArrayList<Word>();
        for(Word w : getData()){
            if(w.isSelected()) {
                selected.add(w);
            }
        }
        return selected;
    }

    public void loadAll(){
        handler.post(new Runnable() {
            @Override
            public void run() {
                Cursor cursor = helper.loaAll();
                boolean notEmpty = cursor.moveToFirst();
                if(notEmpty) {
                    while (cursor.moveToNext()) {
                        long id = cursor.getLong(cursor.getColumnIndexOrThrow(DBWords.DBEntry.ID));
                        String word = cursor.getString(cursor.getColumnIndexOrThrow(DBWords.DBEntry.WORD));
                        String translate = cursor.getString(cursor.getColumnIndexOrThrow(DBWords.DBEntry.TRANSLATE));
                        int i = cursor.getInt(cursor.getColumnIndexOrThrow(DBWords.DBEntry.SELECTED));
                        Word w = new Word(id,word,translate, (i==1));
                        data.add(w);
                    }
                    if(onWords != null) onWords.onLoaded(Words.this);
                }
            }
        });
    }

    public void addWord(final Word word){
        handler.post(new Runnable() {
            @Override
            public void run() {
                long id = helper.insert(word.getWord(), word.getTranslate());
                if(id >= 0){
                    word.setID(id);
                    data.add(word);
                    if(onWords != null) onWords.onInserted();
                }
            }
        });
    }

    public void deleteWord(final Word word){
        handler.post(new Runnable() {
            @Override
            public void run() {
                int count = helper.delete(word.getID());
                if(count > 0){
                    data.remove(word);
                    if(onWords != null) onWords.onDeleted();
                }
            }
        });
    }
    public void updateWord(final Word word){
        handler.post(new Runnable() {
            @Override
            public void run() {
                int count = helper.update(word.getID(), word.getWord(), word.getTranslate(), word.isSelected());
                if(count > 0){
                    Word w = find(word);
                    w.setWord(word.getWord());
                    w.setTranslate(word.getTranslate());
                    w.setSelected(word.isSelected());
                    if(onWords != null) onWords.onUpdated();
                }
            }
        });
    }

    public ArrayList<Word> getData() {
        return data;
    }

    public Word find(Word word){
        for(Word w : data){
            if(w.getID() == word.getID()){
                return w;
            }
        }
        return null;
    }

    public void add(Word word){
        data.add(word);
    }
    public boolean remove(Word word){
        return data.remove(word);
    }
    public void setOnWordsListener(OnWords listener){
        this.onWords = listener;
    }
}
