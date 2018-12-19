package com.huichenghe.xinlvshuju.appRemind;

import android.graphics.drawable.Drawable;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/10/25 0025.
 */

public class AppInfo implements Serializable {
    private Drawable drawable;
    private boolean isCheck;

    public AppInfo(Drawable drawable, boolean isCheck, String packageName, String label) {
        this.drawable = drawable;
        this.isCheck = isCheck;
        this.packageName = packageName;
        this.label = label;
    }

    private String packageName;
    private String label;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }


    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }


    public Drawable getDrawable() {
        return drawable;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }


}
