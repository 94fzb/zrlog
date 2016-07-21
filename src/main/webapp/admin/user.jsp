<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<jsp:include page="include/menu.jsp" />
<script type="text/javascript" src="admin/js/set_update.js"></script>
<script src="assets/js/jquery.liteuploader.min.js"></script>
<script>
	$(document).ready(function() {
		$('#id-input-file-1').ace_file_input({
			no_file : '${session.user.header}',
			btn_choose : '本地上传',
			btn_change : '文件',
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
		信息设置 <small> <i class="icon-double-angle-right"></i> 个人信息设置
		</small>
	</h1>
</div>
<!-- /.page-header -->
<!-- PAGE CONTENT BEGINS -->
<div class="row">
<div class="col-xs-12">
<form role="form" method="post" class="form-horizontal" action="admin/update">
	<input type="hidden" id="logo" name="header" value="${user.header }">
	<div class="form-group">
		<label for="form-field-1"
			class="col-sm-3 control-label no-padding-right"> ${_res.userName} </label>

		<div class="col-sm-9">
			<input type="text" name="userName" value="${user.userName }"
				class="col-xs-10 col-sm-5" placeholder="" id="form-field-1">
		</div>
	</div>

	<div class="form-group">
		<label for="form-field-1"
			class="col-sm-3 control-label no-padding-right"> 邮箱 </label>

		<div class="col-sm-9">
			<input type="text" name="email" value="${user.email }"
				class="col-xs-10 col-sm-5" placeholder="" id="form-field-1">
		</div>
	</div>

	<div class="form-group">
		<label for="form-field-1"class="col-sm-3 control-label no-padding-right">头像</label>
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
			<button id="submit" type="submit" class="btn btn-info">
				<i class="icon-ok bigger-110"></i> 提交
			</button>
		</div>
	</div>

</form>
</div>
</div>
<!-- PAGE CONTENT ENDS -->
<jsp:include page="include/footer.jsp"/>