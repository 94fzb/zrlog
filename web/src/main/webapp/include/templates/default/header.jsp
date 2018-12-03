<%@ page session="false" import="java.util.Map" pageEncoding="UTF-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
    Map<String, Object> res = (Map<String, Object>) request.getAttribute("_res");
    if (res.get("avatar") == null || "".equals(res.get("avatar"))) {
        res.put("avatar", request.getAttribute("url") + "/images/avatar.gif");
    }
    if (res.get("title") == null) {
        String host = request.getHeader("host");
        if (host.contains(":")) {
            host = host.substring(0, host.indexOf(":"));
        }
        res.put("title", host);
    }
%>
<!DOCTYPE html>
<html>
<head>
    <%@ include file="../../core/core_mate.jsp" %>
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0"/>
    <link rel="stylesheet" type="text/css" href="${baseUrl}assets/css/video-js.css"/>
    <link rel="stylesheet" type="text/css" media="screen" href="${templateUrl}/css/style.css"/>
    <link rel="stylesheet" type="text/css" media="screen" href="${templateUrl}/css/editormd.css"/>
    <link rel="stylesheet" type="text/css" media="screen" href="${baseUrl}assets/js/katex/katex.min.css"/>
    <script src="${templateUrl}/js/jquery.min.js"></script>
    <script src="${templateUrl }/js/sheshui.js"></script>
    <script src="${baseUrl }assets/js/video.js"></script>
    <style>
        header .avatar {
            background: url('${_res.avatar}') scroll center center #FFFFFF;
            background-size: cover;
            margin-top: -5px;
            width: 60px;
            height: 60px;
        }

        .gn-menu-main li.sitename .gn-icon {
            width: 50px;
            height: 50px;
            margin-top: 10px;
            border-radius: 50%;
            background: url('${_res.avatar}') no-repeat center center;
            display: inline-block;
            background-size: cover;
        }

        .markdown-body ul, .markdown-body ol {
            padding-left: 0;
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
                    <a title="${_res.title}" href="${rurl}">${_res.title}</a>
                    <span class="slogan">${website.title }</span>
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
