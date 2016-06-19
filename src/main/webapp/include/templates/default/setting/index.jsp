<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<script src="assets/js/jquery.liteuploader.min.js"></script>
<script>
	$(document).ready(function() {
		$('#id-input-file-1').ace_file_input({
			no_file : '${_res.avatar}',
			btn_choose : '文件',
			btn_change : '选择',
			droppable : false,
			onchange : null,
			thumbnail : true
		//| true | large
		//whitelist:'gif|png|jpg|jpeg'
		});

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
	<h1>
		页面设置
	</h1>
</div>
<!-- /.page-header -->
<!-- PAGE CONTENT BEGINS -->
<div class="row">
<div class="col-xs-12">
<form role="form" method="post" action="admin/template/setting" class="form-horizontal">
    <input type="hidden" id="logo" name="avatar" value="${_res.avatar}">
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
        <div class="col-sm-6">
            <div class="ace-file-input">
                <input type="file" name="imgFile" id="id-input-file-1"
                    class="fileUpload icon-upload-alt" value="上传" />
            </div>
        </div>
    </div>
    <div class="space-4"></div>
    <div class="clearfix form-actions">
        <div class="col-md-offset-3 col-md-9">
            <button type="submit" class="btn btn-info">
                <i class="icon-ok bigger-110"></i> 提交
            </button>
        </div>
    </div>
</form>
</div>
</div>