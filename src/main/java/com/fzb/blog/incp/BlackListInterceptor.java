package com.fzb.blog.incp;

import com.fzb.blog.controlle.BaseController;
import com.fzb.blog.util.WebTools;
import com.jfinal.aop.PrototypeInterceptor;
import com.jfinal.core.ActionInvocation;
import com.jfinal.core.JFinal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class BlackListInterceptor extends PrototypeInterceptor {

	private static final Logger LOGGER = LoggerFactory.getLogger(BlackListInterceptor.class);

	@Override
	public void doIntercept(ActionInvocation ai) {
		if (ai.getController() instanceof BaseController) {
			BaseController baseController = (BaseController) ai.getController();
			String ipStr = baseController.getStrValueByKey("blackList");
			if (ipStr != null) {
				Set<String> ipSet = new HashSet<String>(Arrays.asList(ipStr.split(",")));
				String requestIP = WebTools.getRealIp(baseController.getRequest());
				if (ipSet.contains(requestIP)) {
					baseController.render(JFinal.me().getConstants().getErrorView(403));
				} else {
					ai.invoke();
				}
			} else {
				ai.invoke();
			}
		}
		else{
			ai.invoke();
		}
	}
}
