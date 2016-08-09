<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="common_header.jsp"/>
<body class="${webs.admin_dashboard_naver}">
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
    left: -50%;
}
.dropdown-menu {
    float:right;
}
.language {
    width:120px;
}
</style>
<div class="container body">
  <div class="main_container">
    <div class="col-md-3 left_col">
      <div class="left_col scroll-view">
        <div class="navbar nav_title" style="border: 0;">
          <a href="${url}" class="site_title"><i class="fa fa-dashboard"></i> <span>${webs.title}</span></a>
        </div>
        <div class="clearfix"></div>
        <!-- sidebar menu -->
        <div id="sidebar-menu" class="main_menu_side hidden-print main_menu">
          <div class="menu_section">
            <ul class="nav side-menu">
                <li <c:if test="${'index.jsp'==currentPath}">class="active"</c:if>>
                    <a href="admin/index">
                        <i class="fa fa-home"></i>
                        <span class="menu-text"> ${_res.dashboard} </span>
                    </a>
                </li>
                <li <c:if test="${'edit.jsp'==currentPath}">class="active"</c:if>>
                    <a href="admin/edit">
                        <i class="fa fa-edit"></i>
                        <span class="menu-text">${_res['admin.log.edit']} </span>
                    </a>
                </li>
                <li <c:if test="${'log.jsp'==currentPath}">class="active"</c:if>>
                    <a href="admin/log">
                        <i class="fa fa-newspaper-o"></i>
                        <span class="menu-text"> ${_res.blogManage} </span>

                    </a>
                </li>
                <li <c:if test="${'comment.jsp'==currentPath}">class="active"</c:if>>
                    <a href="admin/comment" class="dropdown-toggle">
                        <i class="fa fa-comment"></i>
                        <span class="menu-text">${_res['admin.comment.manage']}</span>
                    </a>
                </li>
                <li <c:if test="${'plugin.jsp'==currentPath or 'plugin_center.jsp'==currentPath}">class="active"</c:if>>
                    <a href="admin/plugin" class="dropdown-toggle">
                        <i class="fa fa-plug"></i>
                        <span class="menu-text"> ${_res['admin.plugin.manage']} </span>
                    </a>
                </li>
                <li <c:if test="${'upgrade.jsp'==currentPath or 'website.jsp'==currentPath or 'user.jsp'==currentPath or 'template.jsp'==currentPath or 'template_center.jsp'==currentPath}">class="active open"</c:if>>
                    <a>
                        <i class="fa fa-cogs"></i>
                        <span class="menu-text">${_res['admin.setting']}</span>
                        <span class="fa fa-chevron-down"></span>
                    </a>

                    <ul class="nav child_menu">
                        <li <c:if test="${'user.jsp'==currentPath}">class="active"</c:if>>
                            <a href="admin/user" <c:if test="${'user.jsp'==currentPath}">class="active"</c:if>>
                                <i class="fa fa-double-angle-right"></i>
                                <span class="menu-text">${_res['admin.user.info']}</span>
                            </a>
                        </li>
                        <li <c:if test="${'website.jsp'==currentPath}">class="active"</c:if>>
                            <a href="admin/website" <c:if test="${'website.jsp'==currentPath}">class="active"</c:if>>
                                <i class="fa fa-double-angle-right"></i>
                                <span class="menu-text">${_res['admin.website.manage']}</span>
                            </a>
                        </li>
                        <li <c:if test="${'template.jsp'==currentPath or 'template_center.jsp'==currentPath}">class="active"</c:if>>
                            <a href="admin/template" <c:if test="${'template.jsp'==currentPath}">class="active"</c:if>>
                                <i class="fa fa-double-angle-right"></i>
                                <span class="menu-text">${_res['admin.template.manage']}</span>
                            </a>
                        </li>
                        <li <c:if test="${'upgrade.jsp'==currentPath}">class="active"</c:if>>
                            <a href="admin/upgrade"  class="dropdown-toggle">
                                <i class="fa fa-double-angle-right"></i>
                                <span class="menu-text"> ${_res['admin.upgrade.manage']}</span>
                            </a>
                        </li>
                    </ul>
                </li>
                <li <c:if test="${'type.jsp'==currentPath or 'tag.jsp'==currentPath or 'nav.jsp'==currentPath or 'link.jsp'==currentPath}">class="active open"</c:if>>
                    <a>
                        <i class="fa fa-list"></i>
                        <span class="menu-text">
                            ${_res['admin.more']}
                        </span>
                        <span class="fa fa-chevron-down"></span>
                    </a>
                    <ul class="nav child_menu">
                        <li  <c:if test="${'type.jsp'==currentPath}">class="active"</c:if>>
                            <a href="admin/type"  class="dropdown-toggle">
                                <i class="fa fa-double-angle-right"></i>
                                <span class="menu-text">${_res['admin.type.manage']}</span>
                            </a>
                        </li>
                        <li  <c:if test="${'tag.jsp'==currentPath}">class="active"</c:if>>
                            <a href="admin/tag"  class="dropdown-toggle">
                                <i class="fa fa-double-angle-right"></i>
                                <span class="menu-text">${_res['admin.tag.manage']}</span>
                            </a>
                        </li>
                        <li <c:if test="${'link.jsp'==currentPath}">class="active"</c:if>>
                            <a href="admin/link" class="dropdown-toggle">
                                <i class="fa fa-double-angle-right"></i>
                                <span class="menu-text">${_res['admin.link.manage']}</span>
                            </a>
                        </li>
                        <li <c:if test="${'nav.jsp'==currentPath}">class="active"</c:if>>
                            <a href="admin/nav" class="dropdown-toggle">
                                <i class="fa fa-double-angle-right"></i>
                                <span class="menu-text"> ${_res['admin.nav.manage']}</span>
                            </a>
                        </li>
                    </ul>
                </li>
            </ul>
          </div>

        </div>
        <!-- /sidebar menu -->
      </div>
    </div>

    <!-- top navigation -->
    <div class="top_nav">
      <div class="nav_menu">
        <nav>
          <div class="nav toggle">
            <a id="menu_toggle"><i class="fa fa-bars"></i></a>
          </div>

          <ul class="nav navbar-nav navbar-right">
            <li class="">
              <a href="javascript:;" class="user-profile dropdown-toggle" data-toggle="dropdown" aria-expanded="false">
                <img src="${session.user.header}" alt="">${session.user.userName}
                <span class=" fa fa-angle-down"></span>
              </a>
              <ul class="dropdown-menu dropdown-usermenu pull-right">
                <li>
                    <a href="admin/user">
                        <i class="fa fa-user"></i>
                        ${_res['admin.user.info']}
                    </a>
                </li>
                <li>
                    <a href="admin/change_password">
                        <i class="fa fa-key"></i>
                        ${_res['admin.changePwd']}
                    </a>
                </li>
                <li class="divider"></li>
                <li>
                    <a href="admin/logout">
                        <i class="fa fa-sign-out"></i>
                        ${_res['admin.user.logout']}
                    </a>
                </li>
              </ul>
            </li>

            <li role="presentation" class="dropdown">
              <a href="javascript:;" class="dropdown-toggle info-number" data-toggle="dropdown" aria-expanded="false">
                <i class="fa fa-envelope-o"></i>
                <span class="badge bg-green">${fn:length(session.comments.rows)}</span>
              </a>
              <ul id="menu1" class="dropdown-menu list-unstyled msg_list" role="menu">
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
                    <div class="text-center">
                    <a href="admin/comment">
                        查看所有消息
                        <i class="fa fa-angle-right"></i>
                    </a>
                    </div>
                </li>
              </ul>
            </li>
            <li  role="presentation" class="dropdown">
                <a href="javascript:;" class="dropdown-toggle info-number" data-toggle="dropdown" aria-expanded="false">
                 <i class="fa fa-globe"></i>
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
          </ul>
        </nav>
      </div>
    </div>
    <!-- /top navigation -->
    <!-- page content -->
    <div class="right_col" role="main"  style="min-height: 1080px;">
