<script src="${basePath}assets/js/jqGrid/jquery.jqGrid.min.js"></script>
<script src="${basePath}assets/js/jqGrid/i18n/grid.locale-${lang}.js"></script>
<script src="${basePath}admin/js/jqgrid_common.js"></script>
<script src="${basePath}admin/js/article_jqgrid.js"></script>
<div class="page-header">
    <h3>${_res.blogManage}</h3>
</div>
<div class="page-title">
    <div class="col-md-2 offset-md-10 col-xs-12 top_search">
        <form class="form-search">
            <div class="input-group">
                <input id="keywords" name="keywords" autocomplete="off" type="text"
                       placeholder="${_res.searchTip}" class="form-control">
                <span class="input-group-btn">
                      <button type="submit" id="searchArticleBtn" class="btn btn-default">${_res.search}</button>
                    </span>
            </div>
        </form>
    </div>
</div>

<div class="row">
    <div id="jqgrid" class="col-md-12">
        <table id="grid-table" min-width="1200"></table>
        <div id="grid-pager"></div>
    </div>
</div>
${pageEndTag}