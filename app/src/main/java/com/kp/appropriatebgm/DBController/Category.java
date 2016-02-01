package com.kp.appropriatebgm.DBController;

//*******************************
//카테고리 정보를 저장하는 Class
//*******************************
public class Category {
    private int cateId;
    private String cateName;

    public Category(int cateId, String cateName) {
        this.cateId = cateId;
        this.cateName = cateName;
    }



    public void setCateId(int cateId) {
        this.cateId = cateId;
    }
    public void setCateName(String cateName) {
        this.cateName = cateName;
    }

    public int getCateId() {
        return cateId;
    }
    public String getCateName() {
        return cateName;
    }
}
