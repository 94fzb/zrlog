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
<form role="form" id="templateAjax" method="post" action="api/admin/template/config" class="form-horizontal">
    <input type="hidden" name="template" value="${template}">

    <div class="form-group row">
        <label
                class="col-sm-3 control-label no-padding-right"> 底部 Slogan </label>

        <div class="col-sm-6">
            <input type="text" name="footerSlogan" value="${_res.footerSlogan}" class="form-control"
                   placeholder="">
        </div>
    </div>

    <div class="form-group row">
        <label class="col-sm-3 control-label no-padding-right">
            关于页 </label>
        <div class="col-sm-6">
            <input class="form-control" name="aboutPage" value="${_res.aboutPage}">
        </div>
    </div>

    <div class="form-group row">
        <label class="col-sm-3 control-label no-padding-right">头像 &nbsp;</label>
        <div class="col-md-9">
            <input class="col-md-6" id="avatar-field" name="avatar" value="${_res.avatar}">
            <label for="avatar" class="custom-file-upload">
                ${_res['upload']}
            </label>
            <input type="file" class="col-md-6 fileUpload icon-upload-alt" id="avatar" name="imgFile" value="上传"/>
        </div>
    </div>

    <div class="ln_solid"></div>
    <div class="form-group row">
        <div class="col-md-offset-3 col-md-9">
            <button type="button" id="template" class="btn btn-info">
                ${_res['submit']}
            </button>
        </div>
    </div>
</form>
${pageEndTag}