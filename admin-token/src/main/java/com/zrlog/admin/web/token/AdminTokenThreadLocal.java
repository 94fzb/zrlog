package com.zrlog.admin.web.token;

import com.zrlog.common.vo.AdminTokenVO;

import java.util.Objects;

/**
 * 使用ThreadLocal作多个类之间取值，和设值。而不是向传统方式需要获得HttpRequest对象才能取值，设值。
 */
public class AdminTokenThreadLocal {

    private AdminTokenThreadLocal() {
    }

    private static final ThreadLocal<AdminTokenVO> userThreadLocal = new ThreadLocal<>();

    public static AdminTokenVO getUser() {
        return userThreadLocal.get();
    }

    public static String getUserProtocol() {
        if (Objects.isNull(getUser())) {
            return "http";
        }
        return getUser().getProtocol();
    }

    static void setAdminToken(AdminTokenVO user) {
        if (userThreadLocal.get() == null) {
            userThreadLocal.set(user);
        }
    }

    public static int getUserId() {
        if (Objects.isNull(userThreadLocal.get())) {
            return -1;
        }
        return userThreadLocal.get().getUserId();
    }

    public static void remove() {
        userThreadLocal.remove();
    }
}
