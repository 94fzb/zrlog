<%@ page session="false" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%
    request.setAttribute("previewDb", com.zrlog.web.config.ZrLogConfig.isPreviewDb());
%>
<!DOCTYPE html>
<html lang="${lang}">
<base href="${basePath}"/>
<head>
    <title>${webs.title} | ${_res['admin.management']}</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <link rel="shortcut icon" href="${basePath}favicon.ico"/>
    <link href="${basePath}assets/css/bootstrap.min.css" rel="stylesheet"/>
    <link href="${basePath}assets/css/font-awesome.min.css" rel="stylesheet"/>
    <link href="${basePath}assets/css/pnotify.css" rel="stylesheet"/>
    <link href="${basePath}assets/css/custom.min.css" rel="stylesheet"/>
    <link href="${basePath}assets/css/custom.colorful.css" rel="stylesheet"/>
    <script src="${basePath}assets/js/route.min.js"></script>
    <script src="${basePath}assets/js/jquery.min.js"></script>
    <script src="${basePath}admin/js/index.js"></script>
    <script src="${basePath}admin/js/common.js"></script>
    <script>
        var _res = ${res};
    </script>
    <style>
        .btn-group > a {
            color: #515356;
        }
    </style>
</head>
<body class="nav-sm">
<c:if test="${previewDb}">
    <div class="container">
        <div class="col-md-12 bg-info text-center">
            <h3 style="color: red"><i class="fa fa-warning"></i> ${_res['defaultDbTips']}</h3>
        </div>
    </div>
