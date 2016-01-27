package com.kp.appropriatebgm;
import android.content.ComponentName;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity {

    /**** 멤버 선언 ****/
    DisplayMetrics metrics;
    int leftMenuWidth;

    //안녕
    // View 선언
    private ImageView actionbarMenuBtn;
    private ImageView actionbarSearchBtn;
    private ImageView actionbarLogo;
    private EditText actionbarSearchTxt;
    private ViewGroup btnFileManage;
    private ViewGroup groupFileManage;
    private ViewGroup btnEditComplete;

    private Toolbar toolbar;
    private ActionBarDrawerToggle drawerToggle;

    private DrawerLayout mainDrawer;
    private RelativeLayout mainLayout;
    private LinearLayout menuLayout;

    private View btnMoveToRecord;
    private View btnMoveToFavorite;
    private View btnMoveToCategory;
    /**** 멤버 선언 ****/

    /**** 화면 설정 ****/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initMember();           // 멤버변수 초기화
        initMenuLayoutSize();   // 메뉴 레이아웃 크기설정
        initDrawerToggle();     // 네비게이션 드로워 리스너설정
    }

    // Method : 초기설정
    // Return Value : void
    // Parameter : void
    // Use : 초기에 현재 액티비티에서 뷰나 뷰그룹들의 인스턴스들을 실제 객체들과 연결시키고, 초기 설정값을 입력한다. onCreate에서 호출.
    private void initMember(){
        toolbar = (Toolbar)findViewById(R.id.main_toolbar);
        btnFileManage = (ViewGroup)findViewById(R.id.main_group_filemanage);
        groupFileManage = (ViewGroup)findViewById(R.id.main_group_filemanagegroup);
        btnEditComplete = (ViewGroup)findViewById(R.id.main_group_editcomplete);
        mainDrawer = (DrawerLayout)findViewById(R.id.main_layout_drawer);
        mainLayout = (RelativeLayout)findViewById(R.id.main_layout_main);
        menuLayout = (LinearLayout)findViewById(R.id.main_layout_menu);

        btnMoveToRecord = findViewById(R.id.main_to_record);
        btnMoveToFavorite = findViewById(R.id.main_to_favorite);
        btnMoveToCategory = findViewById(R.id.main_to_category);

        setSupportActionBar(toolbar);           // Toolbar를 액션바로 사용한다
        getSupportActionBar().setTitle(null);   // 액션바에 타이틀 제거
    }

    // Method : 메뉴 레이아웃 설정
    // Return Value : void
    // Parameter : void
    // Use : 메뉴 버튼을 눌렀을 때 열리는 메뉴 크기를 설정한다. onCreate에서 호출
    private void initMenuLayoutSize(){
        // menuLayout의 너비를 화면의 75%로 설정하기위해 값을 받아온다
        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        leftMenuWidth = (int) ((metrics.widthPixels) * 0.75);

        // 메뉴 레이아웃의 파라미터 가져와서 가로 크기 설정하기
        menuLayout.getLayoutParams().width = leftMenuWidth;
    }

    // Method : 메뉴 레이아웃 설정
    // Return Value : void
    // Parameter : void
    // Use : 네비게이션 메뉴의 DrawerListener를 설정한다. 액션방 onCreate에서 호출
    private void initDrawerToggle(){
        drawerToggle = new ActionBarDrawerToggle(this, mainDrawer, toolbar, R.string.open_menu, R.string.close_menu){
            @Override
            public void onDrawerOpened(View drawerView) {   super.onDrawerOpened(drawerView);  }

            @Override
            public void onDrawerClosed(View drawerView) {   super.onDrawerClosed(drawerView);   }
        };
        mainDrawer.setDrawerListener(drawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(drawerToggle.onOptionsItemSelected(item)){   // drawer menu 여닫는걸 선택했을 때 이벤트 선점
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**** 화면 설정 ****/

    /**** 기타 이벤트 부분 ****/
    // Method : 메뉴버튼 클릭 이벤트
    // Return Value : void
    // Parameter : View(클릭한 뷰 정보)
    // Use : 메뉴안에 선택할 수 있는 버튼을 눌렀을 때 발생하는 이벤트 처리. 다른 액티비티로 이동하는데 사용된다.
    public void onClickMenuSelection(View v){
        Intent intent = new Intent();
        ComponentName componentName = null;
        switch (v.getId()){
            case R.id.main_to_record:{
                componentName = new ComponentName("com.kp.appropritebgm", "com.kp.appropritebgm.record.RecordActivity");
                intent.setComponent(componentName);
                startActivity(intent);
                break;
            }
            case R.id.main_to_favorite:{

                break;
            }
            case R.id.main_to_category:{

                break;
            }
        }
    }

    // Method : 파일관리 클릭이벤트
    // Return Value : void
    // Parameter : View(클릭한 뷰 정보)
    // Use : 파일관리를 클릭했을 때 이벤트 (파일관리의 펼쳐진 세부항목의 클릭 이벤트도 여기에 포함된다)
    public void onClickFilemanage(View v){
        switch (v.getId()){
            case R.id.main_group_filemanage:
            case R.id.main_group_editcomplete: {
                if(groupFileManage.getVisibility() == View.INVISIBLE) {
                    groupFileManage.setVisibility(View.VISIBLE);
                    btnFileManage.setVisibility(View.INVISIBLE);
                } else {
                    groupFileManage.setVisibility(View.INVISIBLE);
                    btnFileManage.setVisibility(View.VISIBLE);
                }
                break;
            }
        }
    }
    /**** 기타 이벤트 부분 ****/

}
