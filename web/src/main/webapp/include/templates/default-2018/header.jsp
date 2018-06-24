﻿
<%@ page language="java" session="false" import="java.util.*" pageEncoding="UTF-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
    Map<String, Object> res = (Map<String, Object>) request.getAttribute("_res");
    if (res.get("avatar") == null || res.get("avatar").toString().length() == 0) {
        res.put("avatar", request.getAttribute("url") + "/images/avatar.gif");
    }
%>
<!DOCTYPE html>
<html lang="zh" class="no-js">
<head>
    <jsp:include page="../../core/core_mate.jsp"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no"/>
    <link rel="stylesheet" type="text/css" href="${baseUrl}assets/css/video-js.css"/>
    <link rel="stylesheet" type="text/css" media="screen" href="${templateUrl}/css/common_2018.css"/>
    <link rel="stylesheet" type="text/css" media="screen" href="${templateUrl}/css/style_2018.css"/>

    <link rel="stylesheet" type="text/css" media="screen" href="${templateUrl}/css/pager.css"/>
    <link rel="stylesheet" type="text/css" media="screen" href="${templateUrl}/css/editormd.css"/>

    <script src="${templateUrl}/js/jquery-1.10.2.min.js"></script>
    <script src="${baseUrl }assets/js/video.js"></script>
    <script src="${templateUrl}/js/modernizr.custom.16617.js"></script>
    <script src="${templateUrl}/js/classie.js"></script>
    <script src="${templateUrl}/js/jquery.lazyload.min.js"></script>
    <!--[if lt IE 9]>
    <link rel="stylesheet" type="text/css" media="screen" href="${templateUrl}/css/style-ie7.css"/>
    <script src="${templateUrl}/js/html5shiv.js"></script>
    <script src="${templateUrl}/js/css3-mediaqueries.js"></script>
    <![endif]-->
    <style>
        @font-face {
            font-family: 'Material Icons';
            font-style: normal;
            font-weight: 400;
            src: url(${templateUrl}/fonts/MaterialIcons-Regular.eot); /* For IE6-8 */
            src: local('Material Icons'),
            local('MaterialIcons-Regular'),
            url(${templateUrl}/fonts/MaterialIcons-Regular.woff2) format('woff2'),
            url(${templateUrl}/fonts/MaterialIcons-Regular.woff) format('woff'),
            url(${templateUrl}/fonts/MaterialIcons-Regular.ttf) format('truetype');
        }

        .markdown-body ul, .markdown-body ol {
            padding-left: 0;
        }

        .side .s-header .avatar,
        .top .avatar {
            background: url('${_res.avatar}') scroll center center #FFFFFF;
            background-size: cover;
            border-radius: 50%;
            overflow: hidden;
        }

        .vjs-default-skin .vjs-control-bar {
            font-size: 60%;
            height: 39px;
        }

        .vjs-default-skin .vjs-control {
            color: #ffffff;
        }

        .vjs-default-skin .vjs-slider-handle {
            color: #ffffff;

        }

        .vjs-default-skin .vjs-duration-display {
            color: #ffffff;
            padding-top: 10px;
        }

        .vjs-default-skin .vjs-current-time-display {
            color: #ffffff;
            padding-top: 10px;

        }

        .vjs-time-divider {
            float: left;
            line-height: 3em;
            margin-top: 9px;
            padding-left: 8px;
            padding-right: 8px;
        }

        .vjs-control-bar div span {
            color: #ffffff;
            padding-top: 12px;
        }

        .vjs-default-skin .vjs-seek-handle {
            top: -5px;
        }
    </style>
