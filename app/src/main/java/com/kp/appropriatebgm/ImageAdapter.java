package com.kp.appropriatebgm;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by Choi on 2016-02-25.
 */
public class ImageAdapter extends PagerAdapter {
    Context context;
    // 스와이프 할 이미지 추가. ID 입력
    private int[] GalImages = new int[] {
            R.drawable.ic_delete_black_18dp,
            R.drawable.ic_delete_wine_18dp,
            R.drawable.ic_delete_white_24dp
    };
    ImageAdapter(Context context){
        this.context=context;
    }
    @Override
    public int getCount() {
        return GalImages.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((ImageView) object);
    }

    // Method : 이미지 뷰의 설정
    // Return Value : Object
    // Parameter : ViewGroup , position
    // Use : List Adapter의 getView 같은 역할인 것으로 보임. 이미지 뷰의 설정을 하고 return 하여 현재 포지션에 있는
    //       Image 를 보여준다.
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imageView = new ImageView(context);
        //int padding = context.getResources().getDimensionPixelSize(R.dimen.padding_medium);
        //imageView.setPadding(10, 10, 10, 10);
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        imageView.setImageResource(GalImages[position]);
        ((ViewPager) container).addView(imageView, 0);
        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((ImageView) object);
    }
}