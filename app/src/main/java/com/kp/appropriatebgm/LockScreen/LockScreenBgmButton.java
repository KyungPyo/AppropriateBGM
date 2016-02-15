package com.kp.appropriatebgm.LockScreen;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kp.appropriatebgm.DBController.DBManager;
import com.kp.appropriatebgm.DBController.Favorite;
import com.kp.appropriatebgm.MusicPlayer;
import com.kp.appropriatebgm.R;

/**
 * Created by GD on 2016-02-11.
 */
public class LockScreenBgmButton extends RelativeLayout {

    private Context context;
    private AttributeSet attr;
    private ImageView bgmFavoriteImg;
    private TextView bgmFavoriteText;
    private Favorite favoriteInfo;
    private MusicPlayer musicPlayer;
    private DBManager dbManager;

    public LockScreenBgmButton(Context context) {
        super(context);
        this.context = context;
        init(context, null);
    }

    public LockScreenBgmButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.attr = attrs;
        init(context, attrs);
    }

    public LockScreenBgmButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        this.attr = attrs;
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {

        //use : 레이아웃 편집기에서의 편집 여부 (편집하면 true return)
        if (isInEditMode()) {
            return;
        }
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.button_layout_favoritebgmlist, this, true);

        bgmFavoriteImg = (ImageView) findViewById(R.id.bgmlist_imageview_img);
        bgmFavoriteText = (TextView) findViewById(R.id.bgmlist_textview_name);

        dbManager = DBManager.getInstance(context);
    }

    public void setBtnInfo(Favorite favorite){
        favoriteInfo = favorite;
        bgmFavoriteText.setText(favorite.getBgmName());
        bgmFavoriteImg.setBackgroundColor(Color.parseColor("#30FF0000"));

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String path = favoriteInfo.getBgmPath();
                if (musicPlayer != null) {  // 전에 재생중인것이 있으면 정지
                    musicPlayer.stopBgm();
                }
                if (dbManager.isInnerfile(path)) {
                    musicPlayer = new MusicPlayer(context, Integer.parseInt(path));
                } else {
                    musicPlayer = new MusicPlayer(context, path);
                }
                musicPlayer.playBgmFromStart();
            }
        });
    }

    public Favorite getFavoriteInfo(){
        return favoriteInfo;
    }
}
