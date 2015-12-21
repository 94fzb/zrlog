<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%
	request.setCharacterEncoding("UTF-8");
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
	request.setAttribute("basePath", basePath);
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<base href="${basePath }">
<title>zrlog 安装向导</title>
<link href="${basePath}assets/css/bootstrap.min.css" rel="stylesheet" />
<link rel="stylesheet" href="${basePath}assets/css/font-awesome.min.css" />
<link rel="stylesheet" href="${basePath}assets/css/ace.min.css" />
<link rel="stylesheet" href="${basePath}assets/css/ace-rtl.min.css" />
</head>

<body>
	<div class="widget-header widget-header-blue widget-header-flat">
		<h4 class="lighter">Zrlog 安装向导</h4>
	</div>
	<div class="widget-body">
		<div class="widget-main">
			<div class="alert alert-danger"
				<c:if test="${empty errorMsg }">style="display: none;"</c:if>>
				<b>${errorMsg}</b>
			</div>