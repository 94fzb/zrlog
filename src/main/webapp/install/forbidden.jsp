<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
request.setAttribute("errorMsg", ((Map)request.getAttribute("_res")).get("installed"));
%>
<jsp:include page="header.jsp" />
<jsp:include page="footer.jsp" />