package com.kp.appropriatebgm.favoritebgm;

import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.kp.appropriatebgm.DBController.BGMInfo;
import com.kp.appropriatebgm.DBController.Category;
import com.kp.appropriatebgm.DBController.DBManager;
import com.kp.appropriatebgm.DBController.Favorite;
import com.kp.appropriatebgm.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.lang.String;

public class SelectBgmActivity extends AppCompatActivity {

    BGMInfo selectedBGM = null;
    ArrayList<BGMInfo> bgms;
    ArrayList<Favorite> favoriteList;
    DBManager dbManager = DBManager.getInstance(this);//DB
    Toolbar toolbar;
    ImageView searchButton;

    ArrayList<Category> categoryList;
    ArrayList<BGMInfo> checkedBgmList;

    ListView list;
    BGMListAdapter adapter;
    EditText editSearch;
    boolean isVisible = false;

    CategoryListAdapterForMain categoryAdapter;
    Button save;
    Button cancel;
    Spinner cateSpinner;

    TextView selectTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_bgm);
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);

        init();
        //addHeaderToList();
        getCategory();
        setListeners();

    }

    // Method : DB 에서 카테고리 받아오기
    // Return Value : void
    // Parameter : void
    // Use :  카테고리를 DB 에서 가져와서 Spinner 에서 보여주는 Method
    private void getCategory() {
        categoryList=dbManager.getCategoryList();//DB

        categoryAdapter = new CategoryListAdapterForMain(this, categoryList);
        cateSpinner = (Spinner) findViewById(R.id.favorite_spinner_category);

        //스피너에서 item을 선택했을 때
        cateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //지금 보여지고 있는 ArrayList를 클리어
                bgms.clear();
                bgms.addAll(dbManager.getBGMList(categoryList.get(position).getCateId()));//int형
                listItemCheckFree();
                adapter.setSearchingList();
                adapter.filter(editSearch.getText().toString());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        cateSpinner.setAdapter(categoryAdapter);
    }

    // Method : 즐겨찾기에 이미 해당 파일이 존재하는지 확인
    // Return Value : boolean
    // Parameter : temp : 전체 즐겨찾기 목록 받아옴, selectedBGM : 현재 사용자가 선택한 BGM의 정보
    // Use :  즐겨찾기 목록의 모든 값의 Path와 현재 사용자가 선택한 파일의 Path를 비교하여 중복이 생기면 true;
    private boolean isCollision(ArrayList<Favorite> temp, BGMInfo selectedBGM){
        boolean collision=false;
        for(int i=0;i<temp.size();i++){
            if(selectedBGM.getBgmPath().equals(temp.get(i).getBgmPath()))
                collision=true;
        }
        return collision;
    }
    // Method : Setting Listeners
    // Return Value : void
    // Parameter : void
    // Use :  리스너 정의
    private void setListeners() {
        //Text가 바뀌는 이벤트가 발생할 때
        editSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
                listItemCheckFree();
                String text = editSearch.getText().toString().toLowerCase(Locale.getDefault());
                adapter.filter(text);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
                // TODO Auto-generated method stub
            }
        });

        //검색창 엔터키 막는 이벤트
        editSearch.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == event.KEYCODE_ENTER)
                {
                    return true;
                }
                return false;
            }
        });

        //검색 ImageView가 클릭되었을때
        searchButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!isVisible) {
                    editSearch.setVisibility(View.VISIBLE);
                    selectTextView.setVisibility(View.INVISIBLE);
                    searchButton.setImageResource(R.drawable.ic_search_cancel_white);
                    isVisible = true;
                } else {
                    final InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    editSearch.setVisibility(View.INVISIBLE);
                    selectTextView.setVisibility(View.VISIBLE);
                    searchButton.setImageResource(R.drawable.ic_search_white_24dp);
                    isVisible = false;
                    editSearch.setText("");

                    //키보드 숨기기
                    imm.hideSoftInputFromWindow(editSearch.getWindowToken(), 0);
                }
            }
        });
        //listView 클릭 이벤트 리스너
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(isCollision(favoriteList,bgms.get(position))){
                    Toast.makeText(getApplicationContext(), "중복된 BGM 입니다.", Toast.LENGTH_SHORT).show();
                    list.setItemChecked(position,false);
                }

            }
        });
        //확인버튼 클릭 리스너
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                checkedBgmList=loadCheckedListItem();
                if (checkedBgmList.size()==0) {//리스크 클릭이 안된 상태에서 확인을 눌렀을 때 취소
                    setResult(RESULT_CANCELED);
                    finish();
                }else{

                    for(int i=0;i<checkedBgmList.size();i++){
                        dbManager.insertFavorite(checkedBgmList.get(i).getBgmPath());
                    }

                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }

    // Method : list상에 check되어있는 item 모두 원래대로 돌아옴
    // Return Value : void
    // Parameter : void
    // Use : list의 모든 아이템을 탐색하여 check 상태를 false로 바꿔줌
    private void listItemCheckFree() {
        for (int i = 0; i < list.getCount(); i++) {
            list.setItemChecked(i, false);
        }
    }

    // Method : 초기설정
    // Return Value : void
    // Parameter : void
    // Use : View를 객체와 연결, listView 설정
    private void init() {
        searchButton = (ImageView) findViewById(R.id.favorite_btn_search);
        editSearch = (EditText) findViewById(R.id.favorite_editText_search);
        toolbar = (Toolbar) findViewById(R.id.favorite_toolbar);
        selectTextView=(TextView)findViewById(R.id.selectbgm_textView_select);
        list = (ListView) findViewById(R.id.musicList);
        checkedBgmList = new ArrayList<>();
        favoriteList=dbManager.getFavoriteList();
        bgms = dbManager.getBGMList(1);

        adapter = new BGMListAdapter(this, bgms);
        save = (Button) findViewById(R.id.favorite_btn_save);
        cancel = (Button) findViewById(R.id.favorite_btn_cancel);


        list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        list.setAdapter(adapter);

    }

    // Method : list상에 check가 되어있는 item을 저장한다.
    // Return Value : ArrayList<BGMInfo>
    // Parameter : void
    // Use : list의 모든 아이템을 탐색하여 check가 true인 item만 checkedBgmList에 추가해 리턴해줌
    private ArrayList<BGMInfo> loadCheckedListItem() {
        ArrayList<BGMInfo> checkedBgmList = new ArrayList<>();
        for (int i = 0; i < list.getCount(); i++) {
            if (list.isItemChecked(i)) {
                checkedBgmList.add(adapter.getItem(i));
            }
        }
        return checkedBgmList;
    }
}
