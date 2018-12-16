<%@ page language="java" session="false" pageEncoding="UTF-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
</section>
<aside>
    <c:if test="${not empty _res.widgetAd}">
        <div class="widget" style="padding-bottom: 15px">
                ${_res.widgetAd}
        </div>
    </c:if>

    <div class="widget search">
        <form id="searchform" action="${searchUrl}" method="post">
            <p class="search_input"><input type="text" size="15" name="key" placeholder="${_res.searchTip}"
                                           value="${key}" class="inputtext"><input type="submit" class="btn"
                                                                                   value="${_res.search}"></p>
        </form>
    </div>
    <c:choose>
        <c:when test="${not empty init.plugins}">
            <c:forEach var="plugin" items="${init.plugins}">
                <c:choose>
                    <c:when test="${plugin.isSystem==false and pageLevel>=plugin.level}">
                        <div class="widget">
                            <h3>${plugin.pTitle}</h3>
                            <p>${plugin.content}</p>
                            <br/>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <c:choose>
                            <c:when test="${plugin.pluginName eq 'types' }">
                                <div class="widget">
                                    <h3>${_res.category}</h3>
                                    <div class="list">
                                        <ul class="category_list">
                                            <c:forEach var="type" items="${init.types}">
                                                <li><a href="${type.url}">${type.typeName} (${type.typeamount})</a>
                                                </li>
                                            </c:forEach>
                                        </ul>
                                    </div>
                                </div>
                            </c:when>
                            <c:when test="${plugin.pluginName eq 'links' and pageLevel>=plugin.level and not empty init.links}">
                                <div class="widget">
                                    <h3>${_res.link}</h3>
                                    <ul>
                                        <c:forEach items="${init.links}" var="link">
                                            <li><a href="${link.url }" title="${link.alt }"
                                                   target="_blank">${link.linkName}</a></li>
                                        </c:forEach>
                                    </ul>
                                </div>
                            </c:when>
                            <c:when test="${plugin.pluginName eq 'archives'}">
                                <div class="widget">
                                    <h3>${_res.archive}</h3>
                                    <ul>
                                        <c:forEach var="archive" items="${init.archiveList}">
                                            <li><a href="${archive.url}" rel="nofollow">${archive.text}
                                                (${archive.count})</a>
                                            </li>
                                        </c:forEach>
                                    </ul>
                                </div>
                            </c:when>
                            <c:when test="${plugin.pluginName eq 'tags'}">
                                <div class="widget">
                                    <h3>${_res.tag}</h3>
                                    <div class="taglist" id="tags">
                                        <c:forEach items="${init.tags}" var="tag">
                                            <a href="${tag.url}" class="size${tag.count%6}"
                                               title="${tag.text}上共有(${tag.count})文章">${tag.text}</a>
                                        </c:forEach>
                                    </div>
                                </div>
                            </c:when>
                        </c:choose>
                    </c:otherwise>
                </c:choose>
            </c:forEach>
        </c:when>
    </c:choose>
</aside>
