package com.kp.appropriatebgm.DBController;

//*******************************
//Favorite 정보를 저장하는 Class
//*******************************
public class Favorite {
    private int favoriteId;
    private String bgmPath;
    private String bgmName;
    private int innerfile;

    public Favorite(int favoriteId, String bgmPath, String bgmName) {
        this.favoriteId = favoriteId;
        this.bgmPath = bgmPath;
        this.bgmName = bgmName;
        this.innerfile = 0;
    }

    public Favorite(int favoriteId, String bgmPath, String bgmName, int innerfile) {
        this.favoriteId = favoriteId;
        this.bgmPath = bgmPath;
        this.bgmName = bgmName;
        this.innerfile = innerfile;
    }

    public void setFavoriteId(int favoriteId) {
        this.favoriteId = favoriteId;
    }
    public void setBgmPath(String bgmPath) {
        this.bgmPath = bgmPath;
    }
    public void setBgmName(String bgmName) {
        this.bgmName = bgmName;
    }

    public int getFavoriteId() {
        return favoriteId;
    }
    public String getBgmPath() {
        return bgmPath;
    }
    public String getBgmName() {
        return bgmName;
    }
    public int getInnerfileCode() { return innerfile; }
    public boolean isInnerfile(){
        if(innerfile != 0)
            return true;
        else
            return false;
    }
}
