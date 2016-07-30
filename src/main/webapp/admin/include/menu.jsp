<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getHeader("host")+path+"/";
request.setAttribute("url", request.getScheme()+"://"+request.getHeader("host")+request.getContextPath());
if(request.getAttribute("currentPath")==null){
	request.setAttribute("currentPath", request.getRequestURL().substring((basePath+"admin/").length()));
}
%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
  	<base href="<%=basePath%>">
  	<c:set var="webs" value="${init.webSite}" scope="session"></c:set>
	<head>
		<meta charset="utf-8" />
		<title>${webs.title} - 后台管理  ${stitle} </title>
		<link rel="stylesheet" href="assets/css/bootstrap.min.css"/>
		<link rel="stylesheet" href="assets/css/font-awesome.min.css" />

		<link rel="stylesheet" href="assets/css/ace.min.css" />
		<link rel="stylesheet" href="assets/css/ace-skins.min.css" />
		<link rel="stylesheet" href="assets/css/jquery.gritter.css" />
		
		<link rel="shortcut icon" href="favicon.ico" />
		<script src="assets/js/jquery-2.0.3.min.js"></script>
		<script src="assets/js/bootstrap.min.js"></script>
		<script src="assets/js/ace-elements.min.js"></script>
		<script src="assets/js/ace.min.js"></script>
		<script src="assets/js/ace-extra.min.js"></script>
		<script src="assets/js/jquery.gritter.min.js"></script>
		<script src="admin/js/dashboard_set.js"></script>

		<style type="text/css">
		.dropdown-submenu {
            position: relative;
        }
        .dropdown-submenu > .dropdown-menu {
            border-radius: 0 6px 6px;
            left: 100%;
            margin-left: -1px;
            margin-top: -6px;
            top: 0;
        }
        .dropdown-submenu:hover > .dropdown-menu {
            display: block;
        }
        .dropup .dropdown-submenu > .dropdown-menu {
            border-radius: 5px 5px 5px 0;
            bottom: 0;
            margin-bottom: -2px;
            margin-top: 0;
            top: auto;
        }
        .dropdown-submenu > a::after {
            border-color: transparent transparent transparent #cccccc;
            border-style: solid;
            border-width: 5px 0 5px 5px;
            content: " ";
            display: block;
            float: right;
            height: 0;
            margin-right: -10px;
            margin-top: 5px;
            width: 0;
        }
        .dropdown-submenu:hover > a::after {
            border-left-color: #ffffff;
        }
        .dropdown-submenu.pull-left {
            float: none;
        }
        .dropdown-submenu.pull-left > .dropdown-menu {
            border-radius: 6px 0 6px 6px;
            left: -100%;
            margin-left: 10px;
        }
		.dropdown-menu {
            float:right;
        }
		</style>
	</head>
