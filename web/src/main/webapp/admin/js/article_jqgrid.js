jQuery(function ($) {
    var keywrods = $.trim(getParameterByName("keywords"));
    $("#keywords").val(keywrods);
    var articleUrl = 'api/admin/article';
    var deleteUrl = "api/admin/article/delete";
    jqGrid = jQuery(grid_selector).jqGrid({
        url: articleUrl + "?keywords=" + keywrods,
        datatype: "json",
        colNames: ['', 'ID', lang.title, lang.keywords, lang.author, lang.type,
            lang.viewCount, lang.rubbish, lang.private, lang.createTime, lang.lastUpdateDate, lang.edit, lang.view],
        colModel: [
            {
                name: 'delete', width: 28, index: 'id', sortable: false,
                formatter: 'actions',
                formatoptions: {
                    keys: true,
                    editbutton: false,
                    delbutton: true,
                    delOptions: {recreateForm: true, beforeShowForm: beforeDeleteCallback, url: deleteUrl}
                }
            },
            {name: 'id', index: 'id', width: 50},
            {name: 'title', index: 'title', width: 300, sortable: false},
            {name: 'keywords', index: 'keywords', width: 150},
            {name: 'userName', index: 'userName', width: 60, sortable: false},
            {name: 'typeName', index: 'typeName', width: 90},
            {name: 'click', index: 'click', width: 50, editable: false},
            {name: 'rubbish', index: 'rubbish', width: 50, editable: false, formatter: renderRubbish},
            {name: 'privacy', index: 'privacy', width: 50, editable: false, formatter: renderPrivate},
            {name: 'releaseTime', index: 'releaseTime', width: 90},
            {name: 'lastUpdateDate', index: 'lastUpdateDate', width: 90},
            {name: 'id', width: 50, index: 'id', sortable: false, formatter: editFormat},
            {name: 'id', width: 50, index: 'id', sortable: false, formatter: viewLog}
        ],
        viewrecords: true,
        rowNum: 10,
        rowList: [10, 20, 30],
        pager: pager_selector,
        altRows: true,
        deleteurl: deleteUrl,
        multiselect: true,
        multiboxonly: true,
        loadComplete: function () {
            var table = this;
            setTimeout(function () {
                updatePagerIcons(table);
                enableTooltips(table);
            }, 0);
        },
        height: 410,
        width: getJqGridWidth()
    });

    function editFormat(cellvalue, options, rowObject) {
        return '<a href="admin/index?id=' + rowObject.id + '#article_edit"><div id="jEditButton_2" class="ui-pg-div ui-inline-edit" onmouseout="jQuery(this).removeClass(\'ui-state-hover\')" onmouseover="jQuery(this).addClass(\'ui-state-hover\');" style="float: left; cursor: pointer; display: block;" title="" data-original-title="' + lang.edit + '"><span class="ui-icon ui-icon-pencil"></span></div></a>'
    }

    function viewLog(cellvalue, options, rowObject) {
        return '<a target="_blank" href="admin/article/preview?id=' + rowObject.id + '"><div id="jEditButton_2" class="ui-pg-div ui-inline-edit" onmouseout="jQuery(this).removeClass(\'ui-state-hover\')" onmouseover="jQuery(this).addClass(\'ui-state-hover\');" style="float: left; cursor: pointer; display: block;" title="" data-original-title="' + lang.view + '  ' + $("<div>" + rowObject.title + '"<div>"').text() + '><i class="fa fa-search-plus ui-icon"></i></div></a>'
    }

    function renderPrivate(cellvalue, options, rowObject) {
        if (rowObject['privacy']) {
            return lang.yes;
        }
        return lang.no;
    }

    function renderRubbish(cellvalue, options, rowObject) {
        if (rowObject.rubbish) {
            return lang.yes;
        }
        return lang.no;
    }

    jQuery(grid_selector).jqGrid('navGrid', pager_selector,
        { 	//navbar options
            edit: false,
            editicon: 'icon-pencil blue',
            add: false,
            addicon: 'icon-plus-sign purple',
            del: true,
            delicon: 'ui-icon ui-icon-trash',
            search: false,
            searchicon: 'icon-search orange',
            refresh: false,
            refreshicon: 'icon-refresh green',
            view: false,
            viewicon: 'icon-zoom-in grey',
        },
        {},
        {},
        {
            recreateForm: true,
            beforeShowForm: function (e) {
                var form = $(e[0]);
                if (form.data('styled')) return false;

                form.closest('.ui-jqdialog').find('.ui-jqdialog-titlebar').wrapInner('<div class="widget-header" />');
                style_delete_form(form);

                form.data('styled', true);
            },
            url: deleteUrl
        },
        {},
        {}
    );

    $("#searchArticleBtn").click(function () {
        var queryStr = '?keywords=' + $.trim($("#keywords").val());
        jqGrid.jqGrid('setGridParam', {url: articleUrl + queryStr, page: 1});
        window.history.replaceState({}, "", window.location.pathname + queryStr + window.location.hash);
        jqGrid.jqGrid('setGridParam', {datatype: 'json'}).trigger('reloadGrid');
        return false;
    })
});