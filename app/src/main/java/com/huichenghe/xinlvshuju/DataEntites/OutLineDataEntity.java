package com.huichenghe.xinlvshuju.DataEntites;

/**此类封装了离线运动数据，用于呈现在RecyclerView上边
 * Created by lixiaoning on 15-11-17.
 */
public class OutLineDataEntity
{

    public static final int TYPE_UNKNOW = -1;
    public static final int TYPE_WALK = 0;
    public static final int TYPE_RUNNINT = 1;
    public static final int TYPE_CLIMING = 2;
    public static final int TYPE_BALL_MOVEMENT = 3;
    public static final int TYPE_MUSCLE = 4;
    public static final int TYPE_AEROBIC = 5;
    public static final int TYPE_CUSTOM = 6;
    private int type;            // 运动类型
    private String time;         // 此条运动测试的时间
    private int stepCount;      // 运动步数
    private int calorie;        // 消耗卡路里
    private String heartReat = "";   // 心率
    private String day;
    private String sportName;


    public OutLineDataEntity(){}

    public OutLineDataEntity(int type, String time, int stepCount, int calorie, String heartReat, String day, String sportName)
    {
        this.calorie = calorie;
        this.time = time;
        this.type = type;
        this.stepCount = stepCount;
        this.heartReat = heartReat;
        this.day = day;
        this.sportName = sportName;
    }

    public void setSportName(String sportName) {
        this.sportName = sportName;
    }

    public String getSportName() {
        return sportName;
    }

    public void setType(int type)
    {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public String getTime() {
        return time;
    }

    public int getStepCount() {
        return stepCount;
    }

    public int getCalorie() {
        return calorie;
    }

    public String getHeartReat() {
        return heartReat;
    }

    public String getDay()
    {
        return day;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setStepCount(int stepCount) {
        this.stepCount = stepCount;
    }

    public void setCalorie(int calorie) {
        this.calorie = calorie;
    }

    public void setHeartReat(String heartReat) {
        this.heartReat = heartReat;
    }

    public void setDay(String day) {
        this.day = day;
    }

    @Override
    public boolean equals(Object o)
    {
        if(o == null)
        {
            return false;
        }
        if(this == o)
        {
            return true;
        }
        if(!(o instanceof OutLineDataEntity))
        {
            return false;
        }
        else
        {
            OutLineDataEntity en = (OutLineDataEntity)o;
            if(en.getTime().equals(this.getTime()))
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
