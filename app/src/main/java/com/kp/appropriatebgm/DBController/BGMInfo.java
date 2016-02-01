package com.kp.appropriatebgm.DBController;

import java.io.Serializable;

/**
 * Created by KP on 2016-01-28.
 */
public class BGMInfo implements Serializable{

    private static final long serialVersionUID=100000L;

    private String bgmName;
    private String bgmPath;
    private boolean innerfile;
    private int categoryId;

    public BGMInfo(String bgmName, String bgmPath, int innerfile, int categoryId){
        this.bgmName = bgmName;
        this.bgmPath = bgmPath;
        this.categoryId = categoryId;
        if(innerfile == 1)
            this.innerfile = true;
        else
            this.innerfile = false;
    }

    public String getBgmName(){ return bgmName; }
    public String getBgmPath(){ return bgmPath; }
    public boolean isInnerfile(){   return innerfile; }
    public int getCategoryId(){ return categoryId;  }
}
