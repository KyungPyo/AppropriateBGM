package com.kp.appropriatebgm.DBController;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
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

        query = "INSERT INTO BGMList(bgm_name, bgm_path, innerfile) VALUES ('인간극장', 'innerfile#', #)";
        query = query.replace("#", Integer.toString(R.raw.human_cinema));
        db.execSQL(query);

        query = "INSERT INTO BGMList(bgm_name, bgm_path, innerfile) VALUES ('함정카드', 'innerfile#', #)";
        query = query.replace("#", Integer.toString(R.raw.trapcard));
        db.execSQL(query);
    }

    // Method : 앱 실행시 BGMList 테이블 무결성 체크
    // Return Value : void
    // Parameter : fileList(단말기에 저장되어있는 음악파일목록 - [0]파일경로, [1]파일명)
    // Use : LogoActivity에서 앱이 실행될 때마다 SQLite에 저장된 외장 BGM 목록과 실제 가지고 있는 파일의 목록이 일치하는지 확인한다.
    public void checkBGMList(ArrayList<String[]> fileList){
        if(fileList.size() != 0) {
            checkBGMFileExist(fileList);
            checkBGMRecordExist(fileList);
        }
    }

    // Method : DB에 저장된 파일경로에 파일이 있는지 확인
    // Return Value : void
    // Parameter : fileList(단말기에 저장되어있는 음악파일목록 - [0]파일경로, [1]파일명)
    // Use : DB에 저장되어 있는 BGM의 경로에 파일이 존재하는지 확인하고 없으면 DB에서 삭제한다.
    private void checkBGMFileExist(ArrayList<String[]> fileList){
        int fileCount = fileList.size();
        StringBuffer query = new StringBuffer();

        query.append("SELECT bgm_path FROM BGMList WHERE bgm_path NOT IN (");
        for (int i = 0; i < fileCount; i++) {
            query.append("'");
            query.append(fileList.get(i)[0]);
            query.append("'");
            if(i+1 != fileCount)
                query.append(",");
        }
        query.append(");");
        String completedQuery = query.toString();
        Log.i("checkBGMFileExist", completedQuery);

        Cursor notExistList = mDataBase.rawQuery(completedQuery, null);

        // 없는 목록을 받아와서 DB에서 지운다
        while(notExistList.moveToNext()){

        }
    }

    // Method : 파일경로에 해당하는 DB 레코드가 존재하는지 확인
    // Return Value : void
    // Parameter : fileList(단말기에 저장되어있는 음악파일목록 - [0]파일경로, [1]파일명)
    // Use : 음악파일이 DB의 BGMList 테이블에 레코드로 등록되어있는지 확인하고, 없으면 Insert한다.
    private void checkBGMRecordExist(ArrayList<String[]> fileList){
        int fileCount = fileList.size();
        StringBuffer query;

        for (int i = 0; i < fileCount; i++) {
            try{
                query = new StringBuffer();
                query.append("INSERT INTO BGMList(bgm_path, bgm_name) VALUES(");
                query.append("'"+fileList.get(i)[0]+"',");
                query.append("'"+fileList.get(i)[1]+"')");
                mDataBase.execSQL(query.toString());
            } catch (SQLiteConstraintException sqlException){
                Log.i("SQLite Error", "이미 존재하는 값 입력 : "+sqlException.toString());
            }
        }

        // 입력 잘됐는지 확인하는 부분(삭제요망)
        String test = "select * from BGMList";
        Cursor cursor = mDataBase.rawQuery(test, null);
        while(cursor.moveToNext()){
            Log.i("잘나오냐??", cursor.getString(1));
        }
    }

    /*****  DB 결과 요청(select)  *****/
    // Method : BGMList 불러오기
    // Return Value : ArrayList<BGMInfo> (BGMList 테이블에 저장된 정보 리스트)
    // Parameter : categoryId(요청한 Activity에서 검색하려는 카테고리 번호)
    // Use : DB에 저장된 BGM 정보를 불러오는 메소드. 원하는 카테고리를 선택해서 요청하고, categoryId가 1인경우에 전체목록 반환
    public ArrayList<BGMInfo> getBGMList(int categoryId){
        ArrayList<BGMInfo> result = new ArrayList<>();
        Cursor cursor;
        String query;
        BGMInfo bgmInfo;

        query = "SELECT * FROM BGMList";
        if (categoryId != 1){
            query = query + " WHERE category_id = " + categoryId;
        }

        cursor = mDataBase.rawQuery(query, null);

        if(cursor == null) {
            return null;
        }

        while(cursor.moveToNext()){
            bgmInfo = new BGMInfo(cursor.getString(0), cursor.getString(1), cursor.getInt(2), cursor.getInt(3));
            result.add(bgmInfo);
        }

        return result;
    }

    // Method : Category List 불러오기
    // Return Value : ArrayList<Category> (BGMList 테이블에 저장된 정보 리스트)
    // Parameter : void
    // Use : DB에 저장된 전체 Category 정보를 불러오는 메소드.
    public ArrayList<Category> getCategoryList(){
        ArrayList<Category> result = new ArrayList<>();
        Cursor cursor;
        String query;
        Category category;

        query = "SELECT * FROM Category";
        cursor = mDataBase.rawQuery(query, null);

        if(cursor == null) {
            return null;
        }

        while (cursor.moveToNext()){
            category = new Category(cursor.getInt(0), cursor.getString(1));
            result.add(category);
        }

        return result;
    }

    // Method : Favorite List 불러오기
    // Return Value : ArrayList<Favorite> (BGMList 테이블에 저장된 정보 리스트)
    // Parameter : void
    // Use : DB에 저장된 전체 Favorite 정보를 불러오는 메소드. bgm_path 값이 없을 때에는 null을 넣어 보내준다.
    public ArrayList<Favorite> getFavoriteList(){
        ArrayList<Favorite> result = new ArrayList<>();
        Cursor cursor;
        String query;
        Favorite favorite;

        query = "SELECT * FROM Favorite order by favorite_id";
        cursor = mDataBase.rawQuery(query, null);

        if(cursor == null) {
            return null;
        }

        while (cursor.moveToNext()){
            favorite = new Favorite(cursor.getInt(0), cursor.getString(1));
            result.add(favorite);
        }

        return result;
    }


    /*****  DB 결과 요청(select)  *****/


}
