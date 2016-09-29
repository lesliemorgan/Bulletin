package com.example.leslie.bulletin;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class Article_feed extends AppCompatActivity {
    ListView list;
    ArticleAdapter adapter;
    ArrayList<Article> a = new ArrayList<>();
    User user;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_feed);

        ReadTask task = new ReadTask();
        task.execute();

        Log.i("curious", "after");

        list = (ListView) findViewById(R.id.listView);


    }

    public void onClick(View v) {
        try {
            TextView urlTV = (TextView) findViewById(R.id.url);
            String item = urlTV.toString();
            Uri url = Uri.parse(item);
            Intent launchBrowser = new Intent(Intent.ACTION_VIEW, url);
            startActivity(launchBrowser);
        } catch (Exception e) {
            Log.e("oops", "something went wrong");
        }
    }

    public class ReadTask extends AsyncTask<Void, Void, ArrayList<Article>> {
        @Override
        protected ArrayList<Article> doInBackground(Void... params) {
            String downloadURL = "http://rss.cnn.com/rss/cnn_topstories.rss";

            ArrayList<Article> results = new ArrayList<>();

            try {
                URL url = new URL(downloadURL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                InputStream inputStream = connection.getInputStream();
                results = processXML(inputStream);

                inputStream.close();
                connection.disconnect();

            } catch (Exception e) {
                Log.i("error", "oops");
            }

            return results;
        }


        @Override
        protected void onPostExecute(ArrayList<Article> articles) {
            super.onPostExecute(articles);
            a = articles;
            adapter = new ArticleAdapter(Article_feed.this, a);
            list.setAdapter(adapter);
            Log.i("curious", "in there");
        }

        public ArrayList<Article> processXML(InputStream inputStream) throws Exception {
            ArrayList<Article> results = new ArrayList<>();

            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document xmlDocument = documentBuilder.parse(inputStream);
            Element rootElement = xmlDocument.getDocumentElement();

            NodeList itemsList = rootElement.getElementsByTagName("item");
            NodeList itemChildren = null;
            Node currentItem = null;
            Node currentChild = null;
            Node currentAttribute = null;

            NamedNodeMap mediaThumbnailAttributes = null;

            for (int i = 0; i < itemsList.getLength(); i++) {
                String title = "";
                String pubDate = "";
                String description = "";
                URL url = null;
                URL aURL = null;

                currentItem = itemsList.item(i);
                itemChildren = currentItem.getChildNodes();
                for (int j = 0; j < itemChildren.getLength(); j++) {
                    currentChild = itemChildren.item(j);
                    if (currentChild.getNodeName().equalsIgnoreCase("title")) {
                        title = currentChild.getTextContent();
                    }
                    if (currentChild.getNodeName().equalsIgnoreCase("pubDate")) {
                        pubDate = currentChild.getTextContent();
                    }
                    if (currentChild.getNodeName().equalsIgnoreCase("Description")) {
                        description = currentChild.getTextContent();
                    }
                    if (currentChild.getNodeName().equalsIgnoreCase("media:thumbnail")) {
                        mediaThumbnailAttributes = currentChild.getAttributes();
                        for (int k = 0; k < mediaThumbnailAttributes.getLength(); k++) {
                            currentAttribute = mediaThumbnailAttributes.item(k);
                            if (currentAttribute.getNodeName().equalsIgnoreCase("url")) {
                                url = new URL(currentAttribute.getTextContent());

                            }
                        }
                    }

                    if (currentChild.getNodeName().equalsIgnoreCase("guid")) {
                        String articleURL = currentChild.getTextContent();

                        aURL = new URL(articleURL);
                    }


                }

                Article art = new Article(title, pubDate, description, url, aURL);
                results.add(art);

            }
            return results;
        }

    }

    class ArticleAdapter extends ArrayAdapter<Article> {
        Context context;
        ArrayList<Article> articles;

        ArticleAdapter(Context c, ArrayList<Article> articles) {
            super(c, 0, articles);
            this.context = c;
            this.articles = articles;
        }

        public void setArticles(ArrayList<Article> articles) {
            this.articles = articles;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view;

            if (convertView == null) {
                view = inflater.inflate(R.layout.articleview, parent, false);
            } else {
                view = convertView;
            }

            return bindData(view, position);
        }

        public View bindData(View view, int position) {
            if (articles.get(position) == null) {
                return view;
            }
            Article item = articles.get(position);

            TextView titleTV = (TextView) view.findViewById(R.id.title);
            titleTV.setText(item.getTitle());

            TextView descTV = (TextView) view.findViewById(R.id.desc);
            descTV.setText(Html.fromHtml(item.getDescription()));

            TextView dateTV = (TextView) view.findViewById(R.id.pubDateT);
            dateTV.setText(item.getPubDate());

            ImageView imgIV = (ImageView) view.findViewById(R.id.image);
            imgIV.setImageBitmap(item.getImage());

            TextView url = (TextView) view.findViewById(R.id.url);
            url.setText(item.articleURL.toString());

            return view;
        }


    }

}
/*
package com.example.leslie.bulletin;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.io.InputStream;
import org.w3c.dom.*;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.provider.DocumentsContract;
import android.support.annotation.RequiresPermission;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.*;
import android.provider.Settings.Global;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.graphics.drawable.*;

import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class Article_feed extends AppCompatActivity {

    ListView list;
    ArticleAdapter adapter;
    ArrayList<Article> a = new ArrayList<>();

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_feed);

        ReadTask task = new ReadTask();
        task.execute();
        Log.i("curious", "after");

        list = (ListView) findViewById(R.id.listView);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_article_feed, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    */
