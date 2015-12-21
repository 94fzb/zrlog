<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:include page="header.jsp"></jsp:include>
<c:set var="pageLevel" value="1" scope="request" />
<c:choose>
	<c:when test="${empty log}">
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
<div id="contentleft">
  <h2 class="post-title">${log.title}</h2>
  <div class="markdown-body editormd-preview-container" previewcontainer="true" style="padding: 20px;">${log.content }</div>
  <p class="data"><a href="${rurl}post/sort/${log.typeAlias}" rel="tag">${log.typeName}</a>
  /<time datetime="${log.releaseTime}">&nbsp;${log.releaseTime.year+1900}年${log.releaseTime.month+1}月${log.releaseTime.date}日</time></p>
  <p style="color:#D4D4D4"> 转载请注明作者和出处(${webs.title})，并添加本页链接。<BR>原文链接:
							<A title="${log.title }" href="${rurl}post/${log.alias}"><SPAN style="color: rgb(51, 102, 255);" span="">${rurl}post/${log.alias}</SPAN></A></p>
  <div class="nextlog">
    «<a title="${log.lastLog.title}" href="${rurl }post/${log.lastLog.alias}"> ${log.lastLog.title}</a>｜
    <a title="${log.nextLog.title}" href="${rurl }post/${log.nextLog.alias}">${log.nextLog.title}</a>» 	
  </div>
<c:choose>
	<c:when test="${log.canComment}">
		<c:choose>
			<c:when test="${init.webSite.user_comment_pluginStatus}">
				 <jsp:include page="../../core/duoshuo_comment.jsp"></jsp:include>
			</c:when>
			<c:otherwise>
				<jsp:include page="comment.jsp"></jsp:include>
			</c:otherwise>
		</c:choose>
	</c:when>  
</c:choose>      
        </div>
	</c:otherwise>
</c:choose>
</div>
<jsp:include page="plugs.jsp"></jsp:include>
<jsp:include page="footer.jsp"></jsp:include>