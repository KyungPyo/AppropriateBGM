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

    FavoriteListAdapter adapter;
    ArrayList<Music> favoriteMusicList;
    DBManager dbManager=DBManager.getInstance(this);
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


    //***즐겨찾기 목록을 Load 해주는 Method
    private void loadFavoriteList(){
        Music tmp=new Music("","","");
        //미리 비어있는 list를 만들고
        for(int i=0;i<FAVORITE_SIZE;i++){
            favoriteMusicList.add(tmp);
        }
        bgm=dbManager.select("Favorite",favorite_columns);

        //만약 즐겨찾기 db에 데이터가 있다면
        if(bgm.getCount()!=0){
            while(bgm.moveToNext()){
                //favorite_id 즉 저장된 position의 현재값을 삭제하고
                favoriteMusicList.remove(bgm.getInt(0));
                Music m=dbManager.selectByMusicId("BGMList", columns, bgm.getInt(1));
                //해당 position에 db에 있는 값을 저장해 ListView에서 그 포지션에 보여지도록 한다.
                favoriteMusicList.add(bgm.getInt(0),m);
            }
        }

    }

    //초기화 Method
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
        adapter=new FavoriteListAdapter(this,favoriteMusicList);
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

    //다른 액티비티로 부터 응답받았을 때 실행되는 Method
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==RESULT_OK){
            Music tmp=(Music)data.getSerializableExtra("selectedMusic");
            int tmpPosition=data.getIntExtra("position",0);
            favoriteMusicList.remove(tmpPosition);
            favoriteMusicList.add(tmpPosition,tmp);
            adapter.notifyDataSetChanged();
        }else if(resultCode==RESULT_CANCELED){
            Toast.makeText(getApplicationContext(), "즐겨찾기 추가를 취소하셨습니다. ", Toast.LENGTH_SHORT).show();
        }
    }
}
