package com.kp.appropriatebgm;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;



//카테고리 액티비티
public class CategoryActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    CtgDBHelper myHelper;       //DB 파일 생성 및 CREATE 클래스
    SQLiteDatabase db;          // SQLiteDB
    Cursor cursor;              // 커서
    CtgCursorAdapter myAdapter; // 커서어댑터
    FloatingActionButton fab;

    final static String KEY_ID = "_id";
    final static String KEY_NAME = "name";
    final static String TABLE_NAME = "ctgtable";

    CheckBox chkbox;
    ListView list;

    // Use : Select 쿼리 (LIST를 계속 갱신해주는 데 필요한 쿼리)
    final static String querySelectAll = String.format( " SELECT * FROM %s ", TABLE_NAME );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Use : DBHelper 객체 생성 및 db 설정
        myHelper = new CtgDBHelper(this);
        db = myHelper.getWritableDatabase();

        // Use : 커서에 select sql문 설정 및 커서 어댑터 객체 생성
        cursor = db.rawQuery(querySelectAll, null);
        myAdapter = new CtgCursorAdapter ( this, cursor, 0 );

        list = (ListView) findViewById( R.id.lv_name_age );

        // Use : list에 커서 어댑터 연결 및 아이템클릭리스너 설정
        list.setAdapter(myAdapter);
        list.setOnItemClickListener(this);

        // Use : 체크 후 삭제 버튼 (floatingActionButton이다)
        fab = (FloatingActionButton) findViewById(R.id.fab);
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
                CtgcheckDelete(TABLE_NAME, list);
                //fab 버튼이 사라지지 않는 경우를 고려하여 invisible, enable(false) 설정
                view.setVisibility(View.INVISIBLE);
                view.setEnabled(false);
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
        getMenuInflater().inflate(R.menu.menu_category, menu);
        return true;
    }

    //Method : 액션바 버튼 액션 설정
    //Return value : boolean
    //Paremeter : void
    //Use : 액션바(menu_category)에 구현되어 있는 추가 버튼과 삭제 버튼의 기능을 설정하기 위해 사용한다.
    //      추가 버튼(action_add)의 경우 다이얼로그를 띄워 DB에 INSERT 쿼리를 실행하며,
    //      삭제 버튼(action_trash)의 경우 체크박스를 리스트에 띄워 항목을 체크할 수 있도록 한다.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        String text = null;

        switch(id)
        {
            // Use : 추가(action_add) 버튼 클릭하는 경우
            // 다이얼로그를 띄워서 추가 시 다이얼로그의 EditText의 값을 DB에 넣는 INSERT 연산 수행 및 취소 버튼
            case R.id.action_add:
                final AlertDialog.Builder add_Digbuild = new AlertDialog.Builder(CategoryActivity.this);
                add_Digbuild.setTitle("카테고리 추가");
                final EditText inputtxt = new EditText(this);
                inputtxt.setHint("8자 이하로 입력해주시오.");
                inputtxt.setSingleLine(true);       // 한줄로 입력이 되게끔 설정
                inputtxt.setSelectAllOnFocus(true);
                add_Digbuild.setView(inputtxt);
                // Use : 취소 버튼을 누른 경우
                add_Digbuild.setNegativeButton(R.string.ctg_addctl_btn, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                        // Use : 카테고리 추가 기능에서 글자가 8자 이상이거나 아무것도 입력되지 않은 경우
                        //      Edittext의 길이를 확인하여 예외 발생시 다이얼로그를 띄우도록 한다.

                .setPositiveButton(R.string.ctg_add_btn, null);

                add_Digbuild.setMessage(R.string.ctg_add_message);
                final AlertDialog add_Dig = add_Digbuild.create();
                add_Dig.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        Button btnPositive = add_Dig.getButton(AlertDialog.BUTTON_POSITIVE);
                         btnPositive.setOnClickListener(new View.OnClickListener()
                         {
                             @Override
                             public void onClick(View v) {
                                 try {
                                     // Use : EditText의 길이가 8 이상인 경우 다이얼로그 띄움
                                     String c_title = inputtxt.getText().toString();
                                     Log.e("inputtxt", c_title);
                                     Log.e("check", Boolean.toString(CtgcheckRepetition(c_title)));
                                     if (CtgcheckRepetition(c_title) == true) {
                                         AlertDialog.Builder repeat_Dig = new AlertDialog.Builder(CategoryActivity.this);
                                         repeat_Dig.setTitle("카테고리 이름이 중복됩니다.")
                                                 .setNegativeButton("확인", new DialogInterface.OnClickListener() {
                                                     @Override
                                                     public void onClick(DialogInterface indialog, int which) {
                                                         indialog.cancel();
                                                     }
                                                 });
                                         repeat_Dig.show();
                                     } else if (inputtxt.length() > 8) {
                                         AlertDialog.Builder eight_Dig = new AlertDialog.Builder(CategoryActivity.this);
                                         eight_Dig.setTitle("카테고리 글자 수를 8자 이하로 해주십시오.")
                                                 .setNegativeButton("확인", new DialogInterface.OnClickListener() {
                                                     @Override
                                                     public void onClick(DialogInterface indialog, int which) {
                                                         indialog.cancel();
                                                     }
                                                 });
                                         eight_Dig.show();
                                     }
                                     // Use : EditText에 아무 것도 입력되지 않은 경우
                                     else if (inputtxt.length() == 0) {
                                         AlertDialog.Builder null_Dig = new AlertDialog.Builder(CategoryActivity.this);
                                         null_Dig.setTitle("카테고리 명을 입력해주세요!")
                                                 .setNegativeButton("확인", new DialogInterface.OnClickListener() {
                                                     @Override
                                                     public void onClick(DialogInterface indialog, int which) {
                                                         indialog.cancel();
                                                     }
                                                 });
                                         null_Dig.show();
                                     }


                                     // 첫번째 공백 및 마지막 공백 예외 처리 넣을 부분 ************************************************** (완료) 주석만 넣자!
                                     else if(c_title.charAt(0) == ' ')
                                     {
                                         AlertDialog.Builder firstblank_Dig = new AlertDialog.Builder(CategoryActivity.this);
                                         firstblank_Dig.setTitle("카테고리명은 공백문자로 시작할 수 없습니다!")
                                                 .setNegativeButton("확인", new DialogInterface.OnClickListener() {
                                                     @Override
                                                     public void onClick(DialogInterface indialog, int which) {
                                                         indialog.cancel();
                                                     }
                                                 });
                                         firstblank_Dig.show();
                                     }
                                     else if(c_title.charAt(inputtxt.length()-1) == ' ')
                                     {
                                         AlertDialog.Builder lastblank_Dig = new AlertDialog.Builder(CategoryActivity.this);
                                         lastblank_Dig.setTitle("카테고리명은 공백문자로 끝날 수 없습니다!")
                                                 .setNegativeButton("확인", new DialogInterface.OnClickListener() {
                                                     @Override
                                                     public void onClick(DialogInterface indialog, int which) {
                                                         indialog.cancel();
                                                     }
                                                 });
                                         lastblank_Dig.show();
                                     }


                                     // Use : 위의 예외들을 통과한 경우 DB INSERT 연산 수행
                                     else {
                                         CtgInsert(TABLE_NAME, c_title);
                                         add_Dig.dismiss();                                     }

                                 } catch (Exception e) {
                                     e.printStackTrace();
                                 }
                             }
                         });
                    }
                });
                add_Dig.show();
                inputtxt.setText("");
                break;

            // Use : 삭제(action_trash) 버튼 클릭하는 경우
            // 체크 삭제 버튼을 띄우도록 한다. 다시 한번 더 클릭 시 원래 메뉴로 돌아가는 기능
            case R.id.action_trash:
                text = "trash click";
                // Use : 삭제 버튼이 보이지 않을 경우에 휴지통을 누르면 보이도록 한다.
                if(fab.getVisibility() == View.INVISIBLE)
                {
                    fab.setVisibility(View.VISIBLE);
                    fab.setEnabled(true);
                }
                // Use ; 삭제 버튼이 보이는 경우 원래 리스트 상태로 돌아가려고 할 때 숨기도록 한다.
                else if(fab.getVisibility() == View.VISIBLE)
                {
                    fab.setVisibility(View.INVISIBLE);
                    fab.setEnabled(false);
                }
                // Use : 리스트 크기만큼의 반복문을 돌려 각각의 리스트 아이템의 체크박스에 접근하여
                //       체크박스의 숨김 / 표시 여부를 설정한다.
                for(int i=0 ; i<list.getCount() ; i++)
                {
                    ViewGroup child = (ViewGroup) list.getChildAt(i);
                    CheckBox listchk = (CheckBox) child.getChildAt(1);
                    if (listchk.getVisibility() == View.INVISIBLE) {
                        listchk.setVisibility(View.VISIBLE);
                        listchk.setEnabled(true);
                    } else if (listchk.getVisibility() == View.VISIBLE) {
                        listchk.setVisibility(View.INVISIBLE);
                        listchk.setEnabled(false);
                    }

                }

                break;
        }
        return super.onOptionsItemSelected(item);
    }
    // Class : CtgCursorAdapter (리스트에 설정할 커서 어댑터)
    // Use : 리스트에 띄울 데이터를 설정하기 위한 클래스
    class CtgCursorAdapter extends CursorAdapter
    {
        CheckBox tvchk;

        // Method : 커서 어댑터 생성자
        // Use : context(액티비티). c(사용할 커서), flags( 어댑터의 동작을 결정 )
        public CtgCursorAdapter(Context context, Cursor c, int flags) {
            super(context, c, flags);
        }

        // Method : 각 아이템의 속성 지정
        // Return value : void
        // paremeter : view(띄울 뷰), context(액티비티), cursor(커서)
        // Use : 리스트의 아이템의 초기값 및 속성을 설정해주는 기능입니다. 객체가 있는 경우에 호출
        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            TextView tvName = (TextView) view.findViewById( R.id.tv_name );
            tvchk = (CheckBox) view.findViewById( R.id.tv_chk );

            String name = cursor.getString(cursor.getColumnIndex(KEY_NAME));
            tvchk.setVisibility(View.INVISIBLE);


            tvName.setText(name);
        }

        // Method : 리스트 뷰에 표시될 뷰를 설정
        // Return value : View
        // parameter : context(액티비티), cursor(커서), parent(부모 뷴)
        // Use : 리스트 뷰에 표시될 뷰를 설정하는 기능입니다. 뷰 객체가 null인 경우에 호출
        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from( context );
            View v = inflater.inflate( R.layout.list_item, parent, false );
            return v;
        }

    }
    // Class : CtgDBHelper (DB 설정 클래스)
    // Use : DB의 파일명 및 DB의 초기 테이블 생성의 기능
    class CtgDBHelper extends SQLiteOpenHelper
    {
        // Method : 생성자
        // Use : context(액티비티), 디비파일명, db 버전 등을 설정하는 생성자입니다.
        public CtgDBHelper(Context context)
        {
            super(context, "CtgData.db", null, 2); //null : 표준커서 사용시 null 값을 주면 됨
        }

        // Method : DBHelper 객체 생성 시 초기 실행
        // parameter : db (sqlite디비)
        // Use : Category DBTable 생성
        public void onCreate(SQLiteDatabase db)
        {
            // AUTOINCREMENT 속성 사용 시 PRIMARY KEY로 지정한다.
            CtgCreate(TABLE_NAME, KEY_NAME, db);
        }
        // Method : DBHelper 업그레이드
        // parameter : db(sqlite 디비), oldVersion(구버전), newVersion(최신버전)
        // Use : 버전이 맞지 않을 시에 테이블을 내리고 다시 생성하는 기능
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            String query = String.format("DROP TABLE IF EXISTS %s", TABLE_NAME);
            db.execSQL( query );
            onCreate( db );
        }

    }
    // Method : 카테고리 리스트 아이템 클릭
    // Return value : void
    // paremeter : parentView, 클릭 뷰, 클릭 아이템 위치, 클릭 아이템 아이디
    // Use : 아이템 클릭 시 세부 속성을 선택하는 다이얼로그를 띄우도록 한다. (이름 변경, 삭제, 취소)
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long lid) {
        final long itemid = lid;
        final CharSequence[] property_items = {"이름 변경", "삭 제", "취 소"};
        AlertDialog.Builder alertDig = new AlertDialog.Builder(view.getContext());
        alertDig.setTitle(R.string.ctg_long_property);
        alertDig.setItems(property_items, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Log.e("id --------", Long.toString(itemid));
                //Log.e("item --------", Integer.toString(which));
                switch(which)
                {
                    case 0:
                        // Use : 이름 변경의 경우
                        // 이름 변경을 하는 다이얼로그를 띄우며 변경 시 해당 아이템의 아이디를 받아 다이얼로그의 EditText의 값을 DB에 갱신하는 UPDATE 연산 수행
                        final EditText nametxt = new EditText(CategoryActivity.this);
                        nametxt.setSingleLine(true);
                        AlertDialog.Builder name_Dig = new AlertDialog.Builder(CategoryActivity.this)
                                .setTitle("이름 변경");
                        name_Dig.setView(nametxt);

                        name_Dig.setNegativeButton(R.string.ctg_addctl_btn, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        //변경 버튼
                        .setPositiveButton(R.string.ctg_add_btn, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    //UPDATE 메소드
                                    CtgUpdate(TABLE_NAME, KEY_NAME, nametxt, itemid);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });      //취소 버튼

                        name_Dig.setMessage(R.string.ctg_modify_message);
                        name_Dig.show();
                        nametxt.setText("");
                        break;
                    case 1:
                        // Use : 카테고리 삭제의 경우
                        //      해당 아이템의 id를 가져와 DELETE 연산을 수행
                        CtgDelete(TABLE_NAME, itemid);
                        dialog.dismiss();
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
    // Method : 카테고리 추가
    // Return value : void
    // paremeter : 테이블명, 추가할 카테고리 명(EditText)
    // Use : id는 Auto-increment로 자동 생성 및 해당 이름의 카테고리를 추가시키도록 INSERT 연산 수행
    //      changeCursor을 통해 리스트 최신화
    public void CtgInsert(String table, String t_title)
    {
        String query = String.format("INSERT INTO %s values (null, '%s');", TABLE_NAME, t_title);
        db.execSQL(query);

        cursor = db.rawQuery(querySelectAll, null);
        myAdapter.changeCursor(cursor);
    }
    // Method : 카테고리 이름 업데이트(변경)
    // Return value : void
    // paremeter : 테이블명, 테이블의 카테고리 이름 attribute, 변경할 카테고리 명(EditText), 리스트의 아이템 ID
    // Use : 해당 아이템 클릭 후 이름 변경 시 UPDATE 연산을 통해 이름을 변경하도록 한다.
    //      changeCursor을 통해 리스트 최신화
    public void CtgUpdate(String table, String t_title, EditText upname, long listid)
    {
        String cm_title = upname.getText().toString();
        String query = String.format("UPDATE %s set %s = '%s' where _id = %d ;", table, t_title, cm_title , listid);
        db.execSQL(query);

        cursor = db.rawQuery(querySelectAll, null);
        myAdapter.changeCursor(cursor);
    }
    // Method : 카테고리 삭제
    // Return value : void
    // paremeter : 테이블명, 리스트의 아이템 ID
    // Use : 해당 아이템 클릭 후 삭제 시 DELETE 연산을 통해 카테고리를 삭제하도록 한다.
    //      changeCursor을 통해 리스트 최신화
    public void CtgDelete(String table, long listid)
    {
        String query = String.format(
                "DELETE FROM %s where _id = '%d';", table, listid);
        //dialog.dismiss();
        db.execSQL(query);
        cursor = db.rawQuery(querySelectAll, null);
        myAdapter.changeCursor(cursor);
    }
    // Method : 카테고리 테이블 생성
    // Return value : void
    // paremeter : 테이블명, 테이블의 카테고리 이름 attribute, 테이블을 생성할 db
    // Use : DBHelper의 oncreate의 db 매개변수를 받아와 id(auto increment)와 카테고리 이름을 가지는 카테고리 테이블을 생성한다.
    public void CtgCreate(String table, String t_title, SQLiteDatabase sqldb)
    {
        String query = String.format("CREATE TABLE %s ("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "%s TEXT UNIQUE );", TABLE_NAME, KEY_NAME);
        sqldb.execSQL(query);
    }
    // Method : 카테고리 체크 부분 삭제
    // Return value : void
    // paremeter : 테이블명, 리스트뷰
    // Use : 각 아이템의 체크유무를 판단하여 체크된 아이템의 아이디를 배열에 저장한 후
    //      배열에 저장된 id값을 통하여 DELETE 연산 수행
    //      changeCursor을 통해 리스트 최신화
    public void CtgcheckDelete(String table, ListView listview)
    {
        long chk_del[] = new long[100];     // delete 연산을 수행하는 데 필요한 id 배열 선언
        int listsize = listview.getCount();
        int n=0;
        for(int i=0; i < listsize ; i++) {                      // list 크기만큼 반복문을 돌려서 각각의 list에 접근
            ViewGroup child = (ViewGroup) listview.getChildAt(i);
            CheckBox list_chk = (CheckBox) child.getChildAt(1); // 각각 list의 checkbox 객체에 접근
            if(list_chk.isChecked() == true) {                  // 체크가 되어 있을시에 itemid에 현재 체크되어있는 리스트의 id 저장
                long itemid = listview.getItemIdAtPosition(i);
                chk_del[n] = itemid;
                n++;
            }
        }
        // Use : 체크되어 있던 아이디를 저장한 배열의 값을 이용하여 반복문을 돌려 DELETE 연산 수행
        //      changeCursor을 통해 리스트 최신화
        //      더 이상 값이 없는 경우의 무한 루프를 고려 조건으로 반복문 탈출
        for(int i=0; i < chk_del.length; i++)
        {
            String query = String.format(
                    "DELETE FROM %s where _id = '%d';", table, chk_del[i]);
            db.execSQL(query);
            cursor = db.rawQuery(querySelectAll, null);
            myAdapter.changeCursor(cursor);
            if(chk_del[i] == 0)
            {
                break;
            }
        }
    }
    // Method : 카테고리 이름 중복 처리
    // Return value : boolean
    // paremeter : 리스트(카테고리)의 아이템 이름
    // Use : select문을 통해 카텥고리 이름을 검색한 후 해당 카테고리가 존재할 시 true, 아닐 시 false 반환
    public boolean CtgcheckRepetition(String s_title)
    {
        String chkquery = String.format(" SELECT * FROM %s where %s = '%s'; ", TABLE_NAME, KEY_NAME, s_title);
        cursor = db.rawQuery(chkquery, null);
        if(cursor != null && cursor.getCount() != 0)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

}
