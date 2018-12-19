package com.huichenghe.xinlvshuju.DataEntites;


/**
 * 封装运动类型的实体类
 * Created by lixiaoning on 15-12-phone.
 */
public class chooseTypeEntity
{
    private int iconId;
    private int typeName;
    private int type;


    public chooseTypeEntity(int iconId, int typeName, int type)
    {
        this.iconId = iconId;
        this.typeName = typeName;
        this.type = type;
    }

    public int getIconId() {
        return iconId;
    }

    public int getTypeName() {
        return typeName;
    }

    public int getType() {
        return type;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }

    public void setTypeName(int typeName) {
        this.typeName = typeName;
    }

    public void setType(int type) {
        this.type = type;
    }
}
