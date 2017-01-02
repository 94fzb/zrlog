package com.fzb.blog.model;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;

import java.io.Serializable;

/**
 * 存放用于管理员的相关信息，对应数据的user表。这里实现序列化的目前是user数据存放在了session中必须实现序列化的接口
 */
public class User extends Model<User> implements Serializable {

    public static final User dao = new User();

    public User login(String userName, String password) {
        return findFirst("select * from user where username=? and password=?",
                userName, password);
    }

    public String getPasswordByName(String userName) {
        return (String) findFirst("select password from user where username=?",
                userName).get("password");
    }

    public boolean updatePassword(String userName, String password) {
        return Db.update("update user set password=? where userName=?",
                password, userName) > 0;
    }

}
