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
        // 공유 프레퍼런스 객체를 얻어온다.
        settingPref = activity.getSharedPreferences("AppSetting", Context.MODE_PRIVATE);
    }

    public void setFirstExcute(){   // 최초실행 후 호출
        SharedPreferences.Editor prefEditor = settingPref.edit();
        prefEditor.putBoolean("FirstExcute", false);
        prefEditor.apply();
    }

    public boolean getFirstExcute(){    // 理쒖큹?ㅽ뻾 ?щ? 諛쏆븘?ㅺ린(湲곕낯媛?true)
        return settingPref.getBoolean("FirstExcute", true);
    }

}
