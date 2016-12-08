var timer;
var upgradeTimer;
var downloadSuccess;
var finish;
function status(){
    $.get('api/admin/upgrade/download',function(data){
        $("#progress").attr("data-percent",data.download+"%");
        $("#progress2").width(data.process+"%");
        if(data.process==100){
            downloadSuccess  = true;
            clearInterval(timer);
            $("#processbar-title").text("更新包下载完成");
        }
    })
}

function upgrade(){
    $.get('api/admin/upgrade/doUpgrade',function(data){
        $("#upgrade-process").html(data.message);
        if(data.process == 100){
            clearInterval(upgradeTimer);
            finish = true;
        }
    })
}

$(function() {
    $('#wizard_verticle').smartWizard({
       onLeaveStep:leaveAStepCallback,
       onFinish:onFinishCallback
    });
    function leaveAStepCallback(obj, context){
        if(context.fromStep == 1){
            $("#progress").attr("data-percent","0%");
            $("#progress2").width("0%");
            $("#progress").show();
            $("#processbar-title").text("更新包下载中");
            timer=setInterval("status()",500);
            return true;
        }
        if(context.fromStep == 2){
            upgradeTimer = setInterval("upgrade()",500);
            if(downloadSuccess){
                return true;
            }
        }
    }

    function onFinishCallback(objs, context){
        if(finish){
            location.href = location.href;
        }
    }

    $('.buttonNext').addClass('btn btn-success');
    $('.buttonPrevious').addClass('btn btn-primary');
    $('.buttonFinish').addClass('btn btn-default');
});