package com.huichenghe.xinlvshuju.slide.settinga;

import android.graphics.drawable.Drawable;

/**
 * Created by Administrator on 2016/11/14 0014.
 */
public class UiMenuInfo {
    private Drawable icon;
    private String name;
    private boolean isOpen;

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public UiMenuInfo(Drawable icon, String name, boolean isOpen) {
        this.icon = icon;
        this.name = name;
        this.isOpen = isOpen;
    }
}
