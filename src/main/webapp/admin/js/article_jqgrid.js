var jqGrid;
function openEditModal(id,catalog,title){
    var options = {
        "url":"admin/article/edit?id="+id,
        "title":decodeURI(title),
        "size":"edit"
    }
    eModal.iframe(options);
}
var deleteUrl = "api/admin/article/delete"

jQuery(function($) {
    $("#keywords").focus();
    var grid_selector = "#grid-table";
    var pager_selector = "#grid-pager";
    var keywords = $("#keywords").val();

    jqGrid = jQuery(grid_selector).jqGrid({

    url:'api/admin/article?keywords='+keywords,
    datatype: "json",
        colNames:['删除','ID','标题','关键词', '作者', '分类','发布时间','查看数','草稿','私有','编辑','浏览'],
        colModel:[
            {name:'delete',width:50,index:'id', sortable:false,
                 formatter: 'actions',
                  formatoptions: {
                   keys: true,
                   editbutton:false,
                   delbutton: true,
                   delOptions: {recreateForm: true, beforeShowForm:beforeDeleteCallback, url: deleteUrl }
                  }
            },
            {name:'id',index:'id', width:50},
            {name:'title',index:'title',width:300, sortable:false},
            {name:'keywords',index:'keywords', width:180},
            {name:'userName',index:'userName', width:60, sortable:false},
            {name:'typeName',index:'typeName', width:90},

            {name:'releaseTime',index:'releaseTime',width:90, sorttype:"date"},
            {name:'click',index:'click', width:50, editable: false},
            {name:'rubbish',index:'rubbish', width:50, editable: false,formatter:renderRubbish},
            {name:'_private',index:'_private', width:50, editable: false,formatter:renderPrivate},
            {name:'id',width:50,index:'id',sortable:false,formatter:editFormat},
            {name:'id',width:50,index:'id',sortable:false,formatter:viewLog},
        ],
        viewrecords : true,
        rowNum:10,
        rowList:[10,20,30],
        pager : pager_selector,
        altRows: true,
        deleteurl:'api/admin/article/delete',
        multiselect: true,
        multiboxonly: true,

        loadComplete : function() {
            var table = this;
            setTimeout(function(){
                updatePagerIcons(table);
                enableTooltips(table);
            }, 0);
        },

        caption: _res.blogManage,
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

    //enable search/filter toolbar
    //jQuery(grid_selector).jqGrid('filterToolbar',{defaultSearch:true,stringResult:true})
    function editFormat( cellvalue, options, rowObject ){
        return '<a href="admin/article/edit?id='+rowObject.id+'"><div id="jEditButton_2" class="ui-pg-div ui-inline-edit" onmouseout="jQuery(this).removeClass(\'ui-state-hover\')" onmouseover="jQuery(this).addClass(\'ui-state-hover\');" style="float: left; cursor: pointer; display: block;" title="" data-original-title="编辑所选记录"><span class="ui-icon ui-icon-pencil"></span></div></a>'
    }
    function viewLog( cellvalue, options, rowObject ){
        return '<a target="_blank" href="admin/article/preview?id='+rowObject.id+'"><div id="jEditButton_2" class="ui-pg-div ui-inline-edit" onmouseout="jQuery(this).removeClass(\'ui-state-hover\')" onmouseover="jQuery(this).addClass(\'ui-state-hover\');" style="float: left; cursor: pointer; display: block;" title="" data-original-title="浏览  '+rowObject.title+'"><span class="ui-icon icon-zoom-in grey"></span></div></a>'
    }
    function renderPrivate( cellvalue, options, rowObject ){
        if(rowObject['_private']){
            return '是'
        }
        return '否'
    }

    function renderRubbish( cellvalue, options, rowObject ){
        if(rowObject.rubbish){
            return '是'
        }
        return '否'
    }

    //navButtons
    jQuery(grid_selector).jqGrid('navGrid',pager_selector,
        { 	//navbar options
            edit: false,
            editicon : 'icon-pencil blue',
            add: false,
            addicon : 'icon-plus-sign purple',
            del: true,
            delicon : 'ui-icon ui-icon-trash',
            search: false,
            searchicon : 'icon-search orange',
            refresh: false,
            refreshicon : 'icon-refresh green',
            view: false,
            viewicon : 'icon-zoom-in grey',
        },
        {},
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
            url: deleteUrl
        },
        {},
        {}
    )

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