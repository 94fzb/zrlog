<%@page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%
	String turl = request.getRequestURI();
	String kurl = request.getRequestURI();
	String url = request.getScheme() + "://" + request.getHeader("host") +request.getContextPath()+((Map<String,Object>)(((Map<String,Object>)request.getAttribute("init")).get("webSite"))).get("template");
	request.setAttribute("url", url);
	request.setAttribute("rurl",request.getScheme() + "://" + request.getHeader("host")+ request.getContextPath()+"/");
	String suffix="";
	if(request.getContextPath()+((Map<String,Object>)(((Map<String,Object>)request.getAttribute("init")).get("webSite"))).get("pseudo_static")!=null){
		suffix=".html";
	}
	request.setAttribute("suffix", suffix);
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html lang='zh-CN' xml:lang='zh-CN' xmlns='http://www.w3.org/1999/xhtml'>
<head>
<c:set var="webs" value="${init.webSite}" scope="request"></c:set>
<meta charset="utf-8"/>
<link rel="shortcut icon" type="image/x-icon" href="${rurl}/favicon.ico" />
<meta name="description" content="${webs.description}"/>
<c:choose>
<c:when test="${empty requestScope.log or empty requestScope.log.keywords}">
<meta name="keywords" content="${webs.keywords}"/>
</c:when>
<c:otherwise>
<meta name="keywords" content="${requestScope.log.keywords}"/>
</c:otherwise>
</c:choose>
<title><c:if test="${not empty requestScope.log.title}">${requestScope.log.title} - </c:if>${webs.title} - ${webs.second_title}</title>
<link type="text/css" href="${url}/style.css" rel="stylesheet"  media="all"/>
<link type="text/css" id="page-list-style-css" href="${url}/css/page-list.css" rel="stylesheet"  media="all"/>
<link type="text/css" rel="stylesheet" href="${rurl }/include/plugs/SyntaxHighlighter/styles/shCoreDefault.css" />
<script type="text/javascript" src="${url}/js/html5.js"></script> 
<script type="text/javascript" src="${url}/js/jquery-1.4.2.min.js"></script>
<script type="text/javascript" src="${rurl }/include/plugs/SyntaxHighlighter/scripts/shCore.js"></script>
<script type="text/javascript" src="${rurl }/include/plugs/SyntaxHighlighter/scripts/shBrushCpp.js"></script>
<script type="text/javascript" src="${rurl }/include/plugs/SyntaxHighlighter/scripts/shBrushCSharp.js"></script>
<script type="text/javascript" src="${rurl }/include/plugs/SyntaxHighlighter/scripts/shBrushJava.js"></script>
<script type="text/javascript" src="${rurl }/include/plugs/SyntaxHighlighter/scripts/shBrushJScript.js"></script>
<script type="text/javascript" src="${rurl }/include/plugs/SyntaxHighlighter/scripts/shBrushSql.js"></script>
<script type="text/javascript" src="${rurl }/include/plugs/SyntaxHighlighter/scripts/shBrushXml.js"></script>
<script type="text/javascript">SyntaxHighlighter.defaults['toolbar'] = false;SyntaxHighlighter.all();  </script>
<STYLE type="text/css">
	/* Link color */
	a,
	#site-title a:focus,
	#site-title a:hover,
	#site-title a:active,
	.entry-title a:hover,
	.entry-title a:focus,
	.entry-title a:active,
	.widget_twentyeleven_ephemera .comments-link a:hover,
	section.recent-posts .other-recent-posts a[rel="bookmark"]:hover,
	section.recent-posts .other-recent-posts .comments-link a:hover,
	.format-image footer.entry-meta a:hover,
	#site-generator a:hover {
		color: #1e73be;
	}
	section.recent-posts .other-recent-posts .comments-link a:hover {
		border-color: #1e73be;
	}
	article.feature-image.small .entry-summary p a:hover,
	.entry-header .comments-link a:hover,
	.entry-header .comments-link a:focus,
	.entry-header .comments-link a:active,
	.feature-slider a.active {
		background-color: #1e73be;
	}
</STYLE>
	 
 
<style id="custom-background-css" type="text/css">
.recentcomments a{display:inline !important;padding:0 !important;margin:0 !important;}
body.custom-background { background-color: #5c8cbc; }
</style>

</head>
<body class="home blog custom-background single-author two-column right-sidebar">
	<div class="hfeed" id="page">
		<header id="branding" role="banner">
		<hgroup>
		<h1 id="site-title">
			<span><a title="${webs.secondTitle}" href="${rurl }" rel="home">${webs.title}</a></span>
		</h1>
		<h2 id="site-description"></h2>
		</hgroup>
		<form id="searchform" action="${rurl }post/search" method="post">
			<label class="assistive-text" for="s">搜索</label> <input name="key" class="field" id="s" type="text" placeholder="<c:out value="${sessionScope.key}" default="请输入关键字" />" value="<c:out value="${sessionscope.keywords}"/>"/> 
			<input name="submit" class="submit" id="searchsubmit" type="submit" value="<c:out value="${sessionScope.key}" default="请输入关键字" />"/>
		</form>
		<nav id="access" role="navigation">
		<div class="menu-%e4%b8%bb%e8%8f%9c%e5%8d%95-container">
			<ul class="menu" id="menu-%e4%b8%bb%e8%8f%9c%e5%8d%95">
				<c:forEach var="lognav" items="${init.logNavs}">
					<c:choose>
						<c:when test="${lognav.url eq requrl}">
				<li id="current" class="menu-item menu-item-type-taxonomy menu-item-object-category current-menu-item"><a href="${lognav.url}"><c:out value="${lognav.navName}" />
				</a><span></span>
				</li>
						</c:when>
						<c:otherwise>
				<li><a href="${lognav.url}"><c:out value="${lognav.navName}" />
				</a><span></span>
				</li>
						</c:otherwise>
					</c:choose>
				</c:forEach>
			</ul>
		</div>
		</nav> 
	</header>