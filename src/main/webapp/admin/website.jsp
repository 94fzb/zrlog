<%@page import="java.io.File"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<jsp:include page="include/menu.jsp" />
<script src="assets/js/ace-elements.min.js"></script>
<script type="text/javascript" src="${url}/admin/js/set_update.js"></script>
<script src="assets/js/jquery.liteuploader.min.js"></script>
<script>
	$(document).ready(function() {
		$('#id-input-file-1').ace_file_input({
			no_file : '${webs.logo }',
			btn_choose : '文件',
			btn_change : '',
			droppable : false,
			onchange : null,
			thumbnail : true
		//| true | large
		//whitelist:'gif|png|jpg|jpeg'
		});

		$('.fileUpload').liteUploader({
			script : '${url}/admin/log/upload?dir=image'
		}).on('lu:success', function(e, response) {
			$('.file-name').attr("data-title", response.url)
			$("#logo").val(response.url);
			$("a .remove").remove();
		});
	});
</script>

<div class="page-header">
	<h1>网站信息设置</h1>
</div>
<!-- /.page-header -->
<div class="tabbable tabs-left">
	<ul class="nav nav-tabs">
		<li class="active"><a href="#basic" data-toggle="tab">基本信息</a></li>
		<li><a href="#other" data-toggle="tab">其他信息</a></li>
		<li><a href="#errorPageEdit" data-toggle="tab">错误页面编辑</a></li>
		<li><a href="#blackList-tab" data-toggle="tab">IP 黑名单</a></li>
	</ul>
	<div class="tab-content">
		<div class="tab-pane in active" id="basic" style="padding: 10px">
			<h4 class="header blue">认真输入，有助于网站被收录</h4>
			<form role="form" class="form-horizontal" id="ajaxbaseMsg">
				<input type="hidden" id="logo" name="logo" value="${webs.logo }">
				<div class="form-group">
					<label for="form-field-1"
						class="col-sm-3 control-label no-padding-right"> 网站标题 </label>

					<div class="col-sm-9">
						<input type="text" name="title" value="${webs.title }"
							class="col-xs-10 col-sm-5" placeholder="请求输入网站标题 "
							id="form-field-1">
					</div>
				</div>

				<div class="form-group">
					<label for="form-field-1"
						class="col-sm-3 control-label no-padding-right"> 网站副标题 </label>

					<div class="col-sm-9">
						<input type="text" name="second_title"
							value="${webs.second_title }" class="col-xs-10 col-sm-5"
							placeholder="请求输入网站副标题 " id="form-field-1">

					</div>
				</div>
				<div class="form-group">
					<label for="form-field-1"
						class="col-sm-3 control-label no-padding-right"> 网站关键词 </label>

					<div class="col-sm-9">
						<input type="text" name="keywords" value="${webs.keywords}"
							class="col-xs-10 col-sm-5" placeholder="请求输入网站关键词 "
							id="form-field-1">
					</div>
				</div>

				<div class="space-4"></div>

				<div class="form-group">
					<label for="form-field-2"
						class="col-sm-3 control-label no-padding-right"> 网站描述 </label>

					<div class="col-sm-9">
						<textarea name="description" class="col-xs-10" rows="5"
							placeholder="" id="form-field-2">${webs.description}</textarea>
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
						<button id="baseMsg" type="button" class="btn btn-info">
							<i class="icon-ok bigger-110"></i> 提交
						</button>
					</div>
				</div>

			</form>
		</div>
		<div class="tab-pane" id="other" style="padding: 10px">
			<h4 class="header blue">ICP,网站统计等信息</h4>
			<form role="form" class="form-horizontal" checkBox="pseudo_staticStatus" id="ajaxotherMsg">
				<div class="form-group">
					<label for="form-field-1"
						class="col-sm-3 control-label no-padding-right"> ICP备案信息 </label>

					<div class="col-sm-9">
						<textarea name="icp" class="col-xs-10" cols="30" rows="3"
							id="form-field-1">${webs.icp}</textarea>
					</div>
				</div>

				<div class="form-group">
					<label for="form-field-1"
						class="col-sm-3 control-label no-padding-right"> 网站统计 </label>

					<div class="col-sm-9">
						<textarea name="webCm" class="col-xs-10" cols="30" rows="8"
							id="form-field-1">${webs.webCm}</textarea>
					</div>
				</div>

				<div class="form-group">
					<label for="form-field-1"class="col-sm-3 control-label no-padding-right"> 静态化文章页 </label>

					<div class="col-sm-9">
						<span class="col-sm-1"> <label class="pull-right inline">
								<input type="hidden" id="pseudo_staticStatus" value="off">
								<input type="checkbox" name="pseudo_staticStatus"
								class="ace ace-switch ace-switch-5"
								<c:if test="${webs.pseudo_staticStatus eq 'on'}">checked="checked"</c:if>
								id="gritter-light" value="off"> <span class="lbl"></span>
						</label>
						</span>
					</div>
				</div>


				<div class="space-4"></div>

				<div class="clearfix form-actions">
					<div class="col-md-offset-3 col-md-9">
						<button id="otherMsg" type="button" class="btn btn-info">
							<i class="icon-ok bigger-110"></i> 提交
						</button>
					</div>
				</div>

			</form>
		</div>
		<div class="tab-pane" id="errorPageEdit" style="padding: 10px">
			<iframe
				src="${url}/admin/black?include=editor/edit&path=/error/&editType=错误页面"
				scrolling="no" style="border: 0px;" width="100%" height="600px">
			</iframe>
		</div>
		<div class="tab-pane" id="blackList-tab" style="padding: 10px">
			<form role="form" class="form-horizontal" id="ajaxblackList">
				<h4 class="header blue">IP 黑名单</h4>
				<div class="form-group">
					<label for="form-field-1"
						class="col-sm-1 control-label no-padding-right"> </label>

					<div class="col-sm-11">
						<textarea name="blackList" class="col-xs-10" cols="30" rows="8"
							id="form-field-1">${webs.blackList}</textarea>
					</div>
				</div>
				<div class="space-4"></div>
				<div class="clearfix form-actions">
					<div class="col-md-offset-3 col-md-9">

						<button id="blackList" type="button" class="btn btn-info">
							<i class="icon-ok bigger-110"></i> 提交
						</button>
					</div>
				</div>
			</form>
		</div>
	</div>
</div>
<jsp:include page="include/footer.jsp" />