<%@ page language="java" session="false" import="java.util.*" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:choose>
<c:when test="${log.canComment}">
<c:choose>
<c:when test="${init.webSite.changyan_status eq 'on'}">
<div id="cwrapper">
<!--PC和WAP自适应版-->
<div id="SOHUCS" sid="${log.logId}" ></div>
<style>
#SOHUCS #SOHU_MAIN .module-cmt-list .block-title-gw ul li {
background-color: #fff;
}
</style>
</div>
<script type="text/javascript">
(function(){
var appid = '${init.webSite.changyan_appId}';
var conf = 'prod_e3ec9450cef3977322648b62a820e338';
var width = window.innerWidth || document.documentElement.clientWidth;
if (width < 960) {
window.document.write('<script id="changyan_mobile_js" charset="utf-8" type="text/javascript" src="//changyan.sohu.com/upload/mobile/wap-js/changyan_mobile.js?client_id=' + appid + '&conf=' + conf + '"><\/script>'); } else { var loadJs=function(d,a){var c=document.getElementsByTagName("head")[0]||document.head||document.documentElement;var b=document.createElement("script");b.setAttribute("type","text/javascript");b.setAttribute("charset","UTF-8");b.setAttribute("src",d);if(typeof a==="function"){if(window.attachEvent){b.onreadystatechange=function(){var e=b.readyState;if(e==="loaded"||e==="complete"){b.onreadystatechange=null;a()}}}else{b.onload=a}}c.appendChild(b)};loadJs("//changyan.sohu.com/upload/changyan.js",function(){window.changyan.api.config({appid:appid,conf:conf})}); } })(); </script>
</c:when>
<c:otherwise>
<c:if test="${not empty log.comments}">
<div class="message-list">
<h2>${_res.comments}</h2>
<c:forEach items="${log.comments}" var="comment">
<ul class="comments">
<li><p>${comment.userComment}</p>
<p class="small"><a rel="nofollow" href="${comment.userHome }">${comment.userName }</a> 在 ${comment.commTime }</p>
</li></ul>
</c:forEach>
</div>
</c:if>
<div id="cwrapper">
<form action="${rurl }post/addComment" method="post" id="txpCommentInputForm">
<div class="comments-wrapper">
<div class="reply">
<h2 id="comment">${_res.comment}</h2>
<p class="comment-write"><label class="hidden" for="message">Message</label><textarea class="txpCommentInputMessage" rows="15" cols="45" name="userComment" id="message"></textarea></p>
<div class="input-group">
<p><label for="userName">姓名</label><input type="text" id="name" class="comment_name_input" size="25" name="userName" value=""></p>
<p><label for="userMail">邮箱</label><input type="text" id="email" class="comment_email_input" size="25" name="userMail" value=""></p>
<p><label for="webHome">网站</label><input type="text" id="web" class="comment_web_input" size="25" name="webHome" value=""></p>
</div>
<div class="button-set">
<span class="submit"><input type="submit"  id="txpCommentSubmit" class="button" name="submit" value="${_res.submit}"></span>
</div>
</div>
<input type="hidden" name="logId" value="${log.logId }">
</div>
</form>
</div>
</c:otherwise>
</c:choose>
</c:when>
</c:choose>
<div class="pager-nav">
<a title="${log.nextLog.title}" href="${log.nextLog.url}"><p class="next">${_res.nextArticle}：${log.nextLog.title}<i class="material-icons arrow-R">navigate_next</i></p></a>
<a title="${log.lastLog.title}" href="${log.lastLog.url}"><p class="prev"><i class="material-icons arrow-L">navigate_before</i>${_res.lastArticle}：${log.lastLog.title}</p></a>
</div>