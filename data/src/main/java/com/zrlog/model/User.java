package com.zrlog.model;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;

import java.util.Map;

/**
 * 存放用于管理员的相关信息，对应数据的user表
 */
public class User extends Model<User> {

    public static final String TABLE_NAME = "user";

    public String getPasswordByUserName(String userName) {
        User user = findFirst("select * from " + TABLE_NAME + " where userName=?", userName);
        if (user != null) {
            return user.get("password");
        }
        return null;
    }

    public User getUserByUserName(String userName) {
        return findFirst("select * from " + TABLE_NAME + " where userName=?", userName);
    }

    public String getPasswordByUserId(int userId) {
        return findFirst("select password from " + TABLE_NAME + " where userId=?", userId).get("password");
    }

    public boolean updatePassword(int userId, String password) {
        return Db.update("update " + TABLE_NAME + " set password=? where userId=?", password, userId) > 0;
    }

    public void updateEmailUserNameHeaderByUserId(String email, String userName, String header, int userId) {
        Db.update("update " + TABLE_NAME + " set header=?,email=?,userName=? where userId=?", header, email, userName, userId);
    }

    public Map<String, Object> getAttrs() {
        return super._getAttrs();
    }
}
