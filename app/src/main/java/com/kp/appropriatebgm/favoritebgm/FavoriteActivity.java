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
    Boolean mBoolean = false;

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
        onOffSwitch.setChecked(mPref.getLockerOnOff());
        onOffSwitch.setOnCheckedChangeListener(checkedChangeListener);

        setSupportActionBar(toolbar);           // Toolbar를 액션바로 사용한다
        getSupportActionBar().setTitle(null);   // 액션바에 타이틀 제거

    }

    //체크 변경 리스너 부분
    private Switch.OnCheckedChangeListener checkedChangeListener = new Switch.OnCheckedChangeListener(){
        // Method : 체크 변경
        // Return Value : void
        // Parameter : buttonView(변경 버튼), isChecked(체크유무)
        // Use : 체크유무에 따라 잠금화면 서비스로 인텐트를 넘겨 서비스 시작 및 종료하며 setChecked에 의해 리스너로 들어가 값이 바뀔 경우
        //       mBoolean값을 false로 하여 공유변수에 값이 저장되지 않도록 하여 값 유지
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            Log.e("favorite", " listener gogo");
            if (isChecked) {
                Log.e("favorite", " ischecked true");
                Intent intent = new Intent(FavoriteActivity.this, LockScreenService.class);
                startService(intent);
            } else {
                Log.e("favorite", " ischecked false");
                Intent intent = new Intent(FavoriteActivity.this, LockScreenService.class);
                stopService(intent);
            }
            if(mBoolean) {
                mPref.setLockerOnOff();
            }
            Log.e("setlocker", mPref.getLockerOnOff() + "");
        }
    };

    // Method : Restart (재시작)
    // Return Value : void
    // Parameter : void
    // Use : mBoolean값을 false로 하여 setChecked시에 리스너의 공유변수 저장이 되지 않도록 하여 뒤엉킴을 방지함
    @Override
    protected void onRestart() {
        Log.e("onrestart", "called");
        mBoolean = false;
        onOffSwitch.setChecked(mPref.getLockerOnOff());
        super.onRestart();
    }

    // Method : Resume (일시정지 후 시작)
    // Return Value : void
    // Parameter : void
    // Use : mBoolean값을 true로 하여 리스너가 문제없이 돌아가도록 한다.
    @Override
    protected void onResume() {
        Log.e("onresume", "called");
        mBoolean = true;
        super.onResume();
    }

    // Method : 초기설정
    // Return Value : void
    // Parameter : void
    // Use : View를 객체와 연결, listView 설정
    private void init(){

        toolbar=(Toolbar)findViewById(R.id.favorite_toolbar);
        onOffSwitch=(Switch)findViewById(R.id.favorite_switch_onOffSwitch);

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
