package com.education.worder.data;

import java.util.Locale;

public class Dictionary {
    private long id;
    private String name;
    private Locale langFrom;
    private Locale langTo;

    public Dictionary(long id, String name, Locale langFrom, Locale langTo){
        this.id = id;
        this.name = name;
        this.langFrom = langFrom;
        this.langTo = langTo;
    }

    public long getID() {
        return id;
    }

    public void setID(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Locale getLangFrom() {
        return langFrom;
    }

    public void setLangFrom(Locale langFrom) {
        this.langFrom = langFrom;
    }

    public Locale getLangTo() {
        return langTo;
    }

    public void setLangTo(Locale langTo) {
        this.langTo = langTo;
    }
}
