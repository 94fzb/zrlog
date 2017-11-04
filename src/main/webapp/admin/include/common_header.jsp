<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page session="false"  pageEncoding="UTF-8" %>
<%
    String path = request.getContextPath();
    String scheme = com.zrlog.web.util.WebTools.getRealScheme(request);
    String basePath = scheme + "://" + request.getHeader("host") + path + "/";
    request.setAttribute("url", scheme + "://" + request.getHeader("host") + request.getContextPath());
    if (request.getAttribute("currentPath") == null) {
        request.setAttribute("currentPath", com.zrlog.util.ZrlogUtil.getFullUrl(request).substring((basePath + "admin/").length()));
    }
    if (request.getAttribute("currentPage") == null) {
        request.setAttribute("currentPage", com.zrlog.util.ZrlogUtil.getFullUrl(request).replaceAll(".jsp", "").substring(basePath.length()));
    }
    request.setAttribute("res", new com.google.gson.Gson().toJson(request.getAttribute("_res")));
%>
<!DOCTYPE html>
<html>
<base href="<%=basePath%>">
<head>
    <title>${webs.title} | <c:if test="${not empty subTitle}"> ${subTitle} | </c:if> ${_res['admin.management']}</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="shortcut icon" href="${cacheFile['/favicon.ico']}"/>
    <link href="${cacheFile['/assets/css/bootstrap.min.css']}" rel="stylesheet">
    <link href="${cacheFile['/assets/css/font-awesome.min.css']}" rel="stylesheet">
    <link href="${cacheFile['/assets/css/nprogress.css']}" rel="stylesheet">
    <link href="${cacheFile['/assets/css/select2.min.css']}" rel="stylesheet">
    <link href="${cacheFile['/assets/css/switchery.min.css']}" rel="stylesheet">
    <link href="${cacheFile['/assets/css/pnotify.css']}" rel="stylesheet"/>
    <link href="${cacheFile['/assets/css/custom.min.css']}" rel="stylesheet">
    <script src="${cacheFile['/assets/js/jquery.min.js']}"></script>
    <script src="${cacheFile['/admin/js/dashboard.js']}"></script>
    <script>
        var _res = ${res};
        var editorMdPath = "admin/markdown/lib/";
    </script>
</head>