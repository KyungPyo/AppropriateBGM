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
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.kp.appropriatebgm.Category.CategoryActivity;
import com.kp.appropriatebgm.DBController.BGMInfo;
import com.kp.appropriatebgm.DBController.Category;
import com.kp.appropriatebgm.DBController.DBManager;
import com.kp.appropriatebgm.favoritebgm.BGMListAdapter;
import com.kp.appropriatebgm.favoritebgm.CategoryListAdapter;
import com.kp.appropriatebgm.favoritebgm.FavoriteActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    /**** 멤버 선언 ****/
    private DisplayMetrics metrics;
    private int leftMenuWidth;
    private boolean isFilemanageOpen = false;
    private MusicPlayer musicPlayer;

    // View 선언
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

    private ListView bgmListView;
    private CategoryListAdapter categoryAdapter;
    private DBManager dbManager;
    private ArrayList<BGMInfo> bgmList;
    private ArrayList<Category> categoryList;
    private Spinner categorySpinner;
    private BGMListAdapter bgmAdapter;
    /**** 멤버 선언 ****/

    /**** 화면 설정 ****/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initMember();           // 멤버변수 초기화
        initMenuLayoutSize();   // 메뉴 레이아웃 크기설정
        initDrawerToggle();     // 네비게이션 드로워 리스너설정
        initBgmList();          // BGMList 초기 구성
        initCategory();         // 카테고리 목록 초기 구성 및 이벤트 정의
        setListeners();


    }

    @Override
    protected void onStop() {
        super.onStop();

        // 액티비티에서 벗어나면 재생중인 브금 정지
        if (musicPlayer != null){
            musicPlayer.stopBgm();
            musicPlayer = null;
        }
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

        btnMoveToRecord = findViewById(R.id.main_menubtn_to_record);
        btnMoveToFavorite = findViewById(R.id.main_menubtn_to_favorite);
        btnMoveToCategory = findViewById(R.id.main_menubtn_to_category);

        bgmListView=(ListView)findViewById(R.id.main_list_soundlist);
        categorySpinner=(Spinner)findViewById(R.id.main_spinner_category);

        dbManager=DBManager.getInstance(this);

        setSupportActionBar(toolbar);           // Toolbar를 액션바로 사용한다
        getSupportActionBar().setTitle(null);   // 액션바에 타이틀 제거

        isFilemanageOpen = false;   // 파일관리가 열려있는지 여부
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
            public void onDrawerOpened(View drawerView) {   super.onDrawerOpened(drawerView);   }

            @Override
            public void onDrawerClosed(View drawerView) {   super.onDrawerClosed(drawerView);   }
        };
        mainDrawer.setDrawerListener(drawerToggle);
    }

    // Method : Main의 BGMList 초기 설정
    // Return Value : void
    // Parameter : void
    // Use :  DB에서 전체리스트를 가져오고 list뷰의 어댑터 설정을 담당. onCreate에서 호출
    private void initBgmList(){
        bgmList=dbManager.getBGMList(1);
        bgmAdapter=new BGMListAdapter(this,bgmList);
        bgmListView.setAdapter(bgmAdapter);
    }

    // Method : DB 에서 카테고리 받아오기
    // Return Value : void
    // Parameter : void
    // Use :  카테고리를 DB 에서 가져와서 Spinner 에서 보여주는 Method
    private void initCategory() {
        categoryList=dbManager.getCategoryList();//DB
        categoryAdapter = new CategoryListAdapter(this, categoryList);

        //스피너에서 item을 선택했을 때
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //지금 보여지고 있는 ArrayList를 클리어
                bgmList.clear();
                bgmList.addAll(dbManager.getBGMList(categoryList.get(position).getCateId()));
                listItemCheckFree();
                bgmAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        categorySpinner.setAdapter(categoryAdapter);
    }

    private void setListeners(){
        bgmListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onClickMusicListItem(position);
            }
        });
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
            case R.id.main_menubtn_to_record:{
                componentName = new ComponentName("com.kp.appropriatebgm", "com.kp.appropriatebgm.record.RecordActivity");
                intent.setComponent(componentName);
                startActivity(intent);
                break;
            }
            case R.id.main_menubtn_to_favorite:{

               /* componentName = new ComponentName("com.kp.appropriatebgm", "com.kp.appropriatebgm.favoritebgm.FavoriteActivity");
                intent.setComponent(componentName);*/
                intent.setClass(getApplicationContext(), FavoriteActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.main_menubtn_to_category:{

                intent.setClass(getApplicationContext(), CategoryActivity.class);
                startActivity(intent);
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
                if(groupFileManage.getVisibility() == View.INVISIBLE && !isFilemanageOpen) {
                    groupFileManage.setVisibility(View.VISIBLE);
                    btnFileManage.setVisibility(View.INVISIBLE);
                    setCheckBoxVisibility(true);
                    bgmListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                    isFilemanageOpen = true;
                } else {
                    groupFileManage.setVisibility(View.INVISIBLE);
                    btnFileManage.setVisibility(View.VISIBLE);
                    listItemCheckFree();
                    setCheckBoxVisibility(false);
                    bgmListView.setAdapter(null);
                    bgmListView.setAdapter(bgmAdapter);
                    bgmListView.setChoiceMode(ListView.CHOICE_MODE_NONE);
                    isFilemanageOpen = false;
                }
                break;
            }
        }
    }

    private void onClickMusicListItem(int position){
        if (isFilemanageOpen){  // 파일관리가 열려있으면 선택모드
            bgmAdapter.notifyDataSetChanged();
        } else {        // 파일관리가 닫혀있으면 재생모드
            BGMInfo bgm = bgmAdapter.getItem(position);
            if (musicPlayer != null){
                musicPlayer.stopBgm();
            }
            if(bgm.isInnerfile()) {
                musicPlayer = new MusicPlayer(this, bgm.getInnerfileCode());
            } else {
                musicPlayer = new MusicPlayer(this, bgm.getBgmPath());
            }
            musicPlayer.playBgm();
        }
    }
    /**** 기타 이벤트 부분 ****/

    // Method : CheckBox controller
    // Return Value : void
    // Parameter : ckecked: check박스를 보여줄지 숨길지를 결정하는 boolean형 변수
    // Use :  호출부에서 ckeck박스 컨트롤을 요청하면 화면에 반영시켜줌.
    private void setCheckBoxVisibility(boolean checked){
        bgmAdapter.setCheckBoxVisible(checked);
        bgmAdapter.notifyDataSetChanged();
    }

    private void listItemCheckFree(){
        for(int i=0;i<bgmListView.getCount();i++){
            bgmListView.setItemChecked(i,false);
        }
    }
}
