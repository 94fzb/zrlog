<%@ page session="false"  pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<c:if test="${not empty requestScope.pager}">
<nav>
  <ul class="pagination">
	<c:if test="${!requestScope.pager.startPage}">
		 <li class="page-item"><a title="${_res.pageStart}" class="page-link" href="${pager.pageStartUrl}">${_res.pageStart}</a></li>
	</c:if>
	<c:forEach items="${requestScope.pager.pageList}" var="page">
		<li class="page-item<c:if test="${page.current}"> active</c:if>"><a class="page-link" href="${page.url}">${page.desc}</a></li>
	</c:forEach>
	<c:if test="${!requestScope.pager.endPage}">
		<li class="page-item"><a title="${_res.pageEnd}" class="page-link" href="${pager.pageEndUrl}">${_res.pageEnd}</a></li>
	</c:if>
  </ul>
</nav>
</c:if>