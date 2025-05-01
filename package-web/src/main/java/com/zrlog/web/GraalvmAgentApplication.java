package com.zrlog.web;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.hibegin.common.dao.dto.PageData;
import com.hibegin.common.util.BeanUtil;
import com.hibegin.common.util.IOUtil;
import com.hibegin.http.server.WebServerBuilder;
import com.hibegin.http.server.util.PathUtil;
import com.hibegin.lambda.rest.ApiGatewayHttp;
import com.hibegin.lambda.rest.ApiGatewayRequestContext;
import com.hibegin.lambda.rest.LambdaApiGatewayRequest;
import com.hibegin.lambda.rest.LambdaApiGatewayResponse;
import com.zrlog.admin.business.rest.request.UpdateTemplateConfigRequest;
import com.zrlog.admin.plugin.rest.response.UploadServiceResponseEntity;
import com.zrlog.blog.web.util.PagerUtil;
import com.zrlog.common.ZrLogConfig;
import com.zrlog.data.cache.vo.Archive;
import com.zrlog.data.cache.vo.BaseDataInitVO;
import com.zrlog.data.cache.vo.HotLogBasicInfoEntry;
import com.zrlog.data.dto.FaviconBase64DTO;
import com.zrlog.util.BlogBuildInfoUtil;
import com.zrlog.util.ZrLogUtil;

import java.io.IOException;
import java.util.*;

/**
 * 该类仅提供基础的 graalvm 运行依赖反射扫描等功能，不作为实际的启动入口
 */
public class GraalvmAgentApplication {

    static {
        System.getProperties().put("java.util.logging.SimpleFormatter.format", "%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS %4$s %5$s%6$s%n");
    }

    private static void json() {
        new Gson().toJson(new LambdaApiGatewayRequest());
        new Gson().toJson(new LambdaApiGatewayResponse());
        new Gson().fromJson(IOUtil.getStringInputStream(GraalvmAgentApplication.class.getResourceAsStream("/lambda.json")), LambdaApiGatewayRequest.class);
        new Gson().toJson(new ApiGatewayHttp());
        new Gson().toJson(new ApiGatewayRequestContext());
    }

    private static void cloneObj() {
        BeanUtil.cloneObject(PagerUtil.generatorPager("/all", 1, 20));
        ArrayList<Object> objects = new ArrayList<>();
        BeanUtil.cloneObject(new BaseDataInitVO());
        BeanUtil.cloneObject(new PageData<>(1L, objects));
        BeanUtil.cloneObject(new Archive());
        BeanUtil.cloneObject(new BaseDataInitVO.Statistics());
        BeanUtil.cloneObject(new HotLogBasicInfoEntry());
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
        BeanUtil.cloneObject(Double.parseDouble("0"));
        BeanUtil.cloneObject(new LinkedTreeMap());
        BeanUtil.cloneObject(Boolean.FALSE);
        BeanUtil.cloneObject(Byte.valueOf((byte) 0));
        BeanUtil.cloneObject(new ArrayList<>());
        BeanUtil.convert(new HashMap<>(), FaviconBase64DTO.class);
        BeanUtil.convert(new HashMap<>(), UpdateTemplateConfigRequest.class);
        BeanUtil.convert(new HashMap<>(), UploadServiceResponseEntity.class);
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
    }


    public static void main(String[] args) throws Exception {
        ZrLogConfig.nativeImageAgent = true;
        BlogBuildInfoUtil.getBlogProp();
        PathUtil.setRootPath(System.getProperty("user.dir").replace("/package-web/target", ""));
        json();
        cloneObj();
        //last
        webserver(args);
    }

    private static void webserver(String[] args) {
        WebServerBuilder webServerBuilder = Application.webServerBuilder(0, ZrLogUtil.getContextPath(args), null);
        webServerBuilder.addCreateSuccessHandle(() -> {
            System.exit(0);
            return null;
        });
        webServerBuilder.startWithThread();
    }


}
