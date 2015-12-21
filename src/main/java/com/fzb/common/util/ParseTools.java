package com.fzb.common.util;

import com.fzb.blog.dev.ThemeUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ParseTools {
    public static int getFirstRecord(int page, int pageSize) {
        return (page - 1) * pageSize;
    }

    public static int getTotalPate(long count, int pageSize) {
        return (int) Math.ceil(count / (pageSize * 1.0D));
    }

    public static String autoDigest(String str, int size) {
        String digest = str.replaceAll("<[^>]*>", "").replaceAll("\t|\r|", "").replace("\n", " ");
        if (digest.length() > size) {
            digest = digest.substring(0, size) + "  ...";
        }
        return digest;
    }

    /**
     * @param str
     * @param defaultValue
     * @return
     */
    public static int strToInt(String str, int defaultValue) {
        if (str == null) return defaultValue;
        if (str.matches("^-?[1-9]\\d*$")) {
            return Integer.parseInt(str);
        }
        return defaultValue;
    }

    public static String toISO8601(Date releaseTime) {
        return releaseTime.getYear() + 1900 + (releaseTime.getMonth() + 1)
                + releaseTime.getDate() + "T" + releaseTime.getHours()
                + releaseTime.getMinutes() + releaseTime.getSeconds() + "+08";
    }

    public static Date getDataBySdf(String sdfStr, Object dateStr)
            throws ParseException {
        if (dateStr != null) {
            SimpleDateFormat sdf = new SimpleDateFormat(sdfStr);
            return sdf.parse(dateStr.toString().replace("T", " "));
        }
        return new Date();
    }

    public static String mapToQueryStr(Map<String, Object> params) {
        String queryStr = "";
        if (params != null && !params.isEmpty()) {
            for (Map.Entry<String, Object> param : params.entrySet()) {
                if (param.getValue() instanceof List) {
                    @SuppressWarnings("unchecked")
                    List<Object> values = (List<Object>) param.getValue();
                    for (Object object : values) {
                        queryStr += param.getKey() + "=" + object + "&";
                    }
                } else {
                    queryStr += param.getKey() + "=" + param.getValue() + "&";
                }
            }
            queryStr = queryStr.substring(0, queryStr.length() - 1);
        }
        return queryStr;
    }

    public static void main(String[] args) {
        /*
         * try { getDataBySdf("yyyy-MM-dd hh:mm", "2014-03-16T22:57:26+08:00");
		 * } catch (ParseException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 */
        /*ZipInputStream zipIn;
        try {
			zipIn = new ZipInputStream(new FileInputStream("E:/putty.zip"));
			ZipEntry in = null;
			ZipFile zip = new ZipFile("E:/putty.zip");
			while ((in = zipIn.getNextEntry()) != null) {
				InputStream fin = zip.getInputStream(in);
				byte[] b = IOUtil.getByteByInputStream(fin);
				System.out.println(new String(b));

				
				 * System.out.println(in);
				 * System.out.println(in.getCompressedSize());
				 
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/

        ThemeUtil.generateTheme("simple", "E:/test");
    }
}
