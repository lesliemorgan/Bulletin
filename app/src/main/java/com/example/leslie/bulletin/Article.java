package com.example.leslie.bulletin;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.io.InputStream;

/**
 * Created by leslie on 4/11/16.
 */

public class Article {

    String title;
    String pubDate;
    String description;
    URL url;
    URL articleURL;
    private Bitmap imageDrawable;

    public Article (String t, String pd, String d, URL url, URL aURL){
        this.title = t;
        this.pubDate = pd;
        this.description = d;
        this.url = url;
        this.articleURL = aURL;
    }
    public String getTitle(){
        return title;
    }

    public String getPubDate(){
        return pubDate;
    }

    public String getDescription(){
        return description;
    }

    public URL getUrl() {
        return url;
    }

    public Bitmap getImage() {
        try {
            DownloadImagesTask task = new DownloadImagesTask();
            return task.execute(url).get();

        }
        catch(Exception e){
            System.out.println(e.fillInStackTrace());
            return null;
        }
    }

    public class DownloadImagesTask extends AsyncTask<URL, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(URL... urls) {
            return download_Image(urls[0]);
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            imageDrawable = result;
        }


        private Bitmap download_Image(URL url) {
            Bitmap image = null;
            try {
                URLConnection conn = url.openConnection();
                conn.connect();
                InputStream is = conn.getInputStream();
                image = BitmapFactory.decodeStream(is);
                is.close();
            } catch (IOException e) {
                Log.e("Hub", "Error getting the image from server : " + e.getMessage().toString());
            }
            return image;
        }


    }

}


