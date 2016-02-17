package com.kp.appropriatebgm.Category;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.ActionMenuItemView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.kp.appropriatebgm.DBController.Category;
import com.kp.appropriatebgm.DBController.DBManager;
import com.kp.appropriatebgm.R;
import com.kp.appropriatebgm.favoritebgm.CategoryListAdapter;

import java.util.ArrayList;
import java.util.HashMap;


//카테고리 액티비티
public class CategoryActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {


    FloatingActionButton fab;
    DBManager dbManager=DBManager.getInstance(this);//DB
    ArrayList<Category> ctgArrayList;
    CategoryListAdapter ctgAdapter;
    ListView ctg_Listview;
    HashMap<String, Integer> ctgcheckList;

    ActionMenuItemView ctg_actionbar_add_button;

    // Use : Select 쿼리 (LIST를 계속 갱신해주는 데 필요한 쿼리)
    //final static String querySelectAll = String.format( " SELECT * FROM %s ", TABLE_NAME );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);

        Toolbar toolbar = (Toolbar) findViewById(R.id.category_toolbar_menu);
        setSupportActionBar(toolbar);

        ctgArrayList = new ArrayList<Category>();
        ctgcheckList = new HashMap<String, Integer>();

        ctg_Listview = (ListView) findViewById( R.id.category_listView_categoryList );

        ctgDBInit();

        ctg_Listview.setOnItemClickListener(this);


        // Use : 체크 후 삭제 버튼 (floatingActionButton이다)
        fab = (FloatingActionButton) findViewById(R.id.category_btn_checkdelete);
        fab.setVisibility(View.INVISIBLE);
        fab.setEnabled(false);


