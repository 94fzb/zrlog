<%@ page session="false" pageEncoding="UTF-8" %>
<link rel="stylesheet" href="${basePath}assets/css/ui.jqgrid.css"/>

<script src="${basePath}assets/js/date-time/bootstrap-datepicker.min.js"></script>
<script src="${basePath}assets/js/jqGrid/jquery.jqGrid.min.js"></script>
<script src="${basePath}assets/js/jqGrid/i18n/grid.locale-${lang}.js"></script>
<script src="${basePath}admin/js/jqgrid_common.js"></script>
<script src="${basePath}admin/js/link_jqgrid.js"></script>
<div class="page-header">
    <h3>
        ${_res['admin.link.manage']}
    </h3>
</div>
<div class="row">
    <div class="col-xs-12">
        <table id="grid-table"></table>
        <div id="grid-pager"></div>
    </div>
</div>
${pageEndTag}