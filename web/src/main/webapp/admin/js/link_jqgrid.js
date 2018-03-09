jQuery(function ($) {
    jqGrid = jQuery(grid_selector).jqGrid({
        url: 'api/admin/link',
        datatype: "json",
        colNames: [' ', 'ID', lang.link, lang.website, lang.desc, lang.order],
        colModel: [
            {
                name: 'myac', index: '', width: 80, fixed: true, sortable: false, resize: false,
                formatter: 'actions',
                formatoptions: {
                    keys: true,
                    delbutton: true,
                    delOptions: {recreateForm: true, beforeShowForm: beforeDeleteCallback, url: "api/admin/link/delete"}
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
                name: 'linkName',
                index: 'linkName',
                width: 150,
                editable: true,
                sortable: false,
                edittype: "textarea",
                editrules: {required: true}
            },
            {
                name: 'alt',
                index: 'alt',
                width: 150,
                editable: true,
                sortable: false,
                edittype: "textarea",
                editrules: {required: true}
            },
            {
                name: 'sort',
                index: 'sort',
                width: 70,
                editable: true,
                sortable: false,
                editrules: {required: true, number: true}
            }

        ],

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

        editurl: "api/admin/link/update",
        caption: _res['admin.link.manage'],
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
            url: 'api/admin/link/add'
        },
        {
            //delete record form
            recreateForm: true,
            beforeShowForm: function (e) {
                var form = $(e[0]);
                if (form.data('styled')) return false;

                form.closest('.ui-jqdialog').find('.ui-jqdialog-titlebar').wrapInner('<div class="widget-header" />')
                style_delete_form(form);

                form.data('styled', true);
            }
        },
        {},
        {},
        {}
    );
});