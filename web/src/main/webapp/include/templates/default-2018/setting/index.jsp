<%@ page session="false" pageEncoding="UTF-8" %>
<script src="${basePath}assets/js/jquery.liteuploader.min.js"></script>
<script src="${basePath}admin/js/set_update.js"></script>
<script>
    $(document).ready(function () {
        var id = "";
        $('.fileUpload').liteUploader({
            script: 'api/admin/upload?dir=image'
        }).on('lu:success', function (e, response) {
            var id = "#" + $(this).attr("id") + '-field';
            $(id).attr("data-title", response.url);
            $(id).val(response.url);
        });
        $("#template").click(function (e) {
            $.post('api/admin/template/setting', $("#template-form").serialize(), function (data) {
                if (data.error == 0) {
                    var message;
                    if (data.message != null && data.message != '') {
                        message = data.message;
                    } else {
                        message = "操作成功...";
                    }
                    new PNotify({
                        title: message,
                        type: 'success',
                        delay: 3000,
                        hide: true,
                        styling: 'bootstrap3'
                    });
                } else {
                    var message;
                    if (data.message != null && data.message != '') {
                        message = data.message;
                    } else {
                        message = "发生了一些异常...";
                    }
                    new PNotify({
                        title: message,
                        delay: 3000,
                        type: 'error',
                        hide: true,
                        styling: 'bootstrap3'
                    });
                }
            });
        });
    });
</script>
<style>
    input {
        height: 34px;
        border-radius: 4px;
        border: 1px solid #ccc;
    }

    input[type="file"] {
        display: none;
    }

    .custom-file-upload {
        border: 1px solid #ccc;
        border-radius: 4px;
        display: inline-block;
        padding: 6px 12px;
        cursor: pointer;
    }
</style>
<form role="form" method="post" class="form-horizontal">
    <input type="hidden" name="template" value="${template}">

    <div class="form-group">
        <label
                class="col-sm-3 control-label no-padding-right"> 底部 Slogan </label>

        <div class="col-sm-6">
            <input type="text" name="footerSlogan" value="${_res.footerSlogan}" class="form-control"
                   placeholder="">
        </div>
    </div>

    <div class="form-group">
        <label
                class="col-sm-3 control-label no-padding-right">
            关于页 </label>
        <div class="col-sm-6">
            <input class="form-control" name="aboutPage" value="${_res.aboutPage}">
        </div>
    </div>

    <div class="form-group">
        <label
                class="col-sm-3 control-label no-padding-right">
            头像 &nbsp;</label>
        <div class="col-md-9">
            <input class="col-md-6" id="avatar-field" name="avatar" value="${_res.avatar}">
            <label for="avatar" class="custom-file-upload">
                ${_res['upload']}
            </label>
            <input type="file" class="col-md-6 fileUpload icon-upload-alt" id="avatar"
                   name="imgFile" value="上传"/>
        </div>
    </div>

    <div class="ln_solid"></div>
    <div class="form-group">
        <div class="col-md-offset-3 col-md-9">
            <button type="button" id="template" class="btn btn-info">
                <i class="fa fa-check bigger-110"></i> 提交
            </button>
        </div>
    </div>
</form>