</c:if>
<div class="container body">
    <div class="main_container">
        <div class="col-md-3 left_col">
            <div id="left_col" class="left_col scroll-view">
                <div class="navbar nav_title" style="border: 0;">
                    <a href="${basePath}" target="_blank" title="${webs.title}" class="site_title"><i
                            class="fa fa-home"></i> <span>${webs.title}</span></a>
                </div>
                <div class="clearfix"></div>
                <div id="sidebar-menu" class="main_menu_side hidden-print main_menu">
                    <div class="menu_section">
                        <ul class="nav side-menu">
                            <li>
                                <a href="admin/index#dashboard">
                                    <i class="fa fa-dashboard"></i>${_res.dashboard}
                                </a>
                            </li>
                            <li>
                                <a id="editPage" href="admin/index#article_edit">
                                    <i class="fa fa-edit"></i>${_res['admin.log.edit']}
                                </a>
                            </li>
                            <li>
                                <a href="admin/index#article">
                                    <i class="fa fa-newspaper-o"></i>${_res.blogManage}
                                </a>
                            </li>
                            <li>
                                <a href="admin/index#comment">
                                    <i class="fa fa-comments"></i>${_res['admin.comment.manage']}
                                </a>
                            </li>
                            <li>
                                <a href="admin/index#plugin">
                                    <i class="fa fa-plug"></i>${_res['admin.plugin.manage']}
                                </a>
                            </li>
                            <li>
                               <a href="admin/index#website"><i class="fa fa-cogs"></i> ${_res['admin.setting']}</a>
                            </li>

                            <li>
                                <a>
                                    <i class="fa fa-list"></i>${_res['admin.more']}
                                    <span class="fa fa-chevron-down"></span>
                                </a>
                                <ul class="nav child_menu">
                                    <li>
                                        <a href="admin/index#template">${_res['admin.template.manage']}</a>
                                    </li>
                                    <li>
                                        <a href="admin/index#type">${_res['admin.type.manage']}</a>
                                    </li>
                                    <li>
                                        <a href="admin/index#link"
                                        >${_res['admin.link.manage']}</a>
                                    </li>
                                    <li>
                                        <a href="admin/index#nav">${_res['admin.nav.manage']}</a>
                                    </li>
                                </ul>
                            </li>
                        </ul>
                    </div>
                </div>
            </div>
        </div>
        <div class="top_nav">
            <div class="nav_menu" id="nav_menu">
                <nav class="nav navbar-nav navbar-right" style="height: 57px;padding-top: 5px">
                    <div>
                        <c:if test="${fn:length(noReadComments) > 0}">
                        <div class="btn-group">
                            <a href="javascript:" class="btn btn-default dropdown-toggle info-number"
                               data-toggle="dropdown"
                               aria-expanded="false">
                                <i class="fa fa-envelope-o" style="font-size: 25px;"></i>
                                <span class="badge bg-green" id="commentNum">${fn:length(noReadComments)}</span>
                            </a>
                            <ul id="menu1" class="dropdown-menu list-unstyled msg_list" role="menu">
                                <c:forEach items="${noReadComments}" var="comment">
                                    <li class="dropdown-item">
                                        <a target="_blank" class="haveRead" id="${comment.id}">
                        <span class="image">
                        <c:choose>
                            <c:when test='${not empty comment.header}'>
                                <img class="msg-photo" src="${comment.header}">
                            </c:when>
                            <c:otherwise>
                                <img class="msg-photo" src="${baseUrl}assets/images/default-portrait.gif">
                            </c:otherwise>
                        </c:choose>
                        </span>
                                            <span>
                          <span>${comment.userName}</span>
                          <span class="time"></span>
                        </span>
                                            <span class="message">
                                                    ${comment.userComment}
                                            </span>
                                        </a>
                                    </li>
                                </c:forEach>
                                <li>
                                    <div class="text-center">
                                        <a href="admin/index#comment">
                                                ${_res['admin.viewAllComment']}
                                            <i class="fa fa-angle-right"></i>
                                        </a>
                                    </div>
                                </li>
                            </ul>
                        </div>
                        </c:if>
                        <c:if test="${lastVersion.upgrade}">
                        <div class="btn-group">
                            <a href="javascript:;" class="btn btn-default dropdown-toggle info-number"
                               data-toggle="dropdown" aria-expanded="false">
                                <i class="fa fa-bell" style="font-size: 25px;padding-top: 7px;"></i>
                                <span class="badge bg-green">新</span>
                            </a>

                            <ul id="bell" class="dropdown-menu list-unstyled msg_list" role="menu">
                                <li class="dropdown-item">
                                    <a style="padding: 0 !important"
                                       href="admin/index?buildId=${lastVersion.version.buildId}#do_upgrade">版本号：&nbsp;&nbsp;V${lastVersion.version.version}-${lastVersion.version.buildId}（${lastVersion.version.type}）
                                        <br/>发布时间：${lastVersion.version.releaseDate}</a>
                                </li>
                            </ul>
                        </div>
                        </c:if>
                        <div class="btn-group">
                            <a href="javascript:;" class="btn btn-default dropdown-toggle user-profile"
                               data-toggle="dropdown" aria-expanded="false">
                                <img src="${user.header}" alt="">${user.userName}</a>
                            <ul class="dropdown-menu  dropdown-usermenu pull-right">
                                <li class="dropdown-item">
                                    <a href="admin/index#user">
                                        <i class="fa fa-user"></i>
                                        ${_res['admin.user.info']}
                                    </a>
                                </li>
                                <li class="dropdown-item">
                                    <a href="admin/index#change_password">
                                        <i class="fa fa-key"></i>
                                        ${_res['admin.changePwd']}
                                    </a>
                                </li>
                                <li class="dropdown-divider"></li>
                                <li class="dropdown-item">
                                    <a href="admin/logout">
                                        <i class="fa fa-sign-out"></i>
                                        ${_res['admin.user.logout']}
                                    </a>
                                </li>
                            </ul>
                        </div>
                </nav>
            </div>
        </div>
        <div class="right_col" id="right_col" role="main" style="min-height: 1440px">
            <div style="min-height: 600px"></div>
        </div>
        <footer>
            <strong>${_res.copyright} <a href="https://www.zrlog.com" target="_blank" rel="noopener"> ZrLog
                . </a></strong>
            All rights reserved.
            <div class="pull-right d-none d-sm-block">
                <span><strong>Version</strong> ${zrlog.version}</span>
            </div>
        </footer>
    </div>
    <div class="modal fade" id="info">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <h4 id="info-title" class="modal-title"></h4>
                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                </div>
                <div class="modal-body">
                    <p id="info-body"></p>
                </div>
                <div class="modal-footer">
                    <button type="button" id="closeBtn" class="btn btn-primary" data-dismiss="modal"></button>
                </div>
            </div>
        </div>
    </div>
</div>
<script src="${basePath}assets/js/bootstrap.min.js"></script>
<script src="${basePath}assets/js/validator.min.js"></script>
<script src="${basePath}assets/js/nprogress.js"></script>
<script src="${basePath}assets/js/custom.min.js"></script>
<script src="${basePath}assets/js/switchery.min.js"></script>
<script src="${basePath}assets/js/pnotify.js"></script>
</body>
</html>
