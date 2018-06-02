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
                    delbutton: true,
                    delOptions: {recreateForm: true, beforeShowForm: beforeDeleteCallback, url: "api/admin/nav/delete"}
                }
            },
            {name: 'id', index: 'id', width: 60, editable: false, sortable: false},
            {
                name: 'url',
                index: 'url',
                width: 150,
                sortable: false,
                editable: true,
                edittype: "textarea",
                formatter: 'link',
                editrules: {required: true}
            },
            {
                name: 'navName',
                index: 'navName',
                width: 150,
                editable: true,
                edittype: "textarea",
                sortable: false,
                editrules: {required: true}
            },

            {
                name: 'sort',
                index: 'sort',
                width: 220,
                editable: true,
                sortable: false,
                editrules: {required: true, number: true}
            }
        ],

        editurl: "api/admin/nav/update",
        viewrecords: true,
        rowNum: 10,
        rowList: [10, 20, 30],
        pager: pager_selector,
        altRows: true,

        multiselect: false,
        multiboxonly: false,

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
            }
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
    );
});