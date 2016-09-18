<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<script src="assets/js/jquery.liteuploader.min.js"></script>
<script>
	$(document).ready(function() {
		$('.fileUpload').liteUploader({
			script : 'admin/log/upload?dir=image'
		}).on('lu:success', function(e, response) {
			$('.file-name').attr("data-title", response.url)
			$("#logo").val(response.url);
			$("a .remove").remove();
		});
	});
</script>
<div class="page-header">
	<h3>
		页面设置
	</h3>
</div>
<!-- /.page-header -->
<!-- PAGE CONTENT BEGINS -->
<div class="row">
<div class="col-xs-12">
<form role="form" method="post" action="admin/template/setting" class="form-horizontal">
    <input type="hidden" name="template" value="${template}">
    <div class="form-group">
        <label for="form-field-1"
            class="col-sm-3 control-label no-padding-right"> 底部 Slogan </label>

        <div class="col-sm-9">
            <input type="text" name="footerSlogan" value="${_res.footerSlogan}"
                class="col-xs-10 col-sm-5" placeholder=""
                id="form-field-1">
        </div>
    </div>

    <div class="form-group">
        <label for="form-field-1"
            class="col-sm-3 control-label no-padding-right">
            网站&nbsp;Logo </label>
        <div class="col-sm-9">
            <input id="logo" class="col-xs-10 col-sm-5" id="form-field-1" name="avatar" value="${_res.avatar}">
            <input type="file" class="col-xs-10 col-sm-5 fileUpload icon-upload-alt" id="form-field-1" name="imgFile" value="上传" />
        </div>
    </div>
    <div class="ln_solid"></div>
    <div class="form-group">
        <div class="col-md-offset-3 col-md-9">
            <button type="submit" class="btn btn-info">
                <i class="icon-check bigger-110"></i> 提交
            </button>
        </div>
    </div>
</form>
</div>
</div>