        fab.setOnClickListener(new View.OnClickListener() {
            //Method : 체크 후 삭제 액션 버튼 설정
            //Return value : void
            //Paremeter : view (fab 버튼)
            //Use : 액션바의 휴지통(삭제) 버튼을 누른 후 각 리스트 아이템의 체크박스 유무를 검사하여
            //      체크된 아이템 항목을 삭제하는 용도로 사용한다.
            @Override
            public void onClick(View view) {

                if(ctgAdapter.addCheckData().size() > 0) {
                    final View mView = view;
                    LayoutInflater inflater = getLayoutInflater();
                    View delete_dialog_view = inflater.inflate(R.layout.dialog_category_deletecheck, null);
                    AlertDialog.Builder delete_Digbuild = new AlertDialog.Builder(CategoryActivity.this);
                    delete_Digbuild.setTitle("카테고리 삭제");
                    delete_Digbuild.setView(delete_dialog_view);
                    delete_Digbuild.setMessage(ctgAdapter.addCheckData().size() + "개 카테고리를 삭제하시겠습니까?");

                    Button ctg_delete_btn = (Button) delete_dialog_view.findViewById(R.id.category_btn_delete);
                    Button ctg_deleteCancel_btn = (Button) delete_dialog_view.findViewById(R.id.category_btn_deletecancel);

                    ctg_delete_btn.setText(R.string.ctgdialog_checkbtn_text);
                    ctg_deleteCancel_btn.setText(R.string.ctgdialog_cancelbtn_text);
                    final AlertDialog deleteCategory_dialog = delete_Digbuild.create();

                    ctg_delete_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            CtgcheckDelete(ctg_Listview);
                            //fab 버튼이 사라지지 않는 경우를 고려하여 invisible, enable(false) 설정
                            mView.setVisibility(View.INVISIBLE);
                            mView.setEnabled(false);
                            ctg_actionbar_add_button.setEnabled(true);
                            deleteCategory_dialog.dismiss();
                        }
                    });
                    ctg_deleteCancel_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            deleteCategory_dialog.dismiss();
                        }
                    });
                    deleteCategory_dialog.show();
                }
                else
                {
                    AlertDialog.Builder no_delete_Digbuild = new AlertDialog.Builder(CategoryActivity.this);
                    no_delete_Digbuild.setMessage(R.string.ctgdialog_notdelete_message);
                    final AlertDialog notDeleteCategory_dialog = no_delete_Digbuild.create();
                    notDeleteCategory_dialog.show();
                }
            }
        });


    }

    // Method : 옵션메뉴 생성
    // Return value : boolean
    // parameter : menu(메뉴)
    // use ; 옵션 메뉴를 설정해준다.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.actionbar_category_menu, menu);
        return true;
    }

    //Method : 액션바 버튼 이벤트 설정
    //Return value : boolean
    //Paremeter : void
    //Use : 액션바(menu_category)에 구현되어 있는 추가 버튼과 삭제 버튼의 기능을 설정하기 위해 사용한다.
    //      추가 버튼(category_actionbar_add)의 경우 다이얼로그를 띄워 DB에 INSERT 쿼리를 실행하며,
    //      삭제 버튼(category_actionbar_delete)의 경우 체크박스를 리스트에 띄워 항목을 체크할 수 있도록 한다.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        ctg_actionbar_add_button = (ActionMenuItemView) findViewById(R.id.category_actionbtn_addmenu);

        switch(id)
        {
            // Use : 추가(category_actionbar_add) 버튼 클릭하는 경우
            // 다이얼로그를 띄워서 추가 시 다이얼로그의 EditText의 값을 DB에 넣는 INSERT 연산 수행 및 취소 버튼
            case R.id.category_actionbtn_addmenu:
                LayoutInflater inflater = getLayoutInflater();
                final View add_dialog_view = inflater.inflate(R.layout.dialog_category_add, null);
                final AlertDialog.Builder add_Digbuild = new AlertDialog.Builder(CategoryActivity.this);
                add_Digbuild.setTitle("카테고리 추가");
                add_Digbuild.setView(add_dialog_view);
                add_Digbuild.setMessage(R.string.ctgdialog_add_message);

                final EditText inputtxt = (EditText)add_dialog_view.findViewById(R.id.category_editText_input);
                Button ctg_add_btn = (Button)add_dialog_view.findViewById(R.id.category_btn_modify);
                Button ctg_cancel_btn = (Button)add_dialog_view.findViewById(R.id.category_btn_cancel);

                ctg_add_btn.setText(R.string.ctgdialog_addbtn_text);
                ctg_cancel_btn.setText(R.string.ctgdialog_cancelbtn_text);
                inputtxt.setHint("8자 이하로 입력해주시오.");

                final AlertDialog addCategory_dialog = add_Digbuild.create();

                ctg_add_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            // Use : EditText의 길이가 8 이상인 경우 다이얼로그 띄움
                            String c_title = inputtxt.getText().toString();

                            // Use : 위의 예외들을 통과한 경우 DB INSERT 연산 수행
                            if(CtgTitleCheckable(inputtxt)){
                                CtgInsert(c_title);
                                addCategory_dialog.dismiss();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                ctg_cancel_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addCategory_dialog.dismiss();
                    }
                });
                addCategory_dialog.show();
                inputtxt.setText("");
                break;

            // Use : 삭제(category_actionbar_delete) 버튼 클릭하는 경우
            // 체크 삭제 버튼을 띄우도록 한다. 다시 한번 더 클릭 시 원래 메뉴로 돌아가는 기능
            case R.id.category_actionbtn_deletemenu:

                // Use : 삭제 버튼이 보이지 않을 경우에 휴지통을 누르면 보이도록 한다.
                if(fab.getVisibility() == View.INVISIBLE)
                {
                    fab.setVisibility(View.VISIBLE);
                    fab.setEnabled(true);
                    setCheckBoxVisibility(true);
                    ctg_actionbar_add_button.setEnabled(false);
                }

                // Use ; 삭제 버튼이 보이는 경우 원래 리스트 상태로 돌아가려고 할 때 숨기도록 한다.
                else if(fab.getVisibility() == View.VISIBLE)
                {
                    fab.setVisibility(View.INVISIBLE);
                    fab.setEnabled(false);
                    setCheckBoxVisibility(false);
                    ctg_actionbar_add_button.setEnabled(true);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    // Method : 카테고리 리스트 아이템 클릭
    // Return value : void
    // paremeter : parentView, 클릭 뷰, 클릭 아이템 위치, 클릭 아이템 아이디
    // Use : 아이템 클릭 시 세부 속성을 선택하는 다이얼로그를 띄우도록 한다. (이름 변경, 삭제, 취소)
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long lid) {
        final int pos = position;
        final int itemId = ctgAdapter.getlistId(position);

        if(position>=2 && ctgAdapter.getCheckBoxVisibility() == false) {
            final CharSequence[] property_items = {"이름 변경", "삭 제", "취 소"};
            final AlertDialog.Builder alertDig = new AlertDialog.Builder(view.getContext());
            alertDig.setTitle(R.string.ctg_long_property);
            alertDig.setItems(property_items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Log.e("id --------", Long.toString(itemid));
                    //Log.e("item --------", Integer.toString(which));
                    final DialogInterface item_Dialog = dialog;
                    LayoutInflater inflater = getLayoutInflater();
                    switch (which) {
                        case 0:
                            // Use : 이름 변경의 경우
                            // 이름 변경을 하는 다이얼로그를 띄우며 변경 시 해당 아이템의 아이디를 받아 다이얼로그의 EditText의 값을 DB에 갱신하는 UPDATE 연산 수행
                            final View add_dialog_view = inflater.inflate(R.layout.dialog_category_add, null);
                            final AlertDialog.Builder add_Digbuild = new AlertDialog.Builder(CategoryActivity.this);
                            add_Digbuild.setTitle("카테고리 이름 변경");
                            add_Digbuild.setView(add_dialog_view);
                            add_Digbuild.setMessage(R.string.ctgdialog_add_message);

                            final EditText up_inputtxt = (EditText)add_dialog_view.findViewById(R.id.category_editText_input);
                            Button ctg_up_btn = (Button)add_dialog_view.findViewById(R.id.category_btn_modify);
                            Button ctg_cancel_btn = (Button)add_dialog_view.findViewById(R.id.category_btn_cancel);

                            ctg_up_btn.setText(R.string.ctgdialog_checkbtn_text);
                            ctg_cancel_btn.setText(R.string.ctgdialog_cancelbtn_text);
                            up_inputtxt.setHint("8자 이하로 입력해주시오.");

                            final AlertDialog updateCategory_dialog = add_Digbuild.create();

                            ctg_up_btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    try {
                                        // Use : EditText의 길이가 8 이상인 경우 다이얼로그 띄움
                                        String modify_title = up_inputtxt.getText().toString();

                                        // Use : 위의 예외들을 통과한 경우 DB INSERT 연산 수행
                                        if(CtgTitleCheckable(up_inputtxt)){
                                            CtgUpdate(modify_title, itemId);
                                            updateCategory_dialog.dismiss();
                                        }

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                            ctg_cancel_btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    updateCategory_dialog.dismiss();
                                }
                            });
                            updateCategory_dialog.show();
                            up_inputtxt.setText("");
                            break;
                        case 1:
                            // Use : 카테고리 삭제의 경우
                            //      해당 아이템의 id를 가져와 DELETE 연산을 수행하며 다이얼로그로 삭제를 한번 더 확인한다.
                            // 삭제 확인 다이얼로그 부분
                            final View delete_dialog_view = inflater.inflate(R.layout.dialog_category_deletecheck, null);
                            final AlertDialog.Builder delete_Digbuild = new AlertDialog.Builder(CategoryActivity.this);
                            delete_Digbuild.setTitle("카테고리 삭제");
                            delete_Digbuild.setView(delete_dialog_view);
                            delete_Digbuild.setMessage("'"+ctgAdapter.getlistName(pos)+"' 를(을) 삭제하시겠습니까?");

                            Button ctg_delete_btn = (Button) delete_dialog_view.findViewById(R.id.category_btn_delete);
                            Button ctg_deleteCancel_btn = (Button) delete_dialog_view.findViewById(R.id.category_btn_deletecancel);

                            ctg_delete_btn.setText(R.string.ctgdialog_checkbtn_text);
                            ctg_deleteCancel_btn.setText(R.string.ctgdialog_cancelbtn_text);
                            final AlertDialog deleteCategory_dialog = delete_Digbuild.create();

                            ctg_delete_btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    CtgDelete(itemId);      // DB 삭제 부분
                                    deleteCategory_dialog.dismiss();
                                }
                            });
                            ctg_deleteCancel_btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    deleteCategory_dialog.dismiss();
                                }
                            });
                            deleteCategory_dialog.show();
                            break;
                        case 2:
                            //취소의 경우 다이얼로그를 닫는다.
                            dialog.cancel();
                            break;
                        default:
                            break;
                    }
                }
            });
            alertDig.show();
        }
        else if(position>=2 && ctgAdapter.getCheckBoxVisibility() == true)
        {
            Log.e("listitem click ",position+"");
            ctgAdapter.setCheckBoxChecked(position);
            ctgAdapter.notifyDataSetChanged();
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if (event.getAction() == android.view.KeyEvent.ACTION_DOWN) {

            if (keyCode == android.view.KeyEvent.KEYCODE_BACK && fab.getVisibility()==View.VISIBLE) {
                fab.setVisibility(View.INVISIBLE);
                fab.setEnabled(false);
                setCheckBoxVisibility(false);
                ctg_actionbar_add_button.setEnabled(true);
                setResult(RESULT_OK);
                return true;
            }
            if (keyCode == android.view.KeyEvent.KEYCODE_BACK) {
                setResult(RESULT_OK);
                finish();
            }
        }

        return super.onKeyDown(keyCode, event);
    }
    // Method : 카테고리 추가
    // Return value : void
    // paremeter : 테이블명, 추가할 카테고리 명(EditText)
    // Use : id는 Auto-increment로 자동 생성 및 해당 이름의 카테고리를 추가시키도록 INSERT 연산 수행
    //      changeCursor을 통해 리스트 최신화
    public void CtgInsert(String t_title)
    {
        dbManager.insertCategory(t_title);
        ctgDBInit();
    }
    // Method : 카테고리 이름 업데이트(변경)
    // Return value : void
    // paremeter : 테이블명, 테이블의 카테고리 이름 attribute, 변경할 카테고리 명(EditText), 리스트의 아이템 ID
    // Use : 해당 아이템 클릭 후 이름 변경 시 UPDATE 연산을 통해 이름을 변경하도록 한다.
    //      changeCursor을 통해 리스트 최신화
    public void CtgUpdate(String t_title, int listId) {
        dbManager.updateCategory(listId, t_title);
        ctgDBInit();
    }
    // Method : 카테고리 삭제
    // Return value : void
    // paremeter : 테이블명, 리스트의 아이템 ID
    // Use : 해당 아이템 클릭 후 삭제 시 DELETE 연산을 통해 카테고리를 삭제하도록 한다.
    //      changeCursor을 통해 리스트 최신화
    public void CtgDelete(int listid)
    {
        int[] delete_list = new int[1];
        delete_list[0] = listid;
        dbManager.deleteCategory(delete_list);
        ctgDBInit();
    }

    // Method : 카테고리 체크 부분 삭제
    // Return value : void
    // paremeter : 테이블명, 리스트뷰
    // Use : 각 아이템의 체크유무를 판단하여 체크된 아이템의 아이디를 배열에 저장한 후
    //      배열에 저장된 id값을 통하여 DELETE 연산 수행
    //      changeCursor을 통해 리스트 최신화
    public void CtgcheckDelete(ListView listview)
    {
        ArrayList<Integer> check_arr = new ArrayList<Integer>();
        ctgcheckList = ctgAdapter.addCheckData();
        int check_list[];
        for(int i=0 ; i < listview.getCount() ; i++)
        {
            Log.e("checklistname", ctgcheckList.containsKey(ctgAdapter.getlistName(i)) + "");
            Log.e("checklist",ctgcheckList.containsValue(i)+"");
            if(ctgcheckList.containsKey(ctgAdapter.getlistName(i)))
            {
                check_arr.add(ctgAdapter.getlistId(i));
            }
        }
        // Use : 체크되어 있던 아이디를 저장한 배열의 값을 이용하여 반복문을 돌려 DELETE 연산 수행
        //      changeCursor을 통해 리스트 최신화
        //      더 이상 값이 없는 경우의 무한 루프를 고려 조건으로 반복문 탈출
        check_list = new int[check_arr.size()];
        for(int i=0 ; i <check_arr.size() ; i++)
        {
            check_list[i] = check_arr.get(i);
        }
        dbManager.deleteCategory(check_list);
        ctgDBInit();

    }

    // Method : 카테고리 추가/갱신 조건 검사
    // Return value : void
    // paremeter : EditText
    // Use : 카테고리를 추가하거나 갱신 시 제목의 추가하지 못하는 조건들을 설정
    public boolean CtgTitleCheckable(EditText editText)
    {
            String t_title = editText.getText().toString();
            //Use : 카테고리 이름 중복 처리 (이름이 중복될 경우 다이얼로그로 알려준다)
            if (dbManager.isExistCategoryName(t_title)) {
                AlertDialog.Builder repeat_Dig = new AlertDialog.Builder(CategoryActivity.this);
                repeat_Dig.setTitle("카테고리 이름이 중복됩니다.")
                        .setNegativeButton(R.string.ctgdialog_checkbtn_text, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface indialog, int which) {
                                indialog.cancel();
                            }
                        });
                repeat_Dig.show();
                return false;
            }
            //Use : 카테고리 이름이 8자 이상인 경우 처리 (다이얼로그)
            else if (editText.length() > 8) {
                AlertDialog.Builder eight_Dig = new AlertDialog.Builder(CategoryActivity.this);
                eight_Dig.setTitle("카테고리 글자 수를 8자 이하로 해주십시오.")
                        .setNegativeButton(R.string.ctgdialog_checkbtn_text, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface indialog, int which) {
                                indialog.cancel();
                            }
                        });
                eight_Dig.show();
                return false;
            }
            // Use : EditText에 아무 것도 입력되지 않은 경우
            else if (editText.length() == 0) {
                AlertDialog.Builder null_Dig = new AlertDialog.Builder(CategoryActivity.this);
                null_Dig.setTitle("카테고리 명을 입력해주세요!")
                        .setNegativeButton(R.string.ctgdialog_checkbtn_text, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface indialog, int which) {
                                indialog.cancel();
                            }
                        });
                null_Dig.show();
                return false;
            }

            //Use : 카테고리 이름 처음에 공백이 들어간 경우 처리
            else if(t_title.charAt(0) == ' ')
            {
                AlertDialog.Builder firstblank_Dig = new AlertDialog.Builder(CategoryActivity.this);
                firstblank_Dig.setTitle("카테고리명은 공백문자로 시작할 수 없습니다!")
                        .setNegativeButton(R.string.ctgdialog_checkbtn_text, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface indialog, int which) {
                                indialog.cancel();
                            }
                        });
                firstblank_Dig.show();
                return false;
            }
            //Use : 카테고리 이름 끝에 공백이 들어간 경우 처리
            else if(t_title.charAt(editText.length()-1) == ' ')
            {
                AlertDialog.Builder lastblank_Dig = new AlertDialog.Builder(CategoryActivity.this);
                lastblank_Dig.setTitle("카테고리명은 공백문자로 끝날 수 없습니다!")
                        .setNegativeButton(R.string.ctgdialog_checkbtn_text, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface indialog, int which) {
                                indialog.cancel();
                            }
                        });
                lastblank_Dig.show();
                return false;
            }
            else
            {
                return true;
            }
    }

    // Method : DB 다시 받아오기
    // Return value : void
    // paremeter : void
    // Use : 카테고리 ListView의 추가,삭제,갱신과 같은 변화가 일어날 시 DB를 다시 받아와 리스트를 쉽게 갱신하도록 선언을 분리해놓았다.
    public void ctgDBInit()
    {
        ctgArrayList = dbManager.getCategoryList();
        ctgAdapter = new CategoryListAdapter(this, ctgArrayList);
        // Use : list에 커서 어댑터 연결 및 아이템클릭리스너 설정
        ctg_Listview.setAdapter(ctgAdapter);
        ctgAdapter.notifyDataSetChanged();
    }
    // Method : CheckBox controller
    // Return Value : void
    // Parameter : ckecked: check박스를 보여줄지 숨길지를 결정하는 boolean형 변수
    // Use :  호출부에서 ckeck박스 컨트롤을 요청하면 화면에 반영시켜줌.
    private void setCheckBoxVisibility(boolean checked){
        ctgAdapter.setCheckBoxEnable(checked);
        ctgAdapter.notifyDataSetChanged();
    }

}
