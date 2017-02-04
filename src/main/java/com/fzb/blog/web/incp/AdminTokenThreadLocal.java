package com.fzb.blog.web.incp;

/**
 * 使用ThreadLocal作多个类之间取值，和设值。而不是向传统方式需要获得HttpRequest对象才能取值，设值。
 */
public class AdminTokenThreadLocal {

    private static ThreadLocal<AdminToken> userThreadLocal = new ThreadLocal<AdminToken>();

    public static AdminToken getUser() {
        return userThreadLocal.get();
    }

    public static void setAdminToken(AdminToken user) {
        if (userThreadLocal.get() == null)
            userThreadLocal.set(user);
    }

    public static Integer getUserId() {
        return userThreadLocal.get().getUserId();
    }

    public static void remove() {
        userThreadLocal.remove();
    }
}
