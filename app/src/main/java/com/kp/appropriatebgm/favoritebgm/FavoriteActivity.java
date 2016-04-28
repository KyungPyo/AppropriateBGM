package com.kp.appropriatebgm.favoritebgm;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import com.kp.appropriatebgm.CheckPref;
import com.kp.appropriatebgm.DBController.BGMInfo;
import com.kp.appropriatebgm.DBController.DBManager;
import com.kp.appropriatebgm.DBController.Favorite;
import com.kp.appropriatebgm.LockScreen.LockNotificationInterface;
import com.kp.appropriatebgm.LockScreen.LockScreenService;
import com.kp.appropriatebgm.R;
import com.kp.appropriatebgm.Setting.SettingActivity;

import java.util.ArrayList;


public class FavoriteActivity extends AppCompatActivity {

    ListView favoriteList;
    Toolbar toolbar;

    FavoriteListAdapter adapter;
    ArrayList<Favorite> favoriteArrayList;
    ArrayList<Integer> checkedFavoriteList;
    ImageButton addFavorite;
    ImageButton deleteFavorite;
    DBManager dbManager=DBManager.getInstance(this);//DB

    FloatingActionButton deleteFloatingButton;

    boolean isActivatedFloatingButton=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        init();

        setSupportActionBar(toolbar);           // Toolbar를 액션바로 사용한다
        getSupportActionBar().setTitle(null);   // 액션바에 타이틀 제거
        setListeners();

    }

    // Method : 초기설정
    // Return Value : void
    // Parameter : void
    // Use : View를 객체와 연결, listView 설정
    private void init(){

        toolbar=(Toolbar)findViewById(R.id.favorite_toolbar);
        favoriteArrayList=new ArrayList<Favorite>();

        //listView 설정
        favoriteArrayList=dbManager.getFavoriteListNotNull();//DB

        favoriteList=(ListView)findViewById(R.id.favorite_listView_favoriteList);
        adapter=new FavoriteListAdapter(this, favoriteArrayList);
        favoriteList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.notifyDataSetChanged();
            }
        });
        favoriteList.setChoiceMode(ListView.CHOICE_MODE_NONE);
        favoriteList.setAdapter(adapter);

        addFavorite=(ImageButton)findViewById(R.id.favorite_btn_addfavorite);
        deleteFavorite=(ImageButton)findViewById(R.id.favorite_btn_deletefavorite);
        deleteFloatingButton=(FloatingActionButton)findViewById(R.id.favorite_btn_checkdelete);

        deleteFloatingButton.setVisibility(View.INVISIBLE);
        deleteFloatingButton.setEnabled(false);

        //즐겨찾기 List중 아이템클릭시 Method
        addFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), SelectBgmActivity.class);
                //현재 클릭된 position값을 넘겨준다.
                startActivityForResult(intent, 0);
            }
        });
        adapter.notifyDataSetChanged();
    }

    private void setListeners(){
        deleteFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isActivatedFloatingButton) {
                    favoriteList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                    deleteFloatingButton.setVisibility(View.VISIBLE);
                    deleteFloatingButton.setEnabled(true);
                    adapter.setCheckBoxVisible(true);
                    adapter.notifyDataSetChanged();
                    isActivatedFloatingButton = true;
                } else {
                    deleteFloatingButton.setVisibility(View.INVISIBLE);
                    adapter.setCheckBoxVisible(false);
                    adapter.notifyDataSetChanged();
                    isActivatedFloatingButton = false;
                    favoriteList.setChoiceMode(ListView.CHOICE_MODE_NONE);
                    favoriteList.setAdapter(null);
                    favoriteList.setAdapter(adapter);
                    listItemCheckFree();
                }
            }
        });
        deleteFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loadCheckedListItem();
                int [] checkedListId= new int [checkedFavoriteList.size()];

                for(int i=0;i<checkedListId.length;i++){
                    checkedListId[i]=checkedFavoriteList.get(i);
                }


                dbManager.deleteFavorite(checkedListId);

                favoriteArrayList.clear();
                favoriteArrayList.addAll(dbManager.getFavoriteListNotNull());


                listItemCheckFree();
                adapter.notifyDataSetChanged();

            }
        });
    }

    // Method : 액티비티 응답 받기
    // Return Value : void
    // Parameter : requestCode-요청 코드, resultCode-응답받는 코드, data-응답받는 intent
    // Use : 각각의 result 코드에 맞춰 액티비티 응답 처리
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==RESULT_OK){//result OK
            favoriteArrayList.clear();
            favoriteArrayList.addAll(dbManager.getFavoriteListNotNull());

            //reset
            listItemCheckFree();
            adapter.setCheckBoxVisible(false);
            isActivatedFloatingButton = false;
            favoriteList.setChoiceMode(ListView.CHOICE_MODE_NONE);
            favoriteList.setAdapter(null);
            favoriteList.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }else if(resultCode==RESULT_CANCELED){//result Canceled
            //Toast.makeText(getApplicationContext(), "즐겨찾기 추가를 취소하셨습니다. ", Toast.LENGTH_SHORT).show();
        }


    }

    // Method : list상에 check되어있는 item 모두 원래대로 돌아옴
    // Return Value : void
    // Parameter : void
    // Use : list의 모든 아이템을 탐색하여 check 상태를 false로 바꿔줌
    private void listItemCheckFree() {
        for (int i = 0; i < favoriteList.getCount(); i++) {
            favoriteList.setItemChecked(i, false);
        }
    }

    // Method : list상에 check가 되어있는 item을 저장한다.
    // Return Value : ArrayList<BGMInfo>
    // Parameter : void
    // Use : list의 모든 아이템을 탐색하여 check가 true인 item만 checkedBgmList에 추가해 리턴해줌
    void loadCheckedListItem() {
        checkedFavoriteList = new ArrayList<>();
        for (int i = 0; i < favoriteList.getCount(); i++) {
            if (favoriteList.isItemChecked(i)) {
                checkedFavoriteList.add(adapter.getItem(i).getFavoriteId());
            }
        }
    }

}
