<%@ page language="java" session="false" pageEncoding="UTF-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="header.jsp"/>
<div class="main">
    <section>
        <c:choose>
            <c:when test="${empty requestScope.data}">
                <c:set var="pageLevel" value="1" scope="request"/>
                <jsp:include page="404.jsp"/>
            </c:when>
            <c:otherwise>
                <c:if test="${not empty tipsType}">
                    <article class="entry">
                        <div class="post">
                            <h2 class="post-title">
                                    ${tipsType}目录：${tipsName}<br/>
                            </h2>
                            <div class="content"><p>以下是与${tipsType} “${tipsName}” 相关联的文章</p></div>
                        </div>
                    </article>
                </c:if>
                <c:if test="${not empty requestScope.data}">
                    <c:forEach var="log" items="${requestScope.data.rows}">
                        <article class="entry">
                            <a rel="bookmark" href="${log.url}"><p class="float-left article-image"><img
                                    src="${log.thumbnail}" alt="${log.thumbnailAlt}"></p></a>
                            <div class="post">
                                <h2 class="post-title"><a rel="bookmark" href="${log.url}">${log.title}</a></h2>
                                <div class="content">
                                    <c:if test="${not empty log.thumbnail}">
                                        <p>
                                                ${log.digest}
                                        </p>
                                    </c:if>
                                    <c:if test="${empty log.thumbnail}">
                                        <p>
                                                ${log.digest}
                                        </p>
                                    </c:if>
                                </div>
                                <div class="meta">
                                    <p class="category"><a rel="tag" href="${log.typeUrl}">${log.typeName}</a></p>
                                    <p style="padding: 0 5px 0 5px">/</p>
                                    <p class="published"><time datetime="${log.releaseTime}">${log.releaseTime.year+1900}-${log.releaseTime.month+1}-${log.releaseTime.date}</time>
                                    </p>
                                </div>
                            </div>
                        </article>
                    </c:forEach>
                </c:if>
            </c:otherwise>
        </c:choose>
        <jsp:include page="pager.jsp"/>
    </section>
</div>
<jsp:include page="footer.jsp"/>
