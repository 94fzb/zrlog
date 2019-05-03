package com.zrlog.web.token;

import com.zrlog.common.vo.AdminTokenVO;

/**
 * 使用ThreadLocal作多个类之间取值，和设值。而不是向传统方式需要获得HttpRequest对象才能取值，设值。
 */
public class AdminTokenThreadLocal {

    private AdminTokenThreadLocal() {
    }

    private static ThreadLocal<AdminTokenVO> userThreadLocal = new ThreadLocal<>();

    public static AdminTokenVO getUser() {
        return userThreadLocal.get();
    }

    static void setAdminToken(AdminTokenVO user) {
        if (userThreadLocal.get() == null) {
            userThreadLocal.set(user);
        }
    }

    public static Integer getUserId() {
        return userThreadLocal.get().getUserId();
    }

    public static void remove() {
        userThreadLocal.remove();
    }
}
