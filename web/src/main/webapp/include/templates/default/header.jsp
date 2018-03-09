<%@ page session="false" import="java.util.Map" pageEncoding="UTF-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
    Map<String, Object> res = (Map<String, Object>) request.getAttribute("_res");
    if (res.get("avatar") == null || "".equals(res.get("avatar"))) {
        res.put("avatar", request.getAttribute("url") + "/images/avatar.gif");
    }
    if (res.get("title") == null) {
        String host = request.getHeader("host");
        System.out.println(host);
        if (host.contains(":")) {
            host = host.substring(0, host.indexOf(":"));
        }
        res.put("title", host);
    }
%>
<!DOCTYPE html>
<html lang="zh" class="no-js">
<head>
    <%@ include file="../../core/core_mate.jsp" %>
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0;"/>
    <link rel="stylesheet" type="text/css" href="${baseUrl}assets/css/video-js.css"/>
    <link rel="stylesheet" type="text/css" media="screen" href="${templateUrl}/css/style.css"/>
    <link rel="stylesheet" type="text/css" media="screen" href="${templateUrl}/css/editormd.css"/>
    <script src="${templateUrl}/js/lib/jquery-1.10.2.min.js"></script>
    <script src="${templateUrl}/js/lib/modernizr.min.js"></script>
    <script src="${templateUrl }/js/sheshui.js"></script>
    <script src="${baseUrl }assets/js/video.js"></script>
    <!--[if lt IE 9]>
    <script src="${templateUrl}/js/html5shiv.js"></script>
    <script src="${templateUrl}/js/css3-mediaqueries.js"></script>
    <link rel="stylesheet" type="text/css" media="screen" href="${templateUrl}/css/style-ie7.css"/>
    <![endif]-->
    <style>
        header .avatar {
            display: block;
            float: left;
            width: 64px;
            height: 64px;
            margin-top: -8px;
            margin-right: 5px;
            border-radius: 50%;
            /*box-shadow: 0px 0px 10px 2px #EDEDEF;*/
            background: url('${_res.avatar}') scroll center center #FFFFFF;
            background-size: cover;
            overflow: hidden;
        }

        .gn-menu-main li.sitename .gn-icon {
            width: 50px;
            height: 50px;
            margin-top: 10px;
            border-radius: 50%;
            background-size: cover;
            background: #f0f0f0 url('${_res.avatar}') no-repeat center center;
            display: inline-block;
            background-size: cover;
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
    <div class="top">
        <div class="inner">
            <header>
                <h1 class="site-name">
                    <i class="avatar"></i>
                    <a href="${rurl}">${_res.title}</a>
                    <span class="slogan">${webs.title }</span>
                </h1>
                <nav class="mainnav">
                    <ul class="section_list">
                        <c:forEach var="lognav" items="${init.logNavs}">
                            <c:choose>
                                <c:when test="${lognav.current}">
                                    <li class="active"><a href="${lognav.url}"><c:out value="${lognav.navName}"/></a>
                                    </li>
                                </c:when>
                                <c:otherwise>
                                    <li><a href="${lognav.url}"><c:out value="${lognav.navName}"/></a></li>
                                </c:otherwise>
                            </c:choose>
                        </c:forEach>
                    </ul>
                </nav>
                <ul id="gn-menu" class="gn-menu-main">
                    <li class="gn-trigger">
                        <a class="gn-icon gn-icon-menu"></a>
                        <nav class="gn-menu-wrapper">
                            <div class="gn-scroller">
                                <ul class="gn-menu">
                                    <li class="gn-search-item">
                                        <form method="post" action="${rurl}post/search" id="searchform1"><input
                                                placeholder="${_res.searchTip}" type="search" name="key"
                                                class="gn-search">
                                            <a class="gn-icon icon-search"><span>${_res.search}</span></a></form>
                                    </li>
                                    <li>
                                        <a class="gn-icon icon-article">${_res.category}</a>
                                        <ul class="gn-submenu">
                                            <c:forEach var="type" items="${init.types}">
                                                <li><a class="gn-icon icon-star"
                                                       href="${rurl}post/sort/${type.alias}">${type.typeName}
                                                    (${type.typeamount})</a></li>
                                            </c:forEach>
                                        </ul>
                                    </li>
                                </ul>
                            </div>
                        </nav>
                    </li>
                    <li class="sitename">
                        <a class="gn-icon icon-info" href="${rurl}"></a>
                    </li>
                </ul>
            </header>
        </div>
    </div>
    <div class="breadcrumb"></div>
    <div class="main clearfloat">
