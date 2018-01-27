jQuery(function ($) {
    jqGrid = jQuery(grid_selector).jqGrid({
        url: 'api/admin/nav',
        datatype: "json",
        colNames: [' ', 'ID', lang.link, lang.navName, lang.order],
        colModel: [
            {
                name: 'myac', index: '', width: 80, fixed: true, sortable: false, resize: false,
                formatter: 'actions',
                formatoptions: {
                    keys: true,
                    delOptions: {recreateForm: true, beforeShowForm: beforeDeleteCallback, url: "api/admin/nav/delete"}
                }
            },
            {name: 'id', index: 'id', width: 60, sorttype: "int", editable: false},
            {
                name: 'url',
                index: 'url',
                width: 150,
                sortable: false,
                editable: true,
                edittype: "textarea",
                formatter: 'link',
                editoptions: {rows: "2", cols: "20"}
            },
            {name: 'navName', index: 'navName', width: 150, editable: true, editoptions: {size: "20", maxlength: "30"}},
            {name: 'sort', index: 'sort', width: 70, editable: true},

        ],

        editurl: "api/admin/nav/update",
        viewrecords: true,
        rowNum: 10,
        rowList: [10, 20, 30],
        pager: pager_selector,
        altRows: true,
        multiselect: false,
        multiboxonly: true,

        loadComplete: function () {
            var table = this;
            setTimeout(function () {
                updatePagerIcons(table);
                enableTooltips(table);
            }, 0);
        },
        caption: _res['admin.nav.manage'],
        height: 421,

        autowidth: true

    });
    //navButtons
    jQuery(grid_selector).jqGrid('navGrid', pager_selector,
        { 	//navbar options
            edit: false,
            editicon: 'icon-pencil blue',
            add: true,
            addicon: 'icon-plus-sign purple',
            del: false,
            delicon: 'icon-trash red',
            search: false,
            searchicon: 'icon-search orange',
            refresh: true,
            refreshicon: 'icon-refresh green',
            view: false,
            viewicon: 'icon-zoom-in grey',
        },
        {
            //edit record form
            //closeAfterEdit: true,
            recreateForm: true,
            beforeShowForm: function (e) {
                var form = $(e[0]);
                form.closest('.ui-jqdialog').find('.ui-jqdialog-titlebar').wrapInner('<div class="widget-header" />')
                style_edit_form(form);
            },
            url: "api/admin/nav/update"
        },
        {
            //new record form
            closeAfterAdd: true,
            recreateForm: true,
            viewPagerButtons: false,
            beforeShowForm: function (e) {
                var form = $(e[0]);
                form.closest('.ui-jqdialog').find('.ui-jqdialog-titlebar').wrapInner('<div class="widget-header" />')
                style_edit_form(form);
            },
            url: "api/admin/nav/add"
        },
        {},
        {}
    )
});