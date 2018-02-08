<%@ page session="false" pageEncoding="UTF-8" %>
<script src="assets/js/jquery.liteuploader.min.js"></script>
<script src="${cacheFile['/admin/js/common.js']}"></script>
<script src="${cacheFile['/admin/js/set_update.js']}"></script>
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
<style>
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
<div class="page-header">
    <h3>
        主题设置
    </h3>
</div>
<!-- /.page-header -->
<!-- PAGE CONTENT BEGINS -->
<div class="row">
    <div class="col-md-6">
        <form id="templateAjax" action="api/admin/template/config" class="form-horizontal">
            <input type="hidden" name="template" value="${template}">
            <div class="form-group">
                <label class="col-sm-3 control-label no-padding-right"> 标题 </label>
                <div class="col-sm-5">
                    <input type="text" name="title" value="${_res.title}" class="form-control col-xs-10 col-sm-5"
                           placeholder="不建议大于10个字符">
                </div>
            </div>
            <div class="form-group">
                <label
                        class="col-sm-3 control-label no-padding-right"> 底部 Slogan </label>

                <div class="col-sm-9">
                    <input type="text" name="footerSlogan" value="${_res.footerSlogan}"
                           class="form-control col-xs-10 col-sm-5" placeholder="放上一句自己喜欢的话语，让更多的人了解你"
                    >
                </div>
            </div>
            <div class="form-group">
                <label class="col-sm-3 control-label no-padding-right">网站&nbsp;Logo </label>
                <div class="col-sm-6">
                    <input id="logo" class="form-control col-sm-10" name="avatar" value="${_res.avatar}">
                </div>
                <div class="col-sm-2">
                    <label for="fileUpload" class="custom-file-upload">
                        ${_res['upload']}
                    </label>
                    <input type="file" id="fileUpload" class="fileUpload icon-upload-alt" name="imgFile" value="上传"/>
                </div>
            </div>
            <div class="ln_solid"></div>
            <div class="form-group">
                <div class="col-md-offset-3 col-md-9">
                    <button id="template" type="button" class="btn btn-info">
                        <i class="icon-check bigger-110"></i> 提交
                    </button>
                </div>
            </div>
        </form>
    </div>
</div>