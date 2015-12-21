<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
request.setAttribute("url", request.getScheme()+"://"+request.getHeader("host")+request.getContextPath());
request.setAttribute("suburl", request.getRequestURL().substring(basePath.length()));
%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 
<!DOCTYPE html>
<html>
  	<base href="<%=basePath%>">
  	<c:set var="webs" value="${init.webSite}" scope="session"></c:set>
	<head>
		<meta charset="utf-8" />
		<title>${webs.title} - 后台管理 </title>
		<link href="${url}/assets/css/bootstrap.min.css" rel="stylesheet" />
		<link rel="stylesheet" href="${url}/assets/css/font-awesome.min.css" />

		<link rel="stylesheet" href="${url}/assets/css/ace.min.css" />
		<link rel="stylesheet" href="${url}/assets/css/ace-rtl.min.css" />
		<link rel="stylesheet" href="${url}/assets/css/ace-skins.min.css" />

		<script src="${url}/assets/js/jquery-2.0.3.min.js"></script>
		<script src="${url}/assets/js/bootstrap.min.js"></script>
		<script src="${url}/assets/js/typeahead-bs2.min.js"></script>
		<script src="${url}/assets/js/ace-elements.min.js"></script>
		<script src="${url}/assets/js/ace.min.js"></script>
		<script src="${url}/assets/js/ace-extra.min.js"></script>
		<link rel="stylesheet" href="${url}/assets/css/jquery.gritter.css" />
		<script src="${url}/assets/js/jquery.gritter.min.js"></script>
		<script src="${url}/assets/js/bootbox.min.js"></script>
	</head>
<body>
<div class="main-container">