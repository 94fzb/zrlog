<%@ page session="false" pageEncoding="UTF-8" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="common_header.jsp"/>
<body class="${webs.admin_dashboard_naver}">
<div class="container body">
    <div class="main_container">
        <div class="col-md-3 left_col">
            <div id="left_col" class="left_col scroll-view">
                <div class="navbar nav_title" style="border: 0;">
                    <a href="${url}" class="site_title"><i class="fa fa-home"></i> <span>${webs.title}</span></a>
                </div>
                <div class="clearfix"></div>
                <div id="sidebar-menu" class="main_menu_side hidden-print main_menu">
                    <div class="menu_section">
                        <ul class="nav side-menu">
                            <li>
                                <a href="admin/index" style="margin-top: 10px">
                                    <i class="fa fa-dashboard"></i>${_res.dashboard}
                                </a>
                            </li>
                            <li <c:if test='${currentPage=="admin/edit"}'>class="current-page"</c:if>>
                                <a href="admin/article/edit<c:if test='${not empty pageContext.request.queryString}'>?${pageContext.request.queryString}</c:if>">
                                    <i class="fa fa-edit"></i>${_res['admin.log.edit']}
                                </a>
                            </li>
                            <li>
                                <a href="admin/article">
                                    <i class="fa fa-newspaper-o"></i>${_res.blogManage}
                                </a>
                            </li>
                            <li>
                                <a href="admin/comment">
                                    <i class="fa fa-comments"></i>${_res['admin.comment.manage']}
                                </a>
                            </li>
                            <li>
                                <a href="admin/plugin">
                                    <i class="fa fa-plug"></i>${_res['admin.plugin.manage']}
                                </a>
                            </li>
                            <li>
                                <a>
                                    <i class="fa fa-cogs"></i>${_res['admin.setting']}
                                    <span class="fa fa-chevron-down"></span>
                                </a>
                                <ul class="nav child_menu">
                                    <li>
                                        <a href="admin/user">${_res['admin.user.info']}</a>
                                    </li>
                                    <li>
                                        <a href="admin/website">${_res['admin.website.manage']}</a>
                                    </li>
                                    <li>
                                        <a href="admin/template">${_res['admin.template.manage']}</a>
                                    </li>
                                    <li>
                                        <a href="admin/upgrade">${_res['admin.upgrade.manage']}</a>
                                    </li>
                                </ul>
                            </li>
                            <li>
                                <a>
                                    <i class="fa fa-list"></i>
                                    <span class="menu-text">
                                        ${_res['admin.more']}
                                    </span>
                                    <span class="fa fa-chevron-down"></span>
                                </a>
                                <ul class="nav child_menu">
                                    <li>
                                        <a href="admin/type" class="dropdown-toggle">${_res['admin.type.manage']}</a>
                                    </li>
                                    <li>
                                        <a href="admin/tag" class="dropdown-toggle">${_res['admin.tag.manage']}</a>
                                    </li>
                                    <li>
                                        <a href="admin/link" class="dropdown-toggle">${_res['admin.link.manage']}</a>
                                    </li>
                                    <li>
                                        <a href="admin/nav"
                                           class="dropdown-toggle">${_res['admin.nav.manage']}</a>
                                    </li>
                                </ul>
                            </li>
                        </ul>
                    </div>
                </div>
            </div>
        </div>
        <div class="top_nav">
            <div class="nav_menu">
                <nav>
                    <div class="nav toggle">
                        <a id="menu_toggle"><i class="fa fa-bars"></i></a>
                    </div>

                    <ul class="nav navbar-nav navbar-right">
                        <li class="">
                            <a href="javascript:" class="user-profile dropdown-toggle" data-toggle="dropdown"
                               aria-expanded="false">
                                <img src="${user.header}" alt="">${user.userName}
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

                        <c:if test="${fn:length(noReadComments) > 0}">
                        <li role="presentation" class="dropdown">
                            <a href="javascript:" class="dropdown-toggle info-number" data-toggle="dropdown"
                               aria-expanded="false">
                                <i class="fa fa-envelope-o"></i>
                                <span class="badge bg-green" id="commentNum">${fn:length(noReadComments)}</span>
                            </a>
                            <ul id="menu1" class="dropdown-menu list-unstyled msg_list" role="menu">
                                <c:forEach items="${noReadComments}" var="comment">
                                    <li>
                                        <a target="_blank" class="haveRead" id="${comment.id}">
                        <span class="image">
                        <c:choose>
                            <c:when test='${not empty comment.header}'>
                                <img class="msg-photo" src="${comment.header}">
                            </c:when>
                            <c:otherwise>
                                <img class="msg-photo" src="${url}/assets/images/default-portrait.gif">
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
                                        <a href="admin/comment">
                                            ${_res['admin.viewAllComment']}
                                            <i class="fa fa-angle-right"></i>
                                        </a>
                                    </div>
                                </li>
                            </ul>
                        </li>
                        </c:if>
                        <c:if test="${lastVersion.upgrade}">
                            <li role="presentation"
                                class='dropdown <c:if test="${'upgrade.jsp'==currentPath}">open</c:if>'>
                                <a href="javascript:;" class="dropdown-toggle info-number" data-toggle="dropdown"
                                   aria-expanded="false">
                                    <i class="fa fa-bell"></i>
                                    <span class="badge bg-green">新</span>
                                </a>
                                <ul id="bell" class="dropdown-menu list-unstyled msg_list" role="menu">
                                    <li>
                                        <a href="admin/do_upgrade?buildId=${lastVersion.version.buildId}">
                        <span>
                          <span>版本号:v${lastVersion.version.version}-${lastVersion.version.buildId}(${lastVersion.version.type})</span>
                        </span>
                                            <span class="message">
                             发布时间:${lastVersion.version.releaseDate}
                        </span>
                                        </a>
                                    </li>
                                </ul>
                            </li>
                        </c:if>
                        <li role="presentation" class="dropdown">
                            <a href="javascript:;" class="dropdown-toggle info-number" data-toggle="dropdown"
                               aria-expanded="false">
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
                        <li role="presentation">
                            <a target="_blank" href="http://blog.zrlog.com/post/feedback"
                               class="dropdown-toggle info-number">
                                <i class="fa fa-support"></i>&nbsp;${_res['suggestAndFeedback']}
                            </a>
                    </ul>
                </nav>
            </div>
        </div>
        <div class="right_col" id="right_col" role="main">
            <div style="min-height: 600px">