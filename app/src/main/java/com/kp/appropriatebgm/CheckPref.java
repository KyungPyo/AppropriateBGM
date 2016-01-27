package com.kp.appropriatebgm;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by KP on 2015-08-19.
 */
public class CheckPref {

    private SharedPreferences settingPref = null;

    public CheckPref(AppCompatActivity activity){
        // ���� �����۷��� ��ü�� ���´�.
        settingPref = activity.getSharedPreferences("AppSetting", Context.MODE_PRIVATE);
    }

    public void setFirstExcute(){   // ���ʽ��� �� ȣ��
        SharedPreferences.Editor prefEditor = settingPref.edit();
        prefEditor.putBoolean("FirstExcute", false);
        prefEditor.apply();
    }

    public boolean getFirstExcute(){    // 최초?�행 ?��? 받아?�기(기본�?true)
        return settingPref.getBoolean("FirstExcute", true);
    }

}
