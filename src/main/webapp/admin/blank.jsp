<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:choose>
<c:when test="${not empty param.menu or not empty menu}">
<jsp:include page="include/menu.jsp"/>
</c:when>
<c:otherwise>
<jsp:include page="include/simple.jsp"/>
</c:otherwise>
</c:choose>
<c:choose>
<c:when test="${empty include}">
<c:set value="${param.include}" var="include"></c:set>
</c:when>
</c:choose>
<c:choose>
<c:when test="${not empty include}">
<jsp:include page="${include}.jsp"/>
</c:when>
</c:choose>
<c:choose>
<c:when test="${not empty param.menu or not empty menu}">
<jsp:include page="include/footer.jsp"/>
</c:when>
<c:otherwise>
<jsp:include page="include/simple_footer.jsp"/>
</c:otherwise>
</c:choose>