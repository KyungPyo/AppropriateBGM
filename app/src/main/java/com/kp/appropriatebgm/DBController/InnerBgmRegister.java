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

        InnerBgmInfo(String inputName, int inputCode){
            name = inputName;
            code = inputCode;
        }
    }

    private ArrayList<InnerBgmInfo> innerBgmList = new ArrayList<>();

    public InnerBgmRegister(){
        initBgmList();
    }

    private void initBgmList(){
        innerBgmList.add(new InnerBgmInfo("인간극장", R.raw.human_cinema));
        innerBgmList.add(new InnerBgmInfo("함정카드", R.raw.trapcard));
        innerBgmList.add(new InnerBgmInfo("사나이 눈물", R.raw.boycry));
        innerBgmList.add(new InnerBgmInfo("숨겨왔던 나의", R.raw.hidden_my_heart));
        innerBgmList.add(new InnerBgmInfo("美味", R.raw.mimi));
        innerBgmList.add(new InnerBgmInfo("야외취침확정", R.raw.misson_failed));
        innerBgmList.add(new InnerBgmInfo("나와라", R.raw.nawara));
        innerBgmList.add(new InnerBgmInfo("착신아리", R.raw.receipt_ari));
        innerBgmList.add(new InnerBgmInfo("끈적끈적", R.raw.sexy));
        innerBgmList.add(new InnerBgmInfo("사건25시", R.raw.sagun25));
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
}
