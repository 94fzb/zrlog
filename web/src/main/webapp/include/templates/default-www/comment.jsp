<%@ page session="false" pageEncoding="UTF-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:choose>
    <c:when test="${log.canComment}">
        <div id="comment-list" class="comment">
            <c:choose>
                <c:when test="${init.webSite.changyan_status eq 'on'}">
                    <plugin name="changyan" view="widget" param="articleId=${log.logId}"></plugin>
                </c:when>
                <c:otherwise>
                    <c:if test="${not empty log.comments}">
                        <h2>${_res.comments}</h2></c:if>
                    <c:forEach items="${log.comments}" var="comment">
                        <ul class="comments">
                            <li><p>${comment.userComment}</p>
                                <p class="small"><a
                                        rel="nofollow">${comment.userName }</a> ${_res.on} ${comment.commTime }
                                </p>
                            </li>
                        </ul>
                    </c:forEach>
                    <form action="${rurl }post/addComment" method="post" id="txpCommentInputForm">
                        <input type="hidden" name="logId" value="${log.logId }">
                        <h2>${_res.comment}</h2>
                        <textarea class="form-control" rows="15" cols="45" name="userComment"></textarea>
                        <div class="input-group  mb-3" style="padding-top: 15px">
                            <div class="input-group-prepend">
                                <span class="input-group-text">${_res.website}</span>
                            </div>
                            <input type="text" name="web" class="form-control"/>
                        </div>
                        <div class="row">
                            <div class="col-6">
                                <div class="input-group mb-3">
                                    <div class="input-group-prepend">
                                        <span class="input-group-text">${_res.userName}</span>
                                    </div>
                                    <input type="text" name="userName" class="form-control"/>
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-6">
                                <div class="input-group  mb-3">
                                    <div class="input-group-prepend">
                                        <span class="input-group-text">${_res.email}</span>
                                    </div>
                                    <input type="text" name="email" class="form-control"/>
                                </div>
                            </div>
                        </div>
                        <button type="submit" class="btn btn-outline-primary">${_res.submit}</button>
                    </form>
                </c:otherwise>
            </c:choose>
        </div>
    </c:when>
</c:choose>
