$(function(){
    PNotify.prototype.options.delay == 3000;
    $(".btn-info").click(function(){
        var formId=$(this).attr("id")+"Ajax";
        if($("#"+formId).attr("checkBox")){
            var checkBoxName=$("#"+formId).attr("checkBox");
            if($("[name='"+checkBoxName+"']").size() && $("[name='"+checkBoxName+"']")[0].checked==true){
                $("#"+checkBoxName).attr("name",checkBoxName).attr("value","on");
            }
            else{
                $("#"+checkBoxName).attr("name",checkBoxName).attr("value","off");
            }
        }
        var uri;
        if($("#"+formId).attr("action")!=null){
            uri = $("#"+formId).attr("action");
        }else{
            uri = 'api/admin/website/update'
        }
        $.post(uri,$("#"+formId).serialize(),function(data){
            if(data.error == 0){
                var message;
                if(data.message!=null && data.message!=''){
                    message = data.message;
                }else{
                    message = "操作成功...";
                }
                new PNotify({
                      title: message,
                      type: 'success',
                      delay:3000,
                      hide: true,
                      styling: 'bootstrap3'
                });
            }else{
                var message;
                if(data.message!=null && data.message!=''){
                    message = data.message;
                }else{
                    message = "发生了一些异常...";
                }
                new PNotify({
                      title: message,
                      delay:3000,
                      type: 'error',
                      hide: true,
                      styling: 'bootstrap3'
                 });
            }
        });
    });
});