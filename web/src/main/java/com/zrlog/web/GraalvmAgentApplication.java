package com.zrlog.web;

import com.google.gson.Gson;
import com.hibegin.common.util.BeanUtil;
import com.hibegin.common.util.LoggerUtil;
import com.hibegin.http.HttpMethod;
import com.hibegin.http.server.WebServerBuilder;
import com.hibegin.http.server.util.FreeMarkerUtil;
import com.hibegin.http.server.util.NativeImageUtils;
import com.hibegin.http.server.util.PathUtil;
import com.zrlog.admin.business.rest.base.*;
import com.zrlog.admin.business.rest.request.*;
import com.zrlog.admin.business.rest.response.*;
import com.zrlog.blog.web.util.WebTools;
import com.zrlog.business.cache.vo.Archive;
import com.zrlog.business.cache.vo.BaseDataInitVO;
import com.zrlog.business.cache.vo.HotLogBasicInfoVO;
import com.zrlog.business.rest.response.ArticleResponseEntry;
import com.zrlog.business.util.PagerUtil;
import com.zrlog.business.util.PagerVO;
import com.zrlog.common.Constants;
import com.zrlog.common.rest.response.ApiStandardResponse;
import com.zrlog.common.type.RunMode;
import com.zrlog.common.vo.*;
import com.zrlog.data.dto.PageData;
import com.zrlog.util.BlogBuildInfoUtil;
import com.zrlog.util.DbConnectUtils;
import org.apache.commons.dbutils.BasicRowProcessor;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * 该类仅提供基础的 graalvm 运行依赖反射扫描等功能，不作为实际的启动入口
 */
public class GraalvmAgentApplication {

    static {
        System.getProperties().put("java.util.logging.SimpleFormatter.format", "%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS %4$s %5$s%6$s%n");
    }

    private static void adminRequestJson() {
        //post
        new Gson().toJson(new CreateArticleRequest());
        new Gson().toJson(new CreateTypeRequest());
        new Gson().toJson(new LoginRequest());
        new Gson().toJson(new UpdateArticleRequest());
        new Gson().toJson(new CreateOrUpdateArticleResponse());
        new Gson().toJson(new CreateLinkRequest());
        new Gson().toJson(new CreateNavRequest());
        //update
        new Gson().toJson(new UpdateNavRequestRequest());
        new Gson().toJson(new UpdateTypeRequest());
        new Gson().toJson(new UpdateLinkRequest());
        new Gson().toJson(new UpdateAdminRequest());
        new Gson().toJson(new ReadCommentRequest());
        new Gson().toJson(new UpdatePasswordRequest());
    }

    private static void adminResponseJson() {
        new Gson().toJson(new TemplateDownloadResponse(""));
        new Gson().toJson(new ArticleResponseEntry());
        new Gson().toJson(new DownloadUpdatePackageResponse(0));
        new Gson().toJson(new UpgradeProcessResponse(false, ""));
        new Gson().toJson(new PreCheckVersionResponse());
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
        new Gson().toJson(new LoginResponse(""));
        new Gson().toJson(new IndexResponse(new StatisticsInfoResponse(), new ArrayList<>(), new ArrayList<>(), false, false));
        new Gson().toJson(new StatisticsInfoResponse());
        new Gson().toJson(new ServerSideDataResponse(new UserBasicInfoResponse(), new HashMap<>(), new Object(), "/", ""));
        new Gson().toJson(new AdminTokenVO());
        new Gson().toJson(new ExceptionResponse());
        new Gson().toJson(new VersionResponse());
        new Gson().toJson(new UploadTemplateResponse());
        new Gson().toJson(new DeleteLogResponse(true));
        new Gson().toJson(new LoadEditArticleResponse());
        new Gson().toJson(new UserBasicInfoResponse());
        new Gson().toJson(new ServerInfo("1", "1"));
    }

    private static void adminJson() {
        adminResponseJson();
        adminRequestJson();
    }

