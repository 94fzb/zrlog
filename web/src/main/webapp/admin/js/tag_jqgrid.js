$(function () {
    jqGrid = $(grid_selector).jqGrid({
        url: 'api/admin/tag',
        datatype: "json",
        colNames: ['ID', lang.tag, lang.count],
        colModel: [
            {name: 'id', index: 'id', width: 60, sortable: false},
            {name: 'text', index: 'text', width: 150, sortable: false},
            {name: 'count', index: 'count', width: 70, sortable: false}
        ],
        rowNum: 10,
        viewrecords: true,
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
        height: 410,
        autowidth: true
    });
});