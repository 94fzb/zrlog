$(document).ready(function() {
    $('#id-input-file-1').ace_file_input({
        no_file:'',
        btn_choose:'本地上传',
        droppable:false,
        onchange:null,
        whitelist:'zip'
    });
    $('.fileUpload').liteUploader({
        script : 'admin/template/upload'
    }).on('lu:success', function(e, response) {
        $('.file-name').attr("data-title", response.url)
        $("#logo").val(response.url);
        $("a .remove").remove();
        location.href = location.href;
    });
});