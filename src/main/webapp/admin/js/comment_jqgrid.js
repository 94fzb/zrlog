$(function($) {
    var grid_selector = "#grid-table";
    var pager_selector = "#grid-pager";

    var jqGrid = jQuery(grid_selector).jqGrid({

    url:'api/admin/comment',
    datatype: "json",
        colNames:['','内容','昵称','评论者主页','IP', '邮箱','评论时间','浏览'],
        colModel:[
            {name:'myac',index:'commentId', width:80, fixed:true, sortable:false, resize:false,
                formatter:'actions',
                formatoptions:{
                    keys:true,
                    delOptions:{recreateForm: true, beforeShowForm:beforeDeleteCallback,url: 'api/admin/comment/delete'},
                }
            },
            {name:'userComment',index:'userComment', width:150, editable: true},
            {name:'userName',index:'userName', width:70, editable: true},
            {name:'userHome',index:'userHome',width:150, sortable:false,editable: false,edittype:"textarea",formatter:'link', editoptions:{rows:"2",cols:"20"}},
            {name:'userIp',index:'userIp', width:150,editable: false,editoptions:{size:"20",maxlength:"30"}},
            {name:'userMail',index:'userMail', width:70, editable: false},
            {name:'commTime',index:'commTime', width:70,editable: false,editoptions:{size:"20",maxlength:"30"}},
            {name:'logId',index:'logId', width:30, editable: false,formatter:viewLog},

        ],
        viewrecords : true,
        editurl:"api/admin/comment/update",
        rowNum:10,
        rowList:[10,20,30],
        pager : pager_selector,
        altRows: true,
        multiselect: true,
        multiboxonly: true,

        loadComplete : function() {
            var table = this;
            setTimeout(function(){
                updatePagerIcons(table);
                enableTooltips(table);
            }, 0);
        },
        caption: _res['admin.comment.manage'],
        height:421,
        autowidth: true

    });

    function resizeJqGrid() {
        var leftWidth = $("#left_col").width();
        if(leftWidth>0){
            leftWidth += 36;
        }else{
            leftWidth += 20;
        }
        jqGrid.setGridWidth($(window).width()-leftWidth);
    }

    var divW = jQuery("#left_col").width();
    var divH = jQuery("#left_col").height();
    function checkResize(){
        var w = jQuery("#left_col").width();
        var h = jQuery("#left_col").height();
        if (w != divW || h != divH) {
            resizeJqGrid();
            divH = h;
            divW = w;
        }
    }
    var timer = setInterval(checkResize, 200);

    $(window).bind('resize', function(){resizeJqGrid()}).trigger('resize');

    function viewLog( cellvalue, options, rowObject ){
        return '<a target="_blank" href="post/'+rowObject.logId+'"><div id="jEditButton_2" class="ui-pg-div ui-inline-edit" onmouseout="jQuery(this).removeClass(\'ui-state-hover\')" onmouseover="jQuery(this).addClass(\'ui-state-hover\');" style="float: left; cursor: pointer; display: block;" title="" data-original-title="浏览"><span class="icon-zoom-in grey"></span></div></a>'
    }
    //navButtons
    jQuery(grid_selector).jqGrid('navGrid',pager_selector,
        { 	//navbar options
            edit: false,
            editicon : 'icon-pencil blue',
            add: false,
            addicon : 'icon-plus-sign purple',
            del: true,
            delicon : 'ui-icon-trash',
            search: false,
            searchicon : 'icon-search orange',
            refresh: true,
            refreshicon : 'icon-refresh green',
            view: false,
            viewicon : 'icon-zoom-in grey',
        },
        {
            //edit record form
            //closeAfterEdit: true,
            recreateForm: true,
            beforeShowForm : function(e) {
                var form = $(e[0]);
                form.closest('.ui-jqdialog').find('.ui-jqdialog-titlebar').wrapInner('<div class="widget-header" />')
                style_edit_form(form);
            }
        },
        {},
        {
            //delete record form
            recreateForm: true,
            beforeShowForm : function(e) {
                var form = $(e[0]);
                if(form.data('styled')) return false;

                form.closest('.ui-jqdialog').find('.ui-jqdialog-titlebar').wrapInner('<div class="widget-header" />')
                style_delete_form(form);

                form.data('styled', true);
            },
            url:"api/admin/comment/delete",
        },
        {}
    )



    function style_edit_form(form) {
        //enable datepicker on "sdate" field and switches for "stock" field
        form.find('input[name=sdate]').datepicker({format:'yyyy-mm-dd' , autoclose:true})
            .end().find('input[name=stock]')
                  .addClass('ace ace-switch ace-switch-5').wrap('<label class="inline" />').after('<span class="lbl"></span>');

        //update buttons classes
        var buttons = form.next().find('.EditButton .fm-button');
        buttons.addClass('btn btn-sm').find('[class*="-icon"]').remove();//ui-icon, s-icon
        buttons.eq(0).addClass('btn-primary').prepend('<i class="icon-ok"></i>');
        buttons.eq(1).prepend('<i class="icon-remove"></i>')

        buttons = form.next().find('.navButton a');
        buttons.find('.ui-icon').remove();
        buttons.eq(0).append('<i class="icon-chevron-left"></i>');
        buttons.eq(1).append('<i class="icon-chevron-right"></i>');
    }

    function style_delete_form(form) {
        var buttons = form.next().find('.EditButton .fm-button');
        buttons.addClass('btn btn-sm').find('[class*="-icon"]').remove();//ui-icon, s-icon
        buttons.eq(0).addClass('btn-danger').prepend('<i class="icon-trash"></i>');
        buttons.eq(1).prepend('<i class="icon-remove"></i>')
    }

    function beforeDeleteCallback(e) {
        var form = $(e[0]);
        if(form.data('styled')) return false;

        form.closest('.ui-jqdialog').find('.ui-jqdialog-titlebar').wrapInner('<div class="widget-header" />')
        style_delete_form(form);

        form.data('styled', true);
    }

    function beforeEditCallback(e) {
        var form = $(e[0]);
        form.closest('.ui-jqdialog').find('.ui-jqdialog-titlebar').wrapInner('<div class="widget-header" />')
        style_edit_form(form);
    }

    //replace icons with FontAwesome icons like above
    function updatePagerIcons(table) {
        var replacement =
        {
            'ui-icon-seek-first' : 'icon-double-angle-left bigger-140',
            'ui-icon-seek-prev' : 'icon-angle-left bigger-140',
            'ui-icon-seek-next' : 'icon-angle-right bigger-140',
            'ui-icon-seek-end' : 'icon-double-angle-right bigger-140'
        };
        $('.ui-pg-table:not(.navtable) > tbody > tr > .ui-pg-button > .ui-icon').each(function(){
            var icon = $(this);
            var $class = $.trim(icon.attr('class').replace('ui-icon', ''));

            if($class in replacement) icon.attr('class', 'ui-icon '+replacement[$class]);
        })
    }

    function enableTooltips(table) {
        $('.navtable .ui-pg-button').tooltip({container:'body'});
        $(table).find('.ui-pg-div').tooltip({container:'body'});
    }
});