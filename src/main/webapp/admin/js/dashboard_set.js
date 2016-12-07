$(function(){
    var checkedSkin = $("body").get(0).getAttribute("class");
    var checkColor =  ""
    $("#skin-colorpicker > option").each(function(e){
        var tColor = $("#skin-colorpicker > option").get(e)
        if(tColor.getAttribute("data-skin")==checkedSkin){
            $(".btn-colorpicker").css("background-color",tColor.value);
            checkColor = tColor.value;
            return false;
        }
    })

    $(".dropdown-colorpicker > ul.dropdown-menu.dropdown-caret > li > a").each(function(e){
        li = $($(".dropdown-colorpicker > ul.dropdown-menu.dropdown-caret > li > a").get(e))
        li.removeClass("selected");
        if(li.attr("data-color") == checkColor){
            li.addClass("selected");
        }
    })

    $('.colorpick-btn').on('click', function() {
        var dataColor = $(this).attr("data-color")
        var skin = "";
        $("#skin-colorpicker > option").each(function(e){
            var tColor = $("#skin-colorpicker > option").get(e)
            if(tColor.value==dataColor){
                skin = tColor.getAttribute("data-skin");
            }
        })
        $.post('api/admin/website/update',{"admin_dashboard_skin":skin},function(data){
            if(data.success){
                /*$.gritter.add({
                    title: '  操作成功...',
                    class_name: 'gritter-success' + (!$('#gritter-light').get(0).checked ? ' gritter-light' : ''),
                });*/
            }else{
                $.gritter.add({
                    title: '  发生了一些异常...',
                    class_name: 'gritter-error' + (!$('#gritter-light').get(0).checked ? ' gritter-light' : ''),
                });
            }
        });
    });
    $("#ace-settings-add-container").click(function(e){
        var status = "";
        if($("#ace-settings-add-container:checked").length){
            status = "container";
        }
        $.post('api/admin/website/update',{"admin_dashboard_inside_container":status},function(data){
            if(data.success){
                /*$.gritter.add({
                    title: '  操作成功...',
                    class_name: 'gritter-success' + (!$('#gritter-light').get(0).checked ? ' gritter-light' : ''),
                });*/
            }else{
                $.gritter.add({
                    title: '  发生了一些异常...',
                    class_name: 'gritter-error' + (!$('#gritter-light').get(0).checked ? ' gritter-light' : ''),
                });
            }
        });
    })

    $("#sidebar-collapse").click(function(e){
        var status = "";
        if($("#sidebar-collapse > i").get(0).getAttribute("class") == "icon-double-angle-left"){
            status = "";
        }else{
            status = "menu-min"
        }
        $.post('api/admin/website/update',{"admin_dashboard_sidebar_collapser":status},function(data){
            if(data.success){
                /*$.gritter.add({
                    title: '  操作成功...',
                    class_name: 'gritter-success' + (!$('#gritter-light').get(0).checked ? ' gritter-light' : ''),
                });*/
            }else{
                $.gritter.add({
                    title: '  发生了一些异常...',
                    class_name: 'gritter-error' + (!$('#gritter-light').get(0).checked ? ' gritter-light' : ''),
                });
            }
        });
    })
    $(".language").click(function(e){
        var language = $(this).attr("id");
        $.post('api/admin/website/update',{"language":language},function(data){
            if(data.success){
                /*$.gritter.add({
                    title: '  操作成功...',
                    class_name: 'gritter-success' + (!$('#gritter-light').get(0).checked ? ' gritter-light' : ''),
                });*/
            }else{
                $.gritter.add({
                    title: '  发生了一些异常...',
                    class_name: 'gritter-error' + (!$('#gritter-light').get(0).checked ? ' gritter-light' : ''),
                });
            }
            window.location.href=window.location.href
        });
    })
});