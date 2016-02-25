package com.kp.appropriatebgm.LockScreen;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.kp.appropriatebgm.R;

/**
 * Created by GD on 2016-02-11.
 */
public class UnlockScreenWidget extends RelativeLayout {

    private Drawable track;
    private View background;

    // Use : 잠금화면 on/off를 위한 인터페이스 내 함수 선언 (off 시 오버라이딩)
    public interface OnUnlockListener{
        void onUnlock();
    }

    // Method : 생성자
    // Return value : void
    // parameter : context(액티비티)
    // use ; 생성자를 통해 init 메소드를 호출하여 위젯을 구성한다.
    public UnlockScreenWidget(Context context) {
        super(context);
        init(context, null);
    }
    // Method : 생성자
    // Return value : void
    // parameter : context(액티비티)
    // use ; 생성자를 통해 init 메소드를 호출하여 위젯을 구성한다.
    public UnlockScreenWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }
    // Method : 생성자
    // Return value : void
    // parameter : context(액티비티)
    // use ; 생성자를 통해 init 메소드를 호출하여 위젯을 구성한다.
    public UnlockScreenWidget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private OnUnlockListener listener;
    private SeekBar seekbar;
    private ImageView unlockImage;
    private int thumbWidth;

    public void setOnUnlockListener(OnUnlockListener listener)
    {
        this.listener = listener;
    }

    // Method : 잠금화면 해제 위젯 정의
    // Return value : void
    // parameter : context(액티비티), 애트리뷰트 Set (말 그대로 모음)
    // use ; 잠금화면 구성 요소를 설정해준다. (seekbar, background 등)
    private void init(Context context, AttributeSet attrs) {

        //use : 레이아웃 편집기에서의 편집 여부 (편집하면 true return)
        if (isInEditMode()) {
            return;
        }
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.widget_lockscreen_unlock, this, true);

        unlockImage = (ImageView) findViewById(R.id.widget_backimage_lock);
        seekbar = (SeekBar) findViewById(R.id.widget_slidebar_seekbar);
        background = findViewById(R.id.widget_background);

        //use : TypedArray로 attrs.xml의 정의된 내용에서 lcokscreen.xml(widget)에서 설정한 각각의 설정값들을 가져와서 사용한다. (String, Drawable)
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.lockscreen_SlideToUnlockView);
        Drawable backimage = attributes.getDrawable(R.styleable.lockscreen_SlideToUnlockView_image);
        Drawable thumb = attributes.getDrawable(R.styleable.lockscreen_SlideToUnlockView_thumb);

        //use : 이미지를 받아오지 못한 경우 이미지 설정
        if (thumb == null) {
            thumb = getResources().getDrawable(R.drawable.ic_lock_selector);
        }
        track = attributes.getDrawable(R.styleable.lockscreen_SlideToUnlockView_tracks);
        //use : 메모리 할당 해제
        attributes.recycle();

        thumbWidth = thumb.getIntrinsicWidth();

        if (track != null) {
            background.setBackground(track);
        }

        if (backimage != null) {
            unlockImage.setImageDrawable(backimage);
        }

        unlockImage.setPadding(thumbWidth, 0, 0, 0);

        int defaultOffset = seekbar.getThumbOffset();
        seekbar.setThumb(thumb);
        seekbar.setThumbOffset(defaultOffset);
        // use ; 초기 seekbar 위치 0으로 설정
        seekbar.setProgress(10);


        seekbar.setOnTouchListener(new OnTouchListener() {
            private boolean isInvalidMove;

            // Method : seekbar 기본 터치이벤트
            // Return value : void
            // parameter : View(여기서는 Seekbar), 이벤트 종류
            // use ; seekbar 터치와 관련된 함수 (처음 눌린 위치가 seekbar 크기를 넘어설 경우 터치 무효)
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        return isInvalidMove = motionEvent.getX() > thumbWidth;
                    case MotionEvent.ACTION_MOVE:
                        return isInvalidMove;
                    case MotionEvent.ACTION_UP:
                        return isInvalidMove;
                }
                return false;
            }
        });
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            // Method : Seekbar 터치 시작
            // Return value : void
            // parameter : seekbar
            // use ; seekbar가 처음 터치될 때 호출되는 함수
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            // Method : Seekbar 변경 함수
            // Return value : void
            // parameter : seekbar, progress(프로그래스 바 위치), 터치 유무
            // use ; 프로그래스바가 변할 때 세팅을 해준다.
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
            }

            // Method : Seekbar 터치 정지
            // Return value : void
            // parameter : seekbar
            // use ; Seekbar가 터치가 끝난 상태에서의 상태를 판단하는 함수
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                ObjectAnimator anim = ObjectAnimator.ofInt(seekBar, "progress", 10);
                anim.setInterpolator(new AccelerateDecelerateInterpolator());
                anim.setDuration(getResources().getInteger(android.R.integer.config_shortAnimTime));  //config_shortAnimTime : 짧은 애니메이션 지속시간
                if(seekBar.getProgress() > 50)
                {
                    if(listener != null) {
                        anim.setIntValues(90);
                        anim.start();
                        listener.onUnlock();
                    }
                }
                else{
                    // use ; 원래 처음으로 돌아가는 애니메이션
                    anim.setIntValues(10);
                    anim.start();
                }
            /*    else {
                    if (listener != null) {
                        listener.onUnlock();
                    }
                }*/
            }
        });
    }

    // Method : 레이아웃 크기 설정
    // Return value : void
    // parameter : widthMeasureSpec(부모뷰로부터 결정된 넓이) , heightMeasureSpec(부모뷰로부터 결정된 높이)
    // use ; 이 레이아웃의 부모뷰로부터 결정된 치수를 가져와 크기를 설정해준다.
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (isInEditMode()) {
            return;
        }

        // use : 높이가 뷰그룹의 wrap_content와 동일할경우 background의 높이를 추가
        // prevents 9-patch background image from full size stretching
        if (getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            background.getLayoutParams().height = seekbar.getHeight() + fromDpToPx(3);
        }

    }
    // Method : dp를 px로 변환
    // Return value : int
    // parameter : dp (dip)
    // use ; 해당 dp를 px(pixel)로 바꿔주는 역할을 하는 함수
    public int fromDpToPx(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }
}