    private static void json() {
        new Gson().toJson(new HotLogBasicInfoVO());
        new Gson().toJson(new UserBasicInfoResponse());
        new Gson().toJson(new BaseDataInitVO());
        new Gson().toJson(new PageData<>());
        new Gson().toJson(new PageData<>());
        new Gson().toJson(MyBasicRowProcessor.createMap());
        ArrayList<Object> objects = new ArrayList<>();
        objects.add(MyBasicRowProcessor.createMap());
        new Gson().toJson(new PageData<>(0L, objects));
        new Gson().toJson(new PageData<>(0L, new ArrayList<>(), 0L, 0L));
        new Gson().toJson(new BaseDataInitVO.Statistics());
        new Gson().toJson(new I18nVO());
        new Gson().toJson(new Outline());
        new Gson().toJson(new Version());
        new Gson().toJson(PagerUtil.generatorPager("/all", 1, 20));
        new Gson().toJson(new PagerVO.PageEntry());
        new Gson().toJson(new Archive());

    }

    public static class MyBasicRowProcessor extends BasicRowProcessor {
        public static Map<String, Object> createMap() {
            return createCaseInsensitiveHashMap(2);
        }
    }

    private static void cloneObj() {
        BeanUtil.cloneObject(PagerUtil.generatorPager("/all", 1, 20));
        ArrayList<Object> objects = new ArrayList<>();
        objects.add(MyBasicRowProcessor.createMap());
        BeanUtil.cloneObject(new BaseDataInitVO());
        BeanUtil.cloneObject(new PageData<>(1L, objects));
        BeanUtil.cloneObject(new Archive());
        BeanUtil.cloneObject(new BaseDataInitVO.Statistics());
        BeanUtil.cloneObject(new HotLogBasicInfoVO());
        BeanUtil.cloneObject(new TreeMap<>());
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
        NativeImageUtils.doLoopResourceLoad(new File(admin).listFiles(), admin, "/admin/");
        String install = currentRelativePath.toAbsolutePath().getParent().getParent().toString() + "/blog-web/src/main/frontend/build/";
        NativeImageUtils.doLoopResourceLoad(new File(install).listFiles(), install, "/install/");
        List<String> sourceFiles = getSourceFiles(currentRelativePath);
        sourceFiles.forEach(e -> NativeImageUtils.doLoopResourceLoad(new File(e).listFiles(), e, "/"));
    }

    private static List<String> getSourceFiles(Path currentRelativePath) {
        List<String> sourceFiles = new ArrayList<>();
        String baseFileName = currentRelativePath.toAbsolutePath().getParent().getParent().toString();
        sourceFiles.add(baseFileName + "/service/src/main/resources/");
        sourceFiles.add(baseFileName + "/blog-web/src/main/resources/");
        sourceFiles.add(baseFileName + "/common/src/main/resources/");
        sourceFiles.add(baseFileName + "/data/src/main/resources/");
        return sourceFiles;
    }

    public static void main(String[] args) throws Exception {
        Constants.runMode = RunMode.NATIVE_AGENT;
        BlogBuildInfoUtil.getBlogProp();
        PathUtil.setRootPath(System.getProperty("user.dir").replace("/package-web/target", ""));
        json();
        adminJson();
        cloneObj();
        database();
        loadResource();
        //last
        webserver(args);
    }

    private static void webserver(String[] args) throws Exception {
        WebServerBuilder webServerBuilder = Application.webServerBuilder(0, null);
        webServerBuilder.addCreateSuccessHandle(() -> {
            try {
                FreeMarkerUtil.init(PathUtil.getStaticFile(Constants.DEFAULT_TEMPLATE_PATH).getPath());
            } catch (Exception e) {
                LoggerUtil.getLogger(GraalvmAgentApplication.class).info("Freemarker init error " + e.getMessage());
            }
            try {
                FreeMarkerUtil.initClassTemplate(Constants.DEFAULT_TEMPLATE_PATH);
            } catch (Exception e) {
                LoggerUtil.getLogger(GraalvmAgentApplication.class).info("Freemarker init error " + e.getMessage());
            }
            try {
                FreeMarkerUtil.renderToFM("empty", WebTools.buildMockRequest(HttpMethod.GET, "/", Constants.zrLogConfig.getRequestConfig(), webServerBuilder.getWebServer().getApplicationContext()));
            } catch (Exception e) {
                LoggerUtil.getLogger(GraalvmAgentApplication.class).info("Freemarker render error " + e.getMessage());
            }
            System.exit(0);
            return null;
        });
        webServerBuilder.startWithThread();
    }
}
