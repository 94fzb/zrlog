<%@ page language="java" session="false" import="java.util.*" pageEncoding="UTF-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:choose>
    <c:when test="${log.canComment}">
        <c:choose>
            <c:when test="${init.webSite.changyan_status eq 'on'}">
                <div id="cwrapper" style="padding: 20px">
                    <plugin name="changyan" view="widget" param="articleId=${log.logId}"></plugin>
                </div>
            </c:when>
            <c:otherwise>
                <c:if test="${not empty log.comments}">
                    <div class="message-list">
                        <h2>${_res.comments}</h2>
                        <c:forEach items="${log.comments}" var="comment">
                            <ul class="comments">
                                <li><p>${comment.userComment}</p>
                                    <p class="small"><a rel="nofollow"
                                                        href="${comment.userHome }">${comment.userName }</a>
                                        在 ${comment.commTime }</p>
                                </li>
                            </ul>
                        </c:forEach>
                    </div>
                </c:if>
                <div id="cwrapper">
                    <form action="${rurl }post/addComment" method="post" id="txpCommentInputForm">
                        <div class="comments-wrapper">
                            <div class="reply">
                                <h2 id="comment">${_res.comment}</h2>
                                <p class="comment-write"><label class="hidden" for="message">Message</label><textarea
                                        class="txpCommentInputMessage" rows="15" cols="45" name="userComment"
                                        id="message"></textarea></p>
                                <div class="input-group">
                                    <p><label for="name">姓名</label><input type="text" id="name"
                                                                              class="comment_name_input" size="25"
                                                                              name="userName" value=""></p>
                                    <p><label for="email">邮箱</label><input type="text" id="email"
                                                                              class="comment_email_input" size="25"
                                                                              name="userMail" value=""></p>
                                    <p><label for="web">网站</label><input type="text" id="web"
                                                                             class="comment_web_input" size="25"
                                                                             name="webHome" value=""></p>
                                </div>
                                <div class="button-set">
                                    <span class="submit"><input type="submit" id="txpCommentSubmit" class="button"
                                                                name="submit" value="${_res.submit}"></span>
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
    <a title="${log.nextLog.title}" href="${log.nextLog.url}"><p class="next">${_res.nextArticle}：${log.nextLog.title}<i
            class="material-icons arrow-R">navigate_next</i></p></a>
    <a title="${log.lastLog.title}" href="${log.lastLog.url}"><p class="prev"><i class="material-icons arrow-L">navigate_before</i>${_res.lastArticle}：${log.lastLog.title}
    </p></a>
</div>