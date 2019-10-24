package com.zrlog.web.render;

import com.jfinal.render.FreeMarkerRender;
import com.jfinal.render.Render;
import com.jfinal.render.RenderException;
import freemarker.template.*;

import javax.servlet.ServletContext;
import java.io.PrintWriter;
import java.util.*;

public class BlogFrontendFreeMarkerRender extends Render {
    private static final String contentType = "text/html; charset=" + getEncoding();
    private static final Configuration config = new Configuration();

    public BlogFrontendFreeMarkerRender(String view) {
        this.view = view;
    }

    /**
     * freemarker can not load freemarker.properies automatically
     */
    public static Configuration getConfiguration() {
        return config;
    }

    public static void setProperties(Properties properties) {
        try {
            FreeMarkerRender.getConfiguration().setSettings(properties);
        } catch (TemplateException e) {
            throw new RuntimeException(e);
        }
    }

    public static void init(ServletContext servletContext, Locale locale, int template_update_delay) {
        // Initialize the FreeMarker configuration;
        // - Create a configuration instance
        // config = new Configuration();
        // - Templates are stoted in the WEB-INF/templates directory of the Web app.
        config.setServletContextForTemplateLoading(servletContext, "/");	// "WEB-INF/templates"
        // - Set update dealy to 0 for now, to ease debugging and testing.
        //   Higher value should be used in production environment.

        if (getDevMode()) {
            config.setTemplateUpdateDelay(0);
        }
        else {
            config.setTemplateUpdateDelay(template_update_delay);
        }

        // - Set an error handler that prints errors so they are readable with
        //   a HTML browser.
        // config.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
        config.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

        // - Use beans wrapper (recommmended for most applications)
        config.setObjectWrapper(ObjectWrapper.BEANS_WRAPPER);
        // - Set the default charset of the template files
        config.setDefaultEncoding(getEncoding());		// config.setDefaultEncoding("ISO-8859-1");
        // - Set the charset of the output. This is actually just a hint, that
        //   templates may require for URL encoding and for generating META element
        //   that uses http-equiv="Content-type".
        config.setOutputEncoding(getEncoding());			// config.setOutputEncoding("UTF-8");
        // - Set the default locale
        config.setLocale(locale /* Locale.CHINA */ );		// config.setLocale(Locale.US);
        config.setLocalizedLookup(false);

        // 去掉int型输出时的逗号, 例如: 123,456
        // config.setNumberFormat("#");		// config.setNumberFormat("0"); 也可以
        config.setNumberFormat("#0.#####");
        config.setDateFormat("yyyy-MM-dd");
        config.setTimeFormat("HH:mm:ss");
        config.setDateTimeFormat("yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 继承类可通过覆盖此方法改变 contentType，从而重用 freemarker 模板功能
     * 例如利用 freemarker 实现 FreeMarkerXmlRender 生成 Xml 内容
     */
    public String getContentType() {
        return contentType;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public void render() {
        response.setContentType(getContentType());

        Map data = new HashMap();
        for (Enumeration<String> attrs = request.getAttributeNames(); attrs.hasMoreElements();) {
            String attrName = attrs.nextElement();
            data.put(attrName, request.getAttribute(attrName));
        }

        PrintWriter writer = null;
        try {
            Template template = config.getTemplate(view);
            writer = response.getWriter();
            template.process(data, writer);		// Merge the data-model and the template
        } catch (Exception e) {
            throw new RenderException(e);
        }
    }
}
