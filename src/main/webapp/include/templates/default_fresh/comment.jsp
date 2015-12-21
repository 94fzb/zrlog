<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<div id="comment-place" class="comment-place">
<c:if test="${not empty log.comments}">										
  <h2>评论列表</h2></c:if>
<c:forEach items="${log.comments}" var="comment">
  <ul class="comments">
	<li>	<p>${comment.userComment}</p>
<p class="small"><a rel="nofollow" href="${comment.userHome }">${comment.userName }</a> 在 ${comment.commTime }</p>
	</li></ul>
</c:forEach>
<p id="comment">给我留言 <span class="en">Say something...</span></p>
<div id="comment-post" class="comment-post">
<form id="commentform" action="${rurl }post/addComment" method="post">
	<input type="hidden" value="${log.logId }" name="logId">
				<p>
		<input type="text" tabindex="1" size="22" value="" maxlength="49" name="name">
		<label for="author"><small>昵称</small></label>
	</p>
	<p>
		<input type="text" tabindex="2" size="22" value="" maxlength="128" name="email">
		<label for="email"><small>邮件地址 (选填)</small></label>
	</p>
	<p>
		<input type="text" tabindex="3" size="22" value="" maxlength="128" name="webHome">
		<label for="url"><small>个人主页 (选填)</small></label>
	</p>
				<p><textarea tabindex="4" rows="10" id="comment" name="userComment"></textarea></p>
	<p> <input type="submit" tabindex="6" value="发表评论" id="comment_submit"></p>
</form>
</div>
</div>
 