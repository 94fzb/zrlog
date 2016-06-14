<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="java.net.URLEncoder"%>
<%
try{
String str = new String(request.getParameter("keywords").toString().getBytes("ISO-8859-1"));
request.setAttribute("keywords", str);
}
catch(Exception e){

}
%>

<jsp:include page="include/menu.jsp"/>

<link href="assets/css/bootstrap.min.css" rel="stylesheet" />
<link rel="stylesheet" href="assets/css/ui.jqgrid.css" />
<link rel="stylesheet" href="assets/css/ace.min.css" />
<style>
	.modal-edit {
		width:1300px;
	}
</style>

<script src="assets/js/date-time/bootstrap-datepicker.min.js"></script>
<script src="assets/js/jqGrid/jquery.jqGrid.min.js"></script>
<script src="assets/js/jqGrid/i18n/grid.locale-cn.js"></script>
<script type="text/javascript" src="assets/js/eModal.min.js"></script>
<script src="admin/js/log_jqgrid.js"></script>

<div class="page-header">
	<h1>
		${_res.blogManage}
		<small>
			<i class="icon-double-angle-right"></i>${_res.viewAllBlog}
		</small>
	</h1>
	<div class="nav-search" style="padding-top: 7px;">
			<form class="form-search">
				<span class="input-icon">
					<input id="keywords" type="text" name="keywords" value="${keywords}" placeholder="${_res.searchTip}" class="input-small nav-search-input" autocomplete="off">
					<i class="icon-search nav-search-icon"></i>
				</span>
			</form>
	</div>
</div>

<div class="row">
	<div class="col-xs-12">
		<!-- PAGE CONTENT BEGINS -->
		<table id="grid-table"></table>
		<div id="grid-pager"></div>
		<!-- PAGE CONTENT ENDS -->
	</div><!-- /.col -->
</div>
<jsp:include page="include/footer.jsp"/>
