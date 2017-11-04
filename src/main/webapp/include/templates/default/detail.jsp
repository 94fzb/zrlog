<%@ page session="false"  pageEncoding="UTF-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="header.jsp" %>
<div style="background:rgba(255,255,255,1);">
    <%@ include file="article.jsp" %>
    <div id="comment-list" class="comment">
        <%@ include file="comment.jsp" %>
    </div>
    <%@ include file="plugin.jsp" %>
<%@ include file="footer.jsp" %>