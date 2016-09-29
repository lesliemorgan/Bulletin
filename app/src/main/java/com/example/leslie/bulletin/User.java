/**
 * Created by leslie on 3/21/16.
 */
package com.example.leslie.bulletin;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class User {

    public String name;
    public String email;
    public String pwd;
    public ArrayList<Preference> preferences;

    public User(String name, String email,String pwd, ArrayList<Preference> pr){
        this.name = name;
        this.email = email;
        this.pwd = pwd;
        this.preferences = pr;

    }
}
