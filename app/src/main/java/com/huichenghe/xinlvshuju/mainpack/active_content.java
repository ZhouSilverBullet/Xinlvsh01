package com.huichenghe.xinlvshuju.mainpack;

/**
 * Created by lixiaoning on 2016/9/27.
 */
public class active_content
{
    private int type;
    private int iconId;
    private String content;
    private String unit;
    private String[] contentArray;

    public active_content(int type, String content, int iconId) {
        this.type = type;
        this.content = content;
        this.iconId = iconId;
    }

    public active_content(int type, int iconId, String content, String unit) {
        this.unit = unit;
        this.content = content;
        this.iconId = iconId;
        this.type = type;
    }

    public String getUnit() {
        return unit;
    }

    public int getIconId() {
        return iconId;
    }

    public active_content(int type, int iconId) {
        this.type = type;
        this.iconId = iconId;

    }

    public int getType() {
        return type;
    }

    public String getContent() {
        return content;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setContentArray(String[] co)
    {
        this.contentArray = co;
    }

    public String[] getContentArray()
    {
        return contentArray;
    }
}