<body class="${webs.admin_dashboard_skin}">
		<div class="navbar" id="navbar">

			<script type="text/javascript">
				try{ace.settings.check('navbar' , 'fixed')}catch(e){}
			</script>

			<div class="navbar-container ${webs.admin_dashboard_inside_container}" id="navbar-container">
				<div class="navbar-header pull-left">
					<a href="${url}" class="navbar-brand">
						<small>
							${webs.title}
						</small>
					</a><!-- /.brand -->
				</div><!-- /.navbar-header -->

				<div class="navbar-header pull-right" role="navigation" style="z-index: 1000">
					<ul class="nav ace-nav">
						<li class="green"  style="z-index: 1002">
							<a href="#" class="dropdown-toggle" data-toggle="dropdown">
								<i class="icon-envelope"></i>
								<span class="badge badge-success">${session.comments.records}</span>
							</a>
		
							<ul class="pull-right dropdown-navbar dropdown-menu dropdown-caret dropdown-close">
								<li class="dropdown-header">
									<i class="icon-envelope-alt"></i>
									最近${fn:length(session.comments.rows)}条消息
								</li>
								<c:forEach items="${session.comments.rows}" var="comment">
								<li>
									<a target="_blank" href="post/${comment.logId}">
										<img class="msg-photo" src="${comment.header}">
										<span class="msg-body">
											<span class="msg-title">
												<span class="blue">${comment.userName}:</span>
												${comment.userComment}
											</span>
										</span>
									</a>
								</li>
								</c:forEach>
								<li>
									<a href="admin/comment">
										查看所有消息
										<i class="icon-arrow-right"></i>
									</a>
								</li>
							</ul>
						</li>
						<li class="light-blue" style="z-index: 1002">
							<a data-toggle="dropdown" href="#" class="dropdown-toggle">
								<img class="nav-user-photo" src="${session.user.header}" alt="${session.user.userName}" />
								<span class="user-info">
									<small>${_res['admin.manager']}</small>${session.user.userName}
								</span>
								<i class="icon-caret-down"></i>
							</a>
							<ul class="user-menu pull-right dropdown-menu dropdown-yellow dropdown-caret dropdown-close"  style="width:150px;">
								<li>
									<a href="admin/user">
										<i class="icon-user"></i>
										${_res['admin.user.info']}
									</a>
								</li>
								<li>
									<a href="admin/change_password">
										<i class="icon-key"></i>
										${_res['admin.changePwd']}
									</a>
								</li>
								<li class="divider"></li>
								<li class="dropdown-submenu pull-left" style="width:150px;">
									<a>
										<i class="icon-globe"></i>
										${_res.language}
									</a>
									<ul class="dropdown-menu">
										<li>
										<a tabindex="-1" class="language" id="zh_CN">${_res.languageChinese}</a>
										</li>
										<li>
											<a tabindex="-1" class="language" id="en_US">${_res.languageEnglish}</a>
										</li>
									</ul>

								</li>

								<li>
									<a href="admin/logout">
										<i class="icon-off"></i>
										${_res['admin.user.logout']}
									</a>
								</li>
							</ul>
						</li>
					</ul><!-- /.ace-nav -->
				</div><!-- /.navbar-header -->
			</div><!-- /.container -->
		</div>

		<div class="main-container ${webs.admin_dashboard_inside_container}" id="main-container">
			<div class="main-container-inner">
				<a class="menu-toggler" id="menu-toggler" href="#">
					<span class="menu-text"></span>
				</a>
				<div class="sidebar ${webs.admin_dashboard_sidebar_collapser}" id="sidebar">
					<ul class="nav nav-list">
						<li <c:if test="${'index.jsp'==currentPath}">class="active"</c:if>>
						
							<a href="admin/index">
								<i class="icon-dashboard"></i>
								<span class="menu-text"> ${_res.dashboard} </span>
							</a>
						</li>
						<li <c:if test="${'edit.jsp'==currentPath}">class="active"</c:if>>
							<a href="admin/edit">
								<i class="icon-edit"></i>
								<span class="menu-text">${_res['admin.log.edit']} </span>
							</a>
						</li>
						<li <c:if test="${'log.jsp'==currentPath}">class="active"</c:if>>
							<a href="admin/log">
								<i class="icon-desktop"></i>
								<span class="menu-text"> ${_res.blogManage} </span>

							</a>
						</li>
						<li <c:if test="${'comment.jsp'==currentPath}">class="active"</c:if>>
							<a href="admin/comment" class="dropdown-toggle">
								<i class="icon-comment"></i>
								<span class="menu-text">${_res['admin.comment.manage']}</span>
							</a>
						</li>
						<li <c:if test="${'plugin.jsp'==currentPath or 'plugin_center.jsp'==currentPath}">class="active"</c:if>>
							<a href="admin/plugin" class="dropdown-toggle">
								<i class="icon-plug"></i>
								<span class="menu-text"> ${_res['admin.plugin.manage']} </span>
							</a>
						</li>
						<li <c:if test="${'upgrade.jsp'==currentPath or 'website.jsp'==currentPath or 'user.jsp'==currentPath or 'template.jsp'==currentPath or 'template_center.jsp'==currentPath}">class="active open"</c:if>>
							<a href="#" class="dropdown-toggle">
								<i class="icon-cogs"></i>
								<span class="menu-text">${_res['admin.setting']}</span>

								<b class="arrow icon-angle-down"></b>
							</a>

							<ul class="submenu">
								<li <c:if test="${'user.jsp'==currentPath}">class="active"</c:if>>
									<a href="admin/user" <c:if test="${'user.jsp'==currentPath}">class="active"</c:if>>
										<i class="icon-double-angle-right"></i>
										<span class="menu-text">${_res['admin.user.info']}</span>
									</a>
								</li>
								<li <c:if test="${'website.jsp'==currentPath}">class="active"</c:if>>
									<a href="admin/website" <c:if test="${'website.jsp'==currentPath}">class="active"</c:if>>
										<i class="icon-double-angle-right"></i>
										<span class="menu-text">${_res['admin.website.manage']}</span>
									</a>
								</li>
								<li <c:if test="${'template.jsp'==currentPath or 'template_center.jsp'==currentPath}">class="active"</c:if>>
									<a href="admin/template" <c:if test="${'template.jsp'==currentPath}">class="active"</c:if>>
										<i class="icon-double-angle-right"></i>
										<span class="menu-text">${_res['admin.template.manage']}</span>
									</a>
								</li>
								<li <c:if test="${'upgrade.jsp'==currentPath}">class="active"</c:if>>
									<a href="admin/upgrade"  class="dropdown-toggle">
										<i class="icon-double-angle-right"></i>
										<span class="menu-text"> ${_res['admin.upgrade.manage']}</span>
									</a>
								</li>
							</ul>
						</li>
						<li <c:if test="${'type.jsp'==currentPath or 'tag.jsp'==currentPath or 'nav.jsp'==currentPath or 'link.jsp'==currentPath}">class="active open"</c:if>>
							<a href="#" class="dropdown-toggle">
								<i class="icon-list"></i>
								<span class="menu-text">
									${_res['admin.more']}
								</span>
								<b class="arrow icon-angle-down"></b>
							</a>
							<ul class="submenu">
								<li  <c:if test="${'type.jsp'==currentPath}">class="active"</c:if>>
									<a href="admin/type"  class="dropdown-toggle">
										<i class="icon-double-angle-right"></i>
										<span class="menu-text">${_res['admin.type.manage']}</span>
									</a>
								</li>
								<li  <c:if test="${'tag.jsp'==currentPath}">class="active"</c:if>>
									<a href="admin/tag"  class="dropdown-toggle">
										<i class="icon-double-angle-right"></i>
										<span class="menu-text">${_res['admin.tag.manage']}</span>
									</a>
								</li>
								<li <c:if test="${'link.jsp'==currentPath}">class="active"</c:if>>
									<a href="admin/link" class="dropdown-toggle">
										<i class="icon-double-angle-right"></i>
										<span class="menu-text">${_res['admin.link.manage']}</span>
									</a>
								</li>
								<li <c:if test="${'nav.jsp'==currentPath}">class="active"</c:if>>
									<a href="admin/nav" class="dropdown-toggle">
										<i class="icon-double-angle-right"></i>
										<span class="menu-text"> ${_res['admin.nav.manage']}</span>
									</a>
								</li>
							</ul>
						</li>
					</ul><!-- /.nav-list -->

					<div class="sidebar-collapse" id="sidebar-collapse">
						<c:choose>
						<c:when test="${webs.admin_dashboard_sidebar_collapser eq ''}">
						<i class="icon-double-angle-left" data-icon1="icon-double-angle-left" data-icon2="icon-double-angle-right"></i>
						</c:when>
						<c:otherwise>
						<i class="icon-double-angle-right" data-icon1="icon-double-angle-left" data-icon2="icon-double-angle-right"></i>
						</c:otherwise>
						</c:choose>
					</div>
				</div>
				<div class="main-content">
					<div class="page-content">