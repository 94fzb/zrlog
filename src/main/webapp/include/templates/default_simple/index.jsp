<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:include page="header.jsp"></jsp:include>
<c:choose>
	<c:when test="${empty requestScope.data}">
		<c:set var="pageLevel" value="1" scope="request"/>
		<div id="main">
		<section id="primary">
			<div id="content" role="main">
				<article id="post-0" class="post no-results not-found">
					<header class="entry-header">
						<h1 class="entry-title">未找到</h1>
					</header>
					<!-- .entry-header -->
	
					<div class="entry-content">
						<p>抱歉，没有符合您搜索条件的结果。请换其它关键词再试。</p>
						<form method="post" id="searchform" action="${rurl }post/search">
							<label for="s" class="assistive-text">搜索</label> <input
								type="text" class="field" name="key" id="s" placeholder="搜索" />
							<input type="submit" class="submit" name="submit"
								id="searchsubmit" value="搜索" />
						</form>
					</div>
					<!-- .entry-content -->
				</article>
				<!-- #post-0 -->
	
	
			</div>
			<!-- #content -->
		</section>
		<!-- #primary -->
	</c:when>
	<c:otherwise>
		<c:set var="pageLevel" value="2" scope="request"/>
		<div id="main">
		<div id="primary">
		<div id="content" role="main">
			<c:if test="${not empty requestScope.data}">
				<c:forEach var="log" items="${requestScope.data.rows}">

					<article
						class="post-33 post type-post status-publish format-standard hentry category-seo"
						id="post-33">
						<header class="entry-header">
							<h1 class="entry-title">
								<a title="链向 ${log.title}" href="${rurl}post/${log.alias}"
									target="_blank" rel="bookmark">${log.title}</a>
							</H1>
							<DIV class="entry-meta">
								<SPAN class="sep">发表于 </SPAN><A
									href="${rurl}post/${log.alias}" rel="bookmark"><time
										class="entry-date" datetime="${log.releaseTime}">${log.releaseTime.year+1900}年${log.releaseTime.month+1}月${log.releaseTime.date}日</time>
								</A><SPAN class="by-author"> <SPAN class="sep"> 由 </SPAN> <SPAN
									class="author vcard"><A title="查看所有由发布的文章"
										class="url fn n" href="${rurl }author/admin" rel="author"></A>
								</SPAN>
								</SPAN>
							</DIV>
							<div class="comments-link">
								<a href="${rurl}post/${log.alias}#comments"
									title="《 ${log.title}》上的评论"> ${log.commentSize}</a>
							</div>
						</HEADER>
						<DIV class="entry-content">${log.digest}<br/></DIV>
						<FOOTER class="entry-meta">
							<SPAN class="cat-links"><SPAN
								class="entry-utility-prep entry-utility-prep-cat-links">发表在</SPAN>
								<A title="查看'${log.typeName}'中的全部文章" href="${rurl}post/sort/${log.typeAlias}"
								rel="category tag">${log.typeName}</A> </SPAN> <SPAN class="sep">|
							</SPAN> <SPAN class="comments-link"><A
								title="《 ${log.title}》上的评论"
								href="${rurl}post/${log.alias}#respond"><SPAN
									class="leave-reply">发表回复</SPAN>
							</A>
							</SPAN>
						</FOOTER>
					</ARTICLE>
				</c:forEach>
			</c:if>
			
			<c:if test="${requestScope.data.total>1}">
			<DIV class="page_navi">
				<c:if test="${requestScope.data.page>1}">
				<A title="跳转到第一页" class="extend" href="${rurl}${requestScope.yurl}1">第一页 </A>
				<A href="${rurl}${requestScope.yurl}${requestScope.data.page-1}">上一页 </A>
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
				<A href="${rurl}${requestScope.yurl}${requestScope.data.page+1}">下一页 </A>
				<A title="跳转到最后一页" class="extend"
					href="${rurl}${requestScope.yurl}${requestScope.data.total}">
					最后一页 </A></c:if>

			</DIV>
			</c:if>
		</DIV>
		
	</DIV>
	</c:otherwise>
</c:choose>
<jsp:include page="plugs.jsp"></jsp:include>
<jsp:include page="footer.jsp"></jsp:include>
