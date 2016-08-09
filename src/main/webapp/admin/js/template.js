$(document).ready(function() {
    $('.fileUpload').liteUploader({
        script : 'admin/template/upload'
    }).on('lu:success', function(e, response) {
        $("#logo").val(response.url);
        location.href = location.href;
    });
});