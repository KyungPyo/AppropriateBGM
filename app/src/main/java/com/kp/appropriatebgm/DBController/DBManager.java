package com.kp.appropriatebgm.DBController;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.kp.appropriatebgm.R;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by KP on 2016-01-27.
 */
public class DBManager extends SQLiteOpenHelper {

    static final String DB_NAME = "AppropriateBGM_DB";
    static final int DB_VERSION = 1;

    Context mContext = null;
    private static DBManager mDBManager = null;
    private SQLiteDatabase mDataBase = null;

    // Method : 싱글톤 인스턴스 받아오기
    // Return Value : DBManger(사용하는 DB의 인스턴스)
    // Parameter : context(사용하려는 Activity)
    // Use : 앱 전체에서 중복적으로 객체를 생성하는것을 방지하기 위해 싱글톤으로 인스턴스 생성하는 메소드
    public static DBManager getInstance (Context context) {
        if (mDBManager == null){
            mDBManager = new DBManager(context, DB_NAME, null, DB_VERSION);
        }

        return mDBManager;
    }

    // Method : 생성자
    // Return Value : void
    // Parameter : context(사용하려는 Activity), dbName(사용하는 DB명), factory, version(DB버전)
    // Use : 최초 앱 실행되면 같이 실행되어 객체가 하나 만들어진다. 최초실행이면 DB를 생성하면서 onCreate를 실행한다.
    private DBManager (Context context, String dbName, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, dbName, factory, version);
        mContext = context;

        // DB 생성 및 열기
        mDataBase = getWritableDatabase();
    }

    // DB 최초 생성 이벤트
    @Override
    public void onCreate(SQLiteDatabase db) {
        String[] SQLquery = null;
        // raw 에 있는 테이블 생성 SQL문이 저장되어있는 Text파일 불러오기(sqlite_create.txt)
        InputStream inputStream = mContext.getResources().openRawResource(R.raw.sqlite_create);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        int i;
        try {
            // 파일 내용을 읽어서 byte스트림에 저장
            i = inputStream.read();
            while (i != -1) {
                byteArrayOutputStream.write(i);
                i = inputStream.read();
            }

            // 파일을 읽어서 String형으로 저장한 후 ; 를 기준으로 문장을 나눠 저장한다.
            String temp = new String(byteArrayOutputStream.toByteArray(), "UTF-8");
            SQLquery = temp.split(";");
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 파일 내용이 있으면 쿼리문 실행
        if (SQLquery != null) {
            // 초기 설정파일 sqlite_create.txt 파일 읽어온 내용을 실행
            for( int n=0; n<SQLquery.length-1; n++) // split 후 맨 뒤에 아무내용없는 값 제외 -1
                db.execSQL(SQLquery[n]);

            // 내장 BGM 파일 DB 등록
            insertInnerBGM(db);

            Log.i("query!!", "init success");

        } else {
            // 파일 내용이 없으면 종료
            Log.e("query!!", "SQLquery is null");
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    // Method : 내장 음악파일추가
    // Return Value : void
    // Parameter : SQLiteDatabase(앱에서 사용하는 DB)
    // Use : 내장음악파일의 정보를 처음 실행할 때 DB에 입력하는 역할
    private void insertInnerBGM(SQLiteDatabase db){
        String query;

        query = "INSERT INTO BGMList(bgm_name, bgm_path, innerfile) VALUES ('인간극장', 'innerfile', #)";
        query = query.replace("#", Integer.toString(R.raw.human_cinema));
        db.execSQL(query);

        query = "INSERT INTO BGMList(bgm_name, bgm_path, innerfile) VALUES ('함정카드', 'innerfile', #)";
        query = query.replace("#", Integer.toString(R.raw.trapcard));
        db.execSQL(query);
    }

    /*****  DB 결과 요청(select)  *****/
    public ArrayList<BGMInfo> getBGMList(int categoryId){
        Cursor cursor;
        String query;
        BGMInfo bgmInfo;
        ArrayList<BGMInfo> result = new ArrayList<>();

        query = "SELECT bgm_id, bgm_name, bgm_path, innerfile FROM BGMList";
        if (categoryId != 1){
            query = query + " WHERE category_id = " + categoryId;
        }

        cursor = mDataBase.rawQuery(query, null);

        if(cursor != null){
            while(cursor.moveToNext()){
                bgmInfo = new BGMInfo(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getInt(3), cursor.getInt(4));
                result.add(bgmInfo);
            }
        }

        return result;
    }

    /*****  DB 결과 요청(select)  *****/
}
