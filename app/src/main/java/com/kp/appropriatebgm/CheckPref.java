package com.kp.appropriatebgm;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

public class CheckPref {

    private SharedPreferences settingPref = null;

    public CheckPref(AppCompatActivity activity){

        settingPref = activity.getSharedPreferences("AppSetting", Context.MODE_PRIVATE);
    }

    // Method : 최초실행여부 저장
    // Return Value : void
    // Parameter : void
    // Use : 최초실행 여부를 '최초실행이 아님(false)' 로 저장한다.
    public void setFirstExcute(){
        SharedPreferences.Editor prefEditor = settingPref.edit();
        prefEditor.putBoolean("FirstExcute", false);
        prefEditor.apply();
    }

    // Method : 최초실행여부 받아오기
    // Return Value : boolean(첫실행이면 true, 아니면 false)
    // Parameter : void
    // Use : 최초실행여부 저장여부 받아오기. 기본 설정값은 true.
    public boolean getFirstExcute(){
        return settingPref.getBoolean("FirstExcute", true);
    }

    // Method : 잠금화면기능 설정여부 변경
    // Return Value : void
    // Parameter : void
    // Use : 잠금화면기능이 사용 설정여부를 변경한다. true->false, false->true 로 변경한다.
    public void setLockerOnOff(){
        SharedPreferences.Editor prefEditor = settingPref.edit();
        if(settingPref.getBoolean("LockerOn", false)) {
            prefEditor.putBoolean("LockerOn", false);
        } else {
            prefEditor.putBoolean("LockerOn", true);
        }
        prefEditor.apply();
    }

    // Method : 잠금화면기능 설정여부 받아오기
    // Return Value : boolean(사용이면 true, 아니면 false)
    // Parameter : void
    // Use : 잠금화면기능 사용여부를 받아온다. 기본설정값은 false.
    public boolean getLockerOnOff(){
        return settingPref.getBoolean("LockerOn", false);
    }
}
