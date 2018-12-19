package com.huichenghe.xinlvshuju.DbEntities;

/**
 * Created by lixiaoning on 2016/11/1.
 */
public class Delete_item_entity
{
    private int icon;
    private String moveData;
    private String moveUnit;

    public Delete_item_entity(int icon, String moveData, String moveUnit) {
        this.icon = icon;
        this.moveData = moveData;
        this.moveUnit = moveUnit;
    }

    public int getIcon() {
        return icon;
    }

    public String getMoveData() {
        return moveData;
    }

    public String getMoveUnit() {
        return moveUnit;
    }
}
