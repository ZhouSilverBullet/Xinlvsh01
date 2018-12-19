package com.huichenghe.xinlvshuju.DataEntites;

/**
 * Created by lixiaoning on 16-clock-lost.
 */
public class updateInfoEntity
{
    private String type;
    private String url;
    private String description;
    private String newVersion;
    private String oldVersion;
    private int filesize;

    public updateInfoEntity(String type, String url, String description, String newVersion, String oldVersion, int filesize) {
        this.type = type;
        this.url = url;
        this.description = description;
        this.newVersion = newVersion;
        this.oldVersion = oldVersion;
        this.filesize = filesize;
    }

    public String getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }

    public String getDescription() {
        return description;
    }

    public String getNewVersion() {
        return newVersion;
    }

    public String getOldVersion() {
        return oldVersion;
    }

    public int getFilesize() {
        return filesize;
    }
}
