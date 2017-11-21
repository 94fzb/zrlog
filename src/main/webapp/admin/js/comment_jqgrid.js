$(function ($) {
    jqGrid = jQuery(grid_selector).jqGrid({
        url: 'api/admin/comment',
        datatype: "json",
        colNames: ['', '内容', '昵称', '评论者主页', 'IP', '邮箱', '评论时间', '浏览'],
        colModel: [
            {
                name: 'myac', index: 'commentId', width: 80, fixed: true, sortable: false, resize: false,
                formatter: 'actions',
                formatoptions: {
                    keys: true,
                    delOptions: {
                        recreateForm: true,
                        beforeShowForm: beforeDeleteCallback,
                        url: 'api/admin/comment/delete'
                    }
                }
            },
            {name: 'userComment', index: 'userComment', width: 150, editable: true, sortable: false},
            {name: 'userName', index: 'userName', width: 70, editable: true, sortable: false},
            {
                name: 'userHome',
                index: 'userHome',
                width: 150,
                sortable: false,
                editable: false,
                edittype: "textarea",
                formatter: 'link',
                editoptions: {rows: "2", cols: "20"}
            },
            {
                name: 'userIp',
                index: 'userIp',
                width: 150,
                editable: false,
                editoptions: {size: "20", maxlength: "30"},
                sortable: false
            },
            {name: 'userMail', index: 'userMail', width: 70, editable: false, sortable: false},
            {
                name: 'commTime',
                index: 'commTime',
                width: 70,
                editable: false,
                editoptions: {size: "20", maxlength: "30"},
                sortable: false
            },
            {name: 'logId', index: 'logId', width: 30, editable: false, formatter: viewLog, sortable: false}

        ],
        viewrecords: true,
        editurl: "api/admin/comment/update",
        rowNum: 10,
        rowList: [10, 20, 30],
        pager: pager_selector,
        altRows: true,
        multiselect: true,
        multiboxonly: true,

        loadComplete: function () {
            var table = this;
            setTimeout(function () {
                updatePagerIcons(table);
                enableTooltips(table);
            }, 0);
        },
        caption: _res['admin.comment.manage'],
        height: 421,
        autowidth: true

    });

    function viewLog(cellvalue, options, rowObject) {
        return '<a target="_blank" href="admin/article/preview?id=' + rowObject.logId + '"><div id="jEditButton_2" class="ui-pg-div ui-inline-edit" onmouseout="jQuery(this).removeClass(\'ui-state-hover\')" onmouseover="jQuery(this).addClass(\'ui-state-hover\');" style="float: left; cursor: pointer; display: block;" title="" data-original-title="浏览"><span class="ui-icon icon-zoom-in grey"></span></div></a>'
    }

    //navButtons
    jQuery(grid_selector).jqGrid('navGrid', pager_selector,
        { 	//navbar options
            edit: false,
            editicon: 'icon-pencil blue',
            add: false,
            addicon: 'icon-plus-sign purple',
            del: true,
            delicon: 'ui-icon-trash',
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
        {},
        {
            //delete record form
            recreateForm: true,
            beforeShowForm: function (e) {
                var form = $(e[0]);
                if (form.data('styled')) return false;

                form.closest('.ui-jqdialog').find('.ui-jqdialog-titlebar').wrapInner('<div class="widget-header" />')
                style_delete_form(form);

                form.data('styled', true);
            },
            url: "api/admin/comment/delete",
        },
        {}
    );
});