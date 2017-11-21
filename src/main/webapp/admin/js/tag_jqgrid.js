jQuery(function ($) {
    jqGrid = jQuery(grid_selector).jqGrid({

        url: 'api/admin/tag',
        datatype: "json",
        colNames: ['ID', '标签名', '计数'],
        colModel: [
            {name: 'id', index: 'id', width: 60, sorttype: "int", sortable: false},
            {name: 'text', index: 'text', width: 150, sortable: false},
            {name: 'count', index: 'count', width: 70, sortable: false}
        ],
        rowNum: 10,
        rowList: [10, 20, 30],
        pager: pager_selector,
        altRows: true,
        loadComplete: function () {
            var table = this;
            setTimeout(function () {
                updatePagerIcons(table);
                enableTooltips(table);
            }, 0);
        },
        caption: _res['admin.tag.manage'],
        height: 421,
        autowidth: true
    });
});