package com.kp.appropriatebgm.Setting;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import com.kp.appropriatebgm.R;


/**
 * Created by GD on 2016-02-17.
 */
public class SettingActivity extends AppCompatActivity{

    // Method : onCreate
    // Return Value : void
    // Parameter : savedInstateState
    // Use : PreferenceFragment를 받기 위한 Activity를 만들고 이를 연결
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        // Use : FragmentManager의 트랜잭션을 SettingFragment(PreferenceFragment)로 대체한다.
        //getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingFragment(getApplicationContext())).commit();
        Log.e("setting","created");
    }
}
