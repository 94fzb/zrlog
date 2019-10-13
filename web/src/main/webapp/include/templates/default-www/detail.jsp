<%@ page language="java" session="false" import="java.util.*" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:include page="header.jsp"/>
<c:choose>
<c:when test="${empty requestScope.log}">
    <c:set var="pageLevel" value="1" scope="request"/>
    <jsp:include page="404.jsp"/>
</c:when>
<c:otherwise>
<div style="background:rgba(255,255,255,1);">
<jsp:include page="article.jsp"/>
<jsp:include page="comment.jsp"/>
</c:otherwise></c:choose>
<jsp:include page="plugin.jsp"/>
<jsp:include page="footer.jsp"/>