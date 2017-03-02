<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
</section>
<aside>
	<div class="widget search">
		<form id="searchform" action="${searchUrl}" method="post">
			<p class="search_input"><input type="text" onblur="OnExit(this)" onfocus="OnEnter(this)" size="15" name="key" title="${_res.searchTip}" value="${key}" class="inputtext"><input type="submit" class="btn" value="${_res.search}"></p>
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
		<br />
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
			<c:when test="${plugin.pluginName eq 'links' and pageLevel>=plugin.level}">
	<div class="widget">
		<h3>${_res.link}</h3>
		<ul>
			<c:forEach items="${init.links}" var="link">
				<li><a href="${link.url }" title="${link.alt }" target="_blank">${link.linkName}</a></li>
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
	<script type="text/javascript">
	$(document).ready(function(){
		var tags_a = $("#tags").find("a");
		tags_a.each(function(){
			var x = 6;
			var y = 0;
			var rand = parseInt(Math.random() * (x - y + 1) + y);
			$(this).addClass("size"+rand);
		});
	});
	</script>
	<style type="text/css">
		.taglist a{padding:0px;display:inline-block;white-space:nowrap;}
		a.size1{font-size:10px;padding:2px;color:#804D40;}
		a.size1:hover{color:#E13728;}
		a.size2{padding:2px;font-size:12px;color:#B9251A;}
		a.size2:hover{color:#E13728;}
		a.size3{padding:3px;font-size:14px;color:#C4876A;}
		a.size3:hover{color:#E13728;}
		a.size4{padding:1px;font-size:18px;color:#B46A47;}
		a.size4:hover{color:#E13728;}
		a.size5{padding:3px;font-size:16px;color:#E13728;}
		a.size5:hover{color:#B46A47;}
		a.size6{padding:2px;font-size:12px;color:#77625E}
		a.size6:hover{color:#E13728;}
	</style>
	<div class="widget">
	<h3>${_res.tag}</h3>
	<div class="taglist" id="tags">
		 <c:forEach items="${init.tags}" var="tag">
			<a href="${tag.url}" title="${tag.text}上共有(${tag.count})文章">${tag.text}</a>
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
