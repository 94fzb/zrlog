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
				</div>
				<!-- #content -->
			</section>
			<!-- #primary -->
	</c:when>
	<c:otherwise>
	<div class="main clearfloat">
		<section>
      <!--[if lt IE 9]>
      <div class="tips not-ie" id="tipsWrap">
        <span class="close" id="clostBtn" title="Close">关闭</span>
        <div class="tips-content">
          <span class="notice">为了您有更好的浏览体验，请升级使用以下浏览器：</span><span class="browsers"><a href="http://www.google.com/chrome" class="chrome" title="谷歌浏览器">Chrome</a><a href="http://www.mozilla.org/en-US/firefox/new/" class="ff" title="火狐">Firefox</a><a href="http://www.opera.com/download/" class="opera" title="Opera">Opera</a><a href="http://www.apple.com/safari/download/" class="safari" title="Safari">Safari</a></span>
        </div>
        <script>$('#clostBtn').click( function(){ $('#tipsWrap').css('display','none'); } ); </script>
      </div>
      <![endif]--> 
        
        <div style="background:rgba(255,255,255,1);">
        <article>
   <h1 class="post-title">${log.title}</h1>


  <div class="meta">
  <p class="category"><a href="${rurl}post/sort/${log.typeAlias}" rel="tag">${log.typeName}</a> </p>
  <p class="published">/<time datetime="${log.releaseTime}">&nbsp;${log.releaseTime.year+1900}年${log.releaseTime.month+1}月${log.releaseTime.date}日</time></p>
  </div>
  <div class="content">
  	${log.content }
  </div>
  
  <p style="color:#D4D4D4"> 转载请注明作者和出处(${webs.title})，并添加本页链接。<BR>原文链接:
							<A title="${log.title }" href="${rurl}post/${log.alias}"><SPAN style="color: rgb(51, 102, 255);" span="">${rurl}post/${log.alias}</SPAN></A></p>
        <div class="pager-nav">
          <a title="${log.lastLog.title}" href="${rurl }post/${log.lastLog.alias}" rel="prev"><p class="prev">上一篇：${log.lastLog.title}</p></a>
          <a title="${log.nextLog.title}" href="${rurl }post/${log.nextLog.alias}" rel="next"><p class="next">下一篇：${log.nextLog.title}</p></a>
        </div>
							</article> 
							 <c:choose>
							<c:when test="${log.canComment}">
								<c:choose>
									<c:when test="${init.webSite.user_comment_pluginStatus}">
										 <jsp:include page="../../core/duoshuo_comment.jsp"></jsp:include>
									</c:when>
									<c:otherwise>
<div id="comment-list" class="comment">
<c:if test="${not empty log.comments}">										
  <h2>评论列表</h2></c:if>
<c:forEach items="${log.comments}" var="comment">
  <ul class="comments">
	<li>	<p>${comment.userComment}</p>
<p class="small"><a rel="nofollow" href="${comment.userHome }">${comment.userName }</a> 在 ${comment.commTime }</p>
	</li></ul>
</c:forEach>

  <div id="cwrapper">
  
<form action="${rurl }post/addComment" method="post" id="txpCommentInputForm">
<div class="comments-wrapper">


<div class="reply">
  <h2 id="comment">给我留言 <span class="en">Say something...</span></h2>
  
    <p class="comment-write"><label class="hidden" for="message">Message</label><textarea class="txpCommentInputMessage" rows="15" cols="45" name="userComment" id="message"></textarea></p>
    <div class="input-group">
      <p><label for="userName">姓名</label><input type="text" id="name" class="comment_name_input" size="25" name="userName" value=""></p>
      <p><label for="userMail">邮箱</label><input type="text" id="email" class="comment_email_input" size="25" name="userMail" value=""></p>
      <p><label for="webHome">网站</label><input type="text" id="web" class="comment_web_input" size="25" name="webHome" value=""></p>
    </div>
    <div class="button-set"><span class="remember"><input type="checkbox" class="checkbox active" checked="checked" id="remember" value="1" name="remember"> <label for="remember">Remember</label> <input type="hidden" name="checkbox_type" value="remember"></span> <span class="submit"><input type="submit"  id="txpCommentSubmit" class="button" name="submit" value="Submit"></span> </div>    
  
</div>
<input type="hidden" name="logId" value="${log.logId }">
</div></form>
</div>
  
</div>
									</c:otherwise>
								</c:choose>
								</c:when>  
							</c:choose>      
        </div>
      </section>
	</c:otherwise>
</c:choose>
<jsp:include page="plugs.jsp"></jsp:include>
<jsp:include page="footer.jsp"></jsp:include>