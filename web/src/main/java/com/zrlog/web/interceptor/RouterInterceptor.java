package com.zrlog.web.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.zrlog.web.handler.GlobalResourceHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JFinal没有对应URI path的过滤的配置，于是需要手动通过URI path进行划分到不同的Interception。
 */
public class RouterInterceptor implements Interceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(RouterInterceptor.class);

    private VisitorInterceptor visitorInterceptor = new VisitorInterceptor();
    private AdminInterceptor adminInterceptor = new AdminInterceptor();

    @Override
    public void intercept(Invocation invocation) {
        try {
            String actionKey = invocation.getActionKey();
            //这样写一点页不优雅，路径少还好，多了就痛苦了
            if (actionKey.startsWith("/admin") || actionKey.startsWith("/api/admin")) {
                adminInterceptor.intercept(invocation);
            } else {
                visitorInterceptor.intercept(invocation);
            }
            GlobalResourceHandler.printUserTime("Router");
        } catch (Exception e) {
            LOGGER.error("interceptor exception ", e);
            invocation.getController().renderError(500);
        }
    }
}
