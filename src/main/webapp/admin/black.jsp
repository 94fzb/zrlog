<%@page import="java.io.File"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:choose>
<c:when test="${not empty param.menu}">
<jsp:include page="include/menu.jsp"/>
</c:when>
<c:otherwise>
<jsp:include page="include/simple.jsp"/>
</c:otherwise>
</c:choose>
<c:set value="${param.include}.jsp" var="include"></c:set>
<jsp:include page="${include}"/>
<jsp:include page="include/footer.jsp"/>