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
<link rel="stylesheet" href="${basePath}assets/css/custom.min.css" />
</head>

<body style="background:#fff">
	<div class="col-md-2"></div>
	<div class="col-md-8">
	<div class="x_panel">
		<div class="x_title">
			<div class="widget-header widget-header-blue widget-header-flat">
				<h3 class="lighter">${_res.installWizard}</h3>
			</div>
			<div class="clearfix"></div>
		</div>

		<div class="x_content">
			<div class="alert alert-danger"
				<c:if test="${empty errorMsg }">style="display: none;"</c:if>>
				<b>${errorMsg}</b>
			</div>
			<c:if test="${currentViewName ne 'forbidden'}">
			<div class="col-xs-12">
				<div id="wizard" class="form_wizard wizard_horizontal">
				  <ul class="wizard_steps anchor">
					<li>
					  <a <c:if test="${currentViewName eq 'index'}">class="selected"</c:if> href="#step-1">
						<span class="step_no">1</span>
						<span class="step_descr">${_res.installDatabaseInfo}</span>
					  </a>
					</li>
					<li>
					  <a  <c:if test="${currentViewName eq 'message'}">class="selected"</c:if>>
						<span class="step_no">2</span>
						<span class="step_descr">${_res.installWebSiteInfo}</span>
					  </a>
					</li>
					<li>
					  <a <c:if test="${currentViewName eq 'success'}">class="selected"</c:if>>
						<span class="step_no">3</span>
						<span class="step_descr">${_res.installComplete}</span>
					  </a>
					</li>
				  </ul>
			  </div>
			  </div>
			  <hr/>
            <div id="step-container"
            	class=" stepContainer row-fluid position-relative">
            </c:if>
