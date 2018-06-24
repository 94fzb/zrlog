<%@ page session="false" pageEncoding="UTF-8" %>
<script src="${basePath}assets/js/jquery.liteuploader.min.js"></script>
<script src="${basePath}admin/js/set_update.js"></script>
<script>
    $(document).ready(function () {
        $('.fileUpload').liteUploader({
            script: 'api/admin/upload?dir=image'
        }).on('lu:success', function (e, response) {
            $('.file-name').attr("data-title", response.url)
            $("#logo").val(response.url);
            $("a .remove").remove();
        });
    });
</script>
<form id="templateAjax" action="api/admin/template/config" class="form-horizontal">
    <input type="hidden" name="template" value="${template}">
    <div class="form-group row">
        <label class="col-sm-3 control-label no-padding-right"> 标题 </label>
        <div class="col-sm-8">
            <input type="text" name="title" value="${_res.title}" class="form-control col-xs-10"
                   placeholder="不建议大于10个字符">
        </div>
    </div>
    <div class="form-group row">
        <label
                class="col-sm-3 control-label no-padding-right"> 底部 Slogan </label>

        <div class="col-sm-9">
            <input type="text" name="footerSlogan" value="${_res.footerSlogan}"
                   class="form-control col-xs-10" placeholder="放上一句自己喜欢的话语，让更多的人了解你"
            >
        </div>
    </div>
    <div class="form-group row">
        <label class="col-sm-3 control-label no-padding-right">网站&nbsp;Logo </label>
        <div class="col-sm-7">
            <input id="logo" class="form-control col-sm-10" name="avatar" value="${_res.avatar}">
            <label for="fileUpload" class="custom-file-upload">
                ${_res['upload']}
            </label>
            <input type="file" id="fileUpload" class="fileUpload icon-upload-alt" name="imgFile" value="${_res['upload']}"/>
        </div>
    </div>
    <div class="ln_solid"></div>
    <div class="form-group row">
        <div class="col-md-offset-3 col-md-9">
            <button id="template" type="button" class="btn btn-info">
                ${_res['submit']}
            </button>
        </div>
    </div>
</form>
${pageEndTag}