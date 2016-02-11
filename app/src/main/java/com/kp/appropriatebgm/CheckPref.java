package com.kp.appropriatebgm;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

public class CheckPref {

    private SharedPreferences settingPref = null;

    public CheckPref(AppCompatActivity activity){

        settingPref = activity.getSharedPreferences("AppSetting", Context.MODE_PRIVATE);
    }

    public void setFirstExcute(){
        SharedPreferences.Editor prefEditor = settingPref.edit();
        prefEditor.putBoolean("FirstExcute", false);
        prefEditor.apply();
    }

    public boolean getFirstExcute(){
        return settingPref.getBoolean("FirstExcute", true);
    }

    public void setLockerOnOff(){
        SharedPreferences.Editor prefEditor = settingPref.edit();
        if(settingPref.getBoolean("LockerOn", false)) {
            prefEditor.putBoolean("LockerOn", false);
        } else {
            prefEditor.putBoolean("LockerOn", true);
        }
        prefEditor.apply();
    }

    public boolean getLockerOnOff(){
        return settingPref.getBoolean("LockerOn", false);
    }
}
