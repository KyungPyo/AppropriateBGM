package com.kp.appropriatebgm.DBController;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import com.kp.appropriatebgm.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by KP on 2016-01-27.
 */
public class DBManager extends SQLiteOpenHelper {

    static final String DB_NAME = "AppropriateBGM_DB";
    static final int DB_VERSION = 5;
    final String recordtempFilePath = Environment.getExternalStorageDirectory() + File.separator + R.string.app_name + File.separator + "record_temp.mp3";

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
        mDataBase = db;

        SQLiteCreateQuery();
        SQLiteInsertQuery();

        // 내장 BGM 파일, 기본 카테고리 DB 등록
        insertInnerBGM();
        insertBasicCategory();

        // 임시 녹음파일은 추가되지 않도록 미리 추가 (없으면)
        insertBanList(recordtempFilePath);

        Log.i("DB Created", "init success");
    }

    // Method : DB Table 생성
    // Return Value : void
    // Parameter : void
    // Use : raw에 저장되어있는 파일(sqlite_create.txt)을 읽어서 테이블 생성 쿼리문을 실행한다.
    public void SQLiteCreateQuery(){
        String[] SQLqueryCreate = null;
        ByteArrayOutputStream osCreate = new ByteArrayOutputStream();

        // raw 에 있는 테이블 생성 SQL문이 저장되어있는 Text파일 불러오기(sqlite_create.txt)
        InputStream inputStream = mContext.getResources().openRawResource(R.raw.sqlite_create);

        // 파일 내용을 읽어서 byte스트림에 저장
        int i;
        try {
            i = inputStream.read();
            while (i != -1) {
                osCreate.write(i);
                i = inputStream.read();
            }
            inputStream.close();

            // 파일을 읽어서 String형으로 저장한 후 ; 를 기준으로 문장을 나눠 저장한다.
            String temp = new String(osCreate.toByteArray(), "UTF-8");
            SQLqueryCreate = temp.split(";");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 파일 내용이 있으면 쿼리문 실행
        if (SQLqueryCreate != null) {
            // 초기 설정파일 sqlite_create.txt, sqlite_insert.txt 파일 읽어온 내용을 실행
            for (int n = 0; n < SQLqueryCreate.length; n++) // split 개수만큼 반복
                mDataBase.execSQL(SQLqueryCreate[n]);
        }
    }

    // Method : DB에 최초 값 입력
    // Return Value : void
    // Parameter : void
    // Use : raw에 저장되어있는 파일(sqlite_insert.txt)을 읽어서 최초에 입력되어야 하는 값들을 입력해 놓는다.
    //       기본 카테고리 및 즐겨찾기 최대 개수만큼 null값으로 추가
    public void SQLiteInsertQuery(){
        String[] SQLqueryInsert = null;
        ByteArrayOutputStream osInsert = new ByteArrayOutputStream();

        // Insert 문이 저장되어있는 Text파일 불러오기(sqlite_insert.txt)
        InputStream inputStream = mContext.getResources().openRawResource(R.raw.sqlite_insert);

        int i;
        try {
            i = inputStream.read();
            while (i != -1) {
                osInsert.write(i);
                i = inputStream.read();
            }
            inputStream.close();

            // 파일을 읽어서 String형으로 저장한 후 ; 를 기준으로 문장을 나눠 저장한다.
            String temp = new String(osInsert.toByteArray(), "UTF-8");
            SQLqueryInsert = temp.split(";");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 파일 내용이 있으면 쿼리문 실행
        if (SQLqueryInsert != null) {
            // 초기 설정파일 sqlite_create.txt, sqlite_insert.txt 파일 읽어온 내용을 실행
            for (int n = 0; n < SQLqueryInsert.length; n++) // split 개수만큼 반복
                mDataBase.execSQL(SQLqueryInsert[n]);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        mDataBase = db;
        if (oldVersion  < 4) {  // version 1.1.2 미만
            dropTableDB();           // 테이블 삭제
            SQLiteCreateQuery();    // 테이블 생성 안된것이 있으면 생성
            SQLiteInsertQuery();
        }
        if (oldVersion < 5) {   // version 2.0.0 미만
            String deleteQuery = "DELETE FROM Favorite WHERE bgm_path=null";

            try {
                mDataBase.execSQL(deleteQuery);
                mDataBase.execSQL("VACUUM");
            } catch (SQLiteException e) {
                Log.e("update", e.toString());
                e.printStackTrace();
            }
        }
        insertInnerBGM();
        insertBasicCategory();
        // 임시 녹음파일은 추가되지 않도록 미리 추가 (없으면)
        insertBanList(recordtempFilePath);
    }

    // Method : 내장 음악파일추가
    // Return Value : void
    // Parameter : SQLiteDatabase(앱에서 사용하는 DB)
    // Use : 내장음악파일의 정보를 처음 실행할 때 DB에 입력하는 역할(이미 들어있는 내장파일 지웠다 다시입력)
    private void insertInnerBGM(){
        Log.i("asdf", "insert");
        InnerBgmRegister innerBgmRegister = new InnerBgmRegister();
        StringBuffer query = new StringBuffer();

        for (int i=0; i<innerBgmRegister.getListSize(); i++) {
            try {
                query.append("INSERT INTO BGMList(bgm_name, bgm_path, innerfile) VALUES (\"");
                query.append(innerBgmRegister.getInnerBgmName(i));
                query.append("\", \"#\", \"#\")");
                mDataBase.execSQL(query.toString().replace("#", innerBgmRegister.getInnerBgmCode(i)));
                query.delete(0, query.length());
            } catch (SQLiteConstraintException e) {
                // 이미 DB에 같은 값이 존재하는경우
                Log.i("InnerFileDB_insert", "내장파일이 이미 추가되었음");
                query.delete(0, query.length());
            }
        }
    }

    private void deleteInnerBGM(){
        Cursor cursor;
        String query;
        StringBuffer deleteQuery = new StringBuffer();
        ArrayList<String> deleteList = new ArrayList<>();

        query = "SELECT bgm_path FROM BGMList";

        try {
            cursor = mDataBase.rawQuery(query, null);

            while(cursor.moveToNext()){
                if(isNumber(cursor.getString(0))){
                    deleteList.add(cursor.getString(0));
                }
            }
            cursor.close();

            deleteQuery.append("DELETE FROM BGMList WHERE bgm_path in (");
            for (int i=0; i<deleteList.size(); i++) {
                Log.d("delete!!", deleteList.get(i));
                deleteQuery.append("\""+deleteList.get(i)+"\"");
                if(i<deleteList.size()-1)
                    deleteQuery.append(",");
            }
            deleteQuery.append(")");
            mDataBase.execSQL(query.toString());
            mDataBase.execSQL("VACUUM");

        } catch (SQLiteException e){
            e.printStackTrace();
        }
    }

    private boolean isNumber(String str){
        try{
            Double.parseDouble(str) ;
            return true ;
        }catch(NumberFormatException e){
            return false;
        }
    }

    // Method : 기본 카테고리 추가
    // Return Value : void
    // Parameter : SQLiteDatabase(앱에서 사용하는 DB)
    // Use : 기본적으로 사용할 수 있는 카테고리 몇가지 추가 및 내장 BGM에 카테고리 업데이트
    //       InnerBgmRegister 클래스에 해당되는 정보가 전부 저장되어 있다.
    private void insertBasicCategory(){
        String[] basicCategories = {"웃긴", "슬픈", "공포", "기쁜", "웅장"};
        InnerBgmRegister innerBgmRegister = new InnerBgmRegister();

        for (int i=0; i<basicCategories.length; i++) {
            insertCategory(basicCategories[i]);
        }

        for (int j=0; j<innerBgmRegister.getListSize(); j++) {
            String[] bgmPath = new String[1];
            bgmPath[0] = innerBgmRegister.getInnerBgmCode(j).toString();
            updateBgmCategory(innerBgmRegister.getInnerBgmCategory(j), bgmPath);
        }
    }

    // Method : 앱 실행시 BGMList 테이블 무결성 체크
    // Return Value : void
    // Parameter : fileList(단말기에 저장되어있는 음악파일목록 - [0]파일경로, [1]파일명)
    // Use : LogoActivity에서 앱이 실행될 때마다 SQLite에 저장된 외장 BGM 목록과 실제 가지고 있는 파일의 목록이 일치하는지 확인한다.
    public void checkBGMList(ArrayList<String[]> fileList){
        if(fileList.size() > 0) {
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

        try {
            query.append("SELECT bgm_path FROM BGMList WHERE innerfile=0 AND bgm_path NOT IN (");
            for (int i = 0; i < fileCount; i++) {
                query.append("\"");
                query.append(fileList.get(i)[0]);
                query.append("\"");
                if (i + 1 != fileCount)
                    query.append(",");
            }
            query.append(");");
            String completedQuery = query.toString();

            Cursor notExistList = mDataBase.rawQuery(completedQuery, null);

            // 없는 목록을 받아와서 DB에서 지운다. 먼저 없는 BGM 리스트를 String으로 만든다.
            StringBuffer deleteList = new StringBuffer();
            File existCheck;
            while (notExistList.moveToNext()) {
                existCheck = new File(notExistList.getString(0));
                // 해당 파일이 정말 존재하지 않으면(미디어풀 DB에 아직 등록되지 않았을 수도 있기 때문에 한번 더 체크)
                if ( !existCheck.isFile() ) {
                    // 삭제 리스트에 추가
                    deleteList.append("\""+notExistList.getString(0)+"\"");
                    if (!notExistList.isLast()) {
                        deleteList.append(",");
                    }
                }
            }
            notExistList.close();

            if (deleteList.length() > 1) {
                // 즐겨찾기 테이블에서 먼저 해당 bgm_path를 참조하고 있는 레코드 값을 null로 만들어준다.
                query = new StringBuffer();
                query.append("UPDATE Favorite SET bgm_path = null WHERE bgm_path in(");
                query.append(deleteList.toString());
                query.append(")");

                mDataBase.execSQL(query.toString());

                // BGMList 테이블에서 삭제해야 할 bgm_path를 가진 레코드를 삭제한다.
                query = new StringBuffer();
                query.append("DELETE FROM BGMList WHERE bgm_path in(");
                query.append(deleteList.toString());
                query.append(")");

                mDataBase.execSQL(query.toString());
                mDataBase.execSQL("VACUUM");
            }
        } catch (SQLiteException e){
            Log.e("checkBGMFileExist", e.toString());
        }

    }

    // Method : 파일경로에 해당하는 DB 레코드가 존재하는지 확인
    // Return Value : void
    // Parameter : fileList(단말기에 저장되어있는 음악파일목록 - [0]파일경로, [1]파일명)
    // Use : 음악파일이 DB의 BGMList 테이블에 레코드로 등록되어있는지 확인하고, 없으면 Insert한다.
    private void checkBGMRecordExist(ArrayList<String[]> fileList){
        int fileCount = fileList.size();
        StringBuffer query;
        String[] bannedExtend = {"3gp", "avi", "mp4", "mpg", "mpeg", "mpe", "wmv", "asf", "asx", "flv", "mov"};
        boolean isVideoFile;

        for (int i = 0; i < fileCount; i++) {
            isVideoFile = false;
            String filepath = fileList.get(i)[0];
            String filename = fileList.get(i)[1];
            String[] splitFileName = filename.split("\\.");
            String fileExtend = splitFileName[splitFileName.length-1];     // 파일 확장자
            Log.d(filename, filepath);

            if(filename.equals("record_temp.mp3")){ // 임시 녹음파일이면 다음파일로
                continue;
            }else if(isBannedFile(filepath)){      // 등록금지된 파일이면 다음파일로
                continue;
            } else {
                for (int j = 0; j < bannedExtend.length; j++) {
                    // 금지된 동영상 확장자들과 현재 파일의 확장자를 소문자로 변환한것을 비교하여 해당되는지 확인
                    if (bannedExtend[j].equals(fileExtend.toLowerCase())) {
                        isVideoFile = true;
                        break;
                    }
                }
            }

            if (isVideoFile) {  // 파일이 동영상이면 다음파일로
                continue;
            } else {            // 동영상파일이 아니면 DB에 추가
                // 파일명에서 확장자 빼고 저장. ( . 으로 나눈 파일명에서 첫번째)
                try {
                    filename = splitFileName[0];
                    query = new StringBuffer();
                    query.append("INSERT INTO BGMList(bgm_path, bgm_name) VALUES(");
                    query.append("\"" + filepath + "\",");
                    query.append("\"" + filename + "\")");
                    mDataBase.execSQL(query.toString());
                } catch (SQLiteConstraintException e) {
                    Log.i("SQLite Error", "이미 DB에 존재함 : " + e.toString());
                } catch (SQLiteException e) {
                    Log.e("checkBGMRecordExist", e.toString());
                } catch (ArrayIndexOutOfBoundsException e){
                    Log.e("checkBGMRecordExist", e.toString());
                }
            }
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

        try{
            cursor = mDataBase.rawQuery(query, null);

            if(cursor == null) {
                return null;
            }

            while(cursor.moveToNext()){
                bgmInfo = new BGMInfo(cursor.getString(0), cursor.getString(1), cursor.getInt(2), cursor.getInt(3));
                result.add(bgmInfo);
            }
            cursor.close();

            return result;
        } catch (SQLiteException e){
            Log.e("getBGMList", e.toString());
            return null;
        }
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
        try {
            cursor = mDataBase.rawQuery(query, null);

            if (cursor == null) {
                return null;
            }

            while (cursor.moveToNext()) {
                category = new Category(cursor.getInt(0), cursor.getString(1));
                result.add(category);
            }
            cursor.close();

            return result;
        } catch (SQLiteException e){
            Log.e("getCategoryList", e.toString());
            return null;
        }
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

        query = "SELECT f.favorite_id, f.bgm_path, b.bgm_name FROM favorite AS f LEFT JOIN bgmlist AS b " +
                "ON f.bgm_path = b.bgm_path ORDER BY f.favorite_id";

        try {
            cursor = mDataBase.rawQuery(query, null);

            if (cursor == null) {
                return null;
            }

            // Favorite 개수만큼 반복하면서 해당 번호에 설정된 즐겨찾기가 있으면 값을 넣어서 보내고 없으면 null로 보낸다.
            while(cursor.moveToNext()){
                favorite = new Favorite(cursor.getInt(0), cursor.getString(1), cursor.getString(2));
                result.add(favorite);
            }
            cursor.close();

            return result;
        } catch (SQLiteException e) {
            Log.e("getFavoriteList", e.toString());
            return null;
        }
    }

    // Method : Favorite List 불러오기(값이 있는 것만)
    // Return Value : ArrayList<Favorite> (BGMList 테이블에 저장된 정보 리스트)
    // Parameter : void
    // Use : DB에 저장된 전체 Favorite 정보를 불러오는 메소드. bgm_path 값이 있는 것만 보내준다.
    public ArrayList<Favorite> getFavoriteListNotNull(){
        ArrayList<Favorite> result = new ArrayList<>();
        Cursor cursor;
        String query;
        Favorite favorite;

        query = "SELECT f.favorite_id, f.bgm_path, b.bgm_name, b.innerfile FROM favorite AS f JOIN bgmlist AS b " +
                "ON f.bgm_path = b.bgm_path ORDER BY f.favorite_id";

        try {
            cursor = mDataBase.rawQuery(query, null);

            if (cursor == null) {
                return null;
            }

            // Favorite 개수만큼 반복하면서 해당 번호에 설정된 즐겨찾기가 있으면 값을 넣어서 보낸다.(값이 있는 것만)
            while(cursor.moveToNext()){
                favorite = new Favorite(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getInt(3));
                result.add(favorite);
            }
            cursor.close();

            return result;
        } catch (SQLiteException e) {
            Log.e("getFavoriteListNotNull", e.toString());
            return null;
        }
    }

    // Method : 카테고리명이 중복되는지 확인
    // Return Value : boolean(중복되면 true, 중복안되면 false)
    // Parameter : categoryName(중복여부 확인하려는 카테고리명)
    // Use : CategoryActivity에서 카테고리명 중복체크 요청을 하면 해당 카테고리명으로 DB에서 검색하여 중복되는 값이 있는지 개수를 확인한다.
    public boolean isExistCategoryName(String categoryName){
        String query;
        Cursor cursor;

        query = "SELECT COUNT(*) FROM Category WHERE category_name = \""+categoryName+"\"";
        try {
            cursor = mDataBase.rawQuery(query, null);

            cursor.moveToNext();
            if (cursor.getInt(0) == 0) {
                cursor.close();
                return false;
            } else {
                cursor.close();
                return true;
            }
        } catch (SQLiteException e){
            // 오류가 난 경우는 일단 중복값이 있다고 반환한다.
            Log.e("isExistCategoryName", e.toString());
            return true;
        }
    }

    // Method : 파일명이 중복되는지 확인
    // Return Value : boolean(중복되면 true, 중복안되면 false)
    // Parameter : fileName(중복여부 확인하려는 파일명)
    // Use : RecordActivity에서 파일명 중복체크 요청을 하면 해당 파일명으로 DB에서 검색하여 중복되는 값이 있는지 개수를 확인한다.
    public boolean isExistFileName(String fileName){
        String query;
        Cursor cursor;

        query = "SELECT COUNT(*) FROM BGMList WHERE bgm_name = \""+fileName+"\"";
        try {
            cursor = mDataBase.rawQuery(query, null);

            cursor.moveToNext();
            if (cursor.getInt(0) == 0) {
                cursor.close();
                return false;
            } else {
                cursor.close();
                return true;
            }
        } catch (SQLiteException e){
            // 오류가 난 경우는 일단 중복값이 있다고 반환한다.
            Log.e("isExistFileName", e.toString());
            return true;
        }
    }

    // Method : 내장파일인지 여부 확인
    // Return Value : boolean(내장파일이면 true, 아니면 false)
    // Parameter : path(DB에서 내장파일 여부 확인하려는 음악의 path[PrimaryKey])
    // Use : BGMList 테이블의 PrimaryKey인 bgm_path를 받아서 조회하여 내장파일인지 확인한다.
    public boolean isInnerfile(String path){
        String query;
        Cursor cursor;

        query = "SELECT innerfile FROM BGMList WHERE bgm_path = \""+path+"\"";
        try {
            cursor = mDataBase.rawQuery(query, null);

            cursor.moveToNext();
            if (cursor.getInt(0) == 0) {    // 내장파일이 아닌 경우 0이 기본값으로 입력되어있다.
                cursor.close();
                return false;
            } else {
                cursor.close();
                return true;
            }
        } catch (SQLiteException e){
            // 오류가 난 경우는 일단 외장파일이라고 판단한다. 나중에 MusicPlayer에서 재생하기전에 파일확인을 한번 더 한다.
            Log.e("isInnerfile", e.toString());
            return false;
        }
    }

    // Method : 리스트에 출력 금지된 파일인지 확인
    // Return Value : boolean(DB등록 금지된 파일이면 true, 아니면 false)
    // Parameter : path(DB에서 내장파일 여부 확인하려는 음악의 path[PrimaryKey])
    // Use : 메인화면의 리스트에서 파일삭제를 할 경우에 등록금지 리스트에 추가되는데
    //       현재 확인하려는 파일이 등록금지 리스트에 추가되었는지 확인
    public boolean isBannedFile(String path){
        String query;
        Cursor cursor;

        query = "SELECT COUNT(*) FROM BanList WHERE bgm_path = \""+path+"\"";
        try {
            cursor = mDataBase.rawQuery(query, null);

            cursor.moveToNext();
            if (cursor.getInt(0) == 0) {
                cursor.close();
                return false;
            } else {
                cursor.close();
                return true;
            }
        } catch (SQLiteException e){
            // 오류가 난 경우는 일단 금지되지 않았다고 반환한다.
            Log.e("isBannedFile", e.toString());
            return false;
        }
    }
    /*****  DB 결과 요청(select)  *****/


    /*****  DB 등록 요청(insert/update)  *****/

    // Method : Favorite 등록
    // Return Value : void
    // Parameter :bgmPath(등록할 bgm경로)
    // Use : 즐겨찾기 등록을 요청하면 DB에 입력받은 bgm의 경로를 새롭게 등록한다.
    public void insertFavorite(String bgmPath){
        StringBuffer query = new StringBuffer();

        query.append("INSERT INTO Favorite(bgm_path) VALUES (\"");
        query.append(bgmPath);
        query.append("\")");

        try {
            mDataBase.execSQL(query.toString());
        } catch (SQLiteException e) {
            Log.e("insertBGM", e.toString());
        }
    }

    // Method : Favorite 수정
    // Return Value : void
    // Parameter : favoriteId(업데이트할 favoriteId), bgmPath(등록할 bgm경로)
    // Use : 즐겨찾기 수정을 요청하면 DB에 해당 번호에 입력받은 bgm의 경로를 등록한다.
    public void updateFavorite(int favoriteId, String bgmPath){
        StringBuffer query = new StringBuffer();

        if (bgmPath != null){   // Favorite 등록
            query.append("UPDATE favorite SET bgm_path=\"");
            query.append(bgmPath + "\"");
        } else {                // Favorite 삭제 (1.1.2 이하 버전)
            query.append("UPDATE favorite SET bgm_path=null");
        }
        query.append(" WHERE favorite_id=");
        query.append(favoriteId);

        try {
            mDataBase.execSQL(query.toString());
        } catch (SQLiteException e) {
            Log.e("setFavorite", e.toString());
        }
    }

    // Method : 새로운 BGM을 DB에 등록
    // Return Value : void
    // Parameter : bgmPath(새로운 BGM의 파일경로), bgmName(BGM의 이름), categoryId(새로운 BGM의 카테고리 분류번호)
    // Use : 새로운 BGM을 DB에 추가한다. RecordActivity에서 녹음이 완료되면 사용
    public void insertBGM(String bgmPath, String bgmName, int categoryId){
        StringBuffer query = new StringBuffer();

        query.append("INSERT INTO BGMList(bgm_path, bgm_name, category_id) VALUES (\"");
        query.append(bgmPath);
        query.append("\", \"");
        query.append(bgmName);
        query.append("\", ");
        query.append(categoryId);
        query.append(")");

        try {
            mDataBase.execSQL(query.toString());
        } catch (SQLiteException e) {
            Log.e("insertBGM", e.toString());
        }
    }

    // Method : 새 카테고리 DB에 추가
    // Return Value : void
    // Parameter : categoryName(새로 추가할 카테고리명)
    // Use : CategoryActivity에서 새로 추가할 카테고리명을 받아서 DB에 추가한다.
    //       카테고리명으로 검색하여 중복되는 이름이 있는지 확인한다. 중복되는 값이 있으면 추가되지 않는다.
    public void insertCategory(String categoryName){
        String query;
        Cursor cursor;
        try {
            query = "SELECT COUNT(*) FROM Category WHERE category_name=\""+categoryName+"\"";
            cursor = mDataBase.rawQuery(query, null);
            cursor.moveToNext();
            if (cursor.getInt(0) == 0) {    // 중복되는 카테고리명이 없는 경우에만 추가
                query = "INSERT INTO Category(category_name) VALUES (\""+categoryName+"\")";
                mDataBase.execSQL(query);
            } else {
                Log.i("insertCategory", "카테고리명 중복");
            }
            cursor.close();
        } catch (SQLiteException e){
            Log.e("insertCategory", e.toString());
        }
    }

    // Method : DB에서 카테고리명 변경
    // Return Value : void
    // Parameter : categoryId(변경할 카테고리의 ID), categoryName(변경되고 난 뒤의 카테고리명)
    // Use : CategoryActivity에서 변경할 카테고리의 ID와 카테고리명을 받아서 DB에 갱신한다.
    public void updateCategory(int categoryId, String categoryName){
        String query;
        Cursor cursor;

        try {
            query = "SELECT COUNT(*) FROM Category WHERE category_name=\""+categoryName+"\"";
            cursor = mDataBase.rawQuery(query, null);
            cursor.moveToNext();
            if (cursor.getInt(0) == 0) {    // 중복되는 카테고리명이 없는 경우에만 변경
                query = "UPDATE Category SET category_name = \"" + categoryName + "\" WHERE category_id = " + categoryId;
                mDataBase.execSQL(query);
            } else {
                Log.i("insertCategory", "카테고리명 중복");
            }
            cursor.close();
        } catch (SQLiteException e){
            Log.e("updateCategory", e.toString());
        }
    }

    // Method : DB에서 BGMList의 레코드들 카테고리 변경
    // Return Value : void
    // Parameter : categoryId(바꾸려는 카테고리ID), bgmPath(변경할 BGM의 Path. 여러개 가능)
    // Use : MainActivity에서 카테고리ID와 BGM Path를 받아서 해당 카테고리 번호로 카테고리를 변경한다.
    public void updateBgmCategory(int categoryId, String[] bgmPath){
        StringBuffer query = new StringBuffer();
        query.append("UPDATE BGMList SET category_id = ");
        query.append(categoryId);
        query.append(" WHERE bgm_path in(");
        for(int i=0; i<bgmPath.length; i++) {
            query.append("\""+bgmPath[i]+"\"");
            if(i+1 < bgmPath.length)   // 마지막이 아니면
                query.append(",");
        }
        query.append(")");

        try {
            mDataBase.execSQL(query.toString());
        } catch (SQLiteException e){
            Log.e("updateBgmCategory", e.toString());
        }
    }

    // Method : DB 등록되지 않게 BanList에 추가
    // Return Value : void
    // Parameter : banPath(등록금지시킬 파일의 경로)
    // Use : BGMList 테이블에 등록되지 않도록 BanList에 추가한다. 여기에 추가되어 있으면 파일 검색 시 DB에 추가하지 않는다.
    public void insertBanList(String banPath){
        String query;
        Cursor cursor;
        try {
            query = "SELECT COUNT(*) FROM BanList WHERE bgm_path=\""+banPath+"\"";
            cursor = mDataBase.rawQuery(query, null);
            cursor.moveToNext();
            if (cursor.getInt(0) == 0) {    // 중복되는 레코드가 없는 경우에만 추가
                query = "INSERT INTO BanList(bgm_path) VALUES (\"" + banPath + "\")";
                mDataBase.execSQL(query);
            }
            cursor.close();
        } catch (SQLiteException e){
            Log.e("insertCategory", e.toString());
        }
    }
    /*****  DB 등록 요청(insert/update)  *****/


    /*****  DB 레코드 삭제(delete)  *****/

    // Method : 즐겨찾기 삭제
    // Return Value : void
    // Parameter : favoriteIdList(삭제할 즐겨찾기 번호 목록을 가지고 있는 배열)
    // Use : CategoryActivity에서 삭제할 즐겨찾기를 여러개 선택해서 삭제할 때 사용한다. 한개도 삭제 가능하다.
    public void deleteFavorite(int[] favoriteIdList){
        StringBuffer deleteQuery = new StringBuffer();

        deleteQuery.append("DELETE FROM Favorite WHERE favorite_id in(");
        for(int i=0; i<favoriteIdList.length; i++){
            deleteQuery.append(favoriteIdList[i]);
            if(i+1 < favoriteIdList.length) {
                deleteQuery.append(",");
            }
        }
        deleteQuery.append(")");

        try {
            mDataBase.execSQL(deleteQuery.toString());
            mDataBase.execSQL("VACUUM");
        } catch (SQLiteException e) {
            Log.e("deleteCategory", e.toString());
        }
    }

    // Method : 카테고리 삭제
    // Return Value : void
    // Parameter : categoryIdList(삭제할 카테고리 번호 목록을 가지고 있는 배열)
    // Use : CategoryActivity에서 삭제할 카테고리를 여러개 선택해서 삭제할 때 사용한다. 한개도 삭제 가능하다.
    //       카테고리 삭제 시 해당 카테고리에 포함되는 BGM들은 분류안됨인 2번 카테고리로 바꿔준다.
    public void deleteCategory(int[] categoryIdList){
        StringBuffer updateQuery = new StringBuffer();
        StringBuffer deleteQuery = new StringBuffer();

        updateQuery.append("UPDATE BGMList SET category_id=2 WHERE category_id in(");
        deleteQuery.append("DELETE FROM Category WHERE category_id in(");
        for(int i=0; i<categoryIdList.length; i++){
            updateQuery.append(categoryIdList[i]);
            deleteQuery.append(categoryIdList[i]);
            if(i+1 < categoryIdList.length) {
                updateQuery.append(",");
                deleteQuery.append(",");
            }
        }
        updateQuery.append(")");
        deleteQuery.append(")");

        try {
            mDataBase.execSQL(updateQuery.toString());
            mDataBase.execSQL(deleteQuery.toString());
            mDataBase.execSQL("VACUUM");
        } catch (SQLiteException e) {
            Log.e("deleteCategory", e.toString());
        }
    }

    // Method : BGMList에서 레코드 삭제 후 BanList 등록
    // Return Value : void
    // Parameter : bgmPath(삭제할 음악파일 경로. 여러개 가능)
    // Use : MainActivity에서 삭제할 음악파일을 여러개 선택해서 삭제할 때 사용한다. 한개도 삭제 가능하다.
    //       파일삭제를 실행하면 실제 파일은 삭제되지 않고 선택된 파일들이 등록금지 리스트에 추가되어
    //       다시 리스트에 나타나지 않는다.
    public void deleteBGMFile(String[] bgmPath){
        // DB 레코드 삭제
        StringBuffer query = new StringBuffer();

        query.append("DELETE FROM BGMList WHERE bgm_path in(");
        for(int i=0; i<bgmPath.length; i++){
            query.append("\""+bgmPath[i]+"\"");
            if(i+1 < bgmPath.length)
                query.append(",");
        }
        query.append(")");

        try {
            mDataBase.execSQL(query.toString());
            mDataBase.execSQL("VACUUM");
        } catch (SQLiteException e) {
            Log.e("deleteBGMFile 레코드삭제", e.toString());
        }

        // BanList에 레코드 추가
        for (int j = 0; j < bgmPath.length; j++) {
            insertBanList(bgmPath[j]);
        }
    }

    // Method : BGM파일 및 DB 삭제(파일 진짜 삭제)
    // Return Value : void
    // Parameter : bgmPath(삭제할 음악파일 경로. 여러개 가능)
    // Use : MainActivity에서 삭제할 음악파일을 여러개 선택해서 삭제할 때 사용한다. 한개도 삭제 가능하다.
    public void removeBGMFile_Old(String[] bgmPath){
        // DB 레코드 삭제
        StringBuffer query = new StringBuffer();

        query.append("DELETE FROM BGMList WHERE bgm_path in(");
        for(int i=0; i<bgmPath.length; i++){
            query.append("\""+bgmPath[i]+"\"");
            if(i+1 < bgmPath.length)
                query.append(",");
        }
        query.append(")");

        try {
            mDataBase.execSQL(query.toString());
            mDataBase.execSQL("VACUUM");
        } catch (SQLiteException e) {
            Log.e("removeBGMFile_Old 레코드삭제", e.toString());
        }

        // 파일 삭제
        File file;
        for(int i=0; i<bgmPath.length; i++){
            try {
                file = new File(bgmPath[i]);
                if (file.isFile())
                    file.delete();
            } catch (Exception e){
                Log.e("deleteBGMFile 파일삭제", e.toString());
            }
        }
    }

    /*****  DB 레코드 삭제(delete)  *****/
    // Method : 파일 삭제 시 삭제된 파일 Favorite update
    // Return Value : void
    // Parameter : bgmPath(삭제할 음악파일 경로. 여러개 가능)
    // Use : MainActivity에서 삭제할 음악파일을 여러개 선택해서 삭제할 때 사용한다. 이 때 해당 음악 파일들이
    //       FavoriteList에 존재한다면 해당 Favorite_id의 bgmPath를 null로 만든다.
    public void updateFavoriteForDeleteFile(String[] bgmPath) {

        StringBuffer query = new StringBuffer();

        query.append("UPDATE favorite SET bgm_path=null WHERE bgm_path in(");
        for (int i = 0; i < bgmPath.length; i++) {
            query.append("\"" + bgmPath[i] + "\"");
            if (i + 1 < bgmPath.length)
                query.append(",");
        }
        query.append(")");

        try {
            mDataBase.execSQL(query.toString());
        } catch (SQLiteException e) {
            Log.e("파일 삭제 시 Favorite update", e.toString());
        }
    }

    /*****  DB 레코드 삭제(delete)  *****/

    /*****  DB 초기화 (truncate)  *****/
    public void dropTableDB(){
        String query;

        try {
            query = "DROP TABLE BanList";
            mDataBase.execSQL(query);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        try {
            query = "DROP TABLE Favorite";
            mDataBase.execSQL(query);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        try {
            query = "DROP TABLE BGMList";
            mDataBase.execSQL(query);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        try {
            query = "DROP TABLE Category";
            mDataBase.execSQL(query);
            mDataBase.execSQL("VACUUM");
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
    }
    /*****  DB 초기화 (truncate)  *****/
}
