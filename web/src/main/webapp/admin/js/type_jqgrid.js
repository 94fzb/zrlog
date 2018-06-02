jQuery(function ($) {
    var grid_selector = "#grid-table";
    var pager_selector = "#grid-pager";

    jqGrid = jQuery(grid_selector).jqGrid({
        url: 'api/admin/type',
        datatype: "json",
        colNames: [' ', 'ID', lang.typeName, lang.alias, lang.mark],
        colModel: [
            {
                name: 'myac', index: '', width: 80, fixed: true, sortable: false, resize: false,
                formatter: 'actions',
                formatoptions: {
                    keys: true,
                    delOptions: {
                        recreateForm: true,
                        beforeShowForm: beforeDeleteCallback,
                        url: "api/admin/type/delete"
                    }
                }
            },
            {name: 'id', index: 'id', width: 60, editable: false, sortable: false},
            {
                name: 'typeName',
                index: 'typeName',
                width: 150,
                sortable: false,
                editable: true,
                editrules: {required: true}
            },
            {
                name: 'alias',
                index: 'alias',
                width: 150,
                editable: true,
                sortable: false,
                editoptions: {size: "20", maxlength: "30"},
                editrules: {required: true}
            },
            {
                name: 'remark',
                index: 'remark',
                width: 150,
                editable: true,
                edittype: "textarea",
                sortable: false
            }

        ],
        editurl: "api/admin/type/update",
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
        height: 410,

        width: getJqGridWidth()

    });

    //navButtons
    jQuery(grid_selector).jqGrid('navGrid', pager_selector,
        { 	//navbar options
            edit: false,
            editicon: 'icon-pencil blue',
            add: true,
            addicon: 'fa fa-plus-circle purple',
            del: false,
            delicon: 'icon-trash red',
            search: false,
            searchicon: 'icon-search orange',
            refresh: true,
            refreshicon: 'fa fa-refresh green',
            view: false,
            viewicon: 'icon-zoom-in grey'
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
            url: "api/admin/type/update"
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
            url: "api/admin/type/add"
        },
        {},
        {},
        {}
    );
});