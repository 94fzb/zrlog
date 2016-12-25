package com.fzb.blog.web.incp;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RouterInterceptor implements Interceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(RouterInterceptor.class);

    private VisitorInterceptor visitorInterceptor = new VisitorInterceptor();
    private AdminInterceptor adminInterceptor = new AdminInterceptor();

    @Override
    public void intercept(Invocation invocation) {
        try {
            String actionKey = invocation.getActionKey();
            if (actionKey.startsWith("/admin") || actionKey.startsWith("/api/admin")) {
                adminInterceptor.intercept(invocation);
            } else {
                visitorInterceptor.intercept(invocation);
            }
        } catch (Exception e) {
            e.printStackTrace();
            invocation.getController().renderError(500);
            LOGGER.error("interceptor exception ", e);
        }
    }
}
