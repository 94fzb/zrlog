package com.fzb.blog.web.incp;

import com.fzb.blog.model.User;

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
