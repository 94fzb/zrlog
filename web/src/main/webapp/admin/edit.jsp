<%@ page session="false" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set scope="request" var="subTitle" value="${_res['admin.log.edit']}"/>
<jsp:include page="include/menu.jsp"/>
<jsp:include page="article_editor.jsp"/>
<jsp:include page="include/footer.jsp"/>