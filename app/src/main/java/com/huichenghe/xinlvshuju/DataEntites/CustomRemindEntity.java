package com.huichenghe.xinlvshuju.DataEntites;

import java.io.Serializable;

/**
 * 自定义提醒实体类
 * Created by lixiaoning on 15-12-info.
 */
public class CustomRemindEntity implements Serializable
{
    private byte number;     // 数据的编号

    private int count;       // 提醒时间的个数
    private byte type;       // 提醒的类型
    private String times;    // 提醒时间
    private String repeats;  // 提醒重复
    private int icon;        // 提醒图标地址
    private String customName;
    private byte weekMetadata;

    public CustomRemindEntity() {
//        this.type = type;
//        this.icon = icon;
//        this.repeats = repeats;
//        this.times = times;
    }


    public void setWeekMetadata(byte data)
    {
        this.weekMetadata = data;
    }

    public byte getWeekMetadata()
    {
        return weekMetadata;
    }


    public void setCustomName(String name)
    {
        this.customName = name;
    }


    public String getCustomName()
    {
        return customName;
    }
    public void setCount(int count)
    {
        this.count = count;
    }

    public int getCount()
    {
        return count;
    }
    public byte getNumber()
    {
        return number;
    }

    public void setNumber(byte number)
    {
        this.number = number;
    }

    public byte getType() {
        return type;
    }

    public String getTimes() {
        return times;
    }

    public String getRepeats() {
        return repeats;
    }

    public int getIcon() {
        return icon;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public void setTimes(String times) {
        this.times = times;
    }

    public void setRepeats(String repeats) {
        this.repeats = repeats;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }


    @Override
    public boolean equals(Object other)
    {
        if(other == null)
        {
            return false;
        }
        if(this == null)
        {
            return false;
        }
        if(!(other instanceof CustomRemindEntity))
        {
            return false;
        }
        else
        {
            CustomRemindEntity en = (CustomRemindEntity)other;
            int number = en.getNumber();
            if(this.getNumber() == number)
            {
                return true;
            }
            else
            {
                return false;
            }
        }
    }
}
