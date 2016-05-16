package com.kp.appropriatebgm.Setting;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.kp.appropriatebgm.CheckPref;
import com.kp.appropriatebgm.DBController.DBManager;
import com.kp.appropriatebgm.DBController.Favorite;
import com.kp.appropriatebgm.LockScreen.LockScreenService;

import java.util.ArrayList;

/**
 * Created by KP on 2016-05-10.
 */
public class PlayerServieceController {

    private Context mContext;
    private DBManager dbManager;
    private ArrayList<Favorite> favoriteList;
    private CheckPref mPref;

    public PlayerServieceController(Context context){
        this.mContext = context;
        dbManager = DBManager.getInstance(mContext.getApplicationContext());
        mPref = new CheckPref(mContext);
    }

    // Method : 잠금플레이어 서비스 실행
    // Return value : 실행성공 true, 아니면 false
    // parameter : void
    // use : 잠금플레이어 서비스를 등록하고, 화면을 껐다가 켜면 맨 앞에 액티비티 나타나게 된다.
    public boolean startLockerPlayerService(){
        if (!mPref.getLockerOnOff()) {
            if (isFavoriteListExist()) {
                Intent intent = new Intent();
                intent.setClass(mContext.getApplicationContext(), LockScreenService.class);
                mContext.startService(intent);
                mPref.setLockerOnOff();
                return true;
            } else {
                Toast.makeText(mContext, "사용하려면 즐겨찾기가 등록되어있어야 합니다.", Toast.LENGTH_LONG).show();
                return false;
            }
        }
        return false;
    }

    // Method : 잠금플레이어 서비스 중지
    // Return value : void
    // parameter : void
    // use : 잠금플레이어 서비스 종료한다.
    public void stopLockerPlayerService(){
        if (mPref.getLockerOnOff()) {
            Intent intent = new Intent();
            intent.setClass(mContext.getApplicationContext(), LockScreenService.class);
            mContext.stopService(intent);
            mPref.setLockerOnOff();
        }
    }

    // Method : 빠른재생 서비스 실행
    // Return value : 실행성공 true, 아니면 false
    // parameter : void
    // use : Notification에 재생툴을 띄운다.
    public boolean startFastPlayerService(){
        if (!mPref.getNotiplayerOnOff()) {
            if (isFavoriteListExist()) {
                Intent intent = new Intent();
                intent.setClass(mContext.getApplicationContext(), NotiPlayer.class);
                mContext.startService(intent);
                mPref.setNotiplayerOnOff();
                return true;
            } else {
                Toast.makeText(mContext, "사용하려면 즐겨찾기가 등록되어있어야 합니다.", Toast.LENGTH_LONG).show();
                return false;
            }
        }
        return false;
    }

    // Method : 빠른재생 서비스 종료
    // Return value : void
    // parameter : void
    // use : 빠른재생 서비스를 종료한다.
    public void stopFastPlayerService(){
        if (mPref.getNotiplayerOnOff()) {
            Intent intent = new Intent();
            intent.setClass(mContext.getApplicationContext(), NotiPlayer.class);
            mContext.stopService(intent);
            mPref.setNotiplayerOnOff();
        }
    }

    // Method : 즐겨찾기 목록 존재확인
    // Return value : 목록이 하나라도 있으면 true, 아니면 false
    // parameter : void
    // use : 사용자가 등록한 즐겨찾기의 전체 목록을 받아와 저장한 후, 하나이상 있는 지 확인하여 리턴한다.
    public boolean isFavoriteListExist()
    {
        favoriteList = dbManager.getFavoriteListNotNull();
        if(favoriteList.size() == 0) {
            return false;
        } else {
            return true;
        }
    }
}
