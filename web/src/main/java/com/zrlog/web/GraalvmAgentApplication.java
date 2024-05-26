package com.zrlog.web;

import com.google.gson.Gson;
import com.hibegin.common.util.BeanUtil;
import com.hibegin.common.util.LoggerUtil;
import com.hibegin.http.server.WebServerBuilder;
import com.hibegin.http.server.util.FreeMarkerUtil;
import com.hibegin.http.server.util.PathUtil;
import com.zrlog.admin.business.rest.base.*;
import com.zrlog.admin.business.rest.request.CreateArticleRequest;
import com.zrlog.admin.business.rest.request.CreateLinkRequest;
import com.zrlog.admin.business.rest.request.LoginRequest;
import com.zrlog.admin.business.rest.response.*;
import com.zrlog.blog.web.util.WebTools;
import com.zrlog.business.cache.vo.Archive;
import com.zrlog.business.cache.vo.BaseDataInitVO;
import com.zrlog.business.cache.vo.HotLogBasicInfoVO;
import com.zrlog.business.util.PagerVO;
import com.zrlog.common.Constants;
import com.zrlog.common.rest.response.ApiStandardResponse;
import com.zrlog.common.vo.*;
import com.zrlog.data.dto.PageData;
import com.zrlog.util.DbConnectUtils;
import com.zrlog.util.ZrLogUtil;
import org.apache.commons.dbutils.BasicRowProcessor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * 该类仅提供基础的 graalvm 运行依赖反射扫描等功能，不作为实际的启动入口
 */
public class GraalvmAgentApplication {

