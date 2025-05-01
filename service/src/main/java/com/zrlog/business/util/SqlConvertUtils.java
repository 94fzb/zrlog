package com.zrlog.business.util;

import com.hibegin.common.util.IOUtil;
import com.hibegin.common.util.StringUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class SqlConvertUtils {

    public static List<String> extractExecutableSqlByInputStream(InputStream inputStream) {
        return extractExecutableSql(IOUtil.getStringInputStream(inputStream));
    }

    public static List<String> extractExecutableSql(String sql) {
        String[] sqlArr = sql.split("\n");
        StringBuilder tempSqlStr = new StringBuilder();
        List<String> sqlList = new ArrayList<>();
        for (String sqlSt : sqlArr) {
            if (sqlSt.startsWith("#") || sqlSt.startsWith("/*")) {
                continue;
            }
            if (sqlSt.startsWith("--")) {
                continue;
            }
            if (sqlSt.startsWith("USE")) {
                continue;
            }
            if (sqlSt.startsWith("CREATE DATABASE")) {
                continue;
            }
            if (sqlSt.startsWith("LOCK TABLES")) {
                continue;
            }
            if (sqlSt.startsWith("UNLOCK TABLES")) {
                continue;
            }
            tempSqlStr.append(sqlSt).append("\n");
        }
        String[] cleanSql = tempSqlStr.toString().split(";\n");
        for (String sqlSt : cleanSql) {
            if (StringUtils.isEmpty(sqlSt) || sqlSt.trim().isEmpty()) {
                continue;
            }
            String[] split = sqlSt.split("\n");
            StringBuilder sb = new StringBuilder();
            for (String t : split) {
                if (StringUtils.isEmpty(t) || t.trim().isEmpty()) {
                    continue;
                }
                sb.append(t);
            }
            sqlList.add(sb.toString());
        }
        return sqlList;
    }
}
