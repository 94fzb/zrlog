<%@ page import="com.zrlog.util.ZrLogUtil" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page session="false" pageEncoding="UTF-8" %>
<%
    String basePath = (String) request.getAttribute("basePath");
    if (request.getAttribute("currentPath") == null) {
        request.setAttribute("currentPath", ZrLogUtil.getFullUrl(request).substring((basePath + "admin/").length()));
    }
    if (request.getAttribute("currentPage") == null) {
        request.setAttribute("currentPage", ZrLogUtil.getFullUrl(request).replaceAll(".jsp", "").substring(basePath.length()));
    }
    request.setAttribute("res", new com.google.gson.Gson().toJson(request.getAttribute("_res")));
%>
<!DOCTYPE html>
<html>
<base href="${basePath}">
<head>
    <title>${webs.title} | <c:if test="${not empty subTitle}"> ${subTitle} | </c:if> ${_res['admin.management']}</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="shortcut icon" href="${basePath}favicon.ico"/>
    <link href="${basePath}assets/css/bootstrap.min.css" rel="stylesheet">
    <link href="${basePath}assets/css/font-awesome.min.css" rel="stylesheet">
    <link href="${basePath}assets/css/nprogress.css" rel="stylesheet">
    <link href="${basePath}assets/css/select2.min.css" rel="stylesheet">
    <link href="${basePath}assets/css/switchery.min.css" rel="stylesheet">
    <link href="${basePath}assets/css/pnotify.css" rel="stylesheet"/>
    <link href="${basePath}assets/css/custom.min.css" rel="stylesheet">
    <link href="${basePath}assets/css/custom.colorful.css" rel="stylesheet">
    <script src="${basePath}assets/js/jquery.min.js"></script>
    <script src="${basePath}admin/js/dashboard.js"></script>
    <script src="${basePath}admin/js/common.js"></script>
    <script src="${basePath}admin/js/i18n.js"></script>
    <script>
        var _res = ${res};
        var editorMdPath = "admin/markdown/lib/";
        initLang('${lang}');
    </script>
</head>