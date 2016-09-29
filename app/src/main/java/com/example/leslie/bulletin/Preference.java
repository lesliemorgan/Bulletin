package com.example.leslie.bulletin;

import android.util.Log;

import java.net.URL;

public class Preference{
    String title;
    URL url;
    String stringURL;
    public Preference (String title, String srcURL){
        this.title = title;
        this.stringURL = srcURL;
        try {
            this.url = new URL(srcURL);
        }
        catch (Exception e){
            Log.e("oops","Something is wrong");
        }
    }

    public URL getURL(){
        return this.url;
    }

    public String getTitle(){
        return title;
    }
}

