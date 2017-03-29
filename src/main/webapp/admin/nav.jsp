<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<jsp:include page="include/menu.jsp"/>

<link rel="stylesheet" href="${cacheFile['/assets/css/ui.jqgrid.css']}" />

<script src="${cacheFile['/assets/js/date-time/bootstrap-datepicker.min.js']}"></script>
<script src="${cacheFile['/assets/js/jqGrid/jquery.jqGrid.min.js']}"></script>
<script src="${url}/assets/js/jqGrid/i18n/grid.locale-${lang}.js"></script>
<script src="${cacheFile['/admin/js/nav_jqgrid.js']}"></script>
<div class="page-header">
	<h3>
		${_res['admin.nav.manage']}
	</h3>
</div><!-- /.page-header -->
<div class="row">
	<div class="col-xs-12">
	<table id="grid-table"></table>
	<div id="grid-pager"></div>
	</div>
</div>
<jsp:include page="include/footer.jsp"/>