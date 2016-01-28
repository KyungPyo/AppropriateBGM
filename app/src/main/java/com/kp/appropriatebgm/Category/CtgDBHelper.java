package com.kp.appropriatebgm.Category;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by GD on 2016-01-28.
 */
// Class : CtgDBHelper (DB 설정 클래스)
// Use : DB의 파일명 및 DB의 초기 테이블 생성의 기능
public class CtgDBHelper extends SQLiteOpenHelper
{
    final static String KEY_ID = "_id";
    final static String KEY_NAME = "name";
    final static String TABLE_NAME = "ctgtable";

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
}

