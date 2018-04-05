$(document).ready(function () {
    $('.fileUpload').liteUploader({
        script: 'api/admin/template/upload'
    }).on('lu:success', function (e, response) {
        if (response.error) {
            notify(response);
        } else {
            reloadPage();
        }

    });

    $(".apply-btn").click(function () {
        var template = $(this).attr("template");
        $.post("api/admin/template/apply", {"template": template}, function (e) {
            e.message = lang.updateSuccess;
            notify(e);
            reloadPage();
        });
        return false;
    });

    $(".delete-btn").click(function () {
        if (confirm("是否删除改主题？")) {
            var template = $(this).attr("template");
            $.post("api/admin/template/delete", {"template": template}, function (e) {
                reloadPage();
            });
        }
        return false;
    })
});