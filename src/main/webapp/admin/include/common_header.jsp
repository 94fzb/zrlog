<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" import="java.util.*" import="com.fzb.blog.common.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String scheme = com.fzb.blog.web.util.WebTools.getRealScheme(request);
String basePath = scheme + "://"+request.getHeader("host")+path+"/";
request.setAttribute("url", scheme + "://"+request.getHeader("host")+request.getContextPath());
if(request.getAttribute("currentPath")==null){
	request.setAttribute("currentPath", com.fzb.blog.util.ZrlogUtil.getFullUrl(request).substring((basePath+"admin/").length()));
}
if(request.getAttribute("currentPage")==null){
	request.setAttribute("currentPage", com.fzb.blog.util.ZrlogUtil.getFullUrl(request).replaceAll(".jsp","").substring(basePath.length()));
}
request.setAttribute("res",new com.google.gson.Gson().toJson(request.getAttribute("_res")));
%>
<!DOCTYPE html>
<html>
  <base href="<%=basePath%>">
  <head>
    <title>${webs.title} | ${_res['admin.management']} </title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <!-- Meta, title, CSS, favicons, etc. -->
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="shortcut icon" href="${cacheFile['/favicon.ico']}" />
    <!-- Bootstrap -->
    <link href="${cacheFile['/assets/css/bootstrap.min.css']}" rel="stylesheet">
    <!-- Font Awesome -->
    <link href="${cacheFile['/assets/css/font-awesome.min.css']}" rel="stylesheet">
    <!-- NProgress -->
    <link href="${cacheFile['/assets/css/nprogress.css']}" rel="stylesheet">
    <link href="${cacheFile['/assets/css/select2.min.css']}" rel="stylesheet">
    <link href="${cacheFile['/assets/css/switchery.min.css']}" rel="stylesheet">
    <!-- Custom Theme Style -->
    <link href="${cacheFile['/assets/css/custom.min.css']}" rel="stylesheet">

    <link rel="stylesheet" href="${cacheFile['/assets/css/pnotify.css']}" />
    <!-- jQuery -->
    <script src="${cacheFile['/assets/js/jquery.min.js']}"></script>
    <script src="${cacheFile['/admin/js/dashboard.js']}"></script>
    <script>
        var _res = ${res};
        var editorMdPath = "admin/markdown/lib/"
    </script>

  </head>