package com.huichenghe.xinlvshuju.DataEntites;


/**
 * Created by lixiaoning on 2016/5/3.
 */
public class Info_entry
{
    private boolean isChecked;
    private int dataId;
    private String dataName;

    public boolean isChecked() {
        return isChecked;
    }

    public Info_entry(int dataId, String dataName, boolean isChecked)
    {
        this.dataId = dataId;
        this.dataName = dataName;
        this.isChecked = isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public void setDataId(int dataId) {
        this.dataId = dataId;
    }

    public void setDataName(String dataName) {
        this.dataName = dataName;
    }

    public int getDataId() {
        return dataId;
    }

    public String getDataName() {
        return dataName;
    }
}
