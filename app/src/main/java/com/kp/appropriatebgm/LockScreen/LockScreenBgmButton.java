package com.kp.appropriatebgm.LockScreen;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
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

    // Method : LockScreenBgmButton 생성자(1)
    // Return value :
    // paremeter : context()
    // Use :
    public LockScreenBgmButton(Context context) {
        super(context);
        this.context = context;
        init(context, null);
    }

    // Method : LockScreenBgmButton 생성자(2)
    // Return value :
    // paremeter : context(), attrs()
    // Use :
    public LockScreenBgmButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.attr = attrs;
        init(context, attrs);
    }

    // Method : LockScreenBgmButton 생성자(3)
    // Return value :
    // paremeter : context(), attrs(), defStyleAttr()
    // Use :
    public LockScreenBgmButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        this.attr = attrs;
        init(context, attrs);
    }

    // Method :
    // Return value : void
    // paremeter : context(), attrs()
    // Use :
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

    // Method : 버튼 정보 입력
    // Return value : void
    // paremeter : favorite(버튼에 저장될 즐겨찾기 정보)
    // Use : 파라미터로 넘겨받은 즐겨찾기 정보를 저장해 두고, 버튼명과 이미지를 설정한다.
    public void setBtnInfo(Favorite favorite) {
        favoriteInfo = favorite;
        bgmFavoriteText.setText(favorite.getBgmName());
        bgmFavoriteImg.setBackgroundColor(Color.parseColor("#30FF0000"));
    }

    // Method : 즐겨찾기 정보 받아가기
    // Return value : Favorite(버튼에 저장된 즐겨찾기 정보)
    // paremeter : void
    // Use : 해당 버튼에 저장된 즐겨찾기 정보를 받아간다.
    public Favorite getFavoriteInfo(){
        return favoriteInfo;
    }
}
