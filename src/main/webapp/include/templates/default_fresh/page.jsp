<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:include page="header.jsp"></jsp:include>
<c:set var="pageLevel" value="2" scope="request"/>
<div id="contentleft">
<c:if test="${not empty tipsType}">
<h2 class="category-title">
	${tipsType}目录：${tipsName}<br/>
	以下是与${tipsType} “${tipsName}” 相关联的文章
</h2>
</c:if>
<c:if test="${not empty requestScope.data}">
<c:forEach var="log" items="${requestScope.data.rows}">
  <h2 class="post-title"><a rel="bookmark" href="${rurl}post/${log.alias}">${log.title}</a></h2>
  <p>${log.digest}</p>
  <p class="date">
  <a rel="tag" href="${rurl}post/sort/${log.typeAlias}">${log.typeName}</a>
  /<time datetime="${log.releaseTime}">&nbsp;${log.releaseTime.year+1900}年${log.releaseTime.month+1}月${log.releaseTime.date}日</time>
  </p>
  <p class="count">
  	<a href="${rurl}post/${log.alias}#comment" class="comments_invite">查看评论 [${log.commentSize}]</a>
  </p>
</c:forEach>
</c:if>
<jsp:include page="pagenavi.jsp"></jsp:include>
</div>
<jsp:include page="plugs.jsp"></jsp:include>
<jsp:include page="footer.jsp"></jsp:include>
