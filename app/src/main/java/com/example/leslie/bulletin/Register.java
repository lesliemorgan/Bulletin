package com.example.leslie.bulletin;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.firebase.client.Firebase;

import java.util.*;

public class Register extends AppCompatActivity {

    static Firebase db = new Firebase("https://bulletinqlovur.firebaseio.com");

    CheckBox politics;
    CheckBox entertainment;
    CheckBox health;
    CheckBox travel;
    CheckBox world;
    CheckBox us;
    CheckBox science;
    CheckBox tech;
    CheckBox lifestyle;


    public String n;
    public String e;
    public String p;
    public ArrayList<Preference> pr;





    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Firebase.setAndroidContext(this);
        politics = (CheckBox)findViewById(R.id.politics);
        //sports = (CheckBox) findViewById(R.id.sports);
        entertainment = (CheckBox) findViewById(R.id.entertainment);
        health = (CheckBox) findViewById(R.id.health);
        travel = (CheckBox) findViewById(R.id.travel);
        world = (CheckBox) findViewById(R.id.world);
        us = (CheckBox) findViewById(R.id.us);
        science = (CheckBox) findViewById(R.id.science);
        tech = (CheckBox) findViewById(R.id.tech);
        lifestyle = (CheckBox) findViewById(R.id.lifestyle);

    }

    public void openFeed(View view){
        try {
            EditText et = (EditText) findViewById(R.id.name);
            this.n = et.getText().toString();
            EditText em = (EditText) findViewById(R.id.email);
            this.e = em.getText().toString();
            EditText pw = (EditText) findViewById(R.id.pw);
            this.p= pw.getText().toString();
            pr = findBeginningPref();
            User u = new User (n,e,p,pr);



            boolean valid = validate();
            if (valid) {
                Firebase refPt = db.child("bulletinqlovur").child("Users");
                refPt.child(n).setValue(u);
                Intent intent = new Intent (Register.this, Article_feed.class);
                startActivity(intent);

            }
            else{
                new AlertDialog.Builder(Register.this)
                        .setTitle("Whoops!")
                        .setMessage("Make sure all fields are filled out correctly!")
                        .setPositiveButton("Okie Doke", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .show();

            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

  public ArrayList<Preference> findBeginningPref(){
      ArrayList<Preference> preferences = new ArrayList<>();

        if (politics.isChecked()){
            preferences.add(new Preference("Politics","http://rss.cnn.com/rss/cnn_allpolitics.rss"));
        }

    /*    if (sports.isChecked()){
            preferences.add("sports");
        }*/

        if (entertainment.isChecked()){
            preferences.add(new Preference("entertainment","http://rss.cnn.com/rss/cnn_showbiz.rss"));
        }

        if (health.isChecked()){
            preferences.add(new Preference("health","http://rss.cnn.com/rss/cnn_health.rss"));
        }

       if (travel.isChecked()){
            preferences.add(new Preference("travel","http://rss.cnn.com/rss/cnn_travel.rss"));
        }

        if (world.isChecked()){
            preferences.add(new Preference("world","http://rss.cnn.com/rss/cnn_world.rss"));
        }

        if (us.isChecked()){
            preferences.add(new Preference("us","http://rss.cnn.com/rss/cnn_us.rss"));
        }

        if (science.isChecked()){
            preferences.add(new Preference("science",""));
        }

        if (tech.isChecked()){
            preferences.add(new Preference("tech","http://rss.cnn.com/rss/cnn_tech.rss"));
        }

        if (lifestyle.isChecked()){
            preferences.add(new Preference("lifestyle","http://rss.cnn.com/rss/cnn_living.rss"));
        }
      return preferences;

    }


    public boolean validate(){

       if (!(e.contains("@")||p.length()< 8)){
            return false;
        }
        else if(e.equals(null)||p.equals(null)||n.equals(null)){
           return false;
        }

        return true;

    }
}
