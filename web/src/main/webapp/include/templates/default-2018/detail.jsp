﻿
<%@ page language="java" session="false" pageEncoding="UTF-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="header.jsp"/>
<div class="main clearfloat">
    <section>
        <style>
            /*video播放进度条问题*/
            .markdown-body {
                word-wrap: normal
            }
        </style>
        <c:choose>
        <c:when test="${not empty log}">
        <jsp:include page="article.jsp"/>
        <div id="comment-list" class="comment">
            <jsp:include page="comment.jsp"/>
            </c:when>
            <c:otherwise>
                <%@ include file="404.jsp" %>
            </c:otherwise>
            </c:choose>
        </div>
    </section>
<jsp:include page="footer.jsp"/>