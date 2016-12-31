package com.fzb.blog.web.incp;

import com.fzb.blog.model.User;

/**
 * 使用ThreadLocal作多个类之间取值，和设值。而不是向传统方式需要获得HttpRequest对象才能取值，设值。
 */
public class AdminTokenThreadLocal {

    private static ThreadLocal<User> userThreadLocal = new ThreadLocal<User>();

    public static User getUser() {
        return userThreadLocal.get();
    }

    public static void setUser(User user) {
        if (userThreadLocal.get() == null)
            userThreadLocal.set(user);
    }

    public static Integer getUserId() {
        return userThreadLocal.get().get("userId");
    }

    public static void remove() {
        userThreadLocal.remove();
    }
}
