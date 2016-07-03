<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%
	request.setCharacterEncoding("UTF-8");
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()+ path + "/";
	request.setAttribute("basePath", basePath);
	request.setAttribute("currentViewName", request.getRequestURL().substring((basePath+"install/").length()).replaceAll(".jsp",""));
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<base href="${basePath }">
<title>${_res.installWizard}</title>
<link href="${basePath}assets/css/bootstrap.min.css" rel="stylesheet" />
<link rel="stylesheet" href="${basePath}assets/css/font-awesome.min.css" />
<link rel="stylesheet" href="${basePath}assets/css/ace.min.css" />
</head>

<body>
	<div class="col-md-3"></div>
	<div class="col-md-6">
	<div class="widget-header widget-header-blue widget-header-flat">
		<h3 class="lighter">${_res.installWizard}</h3>
	</div>
	<div class="widget-body">
		<div class="widget-main">
			<div class="alert alert-danger"
				<c:if test="${empty errorMsg }">style="display: none;"</c:if>>
				<b>${errorMsg}</b>
			</div>
			<c:if test="${currentViewName ne 'forbidden'}">
			<div data-target="#step-container" class="row-fluid" id="fuelux-wizard">
            	<ul class="wizard-steps">
            		<li <c:if test="${currentViewName eq 'index'}">class="active"</c:if> data-target="#step1"><span class="step">1</span>
            			<span class="title">${_res.installDatabaseInfo}</span></li>

            		<li <c:if test="${currentViewName eq 'message'}">class="active"</c:if> data-target="#step2" class=""><span class="step">2</span> <span
            			class="title">${_res.installWebSiteInfo}</span></li>

            		<li <c:if test="${currentViewName eq 'success'}">class="active"</c:if> data-target="#step3" class=""><span class="step">3</span> <span
            			class="title">${_res.installComplete}</span></li>
            	</ul>
            </div>
            <div id="step-container"
            	class="step-content row-fluid position-relative">
            </c:if>
