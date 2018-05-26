var timer;
var upgradeTimer;
var downloadSuccess;
var checkRestartTimer;
var finish;
var downloadRequestPending = false;
var upgradeRequestPending = false;
var buildId;

function status() {
    if (!downloadRequestPending) {
        downloadRequestPending = true;
        $.get('api/admin/upgrade/download', function (data) {
            $("#downloadFileProcessBar").attr("aria-valuenow", data.process + "%");
            $("#downloadFileProcessBar").width(data.process + "%");
            $("#downloadFileProcessBar").text(data.process + "%");
            if (data.process == 100) {
                downloadSuccess = true;
                clearInterval(timer);
                $("#processbar-title").text("更新包下载完成");
            }
            downloadRequestPending = false;
        })
    }
}

function upgrade() {
    if (!upgradeRequestPending) {
        upgradeRequestPending = true;
        $.get('api/admin/upgrade/doUpgrade', function (data) {
            $("#upgrade-process").html(data.message);
            if (data.finish) {
                clearInterval(upgradeTimer);
                finish = true;
                buildId = data.buildId;
                checkRestartTimer = setInterval('checkRestartSuccess()', 500);
            }
            upgradeRequestPending = false;
        })
    }
}

var checkRequestPadding = false;

function checkRestartSuccess() {
    if (!checkRequestPadding) {
        checkRequestPadding = true;
        $.get('api/admin/website/version', function (data) {
            checkRequestPadding = false;
            if (data.buildId == buildId) {
                clearInterval(checkRestartTimer);
                var ok = confirm("更新成功，跳转到管理首页？")
                if (ok) {
                    location.href = 'admin';
                }
            }
        }).fail(function (jqXHR, textStatus, errorThrown) {
            checkRequestPadding = false;
        })
    }
}

$(function () {
    $('#wizard_verticle').smartWizard({
        onLeaveStep: leaveAStepCallback,
        onFinish: onFinishCallback,
        labelPrevious: _res['labelPrevious'],
        labelNext: _res['labelNext'],
        labelFinish: _res['labelFinish']
    });

    function leaveAStepCallback(obj, context) {
        if (context.fromStep == 1) {
            $("#processbar-title").text("更新包下载中");
            $("#progress").show();
            timer = setInterval("status()", 500);
            return true;
        }
        if (context.fromStep == 2) {
            upgradeTimer = setInterval("upgrade()", 500);
            if (downloadSuccess) {
                return true;
            }
        }
    }

    function onFinishCallback(objs, context) {
        if (finish) {
            location.href = location.href;
        }
    }

    $('.buttonNext').addClass('btn btn-success').text(lang.labelNext);
    $('.buttonPrevious').addClass('btn btn-primary').text(lang.labelPrevious);
    $('.buttonFinish').addClass('btn btn-info').text(lang.labelFinish);
});