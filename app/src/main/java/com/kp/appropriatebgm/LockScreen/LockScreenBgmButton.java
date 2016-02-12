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

    }
}
