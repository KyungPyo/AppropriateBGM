package com.kp.appropriatebgm.DBController;

//*******************************
//Favorite 정보를 저장하는 Class
//*******************************
public class Favorite {
    private int favoriteId;
    private String bgmPath;

    public Favorite(int favoriteId, String bgmPath) {
        this.favoriteId = favoriteId;
        if (bgmPath.equals("")){
            this.bgmPath = null;
        } else {
            this.bgmPath = bgmPath;
        }
    }

    public void setFavoriteId(int favoriteId) {
        this.favoriteId = favoriteId;
    }
    public void setBgmPath(String bgmPath) {
        this.bgmPath = bgmPath;
    }

    public int getFavoriteId() {
        return favoriteId;
    }
    public String getBgmPath() {
        return bgmPath;
    }
}
