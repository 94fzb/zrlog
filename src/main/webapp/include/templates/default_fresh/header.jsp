<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<jsp:include page="../../core/core_header.jsp"></jsp:include>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>${title }</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta name="keywords" content="${webs.keywords}" />
<meta name="description" content="${webs.description}" />
<link rel="shortcut icon" href="${rurl }/favicon.ico" />
<link href="${url }/css/main.css" rel="stylesheet" type="text/css" />
<link href="${url }/css/prettify.css" rel="stylesheet" type="text/css" />
<script src="${url }/js/prettify.js" type="text/javascript"></script>
<script src="${url }/js/common_tpl.js" type="text/javascript"></script>
<script src="${url }/js/jquery-1.7.1.js" type="text/javascript"></script>
</head>
<body>
<div id="wrap">
  <div id="header">
    <h1><a href="${rurl }">${webs.title}</a></h1>
    <h3>${webs.second_title}</h3>
  </div>
  	<div id="banner"><a href="${rurl }"><img src="${url }/css/images/top/default.jpg" height="134" width="960" /></a></div>
    <div id="nav">	
    <ul class="bar">
    	<c:forEach var="lognav" items="${init.logNavs}">
			<c:choose>
				<c:when test="${lognav.url eq requrl}">
		<li  class="item current"><a href="${lognav.url}"><c:out value="${lognav.navName}" /></a></li>
				</c:when>
				<c:otherwise>
		<li class="item common"><a  href="${lognav.url}"><c:out value="${lognav.navName}" /></a></li>			
				</c:otherwise>
			</c:choose>
		</c:forEach>
	</ul>
</div>
<div id="content">