package com.example.taskapp;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class Prefs {
    private SharedPreferences preferences;

   public Prefs(Activity activity){
       preferences = activity.getPreferences(Context.MODE_PRIVATE);
       //preferences = activity.getPreferences(Context.MODE_APPEND);
   }

   public void isShown(boolean value){
       preferences.edit().putBoolean("isShown",value).apply();
   }

   public boolean isShown(){
       return preferences.getBoolean("isShown",false);
   }

   public void clear(){
       preferences.edit().clear().apply();
   }

    public void saveName(String name) {
        preferences.edit().putString("name",name).apply();
    }

    public String getName() {
        return preferences.getString("name","");
    }
}