/**
     * A placeholder fragment containing a simple view.
     *//*

    public static class PlaceholderFragment extends Fragment {


        private final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            setRetainInstance(true);
        }

        public PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_cnn_feed, container, false);


            return rootView;
        }


    }


    public class ReadTask extends AsyncTask<Void, Void, ArrayList<Article>> {
        ArrayList<Article> results;
        String[] titleArray;
        String[] prevArray ;
        Drawable[] imgArray;
        @Override
        protected ArrayList<Article> doInBackground(Void... params) {
            String downloadURL = "http://rss.cnn.com/rss/cnn_topstories.rss";
            ArrayList<Article> results = new ArrayList<>();
            try {
                URL url = new URL(downloadURL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                InputStream inputStream = connection.getInputStream();
                results = processXML(inputStream);


            } catch (Exception e) {
                Log.i("error", "oops");
            }
            return results;
        }
        @Override
        protected void onPostExecute(ArrayList<Article> articles) {
            super.onPostExecute(articles);
            a = articles;
            adapter = new ArticleAdapter(Article_feed.this, a);
            list.setAdapter(adapter);
            Log.i("curious", "in there");
        }


            */
/*titleArray = new String[results.size()];
            prevArray = new String[results.size()];
            imgArray = new Drawable[results.size()];

            for (int i = 0; i < results.size(); i++) {
                Article A = results.get(i);
                titleArray[i] = A.getTitle();
            }

            try {
                for (int j = 0; j < results.size(); j++) {
                    Article A = results.get(j);
                    imgArray[j] = A.getImage();
                    j++;
                }
            } catch (Exception e) {
                Log.e("error", "Image not found");
            }

            for (int k = 0; k < results.size(); k++) {
                Article A = results.get(k);
                String descFull = A.getDescription();
                String desc = descFull.substring(0, 250);
                prevArray[k] = desc;
                k++;
            }
            ArticleAdapter articleAdapter = new ArticleAdapter(Article_feed.this, titleArray, imgArray, prevArray);
            list.setAdapter(articleAdapter);
            Log.i("works", articles + "");*//*

        }

    public ArrayList<Article> processXML(InputStream inputStream) throws Exception {
        ArrayList<Article> results = new ArrayList<>();

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document xmlDocument = documentBuilder.parse(inputStream);
        Element rootElement = xmlDocument.getDocumentElement();

        NodeList itemsList = rootElement.getElementsByTagName("item");
        NodeList itemChildren = null;
        Node currentItem = null;
        Node currentChild = null;
        Node currentAttribute = null;

        NamedNodeMap mediaThumbnailAttributes = null;

        for (int i = 0; i < itemsList.getLength(); i++) {
            String title = "";
            String pubDate = "";
            String description = "";
            URL url = null;

            currentItem = itemsList.item(i);
            itemChildren = currentItem.getChildNodes();
            for (int j = 0; j < itemChildren.getLength(); j++) {
                currentChild = itemChildren.item(j);
                if (currentChild.getNodeName().equalsIgnoreCase("title")) {
                    title = currentChild.getTextContent();
                }
                if (currentChild.getNodeName().equalsIgnoreCase("pubDate")) {
                    pubDate = currentChild.getTextContent();
                }
                if (currentChild.getNodeName().equalsIgnoreCase("Description")) {
                    description = currentChild.getTextContent();
                }
                if (currentChild.getNodeName().equalsIgnoreCase("media:thumbnail")) {
                    mediaThumbnailAttributes = currentChild.getAttributes();
                    for (int k = 0; k < mediaThumbnailAttributes.getLength(); k++) {
                        currentAttribute = mediaThumbnailAttributes.item(k);
                        if (currentAttribute.getNodeName().equalsIgnoreCase("url")) {
                            url = new URL(currentAttribute.getTextContent());

                        }
                    }
                }


            }

            Article art = new Article(title, pubDate, description, url);
            results.add(art);

        }
        return results;
    }

}




    class ArticleAdapter extends ArrayAdapter<Article> {
        Context context;
        ArrayList<Article> articles;

        ArticleAdapter(Context c, ArrayList<Article> articles) {
            super(c, 0,articles);
            this.context = c;
            this.articles = articles;
        }

        public void setArticles(ArrayList<Article> articles) {
            this.articles = articles;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view;

            if (convertView == null) {
                view = inflater.inflate(R.layout.articleview, parent, false);
            } else {
                view = convertView;
            }

            return bindData(view, position);
        }

        public View bindData(View view, int position) {
            if (articles.get(position) == null) {
                return view;
            }
            Article item = articles.get(position);

            TextView titleTV = (TextView) view.findViewById(R.id.title);
            titleTV.setText(item.getTitle());

            TextView descTV = (TextView) view.findViewById(R.id.desc);
            descTV.setText(Html.fromHtml(item.getDescription()));

            ImageView imgIV = (ImageView) view.findViewById(R.id.image);
            imgIV.setImageDrawable(item.getImage());

            return view;
        }
    }





*/
