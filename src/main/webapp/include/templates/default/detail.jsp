<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:include page="header.jsp"></jsp:include>
<div style="background:rgba(255,255,255,1);">
<jsp:include page="article.jsp"></jsp:include>
<div id="comment-list" class="comment">
<jsp:include page="comment.jsp"></jsp:include>
</div>
<jsp:include page="plugin.jsp"></jsp:include>
<jsp:include page="footer.jsp"></jsp:include>