<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getHeader("host")+path+"/";
request.setAttribute("url", request.getScheme()+"://"+request.getHeader("host")+request.getContextPath());
if(request.getAttribute("currentPath")==null){
	request.setAttribute("currentPath", request.getRequestURL().substring((basePath+"admin/").length()));
}
Map init = (Map)request.getAttribute("init");
Map webSite = (Map)init.get("webSite");
if(webSite.get("admin_dashboard_naver") == null){
    webSite.put("admin_dashboard_naver","nav-md");
}
request.getSession().setAttribute("webs",webSite);
%>
<!DOCTYPE html>
<html>
  <base href="<%=basePath%>">
  <head>
    <title>${webs.title} | 后台管理 </title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <!-- Meta, title, CSS, favicons, etc. -->
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="shortcut icon" href="${url}/favicon.ico" />
    <!-- Bootstrap -->
    <link href="${url}/assets/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome -->
    <link href="${url}/assets/css/font-awesome.min.css" rel="stylesheet">
    <!-- NProgress -->
    <link href="${url}/assets/css/nprogress.css" rel="stylesheet">
    <link href="${url}/assets/css/select2.min.css" rel="stylesheet">
    <link href="${url}/assets/css/switchery.min.css" rel="stylesheet">
    <!-- Custom Theme Style -->
    <link href="${url}/assets/css/custom.min.css" rel="stylesheet">

    <link rel="stylesheet" href="assets/css/pnotify.css" />
    <!-- jQuery -->
    <script src="${url}/assets/js/jquery.min.js"></script>
    <script src="${url}/admin/js/dashboard.js"></script>
  </head>