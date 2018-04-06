<%@ page session="false" pageEncoding="UTF-8" %>
<%
    try {
        String str = new String(request.getParameter("keywords").getBytes("ISO-8859-1"), "UTF-8");
        request.setAttribute("keywords", str);
    } catch (Exception e) {

    }
%>
<link rel="stylesheet" href="${basePath}assets/css/ui.jqgrid.css"/>
<script src="${basePath}assets/js/jqGrid/jquery.jqGrid.min.js"></script>
<script src="${basePath}assets/js/jqGrid/i18n/grid.locale-${lang}.js"></script>
<script src="${basePath}admin/js/jqgrid_common.js"></script>
<script src="${basePath}admin/js/article_jqgrid.js"></script>
<div class="page-title">
    <div class="title_left">
        <h3>${_res.blogManage}</h3>
    </div>
    <div class="title_right">
        <div class="col-md-5 col-sm-5 col-xs-12 form-group pull-right top_search">
            <form class="form-search">
                <div class="input-group">
                    <input id="keywords" value="${keywords}" name="keywords" autocomplete="off" type="text"
                           placeholder="${_res.searchTip}" class="form-control">
                    <span class="input-group-btn">
						  <button type="submit" id="searchArticleBtn" class="btn btn-default">${_res.search}</button>
						</span>
                </div>
            </form>
        </div>
    </div>
</div>

<div class="row">
    <div id="jqgrid" class="col-xs-12">
        <table id="grid-table" min-width="1200"></table>
        <div id="grid-pager"></div>
    </div>
</div>
${pageEndTag}