<%@ page session="false" pageEncoding="UTF-8" %>
<script src="${basePath}assets/js/jquery.liteuploader.min.js"></script>
<script src="${basePath}admin/js/set_update.js"></script>
<script>
    $(document).ready(function () {
        $('.fileUpload').liteUploader({
            script: 'api/admin/upload?dir=image'
        }).on('lu:success', function (e, response) {
            var id = "#" + $(this).attr("id") + '-field';
            $(id).attr("data-title", response.url);
            $(id).val(response.url);
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
                ${_res['submit']}
            </button>
        </div>
    </div>
</form>