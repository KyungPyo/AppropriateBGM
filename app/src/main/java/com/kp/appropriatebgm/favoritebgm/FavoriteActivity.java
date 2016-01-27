package com.kp.appropriatebgm.favoritebgm;
import com.kp.appropriatebgm.R;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;


public class FavoriteActivity extends AppCompatActivity {

    static int FAVORITE_SIZE=9;

    ListView favoriteList;
    Toolbar toolbar;
    Switch onOffSwitch;

    BGMListAdapter adapter;
    ArrayList<Music> favoriteMusicList;
    DBManager dbManager=DBManager.getInstance(this);//DB
    Cursor bgm;


    String [] favorite_columns={"favorite_id","bgm_id"};
    String [] columns={"bgm_id","bgm_name","bgm_path"};



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("만들어져따", "내ㅐㅐㅐ가 온크리에이트다ㅏㅏㅏㅏ");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_favorite);

        init();

        setSupportActionBar(toolbar);           // Toolbar를 액션바로 사용한다
        getSupportActionBar().setTitle(null);   // 액션바에 타이틀 제거
       // setCustomActionbar();


    }


    // Method : 목록 Making
    // Return Value : void
    // Parameter : void
    // Use : favorite Array List Setting
    private void loadFavoriteList(){
        Music tmp=new Music("","","");
        //미리 비어있는 list를 만들고
        for(int i=0;i<FAVORITE_SIZE;i++){
            favoriteMusicList.add(tmp);
        }
        bgm=dbManager.select("Favorite",favorite_columns); //DB

        //만약 즐겨찾기 db에 데이터가 있다면
        if(bgm.getCount()!=0){
            while(bgm.moveToNext()){
                //favorite_id 즉 저장된 position의 현재값을 삭제하고
                favoriteMusicList.remove(bgm.getInt(0));
                Music m=dbManager.selectByMusicId("BGMList", columns, bgm.getInt(1));//DB
                //해당 position에 db에 있는 값을 저장해 ListView에서 그 포지션에 보여지도록 한다.
                favoriteMusicList.add(bgm.getInt(0),m);
            }
        }

    }

    // Method : 초기설정
    // Return Value : void
    // Parameter : void
    // Use : View를 객체와 연결, listView 설정
    private void init(){

        toolbar=(Toolbar)findViewById(R.id.favorite_toolbar);
        onOffSwitch=(Switch)findViewById(R.id.onOffSwitch);
        onOffSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Toast.makeText(getApplicationContext(), "룰루라라라랄 ", Toast.LENGTH_SHORT).show();
            }
        });

        //listView 설정
        favoriteMusicList=new ArrayList<Music>();
        favoriteList=(ListView)findViewById(R.id.favorite_list);
        adapter=new BGMListAdapter(this,favoriteMusicList);
        favoriteList.setAdapter(adapter);
        //즐겨찾기 List중 아이템클릭시 Method
        favoriteList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), SelectBgmActivity.class);
                Log.d("ActivityA", position + "");
                //현재 클릭된 position값을 넘겨준다.
                intent.putExtra("position", position);

                startActivityForResult(intent, 0);
            }
        });

        loadFavoriteList();
        adapter.notifyDataSetChanged();
    }

    // Method : 액티비티 응답 받기
    // Return Value : void
    // Parameter : requestCode-요청 코드, resultCode-응답받는 코드, data-응답받는 intent
    // Use : 각각의 result 코드에 맞춰 액티비티 응답 처리
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==RESULT_OK){//result OK
            Music tmp=(Music)data.getSerializableExtra("selectedMusic");
            int tmpPosition=data.getIntExtra("position",0);
            favoriteMusicList.remove(tmpPosition);
            favoriteMusicList.add(tmpPosition,tmp);
            adapter.notifyDataSetChanged();
        }else if(resultCode==RESULT_CANCELED){//result Canceled
            Toast.makeText(getApplicationContext(), "즐겨찾기 추가를 취소하셨습니다. ", Toast.LENGTH_SHORT).show();
        }
    }
}
