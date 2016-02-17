package com.kp.appropriatebgm.LockScreen;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.kp.appropriatebgm.R;

/**
 * Created by GD on 2016-02-11.
 */
public class LockScreenBgmButton extends RelativeLayout {

    Context context;
    AttributeSet attr;
    ImageView bgmFavoriteImg;
    TextView bgmFavoriteText;

    // Method : 생성자
    // Return value : void
    // parameter : context(액티비티)
    // use ; 해당 context를 받고 레이아웃 xml의 요소들을 받아온다.
    public LockScreenBgmButton(Context context) {
        super(context);
        this.context = context;
        init(context);
    }
    // Method : 생성자
    // Return value : void
    // parameter : context(액티비티), attrs(애트리뷰트 모음)
    // use ; 해당 context를 받고 레이아웃 xml의 요소들을 받아온다. (동일)
    public LockScreenBgmButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.attr = attrs;
        init(context);
    }
    // Method : 생성자
    // Return value : void
    // parameter : context(액티비티), attrs(애트리뷰트 모음), defStyleAttr(초기 스타일)
    // use ; 해당 context를 받고 레이아웃 xml의 요소들을 받아온다. (동일)
    public LockScreenBgmButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        this.attr = attrs;
        init(context);
    }

    // Method : 레이아웃 초기 설정
    // Return value : void
    // parameter : context(액티비티)
    // use ; 레이아웃을 받아와서 imageView와 textView를 받아오도록 한다.
    private void init(Context context) {

        //use : 레이아웃 편집기에서의 편집 여부 (편집하면 true return)
        if (isInEditMode()) {
            return;
        }
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.button_layout_favoritebgmlist, this, true);

        bgmFavoriteImg = (ImageView) findViewById(R.id.bgmlist_imageview_img);
        bgmFavoriteText = (TextView) findViewById(R.id.bgmlist_textview_name);

    }
}
