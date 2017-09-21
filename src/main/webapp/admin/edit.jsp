<%@ page language="java" session="false" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set scope="request" var="subTitle" value="文章编辑"/>
<jsp:include page="include/menu.jsp"/>
<div class="page-header">
	<h3>
		${_res['admin.log.edit']}
	</h3>
</div>

<jsp:include page="article_editor.jsp"/>
<jsp:include page="include/footer.jsp"/>