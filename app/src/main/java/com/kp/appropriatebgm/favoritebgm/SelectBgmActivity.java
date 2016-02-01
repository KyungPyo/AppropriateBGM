package com.kp.appropriatebgm.favoritebgm;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.kp.appropriatebgm.DBController.*;

import java.util.ArrayList;
import java.util.Locale;

import com.kp.appropriatebgm.R;

public class SelectBgmActivity extends AppCompatActivity {

    BGMInfo selectedBGM = null;
    ArrayList<BGMInfo> bgms;
    DBManager dbManager = DBManager.getInstance(this);//DB
    Toolbar toolbar;
    ImageView searchButton;

    ArrayList<Category> categoryList;

    ListView list;
    BGMListAdapter adapter;
    EditText editSearch;
    int position;
    boolean isVisible = false;

    Cursor cursor;

    CategoryListAdapter categoryAdapter;
    Button save;
    Button cancel;
    Button clear_favorite;
    Spinner cateSpinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_bgm);

        init();
        //setCustomActionbar();
        addHeaderToList();

    }

    // Method : 리스트에 헤더 추가
    // Return Value : void
    // Parameter : void
    // Use :  List뷰의 Header에 비워두기 버튼을 추가한다.
    private void addHeaderToList() {

        View headerView = getLayoutInflater().inflate(R.layout.favorite_listview_header_layout, null);
        clear_favorite = (Button) headerView.findViewById(R.id.clearItem);
        //비워두기 버튼 클릭 리스너
        clear_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                position = intent.getIntExtra("position", 0);

                //해당 position은 DB에서 Favorite_id 이므로 해당 id가 있는 row를 지운다.
                dbManager.delete(position);//DB

                intent.putExtra("position", position);

                setResult(RESULT_OK, intent);
                finish();
            }
        });
        list.addHeaderView(headerView);

    }

    // Method : DB 에서 카테고리 받아오기
    // Return Value : void
    // Parameter : void
    // Use :  카테고리를 DB 에서 가져와서 Spinner 에서 보여주는 Method
    private void getCategory() {
        Toast.makeText(getApplicationContext(), "카테고리 목록을 만듦 ", Toast.LENGTH_SHORT).show();
        categoryList=dbManager.getCategoryList();//DB

        categoryAdapter = new CategoryListAdapter(this, categoryList);
        cateSpinner = (Spinner) findViewById(R.id.select_category);

        //스피너에서 item을 선택했을 때
        cateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //지금 보여지고 있는 ArrayList를 클리어
                bgms.clear();
                bgms.addAll(dbManager.getBGMList(categoryList.get(position).getCateId()));//int형

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        cateSpinner.setAdapter(categoryAdapter);
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

        //검색 ImageView가 클릭되었을때
        searchButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!isVisible) {
                    editSearch.setVisibility(View.VISIBLE);
                    isVisible = true;
                } else {
                    final InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    editSearch.setVisibility(View.INVISIBLE);
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

                selectedBGM = bgms.get(position - 1);

            }
        });
        //확인버튼 클릭 리스너
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isExist = false;
                Intent intent = getIntent();
                position = intent.getIntExtra("position", 0);
                if (selectedBGM == null) {//리스크 클릭이 안된 상태에서 확인을 눌렀을 때 취소
                    setResult(RESULT_CANCELED);
                    finish();
                } else {
                    intent.putExtra("selectedBGM", selectedBGM);
                    intent.putExtra("position", position);

                    //같은 자리에 있는지 확인
                    Cursor bgm = dbManager.selectFavorite();//DB
                    while (bgm.moveToNext()) {
                        if (bgm.getInt(0) == position) {
                            isExist = true;
                            break;
                        } else {
                            isExist = false;
                        }
                    }

                    if (isExist) {//존재하면 업데이트
                        dbManager.update(selectedBGM.getBgmId(), position);//DB
                    } else {//존재하지 않으면 추가 -> 이거 왜하냐면 딱 Favorite 크기만큼만 추가하려고
                        dbManager.insert(selectedBGM.getBgmId(), position);//DB
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

    // Method : 초기설정
    // Return Value : void
    // Parameter : void
    // Use : View를 객체와 연결, listView 설정
    private void init() {
        searchButton = (ImageView) findViewById(R.id.search_button);
        editSearch = (EditText) findViewById(R.id.search);
        toolbar = (Toolbar) findViewById(R.id.favorite_toolbar);
        list = (ListView) findViewById(R.id.musicList);
        editSearch = (EditText) findViewById(R.id.search);
        bgms = dbManager.getBGMList(1);

        adapter = new BGMListAdapter(this, bgms);
        list.setAdapter(adapter);

        save = (Button) findViewById(R.id.save_favorite);
        cancel = (Button) findViewById(R.id.cancel);

        getCategory();

        setListeners();

    }
}
