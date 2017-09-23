package com.fzb.blog.model;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;

/**
 * 存放用于管理员的相关信息，对应数据的user表
 */
public class User extends Model<User> {

    public static final User dao = new User();

    public String getPasswordByUserName(String userName) {
        User user = findFirst("select * from user where username=?", userName);
        if (user != null) {
            return user.get("password");
        }
        return null;
    }

    public Integer getIdByUserName(String userName) {
        User user = findFirst("select * from user where username=?", userName);
        if (user != null) {
            return user.getInt("userId");
        }
        return null;
    }

    public String getPasswordByUserId(int userId) {
        return (String) findFirst("select password from user where userId=?", userId).get("password");
    }

    public boolean updatePassword(int userId, String password) {
        return Db.update("update user set password=? where userId=?", password, userId) > 0;
    }

}
