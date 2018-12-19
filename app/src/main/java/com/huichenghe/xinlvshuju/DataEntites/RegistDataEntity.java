package com.huichenghe.xinlvshuju.DataEntites;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * 注册需要向服务器发送的数据对象，实现序列化，可进行传输
 * Created by lixiaoning on 15-11-23.
 */
public class RegistDataEntity implements Serializable
{
    private String account;
    private String password;
    private String email;
    private String nickName;
    private String birthday;
    private String height;
    private String weight;
    private String gender;
    private Bitmap icon;

    public RegistDataEntity(String account,
                            String password,
                            String email,
                            String birthday,
                            String nickName,
                            String height,
                            String weight,
                            String gender,
                            Bitmap icon) {
        this.account = account;
        this.password = password;
        this.email = email;
        this.birthday = birthday;
        this.nickName = nickName;
        this.height = height;
        this.weight = weight;
        this.gender = gender;
        this.icon = icon;
    }
}
