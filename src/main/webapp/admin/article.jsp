<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="java.net.URLEncoder"%>
<%
try{
String str = new String(request.getParameter("keywords").toString().getBytes("ISO-8859-1"),"UTF-8");
request.setAttribute("keywords", str);
}
catch(Exception e){

}
%>
<jsp:include page="include/menu.jsp"/>

<link rel="stylesheet" href="${cacheFile['/assets/css/ui.jqgrid.css']}" />
<script src="${cacheFile['/assets/js/jqGrid/jquery.jqGrid.min.js']}"></script>
<script src="${url}/assets/js/jqGrid/i18n/grid.locale-${lang}.js"></script>
<script src="${cacheFile['/admin/js/article_jqgrid.js']}"></script>
<div class="page-title">
	<div class="title_left">
		<h3>${_res.blogManage}</h3>
	</div>
	<div class="title_right">
		<div class="col-md-5 col-sm-5 col-xs-12 form-group pull-right top_search">
			<form class="form-search">
				<div class="input-group">
						<input id="keywords"  value="${keywords}" name="keywords" autocomplete="off" type="text" placeholder="${_res.searchTip}" class="form-control">
						<span class="input-group-btn">
						  <button type="submit" class="btn btn-default">${_res.search}</button>
						</span>
				</div>
			</form>
		</div>
	</div>
</div>

<div class="row">
	<div id="jqgrid" class="col-xs-12">
		<table id="grid-table"></table>
		<div id="grid-pager"></div>
	</div>
</div>
<jsp:include page="include/footer.jsp"/>
