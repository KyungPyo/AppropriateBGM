package com.kp.appropriatebgm.favoritebgm;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;

import com.kp.appropriatebgm.CheckPref;
import com.kp.appropriatebgm.DBController.DBManager;
import com.kp.appropriatebgm.DBController.Favorite;
import com.kp.appropriatebgm.LockScreen.LockScreenService;
import com.kp.appropriatebgm.R;

import java.util.ArrayList;


public class FavoriteActivity extends AppCompatActivity {

    ListView favoriteList;
    Toolbar toolbar;
    Switch onOffSwitch;

    FavoriteListAdapter adapter;
    ArrayList<Favorite> favoriteArrayList;
    DBManager dbManager=DBManager.getInstance(this);//DB

    private CheckPref mPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        mPref = new CheckPref(this);        // 공유 프레퍼런스 객체

        init();

        setSupportActionBar(toolbar);           // Toolbar를 액션바로 사용한다
        getSupportActionBar().setTitle(null);   // 액션바에 타이틀 제거

    }

    // Method : 초기설정
    // Return Value : void
    // Parameter : void
    // Use : View를 객체와 연결, listView 설정
    private void init(){

        toolbar=(Toolbar)findViewById(R.id.favorite_toolbar);
        onOffSwitch=(Switch)findViewById(R.id.favorite_switch_onOffSwitch);
        onOffSwitch.setChecked(mPref.getLockerOnOff());
        onOffSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    Intent intent = new Intent(FavoriteActivity.this, LockScreenService.class);
                    startService(intent);
                }
                else
                {
                    Intent intent = new Intent(FavoriteActivity.this, LockScreenService.class);
                    stopService(intent);
                }
                mPref.setLockerOnOff();
            }
        });

        favoriteArrayList=new ArrayList<Favorite>();

        //listView 설정
        favoriteArrayList=dbManager.getFavoriteList();//DB

        favoriteList=(ListView)findViewById(R.id.favorite_listView_favoriteList);
        adapter=new FavoriteListAdapter(this, favoriteArrayList);
        favoriteList.setAdapter(adapter);
        //즐겨찾기 List중 아이템클릭시 Method
        favoriteList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), SelectBgmActivity.class);
                //현재 클릭된 position값을 넘겨준다.
                intent.putExtra("position", position);

                startActivityForResult(intent, 0);
            }
        });
        adapter.notifyDataSetChanged();
    }

    // Method : 액티비티 응답 받기
    // Return Value : void
    // Parameter : requestCode-요청 코드, resultCode-응답받는 코드, data-응답받는 intent
    // Use : 각각의 result 코드에 맞춰 액티비티 응답 처리
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==RESULT_OK){//result OK
            favoriteArrayList.clear();
            favoriteArrayList.addAll(dbManager.getFavoriteList());
            adapter.notifyDataSetChanged();
        }else if(resultCode==RESULT_CANCELED){//result Canceled
            //Toast.makeText(getApplicationContext(), "즐겨찾기 추가를 취소하셨습니다. ", Toast.LENGTH_SHORT).show();
        }
    }
}
