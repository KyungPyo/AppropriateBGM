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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;

import com.kp.appropriatebgm.CheckPref;
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
    Switch onOffSwitch;
    Boolean mBoolean = false;

    FavoriteListAdapter adapter;
    ArrayList<Favorite> favoriteArrayList;
    DBManager dbManager=DBManager.getInstance(this);//DB

    private LockNotificationInterface binder = null;

    private CheckPref mPref;

    ArrayList<Favorite> realBgmNameList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        mPref = new CheckPref(this);        // 공유 프레퍼런스 객체
        init();
        onOffSwitch.setChecked(mPref.getLockerOnOff());
        onOffSwitch.setOnCheckedChangeListener(checkedChangeListener);

        Intent serviceintent = new Intent(FavoriteActivity.this, LockScreenService.class);
        bindService(serviceintent, lockServiceConnection, BIND_AUTO_CREATE);

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
            if (isChecked) {
                if(favoriteRealListCheck()) {
                    Intent intent = new Intent(FavoriteActivity.this, LockScreenService.class);
                    startService(intent);
                }
                else
                {
                    AlertDialog.Builder noLocker_Dig = new AlertDialog.Builder(FavoriteActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
                    noLocker_Dig.setTitle("즐겨찾기 목록이 존재하지 않습니다!")
                            .setNegativeButton(R.string.ctgdialog_checkbtn_text, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface indialog, int which) {
                                    indialog.cancel();
                                }
                            });
                    noLocker_Dig.show();
                    buttonView.setChecked(!isChecked);
                    mPref.setLockerOnOff();
                }
            } else {
                Intent intent = new Intent(FavoriteActivity.this, LockScreenService.class);
                stopService(intent);
            }
            if(mBoolean) {
                mPref.setLockerOnOff();
            }
            setBinderNotificationOnOff();

        }
    };

    // Method : Restart (재시작)
    // Return Value : void
    // Parameter : void
    // Use : mBoolean값을 false로 하여 setChecked시에 리스너의 공유변수 저장이 되지 않도록 하여 뒤엉킴을 방지함
    @Override
    protected void onRestart() {
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

    //Use : 서비스 연결 객체 선언
    private ServiceConnection lockServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = LockNotificationInterface.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    // Method : 바인드 서비스 종료
    // Return value : void
    // parameter : void
    // use ; 액티비티가 종료될 시기에 onCreate에서 bindService의 서비스 연결을 해제(unbind)해준다.
    @Override
    protected void onDestroy() {
        unbindService(lockServiceConnection);
        super.onDestroy();
    }


    // Method : 알림 on/off 설정 (SettingActivity에도 동일한 메소드)
    // Return value : void
    // parameter : void
    // use ; 서비스 액티비티에서 aidl을 통해 설정해 준 알림 띄우는 함수를 서비스를 연결한 뒤에 함수를 받아온다. (통신)
    //       remoteException 예외 처리를 꼭 해주어야 함!
    public void setBinderNotificationOnOff()
    {
        try{
            binder.setNotificationOnOff();
        }
        catch (RemoteException e)
        {
            e.printStackTrace();
        }
    }

    public boolean favoriteRealListCheck()
    {
        realBgmNameList = new ArrayList<>();
        for(int i=0; i < favoriteArrayList.size() ; i++) {
            if (favoriteArrayList.get(i).getBgmPath() != null) {
                realBgmNameList.add(favoriteArrayList.get(i));
            }
        }
        if(realBgmNameList.size() == 0)
        {
            return false;
        }
        else
        {
            return true;
        }
    }
}
