<%@ page session="false" pageEncoding="UTF-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="header.jsp" %>
<section>
    <c:choose>
        <c:when test="${not empty log}">
            <div style="background:rgba(255,255,255,1);">
                <%@ include file="article.jsp" %>
                <div id="comment-list" class="comment">
                    <%@ include file="comment.jsp" %>
                </div>
            </div>
        </c:when>
        <c:otherwise>
            <%@ include file="404.jsp" %>
        </c:otherwise>
    </c:choose>
</section>
<%@ include file="plugin.jsp" %>
<%@ include file="footer.jsp" %>