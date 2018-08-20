package com.education.worder.data;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Type;

public class Word{

    private static final String TAG = Word.class.getSimpleName();

    @SerializedName("id")
    private long id;
    @SerializedName("word")
    private String word;
    @SerializedName("translate")
    private String translate;
    @SerializedName("selected")
    private boolean selected;

    public Word(long id, String word, String translate, boolean selected) {
        this.id = id;
        this.word = word;
        this.translate = translate;
        this.selected = selected;
    }
    public Word(long id, String word, String translate) {
        this(id,word,translate, false);
    }

    public Word(String word, String translate){
        this(-1, word, translate);
    }

    public Word(){
        this(-1,"", "");
    }

    public long getID() {
        return id;
    }

    public void setID(long id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getTranslate() {
        return translate;
    }

    public void setTranslate(String translate) {
        this.translate = translate;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
