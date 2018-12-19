package com.huichenghe.xinlvshuju.DbEntities;

import java.util.Date;

import android.content.Context;

import com.lidroid.xutils.db.annotation.Column;
public abstract class EntityBase {

    //@Id // 如果主键没有命名名为id或_id的时，需要为主键添加此注解
    //@NoAutoIncrement // int,long类型的id默认自增，不想使用自增时添加此注解
	public int id;

//    @Column(column = "account") // 建议加上注解， 混淆后列名不受影响
//    public String account;

//    @Unique
//    
    @Column(column = "date")
    public Date date;
    
    public EntityBase() {}
    
    public EntityBase(Context c, Date date) {
//    	String account = MyGlobalConfig.getUserDataAtApp(c).read(UserInfoConfig.account);
//    	setAccount(account);
    	this.date = date;
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
//    public String getAccount() {
//        return account;
//    }
//
//    public void setAccount(String account) {
//        this.account = account;
//    }
    
    
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
    
    
}
