<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<jsp:include page="include/menu.jsp"/>

<link href="assets/css/bootstrap.min.css" rel="stylesheet" />
<link rel="stylesheet" href="assets/css/ui.jqgrid.css" />
<link rel="stylesheet" href="assets/css/ace.min.css" />

<script src="assets/js/date-time/bootstrap-datepicker.min.js"></script>
<script src="assets/js/jqGrid/jquery.jqGrid.min.js"></script>
<script src="assets/js/jqGrid/i18n/grid.locale-cn.js"></script>
<script src="admin/js/tag_jqgrid.js"></script>
<div class="page-header">
	<h1>
		查看标签
		<small>
			<i class="icon-double-angle-right"></i>
			查看所有
		</small>
	</h1>
</div><!-- /.page-header -->
<div class="row">
	<div class="col-xs-12">
		<!-- PAGE CONTENT BEGINS -->

		<table id="grid-table"></table>

		<div id="grid-pager"></div>

		<!-- PAGE CONTENT ENDS -->
	</div><!-- /.col -->
</div>
<jsp:include page="include/footer.jsp"/>