</head>
<body class="default front">
<div class="page">
    <div id="overLayer"></div>
    <div class="top">
        <div class="inner">
            <header>
                <a href="${pageContext.request.contextPath}/?avatar"><i class="avatar"></i></a>
                <h1 class="page-title">
                    <a href='${rurl}'>${website.title}</a>
                </h1>
                <nav class="mainnav">
                    <ul class="section_list">
                        <c:forEach var="lognav" items="${init.logNavs}">
                        <c:choose>
                        <c:when test="${lognav.current}">
                        <li class="active"><a href="${lognav.url}"><c:out value="${lognav.navName}"/></a></li>
                        </c:when>
                        <c:otherwise>
                        <li><a href="${lognav.url}"><c:out value="${lognav.navName}"/></a></li>
                        </c:otherwise>
                        </c:choose>
                        </c:forEach>
                </nav>
                <div class="mainmenu">
                    <div class="widget search">
                        <a class="" href="#search"><i class="material-icons">search</i></a>
                        <form method="get" action="${rurl}post/search" id="searchform">
                            <p class="search_input"><input class="inputtext" name="key" type="text"
                                                           value='${_res.searchTip}' title="输入关键字，敲回车搜索"
                                                           size="15" onfocus="OnEnter(this)" onblur="OnExit(this)"></p>
                        </form>
                    </div>
                    <button id="expendDrawerPannel" class="hamburger-button active"><i class="material-icons">menu</i>
                    </button>
                </div>
            </header>
        </div>
    </div>
    <div class="side side-left" id="drawer-pannel">
        <aside>
            <div class="side-inner">
                <i id="collapseDrawerPannel" class="material-icons">navigate_next</i>
                <h2 class="s-title">${website.title}</h2>
                <div class="widget category-list">
                    <h3>博客分类 <span class="en">Categories</span></h3>
                    <div class="list">
                        <ul>
                            <c:forEach var="type" items="${init.types}">
                                <li class="parent"><a title="${type.typeName}" href="${rurl}post/sort/${type.alias}"><i
                                        class="material-icons">label</i> ${type.typeName} (${type.typeamount})</a></li>
                            </c:forEach>
                        </ul>
                    </div>
                </div>
                <script type="text/javascript">
                    $(document).ready(function () {
                        var tags_a = $("#tags").find("a");
                        tags_a.each(function () {
                            var x = 6;
                            var y = 0;
                            var rand = parseInt(Math.random() * (x - y + 1) + y);
                            $(this).addClass("size" + rand);
                        });
                    });
                </script>
                <style type="text/css">
                    /*.taglist a {
                        padding: 0;
                        display: inline-block;
                        white-space: nowrap;
                    }*/

                    a.size1 {
                        font-size: 10px;
                        padding: 2px;
                        color: #804D40;
                    }

                    a.size1:hover {
                        color: #E13728;
                    }

                    a.size2 {
                        padding: 2px;
                        font-size: 12px;
                        color: #B9251A;
                    }

                    a.size2:hover {
                        color: #E13728;
                    }

                    a.size3 {
                        padding: 3px;
                        font-size: 14px;
                        color: #C4876A;
                    }

                    a.size3:hover {
                        color: #E13728;
                    }

                    a.size4 {
                        padding: 1px;
                        font-size: 18px;
                        color: #B46A47;
                    }

                    a.size4:hover {
                        color: #E13728;
                    }

                    a.size5 {
                        padding: 3px;
                        font-size: 16px;
                        color: #E13728;
                    }

                    a.size5:hover {
                        color: #B46A47;
                    }

                    a.size6 {
                        padding: 2px;
                        font-size: 12px;
                        color: #77625E
                    }

                    a.size6:hover {
                        color: #E13728;
                    }
                </style>
                <div class="widget category-list" style="padding-bottom: 10px">
                    <h3>标签 <span class="en">Tags</span></h3>
                    <div class="taglist" id="tags">
                        <c:forEach items="${init.tags}" var="tag">
                            <a href="${tag.url}" title="${tag.text}上共有(${tag.count})文章">${tag.text}</a>
                        </c:forEach>
                    </div>
                </div>
                <div class="widget channel-index">
                    <ul>
                        <li><a href="${rurl}post/all"><i class="material-icons">description</i><span>博客</span></a></li>
                        <c:choose>
                            <c:when test="${_res.aboutPage ne ''}">
                                <li><a href="${rurl}${_res.aboutPage}"><i class="material-icons">info_outline</i><span>关于</span></a>
                                </li>
                            </c:when>
                        </c:choose>
                    </ul>
                </div>
            </div>
        </aside>
    </div>
    <div class="ribbon">
        <div class="breadcrumb" style="font-size:14px">
            <c:if test="${not empty log}">
                <section>
                    <a href="${baseUrl}" class="item">首页</a> &gt; <a href="${log.typeUrl}"
                                                                     class="item">${log.typeName}</a> &gt; ${log.title}
                </section>
            </c:if>
        </div>
    </div>
<jsp:include page="update_browser.jsp"/>