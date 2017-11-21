$(document).ready(function () {
    $('.fileUpload').liteUploader({
        script: 'api/admin/template/upload'
    }).on('lu:success', function (e, response) {
        $("#logo").val(response.url);
        location.href = location.href;
    });

    $(".apply-btn").click(function () {
        var template = $(this).attr("template");
        $.post("api/admin/template/apply", {"template": template}, function (e) {
            new PNotify({
                delay: 3000,
                title: '保存成功...',
                type: 'success',
                hide: true,
                styling: 'bootstrap3'
            });
            location.href = location.href;
        });
        return false;
    })

    $(".delete-btn").click(function () {
        if (confirm("是否删除改主题？")) {
            var template = $(this).attr("template");
            $.post("api/admin/template/delete", {"template": template}, function (e) {
                new PNotify({
                    delay: 3000,
                    title: '删除成功...',
                    type: 'success',
                    hide: true,
                    styling: 'bootstrap3'
                });
                location.href = location.href;
            });
        }
        return false;
    })
});