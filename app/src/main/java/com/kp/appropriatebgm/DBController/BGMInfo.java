package com.kp.appropriatebgm.DBController;

import java.io.Serializable;

/**
 * Created by KP on 2016-01-28.
 */
public class BGMInfo implements Serializable{

    private static final long serialVersionUID=100000L;

    private int bgmId;
    private String bgmName;
    private String bgmPath;
    private boolean innerfile;
    private int categoryId;

    public BGMInfo(int bgmId, String bgmName, String bgmPath, int innerfile, int categoryId){
        this.bgmId = bgmId;
        this.bgmName = bgmName;
        this.bgmPath = bgmPath;
        this.categoryId = categoryId;
        if(innerfile == 1)
            this.innerfile = true;
        else
            this.innerfile = false;
    }

    public int getBgmId(){  return bgmId;   }
    public String getBgmName(){ return bgmName; }
    public String getBgmPath(){ return bgmPath; }
    public boolean isInnerfile(){   return innerfile; }
    public int getCategoryId(){ return categoryId;  }
}
