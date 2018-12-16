<%@ page language="java" session="false" pageEncoding="UTF-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="header.jsp"/>
<c:choose>
    <c:when test="${empty requestScope.data}">
        <c:set var="pageLevel" value="1" scope="request"/>
        <jsp:include page="404.jsp"/>
    </c:when>
    <c:otherwise>
        <c:if test="${not empty tipsType}">
            <div class="category-title">
                <h3>${tipsType}目录：${tipsName}</h3>
                <h3>以下是与${tipsType} “${tipsName}” 相关联的文章</h3>
            </div>
        </c:if>
        <c:if test="${not empty requestScope.data}">
            <c:forEach var="log" items="${requestScope.data.rows}">
                <article class="entry">
                    <c:if test="${not empty log.thumbnail}">
                        <img width="760px" alt="${log.title}" src="${log.thumbnail}"/>
                    </c:if>
                    <h2 class="post-title"><a rel="bookmark" href="${log.url}">${log.title}</a></h2>
                    <div class="content"><p>${log.digest}</p></div>
                    <div class="meta">
                        <p class="category">
                            <a href="${log.typeUrl}">${log.typeName}</a>
                        </p>
                        /<p class="published">
                            ${log.releaseTime.year+1900}-${log.releaseTime.month+1}-${log.releaseTime.date}
                    </p>
                        <p class="commentlink">
                            <a href="${log.url}#comment" class="comments_invite">
                                    ${_res.commentView} <c:if test="${not staticBlog}"> [${log.commentSize}] </c:if>
                            </a>
                        </p>
                    </div>
                </article>
            </c:forEach>
        </c:if>
    </c:otherwise>
</c:choose>
<jsp:include page="pager.jsp"/>
<jsp:include page="plugin.jsp"/>
<jsp:include page="footer.jsp"/>
