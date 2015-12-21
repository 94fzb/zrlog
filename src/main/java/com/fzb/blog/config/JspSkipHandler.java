package com.fzb.blog.config;

import com.fzb.common.util.HttpUtil;
import com.fzb.common.util.IOUtil;
import com.jfinal.handler.Handler;
import com.jfinal.kit.PathKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

/**
 * @author zhengchangchun 用于拦截通过请求 .jsp 后缀的请求 如果用户访问的后缀为 .html 的情况下. 第一次使用程序进行抓取.
 *         后面的直接跳转到静态文件
 */
public class JspSkipHandler extends Handler {

    private static Logger LOGGER = LoggerFactory.getLogger(JspSkipHandler.class);

    public void handle(String target, HttpServletRequest request,
                       HttpServletResponse response, boolean[] isHandled) {
        if (!target.endsWith(".jsp")) {
            // 处理静态化文件
            if (target.endsWith(".html")) {
                File htmlFile = new File(PathKit.getWebRootPath()
                        + request.getServletPath());
                if (!htmlFile.exists()) {
                    String tempTarget = target.substring(0,
                            target.lastIndexOf("."));
                    String home = request.getScheme() + "://"
                            + request.getHeader("host")
                            + request.getContextPath() + tempTarget;
                    target = tempTarget;
                    convert2Html(home, htmlFile);
                }
                this.nextHandler.handle(target, request, response, isHandled);
            } else {
                this.nextHandler.handle(target, request, response, isHandled);
            }
        } else {
            try {
                // 访问 .jsp 的情况下认为非法请求, 返回403
                request.getSession();
                response.sendError(403);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void convert2Html(String sSourceUrl, File file) {
        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs();
            }
            IOUtil.writeStrToFile(HttpUtil.getResponse(sSourceUrl), file);
        } catch (IOException e) {
            LOGGER.error("convert2Html error", e);
        }
    }
}
