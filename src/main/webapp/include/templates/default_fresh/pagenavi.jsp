<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:if test="${requestScope.data.total>1}">
<div id="pagenavi">
       <c:if test="${requestScope.data.page>1}">
<a title="跳转到第一页" class="extend" href="${rurl}${requestScope.yurl}1">第一页 </a>
<a href="${rurl}${requestScope.yurl}${requestScope.data.page-1}">上一页 </a>
</c:if>
<c:choose>
<c:when test="${requestScope.data.total>11}">
	<c:choose>
		<c:when test="${requestScope.data.page<3 or requestScope.data.total-4<requestScope.data.page}">
			<c:forEach begin="1" end="3" step="1" var="i">
				<a <c:if test="${i eq requestScope.data.page}">class="current"</c:if> href="${rurl}${requestScope.yurl}<c:out value="${i}"></c:out>"><c:out value="${i}"></c:out></a>
			</c:forEach>
		</c:when>

		<c:otherwise>
			<c:forEach begin="${requestScope.data.page-2}" end="${requestScope.data.page}" step="1" var="i">
				<a <c:if test="${i eq requestScope.data.page}">class="current"</c:if> href="${rurl}${requestScope.yurl}<c:out value="${i}"></c:out>"><c:out value="${i}"></c:out></a>
			</c:forEach>
		</c:otherwise>
	</c:choose>
	<c:forEach begin="${requestScope.data.total-3}" end="${requestScope.data.total}"
	step="1" var="i">
	<a <c:if test="${i eq requestScope.data.page}">class="current"</c:if> href="${rurl}${requestScope.yurl}<c:out value="${i}"></c:out>"><c:out value="${i}"></c:out></a>
</c:forEach>
</c:when>
<c:otherwise>
<c:forEach begin="1" end="${requestScope.data.total}"
	step="1" var="i">
		<a <c:if test="${i eq requestScope.data.page}">class="current"</c:if> href="${rurl}${requestScope.yurl}<c:out value="${i}"></c:out>"><c:out value="${i}"></c:out></a>		
</c:forEach>
</c:otherwise>
</c:choose>
<c:if test="${requestScope.data.total ne requestScope.data.page}">
<a href="${rurl}${requestScope.yurl}${requestScope.data.page+1}">下一页 </a>
<a title="跳转到最后一页" class="extend"
	href="${rurl}${requestScope.yurl}${requestScope.data.total}">
	最后一页 </a></c:if>
</div>
</c:if>

