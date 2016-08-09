package com.fzb.blog.config;

import com.fzb.common.util.IOUtil;
import com.fzb.common.util.http.HttpUtil;
import com.fzb.common.util.http.handle.CloseResponseHandle;
import com.jfinal.handler.Handler;
import com.jfinal.kit.PathKit;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * @author zhengchangchun 用于拦截通过请求 .jsp 后缀的请求 如果用户访问的后缀为 .html 的情况下. 第一次使用程序进行抓取.
 *         后面的直接跳转到静态文件
 */
public class JspSkipHandler extends Handler {

    private static final Logger LOGGER = LoggerFactory.getLogger(JspSkipHandler.class);

    private static final Set<String> FORBIDDEN_URI_EXT_SET = new HashSet<String>();

    static {
        FORBIDDEN_URI_EXT_SET.add(".jsp");
        FORBIDDEN_URI_EXT_SET.add(".properties");
    }

    public void handle(String target, HttpServletRequest request,
                       HttpServletResponse response, boolean[] isHandled) {
        String ext = null;
        if (target.contains("/")) {
            String name = target.substring(target.lastIndexOf('/'));
            if (name.contains(".")) {
                ext = name.substring(name.lastIndexOf('.'));
            }
        }
        if (ext != null) {
            if (!FORBIDDEN_URI_EXT_SET.contains(ext)) {
                // 处理静态化文件,仅仅缓存文章页(变化较小)
                if (target.endsWith(".html") && target.startsWith("/post/")) {
                    File htmlFile = new File(PathKit.getWebRootPath()
                            + request.getServletPath());
                    if (!htmlFile.exists()) {
                        String tempTarget = target.substring(0,
                                target.lastIndexOf('.'));
                        String home = request.getScheme() + "://"
                                + request.getHeader("host")
                                + request.getContextPath() + tempTarget;
                        target = tempTarget;
                        convert2Html(home, htmlFile);
                    }
                    this.next.handle(target, request, response, isHandled);
                } else {
                    this.next.handle(target, request, response, isHandled);
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
        } else {
            this.next.handle(target, request, response, isHandled);
        }

    }

    private void convert2Html(String sSourceUrl, File file) {
        if (!file.exists()) {
            file.getParentFile().mkdirs();
        }
        try {
            CloseableHttpResponse closeableHttpResponse = HttpUtil.sendGetRequest(sSourceUrl, new CloseResponseHandle(), new HashMap<String, String>()).getT();
            if (closeableHttpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                String str = IOUtil.getStringInputStream(closeableHttpResponse.getEntity().getContent());
                if (str != null) {
                    IOUtil.writeStrToFile(str, file);
                }
            }
        } catch (IOException e) {
            LOGGER.error("convert2Html error", e);
        }
    }
}
