<%@ page session="false"  pageEncoding="UTF-8" %>
<%
    String path = request.getContextPath();
    String basePath = com.zrlog.web.util.WebTools.getRealScheme(request) + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
    request.setAttribute("url", com.zrlog.web.util.WebTools.getRealScheme(request) + "://" + request.getHeader("host") + request.getContextPath());
%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="common_header.jsp"/>

<body style="background:#fff">
<div>