var jqGrid;
var grid_selector = "#grid-table";
var pager_selector = "#grid-pager";

function style_delete_form(form) {
    var buttons = form.next().find('.EditButton .fm-button');
    buttons.addClass('btn btn-sm').find('[class*="-icon"]').remove();//ui-icon, s-icon
    buttons.eq(0).addClass('btn-danger').prepend('<i class="fa fa-trash"></i>');
    buttons.eq(1).addClass('btn-primary').prepend('<i class="fa fa-remove"></i>')
}

function beforeDeleteCallback(e) {
    var form = $(e[0]);
    if (form.data('styled')) return false;

    form.closest('.ui-jqdialog').find('.ui-jqdialog-titlebar').wrapInner('<div class="widget-header" />');
    style_delete_form(form);

    form.data('styled', true);
}

function style_edit_form(form) {
    //enable datepicker on "sdate" field and switches for "stock" field
    form.find('input[name=sdate]').datepicker({format: 'yyyy-mm-dd', autoclose: true})
        .end().find('input[name=stock]')
        .addClass('ace ace-switch ace-switch-5').wrap('<label class="inline" />').after('<span class="lbl"></span>');

    //update buttons classes
    var buttons = form.next().find('.EditButton .fm-button');
    buttons.addClass('btn btn-sm').find('[class*="-icon"]').remove();//ui-icon, s-icon
    buttons.eq(0).addClass('btn-primary').prepend('<i class="fa fa-paper-plane"></i>');
    buttons.eq(1).addClass('btn-primary').prepend('<i class="fa fa-remove"></i>');

    buttons = form.next().find('.navButton a');
    buttons.find('.ui-icon').remove();
    buttons.eq(0).append('<i class="icon-chevron-left"></i>');
    buttons.eq(1).append('<i class="icon-chevron-right"></i>');
}

function beforeEditCallback(e) {
    var form = $(e[0]);
    form.closest('.ui-jqdialog').find('.ui-jqdialog-titlebar').wrapInner('<div class="widget-header" />');
    style_edit_form(form);
}

//replace icons with FontAwesome icons like above
function updatePagerIcons(table) {
    resizeJqGrid();
    $(".ui-jqgrid-titlebar-close").remove();
    var replacement =
        {
            'ui-icon-seek-first': 'fa fa-angle-double-left bigger-140',
            'ui-icon-seek-prev': 'fa fa-angle-left bigger-140',
            'ui-icon-seek-next': 'fa fa-angle-right bigger-140',
            'ui-icon-seek-end': 'fa fa-angle-double-right bigger-140'
        };
    $('.ui-pg-table:not(.navtable) > tbody > tr > .ui-pg-button > .ui-icon').each(function () {
        var icon = $(this);
        var $class = $.trim(icon.attr('class').replace('ui-icon', ''));

        if ($class in replacement) icon.attr('class', 'ui-icon ' + replacement[$class]);
    })
}

function enableTooltips(table) {
    $('.navtable .ui-pg-button').tooltip({container: 'body'});
    $(table).find('.ui-pg-div').tooltip({container: 'body'});
}

function resizeJqGrid() {
    if (jqGrid) {
        jqGrid.setGridWidth(getJqGridWidth());
    }
}

function getJqGridWidth(){
    var minWidth = $("#grid-table").attr("min-width");
    if (minWidth && $("#right_col").width() < minWidth) {
        return minWidth;
    } else {
        return $("#right_col").width();
    }
}

var divW = jQuery("#right_col").width();

function checkResize() {
    var w = jQuery("#right_col").width();
    if (w !== divW) {
        resizeJqGrid();
        divW = w;
    }
}

var timer = setInterval(checkResize, 200);

$(window).bind('resize', function () {
    resizeJqGrid()
}).trigger('resize');