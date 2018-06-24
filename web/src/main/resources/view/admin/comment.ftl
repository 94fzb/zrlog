<script src="${basePath}assets/js/jqGrid/jquery.jqGrid.min.js"></script>
<script src="${basePath}assets/js/jqGrid/i18n/grid.locale-${lang}.js"></script>
<script src="${basePath}admin/js/jqgrid_common.js"></script>
<script src="${basePath}admin/js/comment_jqgrid.js"></script>
<div class="page-header">
    <h3>
        ${_res['admin.comment.manage']}
    </h3>
</div>
<div class="row">
    <div id="jqgrid" class="col-md-12">
        <table id="grid-table" min-width="1200"></table>
        <div id="grid-pager"></div>
    </div>
</div>
${pageEndTag}