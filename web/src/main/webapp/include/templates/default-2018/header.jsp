<%@ page language="java" session="false" import="java.util.Map" pageEncoding="UTF-8" %>
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
    <link rel="stylesheet" type="text/css" media="screen" href="${templateUrl}/css/style_2018.css"/>
    <link rel="stylesheet" type="text/css" media="screen" href="${templateUrl}/css/editormd.css"/>
    <script src="${templateUrl}/js/jquery-1.10.2.min.js"></script>
    <script src="${baseUrl }assets/js/video.js"></script>
    <script src="${templateUrl}/js/modernizr.custom.16617.js"></script>
    <script src="${templateUrl}/js/jquery.lazyload.min.js"></script>
    <style>
        .side .s-header .avatar,
        .top .avatar {
            background: url('${_res.avatar}') scroll center center #FFFFFF;
            background-size: cover;
            border-radius: 50%;
            overflow: hidden;
            margin-left: 10px;
        }
    </style>
</head>
<body>
<div class="page">
    <div id="overLayer"></div>
    <div class="top">
        <div class="inner">
            <header>
                <a href="${baseUrl}"><i class="avatar"></i></a>
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
                                <li><a title="${type.typeName}" href="${rurl}post/sort/${type.alias}"><i
                                        class="material-icons">label</i> ${type.typeName} (${type.typeamount})</a></li>
                            </c:forEach>
                        </ul>
                    </div>
                </div>
                <div class="widget category-list">
                    <h3>标签 <span class="en">Tags</span></h3>
                    <div class="taglist" id="tags">
                        <c:forEach items="${init.tags}" var="tag">
                            <a href="${tag.url}" title="${tag.text}上共有(${tag.count})文章">${tag.text}</a>
                        </c:forEach>
                    </div>
                </div>
                <div class="widget category-list">
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