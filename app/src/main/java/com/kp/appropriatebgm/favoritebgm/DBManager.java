package com.kp.appropriatebgm.favoritebgm;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
/**
 * Created by LG on 2015-12-29.
 */
public class DBManager {

    static final String DB_NAME="AppropriateBGM.db";
    static final int DB_VERSION = 1;

    Context mContext=null;
    //싱글톤
    private  static DBManager mDBManager=null;
    private SQLiteDatabase mDatabase=null;
    private  DBHelper mDbHelper=null;

    public static DBManager getInstance (Context context) {
        if (mDBManager == null){
            mDBManager = new DBManager(context);
        }
        return mDBManager;
    }


    public void insert(String dbName,ContentValues values) {
        mDatabase = mDbHelper.getWritableDatabase(); // db 객체를 얻어온다. 쓰기 가능

        mDatabase.insert(dbName, null, values); // 테이블/널컬럼핵/데이터(널컬럼핵=디폴트)
    }

    // update
    public void update (String dbName,ContentValues values,String where,String [] whereArgs) {
        mDatabase = mDbHelper.getWritableDatabase(); //db 객체를 얻어온다. 쓰기가능
        mDatabase.update(dbName, values, where, whereArgs);

    }

    // delete
    public void delete (String dbName, String where,String[] whereArgs) {
        mDatabase = mDbHelper.getWritableDatabase();
        mDatabase.delete(dbName, where, whereArgs);
    }

    // select
    public Cursor select(String dbName,String [] columns) {

        mDatabase = mDbHelper.getReadableDatabase(); // db객체를 얻어온다. 읽기 전용
        Cursor c = mDatabase.query(dbName, columns, null, null, null, null, null);

        return c;
    }

    public Music selectByMusicId(String dbName,String [] columns,int id){
        Music selected=null;
        Cursor c=mDatabase.query(dbName,columns,"bgm_id=?",new String[]{id+""},null,null,null);
        while(c.moveToNext()){
            Log.d("SelectByID",c.getString(2)+"      "+c.getString(1)+"   "+c.getString(0));
            selected=new Music(c.getString(2),c.getString(1),c.getString(0));
        }
        return selected;
    }

    public Cursor selectByCategoryId(String dbName,String [] columns,String id){
        Cursor c=mDatabase.query(dbName,columns,"category_id=?",new String[]{id},null,null,null);
        return c;
    }

    private DBManager(Context context){
        mContext=context;
        mDbHelper=new DBHelper(mContext,DB_NAME,null,DB_VERSION);
        //createTables();
    }


}
