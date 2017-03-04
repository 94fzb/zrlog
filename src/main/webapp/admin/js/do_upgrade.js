var timer;
var upgradeTimer;
var downloadSuccess;
var finish;
var downloadRequestPending = false;
var upgradeRequestPending = false;
function status(){
    if(!downloadRequestPending){
        downloadRequestPending = true;
        $.get('api/admin/upgrade/download',function(data){
            $("#progress").attr("data-percent",data.download+"%");
            $("#progress2").width(data.process+"%");
            if(data.process==100){
                downloadSuccess  = true;
                clearInterval(timer);
                $("#processbar-title").text("更新包下载完成");
            }
            downloadRequestPending = false;
        })
    }
}

function upgrade(){
    if(!upgradeRequestPending){
        upgradeRequestPending = true;
        $.get('api/admin/upgrade/doUpgrade',function(data){
            $("#upgrade-process").html(data.message);
            if(data.finish){
                clearInterval(upgradeTimer);
                finish = true;
            }
            upgradeRequestPending = false;
        })
    }
}

$(function() {
    $('#wizard_verticle').smartWizard({
       onLeaveStep:leaveAStepCallback,
       onFinish:onFinishCallback,
       labelPrevious:_res['labelPrevious'],
       labelNext:_res['labelNext'],
       labelFinish:_res['labelFinish']
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