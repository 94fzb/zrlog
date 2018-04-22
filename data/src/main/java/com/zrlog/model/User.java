package com.zrlog.model;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;

/**
 * 存放用于管理员的相关信息，对应数据的user表
 */
public class User extends Model<User> {

    public static final User dao = new User();
    public static final String TABLE_NAME = "user";

    public String getPasswordByUserName(String userName) {
        User user = findFirst("select * from " + TABLE_NAME + " where username=?", userName);
        if (user != null) {
            return user.get("password");
        }
        return null;
    }

    public User getIdByUserName(String userName) {
        User user = findFirst("select * from " + TABLE_NAME + " where username=?", userName);
        if (user != null) {
            return user;
        }
        return null;
    }

    public String getPasswordByUserId(int userId) {
        return (String) findFirst("select password from " + TABLE_NAME + " where userId=?", userId).get("password");
    }

    public boolean updatePassword(int userId, String password) {
        return Db.update("update " + TABLE_NAME + " set password=? where userId=?", password, userId) > 0;
    }

    public void updateEmailUserNameHeaderByUserId(String email, String userName, String header, int userId) {
        Db.update("update " + TABLE_NAME + " set header=?,email=?,userName=? where userId=?", header, email, userName, userId);
    }

}
