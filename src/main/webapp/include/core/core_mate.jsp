<%@page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
  <meta charset="utf-8" />
  <c:set var="webs" value="${init.webSite}" scope="request"></c:set>
  <title><c:if test="${not empty requestScope.log.title}">${requestScope.log.title} - </c:if>${webs.title} - ${webs.second_title}</title>
  <meta name="description" content="${webs.description}"/>
  <c:choose>
  <c:when test="${empty requestScope.log or empty requestScope.log.keywords}">
  <meta name="keywords" content="${webs.keywords}"/>
  </c:when>
  <c:otherwise>
  <meta name="keywords" content="${requestScope.log.keywords}"/>
  </c:otherwise>
  </c:choose>
  <link rel="shortcut icon" href="${rurl }/favicon.ico" />
