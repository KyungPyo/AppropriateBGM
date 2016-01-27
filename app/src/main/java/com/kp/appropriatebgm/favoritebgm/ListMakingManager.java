package com.kp.appropriatebgm.favoritebgm;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;
import com.kp.appropriatebgm.R;
/**
 * Created by LG on 2015-12-29.
 */
public class ListMakingManager {

    ArrayList<Music> musicList;
    DBManager dbManager;
    String[] colums = {"bgm_id", "bgm_name", "bgm_path"};

    public ListMakingManager(DBManager dbManager) {
        this.dbManager = dbManager;
    }

    ArrayList<Music> makeList(Context context) {
        //현재는 DB에 추가하고 데이터를 모두 삭제하고 또 추가하는 방식
        //이부분은 전체 미디어에서 받아오는 것이 아니라 DB에서 받아오는 것으로
        //수정되어야함.
        ContentValues addRowValue = new ContentValues();
        musicList = new ArrayList<Music>();
        Music tmpMusic;

        Cursor musics = dbManager.select("BGMList", colums);
        Log.d("DB리셋하기 전 ", musics.getCount() + "");
        if (musics.getCount() == 0) {
            Cursor c = context.getContentResolver().query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    new String[]{MediaStore.Audio.Media._ID,
                            MediaStore.Audio.Media.DATA,
                            MediaStore.Audio.Media.ARTIST,
                            MediaStore.Audio.Media.ALBUM,
                            MediaStore.Audio.Media.DISPLAY_NAME}, "1=1", null, null);

            if (c != null) {
                while (c.moveToNext()) {
                    tmpMusic = new Music(c.getString(1), c.getString(4));
                    addRowValue.put("bgm_name", tmpMusic.getMusicName());
                    addRowValue.put("bgm_path", tmpMusic.getMusicPath());
                    dbManager.insert("BGMList", addRowValue);
                    musicList.add(tmpMusic);
                }

            }
        } else {
            //이부분
            while(musics.moveToNext()) {
                tmpMusic = new Music(musics.getString(2), musics.getString(1), musics.getString(0));
                musicList.add(tmpMusic);
            }
        }

        return musicList;
    }

}