    private static void doLoopLoad(File[] files, String basePath, String uriStart) {
        if (Objects.isNull(files)) {
            return;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                doLoopLoad(file.listFiles(), basePath, uriStart);
            } else {
                String binPath = file.toString().substring(basePath.length());
                String rFileName = uriStart + binPath.replace("\\", "/");
                try (InputStream inputStream = GraalvmAgentApplication.class.getResourceAsStream(rFileName)) {
                    if (Objects.nonNull(inputStream)) {
                        System.out.println("add filename " + rFileName);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private static void adminJson() {
        new Gson().toJson(new ArticleResponseEntry());
        new Gson().toJson(new PreCheckVersionResponse());
        new Gson().toJson(new WebSiteSettingsResponse());
        new Gson().toJson(new BlogWebSiteInfo());
        new Gson().toJson(new OtherWebSiteInfo());
        new Gson().toJson(new AdminWebSiteInfo());
        new Gson().toJson(new UpgradeWebSiteInfo());
        new Gson().toJson(new TemplateVO());
        new Gson().toJson(new BasicWebSiteInfo());
        new Gson().toJson(new ArticleGlobalResponse());
        new Gson().toJson(new ApiStandardResponse<PageData<ArticleResponseEntry>>());
        new Gson().toJson(new ApiStandardResponse<ArticleGlobalResponse>());
        new Gson().toJson(new ArticleTypeResponseEntry());
        new Gson().toJson(new UploadFileResponse(""));
        new Gson().toJson(new LoginRequest());
        new Gson().toJson(new LoginResponse(""));
        new Gson().toJson(new CreateArticleRequest());
        new Gson().toJson(new CreateLinkRequest());
        new Gson().toJson(new IndexResponse(new StatisticsInfoResponse(), new ArrayList<>(), new ArrayList<>(), false));
        new Gson().toJson(new StatisticsInfoResponse());
        new Gson().toJson(new ServerSideDataResponse(new UserBasicInfoResponse(), new HashMap<>(), new Object(), "/"));
        new Gson().toJson(new AdminTokenVO());
        new Gson().toJson(new ServerInfo("1", "1"));
    }

    private static void json() {
        new Gson().toJson(new HotLogBasicInfoVO());
        new Gson().toJson(new UserBasicInfoResponse());
        new Gson().toJson(new BaseDataInitVO());
        new Gson().toJson(new PageData<>());
        new Gson().toJson(new PageData<>(0L, new ArrayList<>()));
        new Gson().toJson(new PageData<>(0L, new ArrayList<>(), 0L, 0L));
        new Gson().toJson(new BaseDataInitVO.Statistics());
        new Gson().toJson(new I18nVO());
        new Gson().toJson(new Outline());
        new Gson().toJson(new Version());
        new Gson().toJson(new PagerVO());
        new Gson().toJson(new PagerVO.PageEntry());
        new Gson().toJson(new Archive());

    }

    public static class MyBasicRowProcessor extends BasicRowProcessor {
        public static Map<String, Object> createMap() {
            return createCaseInsensitiveHashMap(2);
        }
    }

    private static void cloneObj() {
        BeanUtil.cloneObject(new BaseDataInitVO());
        BeanUtil.cloneObject(new Archive());
        BeanUtil.cloneObject(new BaseDataInitVO.Statistics());
        BeanUtil.cloneObject(new HotLogBasicInfoVO());
        BeanUtil.cloneObject(Constants.WEB_SITE);
        BeanUtil.cloneObject(new HashMap<>());
        BeanUtil.cloneObject(new LinkedHashMap<>());
        BeanUtil.cloneObject(new TreeMap<>());
        BeanUtil.cloneObject(new String(""));
        BeanUtil.cloneObject(new Number() {
            @Override
            public int intValue() {
                return 0;
            }

            @Override
            public long longValue() {
                return 0;
            }

            @Override
            public float floatValue() {
                return 0;
            }

            @Override
            public double doubleValue() {
                return 0;
            }
        });
        BeanUtil.cloneObject(Long.parseLong("0"));
        BeanUtil.cloneObject(Integer.parseInt("0"));
        BeanUtil.cloneObject(Short.parseShort("0"));
        BeanUtil.cloneObject(Boolean.FALSE);
        BeanUtil.cloneObject(Byte.valueOf((byte) 0));
        BeanUtil.cloneObject(new ArrayList<>());
        try {
            Arrays.stream(Class.forName("java.util.ImmutableCollections$ListN").getDeclaredConstructors()).forEach(e -> {
                try {
                    e.setAccessible(true);
                    BeanUtil.cloneObject(e.newInstance(new String[0], true));
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            });
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        BeanUtil.cloneObject(MyBasicRowProcessor.createMap());
    }

    private static void database() {
        Properties properties = new Properties();
        properties.put("jdbcUrl", "jdbc:mysql://0.0.0.0:3306/zrlog_test?characterEncoding=UTF-8&allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=GMT");
        properties.put("user", "root");
        properties.put("password", "123456");
        properties.put("driverClass", "com.mysql.cj.jdbc.Driver");
        try {
            DbConnectUtils.getConnection(properties);
        } catch (Exception e) {
            LoggerUtil.getLogger(GraalvmAgentApplication.class).info("For test connect " + e.getMessage());
        }
    }

    private static void loadResource() {
        Path currentRelativePath = Paths.get("");
        String admin = currentRelativePath.toAbsolutePath().getParent().getParent().toString() + "/admin-web/src/main/frontend/build/";
        doLoopLoad(new File(admin).listFiles(), admin, "/admin/");
        String install = currentRelativePath.toAbsolutePath().getParent().getParent().toString() + "/blog-web/src/main/frontend/build/";
        doLoopLoad(new File(install).listFiles(), install, "/install/");
    }

    public static void main(String[] args) {
        json();
        adminJson();
        cloneObj();
        database();
        loadResource();
        //last
        webserver(args);
    }

    public static void webserver(String[] args) {
        WebServerBuilder webServerBuilder = Application.webServerBuilder(args, 0, null, false);
        webServerBuilder.addStartSuccessHandle(() -> {
            try {
                FreeMarkerUtil.init(PathUtil.getStaticFile(Constants.DEFAULT_TEMPLATE_PATH).getPath().replace("/package-web/target", ""));
            } catch (Exception e) {
                LoggerUtil.getLogger(GraalvmAgentApplication.class).info("Freemarker init error " + e.getMessage());
            }
            try {
                FreeMarkerUtil.renderToFM("empty", WebTools.buildMockRequest("GET", "/", Constants.zrLogConfig.getRequestConfig(), webServerBuilder.getWebServer().getApplicationContext()));
            } catch (Exception e) {
                LoggerUtil.getLogger(GraalvmAgentApplication.class).info("Freemarker render error " + e.getMessage());
            }
            webServerBuilder.getWebServer().getApplicationContext().getServerConfig().getRouter().getRouterMap().forEach((key, value) -> {
                try {
                    value.invoke(ZrLogUtil.buildController(value, WebTools.buildMockRequest("GET", key, Constants.zrLogConfig.getRequestConfig(), webServerBuilder.getWebServer().getApplicationContext()), null));
                } catch (RuntimeException e) {
                    LoggerUtil.getLogger(GraalvmAgentApplication.class).severe("" + e);
                } catch (Exception e) {
                    //ignore
                    //LOGGER.severe("" + e);
                }
            });
            System.exit(0);
            return null;
        });
        webServerBuilder.startWithThread();
    }
}
