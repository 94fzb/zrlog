<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<jsp:include page="include/menu.jsp" />
<script type="text/javascript" src="${url}/admin/js/set_update.js"></script>
<script src="${url}/assets/js/jquery.liteuploader.min.js"></script>
<script>
	$(document).ready(function() {
		$('#id-input-file-1').ace_file_input({
			no_file : '请选择文件',
			btn_choose : '文件',
			btn_change : '选择',
			droppable : false,
			onchange : null,
			thumbnail : true
		//| true | large
		//whitelist:'gif|png|jpg|jpeg'
		});

		$('.fileUpload').liteUploader({
			script : '${url}/admin/log/upload?dir=images'
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

<div class="col-xs-12">
	<!-- PAGE CONTENT BEGINS -->

	<form role="form" method="post" class="form-horizontal"
		action="${url}/admin/update">
		<input type="hidden" id="logo" name="header" value="${user.header }">
		<div class="form-group">
			<label for="form-field-1"
				class="col-sm-3 control-label no-padding-right"> 登陆用户名 </label>

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
				<button id="submit" type="submit" class="btn btn-info">
					<i class="icon-ok bigger-110"></i> 提交
				</button>

				&nbsp; &nbsp; &nbsp;
				<button type="reset" class="btn">
					<i class="icon-undo bigger-110"></i> 重置
				</button>
			</div>
		</div>

	</form>
	<!-- PAGE CONTENT ENDS -->
</div>
<!-- /.col -->
</div>

<div style="display: none"></div>
<body>
</html>