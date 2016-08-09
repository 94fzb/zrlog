<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set scope="request" var="stitle" value="文章编辑"/>
<c:if test="${not empty session.log}">
	<c:set scope="request" var="log" value="${session.log}"></c:set>
</c:if>
<jsp:include page="include/menu.jsp"/>
<div class="page-header">
	<h3>
		文章撰写
	</h3>
</div>

<jsp:include page="editor.jsp"/>
<jsp:include page="include/footer.jsp"/>