package com.huichenghe.xinlvshuju.DataEntites;

import java.io.Serializable;

/**
 * Created by lixiaoning on 2016/5/26.
 */
public class ThirdPartyLoginInfoEntity implements Serializable
{
    private String icon;
    private String userId;
    private String nickName;
    private String gender;
    private String token;
    private String ptName;
    private String type;
    private String account;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;

    }

    public ThirdPartyLoginInfoEntity(String account, String userId, String type, String useIc, String usNick, String userGende)
    {
        this.account = account;
        this.userId = userId;
        this.type = type;
        this.icon = useIc;
        this.userId = userId;
        this.nickName = usNick;
        this.gender = userGende;
    }

    public ThirdPartyLoginInfoEntity(String useIc, String usNick, String userGende)
    {
        this.icon = useIc;
        this.nickName = usNick;
        this.gender = userGende;
    }

    public String getPtName() {
        return ptName;
    }

    public void setPtName(String ptName) {
        this.ptName = ptName;
    }
    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public String getIcon() {
        return icon;
    }

    public String getUserId() {
        return userId;
    }

    public String getNickName() {
        return nickName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
