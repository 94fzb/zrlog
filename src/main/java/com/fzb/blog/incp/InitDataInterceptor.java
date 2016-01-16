package com.fzb.blog.incp;

import com.fzb.blog.controller.BaseController;
import com.jfinal.aop.Invocation;
import com.jfinal.aop.PrototypeInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by xiaochun on 16-1-16.
 */
public class InitDataInterceptor extends PrototypeInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(InitDataInterceptor.class);

    @Override
    public void doIntercept(Invocation invocation) {
        if (invocation.getController() instanceof BaseController) {
            HttpServletRequest request = invocation.getController().getRequest();
            invocation.getController().setAttr("requrl", request.getRequestURL());
            ((BaseController) invocation.getController()).initData();
        }
        invocation.invoke();
    }
}
