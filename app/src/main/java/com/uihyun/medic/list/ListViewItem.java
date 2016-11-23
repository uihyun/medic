package com.uihyun.medic.list;

import android.graphics.Bitmap;

/**
 * Created by Uihyun on 2016. 5. 25..
 */
public class ListViewItem {
    private Bitmap image;
    private String titleStr;
    private String descStr;

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getTitle() {
        return this.titleStr;
    }

    public void setTitle(String title) {
        titleStr = title;
    }

    public String getDesc() {
        return this.descStr;
    }

    public void setDesc(String desc) {
        descStr = desc;
    }
}
