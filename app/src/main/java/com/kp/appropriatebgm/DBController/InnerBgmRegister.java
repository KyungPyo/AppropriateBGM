package com.kp.appropriatebgm.DBController;

import com.kp.appropriatebgm.R;

import java.util.ArrayList;

/**
 * Created by KP on 2016-02-18.
 */
public class InnerBgmRegister {
    private class InnerBgmInfo {
        String name;
        int code;
        int category;

        InnerBgmInfo(String inputName, int inputCode, int inputCategory){
            name = inputName;
            code = inputCode;
            category = inputCategory;
        }
    }

    private ArrayList<InnerBgmInfo> innerBgmList = new ArrayList<>();

    public InnerBgmRegister(){
        initBgmList();
    }

    private void initBgmList(){
        // category = (2:분류안됨, 3:웃긴, 4:슬픈, 5:공포)
        innerBgmList.add(new InnerBgmInfo("인간극장", R.raw.human_cinema, 4));
        innerBgmList.add(new InnerBgmInfo("함정카드", R.raw.trapcard, 3));
        innerBgmList.add(new InnerBgmInfo("사나이 눈물", R.raw.boycry, 2));
        innerBgmList.add(new InnerBgmInfo("숨겨왔던 나의", R.raw.hidden_my_heart, 2));
        innerBgmList.add(new InnerBgmInfo("美味", R.raw.mimi, 2));
        innerBgmList.add(new InnerBgmInfo("야외취침확정", R.raw.misson_failed, 4));
        innerBgmList.add(new InnerBgmInfo("나와라", R.raw.nawara, 3));
        innerBgmList.add(new InnerBgmInfo("착신아리", R.raw.receipt_ari, 5));
        innerBgmList.add(new InnerBgmInfo("끈적끈적", R.raw.sexy, 2));
        innerBgmList.add(new InnerBgmInfo("방귀소리", R.raw.fart, 3));
        innerBgmList.add(new InnerBgmInfo("고자라니", R.raw.gozarani, 3));
        innerBgmList.add(new InnerBgmInfo("공포브금", R.raw.horror, 5));
        innerBgmList.add(new InnerBgmInfo("나는 행복합니다", R.raw.im_happy, 2));
        innerBgmList.add(new InnerBgmInfo("절규", R.raw.scream, 4));
        innerBgmList.add(new InnerBgmInfo("샷건", R.raw.shotgun, 2));
        innerBgmList.add(new InnerBgmInfo("기상나팔", R.raw.soldier_alarm, 2));
        innerBgmList.add(new InnerBgmInfo("뚜-뚜루-뚜", R.raw.ssum, 2));
        innerBgmList.add(new InnerBgmInfo("짜잔", R.raw.tada, 2));
        innerBgmList.add(new InnerBgmInfo("축하합니다", R.raw.congretulation, 2));
    }

    public int getListSize(){
        return innerBgmList.size();
    }

    public String getInnerBgmName(int i){
        return innerBgmList.get(i).name;
    }

    public String getInnerBgmCode(int i){
        return Integer.toString(innerBgmList.get(i).code);
    }

    public int getInnerBgmCategory(int i) {
        return innerBgmList.get(i).category;
    }
}
