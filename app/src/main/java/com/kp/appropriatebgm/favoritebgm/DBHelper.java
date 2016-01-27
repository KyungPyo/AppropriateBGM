package com.kp.appropriatebgm.favoritebgm;

/**
 * Created by Choi on 2016-01-07.
 */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import com.kp.appropriatebgm.R;
/*
* sql을 사용하기 위한 제반 클래스
* SQLiteOpenHelper는 사용에 도움을 주는 클래스이다.
*/
public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context, String name,
                              CursorFactory factory, int version) {
        super(context, name, factory, version);
        // TODO Auto-generated constructor stub

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        // SQLiteOpenHelper 가 최초 실행 되었을 때
        String bgmListCreate="CREATE TABLE IF NOT EXISTS BGMList (" +
                "  bgm_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                "  bgm_name VARCHAR(255) NOT NULL," +
                "  bgm_path VARCHAR(255) NOT NULL," +
                "  innerfile INTEGER NOT NULL DEFAULT 0," +
                "  category_id INTEGER NOT NULL DEFAULT 3," +
                "  CONSTRAINT fk_BGMList_Category FOREIGN KEY (category_id) REFERENCES Category (category_id));";
        String categoryCreate="CREATE TABLE IF NOT EXISTS Category (" +
                "  category_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                "  category_name VARCHAR(20) NOT NULL);";
        String favoriteCreate="" +
                "CREATE TABLE IF NOT EXISTS Favorite (" +
                "  favorite_id INTEGER NOT NULL PRIMARY KEY," +
                "  bgm_id INTEGER NULL," +
                "  CONSTRAINT fk_Favorite_BGMList1 FOREIGN KEY (bgm_id) REFERENCES BGMList (bgm_id));";
        String inputCategory="INSERT INTO Category VALUES (1, '카테고리1');" ;
        String inputCategory2="INSERT INTO Category VALUES (2, '카테고리2');";
        String inputCategory3="INSERT INTO Category VALUES (3, '카테고리없음');";
        db.execSQL(categoryCreate);
        db.execSQL(bgmListCreate);
        db.execSQL(favoriteCreate);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // db = 적용할 db, old/new 구 버전/신버전
        // TODO Auto-generated method stub

        String sql = "drop table if exists student";
        db.execSQL(sql);

        onCreate(db); // 테이블을 지웠으므로 다시 테이블을 만들어주는 과정
    }
}
