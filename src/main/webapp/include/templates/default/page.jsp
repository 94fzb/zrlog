<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:include page="header.jsp"></jsp:include>
<div class="main clearfloat">
<div class="breadcrumb"></div>
<section>
<jsp:include page="update_browser.jsp"/>
<c:if test="${not empty tipsType}">
<h2 class="category-title">
	${tipsType}目录：${tipsName}<br/>
	以下是与${tipsType} “${tipsName}” 相关联的文章
</h2>
</c:if>
<c:choose>
	<c:when test="${empty requestScope.data}">
		<c:set var="pageLevel" value="1" scope="request"/>
	</c:when>
	<c:otherwise>
		<c:set var="pageLevel" value="2" scope="request"/>
	<c:if test="${not empty requestScope.data}">
<c:forEach var="log" items="${requestScope.data.rows}">
<article class="entry">
<h2 class="post-title"><a rel="bookmark" href="${rurl}post/${log.alias}">${log.title}</a></h2>
  <div class="content"><p>${log.digest}</p></div>
	  <div class="meta">
	  <p class="category"><a rel="tag" href="${rurl}post/sort/${log.typeAlias}">${log.typeName}</a> </p>
	  <p class="published">/<time datetime="${log.releaseTime}">&nbsp;${log.releaseTime.year+1900}${_res.year}${log.releaseTime.month+1}${_res.month}${log.releaseTime.date}${_res.day}</time></p>
	  <p class="commentlink"><a href="${rurl}post/${log.alias}#comment" class="comments_invite">${_res.commentView} [${log.commentSize}]</a></p>
  </div>
</article>   
</c:forEach>
</c:if>
</c:otherwise>
</c:choose>
<c:if test="${not empty requestScope.pager}">
<div class="page_navi">
	<c:if test="${!requestScope.pager.startPage}">
		<A title="${_res.pageStart}" class="extend" href="${rurl}${requestScope.yurl}1">${_res.pageStart}</A>
	</c:if>
	<c:forEach items="${requestScope.pager.pageList}" var="page">
		<a <c:if test="${page.current}">class="current"</c:if> href="${page.url}">${page.desc}</a>
	</c:forEach>
	<c:if test="${!requestScope.pager.endPage}">
		<A title="${_res.pageEnd}" class="extend" href="${rurl}${requestScope.yurl}${requestScope.data.total}">${_res.pageEnd}</A>
	</c:if>
	<div class="breadcrumb"></div>
</div>
</c:if>
</section>
<jsp:include page="plugs.jsp"></jsp:include>
<jsp:include page="footer.jsp"></jsp:include>
