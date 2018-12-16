<!DOCTYPE html>
<html lang="${lang}">
<base href="${basePath}"/>
<head>
    <title>${website.title} | ${_res['admin.management']}</title>
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
<#if previewDb>
<div class="text-center">
    <h3 style="color: red"><i class="fa fa-warning"></i> ${_res['defaultDbTips']}</h3>
</div>
</#if>
<div class="container body">
    <div class="main_container">
        <div class="col-md-3 left_col">
            <div id="left_col" class="left_col scroll-view">
                <div class="navbar nav_title" style="border: 0;">
                    <a href="${basePath}" target="_blank" title="${website.title}" class="site_title"><i
                            class="fa fa-home"></i> <span>${website.title}</span></a>
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
                <nav class="nav navbar-nav navbar-right" style="height: 57px;">
                    <div>
                        <#if noReadComments??>
                        <div class="btn-group" id="commentMessages">
                            <button class="btn btn-default dropdown-toggle info-number" data-toggle="dropdown"
                                    aria-expanded="false">
                                <i class="fa fa-envelope-o"></i>
                                <span class="badge bg-green" id="commentNum">${noReadComments?size}</span>
                            </button>
                            <ul class="dropdown-menu dropdown-menu-right list-unstyled msg_list">
                                <#list noReadComments as comment>
                                    <li style="min-width: 240px;">
                                        <a target="_blank" class="haveRead dropdown-item user-profile"
                                           id="${comment.id}">
                        <span class="image">
                            <img class="msg-photo" style="min-height: 38px;min-width: 38px" src="${comment.header}">
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
                                </#list>
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
                        </#if>
                        <#if lastVersion.upgrade>
                        <div class="btn-group">
                            <button class="btn btn-default dropdown-toggle info-number" data-toggle="dropdown"
                                    aria-expanded="false">
                                <i class="fa fa-bell"></i>
                                <span class="badge bg-green">新</span>
                            </button>
                            <ul id="bell" class="dropdown-menu dropdown-menu-right list-unstyled msg_list">
                                <li>
                                    <a class="dropdown-item" style="padding: 0 !important"
                                       href="admin/index?buildId=${lastVersion.version.buildId}#do_upgrade">
                                        版本号：&nbsp;&nbsp;V${lastVersion.version.version}-${lastVersion.version.buildId}
                                        （${lastVersion.version.type}）
                                        <br/>发布时间：${lastVersion.version.releaseDate}</a>
                                </li>
                            </ul>
                        </div>
                        </#if>
                        <div class="btn-group">
                            <button class="btn btn-default dropdown-toggle user-profile" data-toggle="dropdown"
                                    aria-expanded="false">
                                <img src="${user.header}" alt="">${user.userName}
                            </button>
                            <div class="dropdown-menu dropdown-menu-right">
                                <a class="dropdown-item" href="admin/index#user">
                                    <i class="fa fa-user pr5"></i>
                                ${_res['admin.user.info']}
                                </a>
                                <a class="dropdown-item" href="admin/index#change_password">
                                    <i class="fa fa-key pr5"></i>
                                ${_res['admin.changePwd']}
                                </a>
                                <div class="dropdown-divider"></div>
                                <a class="dropdown-item" href="admin/logout">
                                    <i class="fa fa-sign-out pr5"></i>
                                ${_res['admin.user.logout']}
                                </a>
                            </div>
                        </div>
                </nav>
            </div>
        </div>
        <div class="right_col" id="right_col" role="main" style="min-height: 1440px">
            <div style="min-height: 600px">
                <#if message??>
                    <div class="alert alert-block alert-info" style="margin-top: 66px">
                        <p>
                            ${message}
                        </p>
                        <p>
                            <a href="${backUrl!'javascript:history.go(-1);'}">
                                <button class="btn btn-sm btn-primary">${_res.goBack}</button>
                            </a>
                        </p>
                    </div>
                </#if>
            </div>
        </div>
        <footer>
            <strong>${_res.copyright}
                <a href="https://www.zrlog.com" target="_blank" rel="noopener">ZrLog .</a></strong>
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
