<%@ page session="false" pageEncoding="UTF-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="header.jsp" %>
<section>
    <c:choose>
        <c:when test="${empty requestScope.data}">
            <c:set var="pageLevel" value="1" scope="request"/>
            <%@ include file="404.jsp" %>
        </c:when>
        <c:otherwise>
            <c:if test="${not empty tipsType}">
                <h2 class="category-title">
                        ${tipsType}目录：${tipsName}<br/>
                    以下是与${tipsType} “${tipsName}” 相关联的文章
                </h2>
            </c:if>
            <c:if test="${not empty requestScope.data}">
                <c:forEach var="log" items="${requestScope.data.rows}">
                    <article class="entry">
                        <c:if test="${not empty log.thumbnail}">
                            <img class="thumbnail-img" alt="${log.thumbnailAlt}" src="${log.thumbnail}"/>
                        </c:if>
                        <h2 class="post-title"><a rel="bookmark" href="${log.url}">${log.title}</a></h2>
                        <div class="content"><p>${log.digest}</p></div>
                        <div class="meta">
                            <p class="category">
                                <a rel="tag" style="padding-right: 3px" href="${log.typeUrl}">${log.typeName}</a>
                            </p>
                            <p class="published">/
                                <time style="padding-left: 3px">${log.releaseTime.year+1900}-${log.releaseTime.month+1}-${log.releaseTime.date}</time>
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
    <%@ include file="pager.jsp" %>
</section>
<%@ include file="plugin.jsp" %>
<%@ include file="footer.jsp" %>
