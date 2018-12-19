package com.huichenghe.xinlvshuju.DataEntites;

/**
 * Created by lixiaoning on 2016/6/3.
 */
public class AttionPersionInfoEntity
{
    private String id;
    private String mark;
    private String header;
    private String indexfatigue;
    private String lastdate;
    private String sleepstatus;
    private String finishtimes;

    public AttionPersionInfoEntity(String id, String finishtimes, String sleepstatus, String lastdate, String indexfatigue, String header, String mark) {
        this.id = id;
        this.finishtimes = finishtimes;
        this.sleepstatus = sleepstatus;
        this.lastdate = lastdate;
        this.indexfatigue = indexfatigue;
        this.header = header;
        this.mark = mark;
    }

    public void setId(String id) {
        this.id = id;

    }

    public void setFinishtimes(String finishtimes) {
        this.finishtimes = finishtimes;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public void setIndexfatigue(String indexfatigue) {
        this.indexfatigue = indexfatigue;
    }

    public void setLastdate(String lastdate) {
        this.lastdate = lastdate;
    }

    public void setSleepstatus(String sleepstatus) {
        this.sleepstatus = sleepstatus;
    }

    public String getFinishtimes() {
        return finishtimes;
    }

    public String getSleepstatus() {
        return sleepstatus;
    }

    public String getIndexfatigue() {
        return indexfatigue;
    }

    public String getLastdate() {
        return lastdate;
    }

    public String getHeader() {
        return header;
    }

    public String getId() {
        return id;
    }

    public String getMark() {
        return mark;
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

        if(o instanceof AttionPersionInfoEntity)
        {
            AttionPersionInfoEntity enti = ((AttionPersionInfoEntity) o);
            if(enti.getId().equals(this.getId()))
            {
                return true;
            }
            return false;
        }
        else
        {
            return false;
        }
    }
}
