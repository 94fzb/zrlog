<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<script src="assets/js/jquery.liteuploader.min.js"></script>
<script>
	$(document).ready(function() {
		$('.fileUpload').liteUploader({
			script : 'api/admin/upload?dir=image'
		}).on('lu:success', function(e, response) {
			$('.file-name').attr("data-title", response.url)
			$("#logo").val(response.url);
			$("a .remove").remove();
		});
		$("#template").click(function(e){
		  $.post('api/admin/template/setting',$("#template-form").serialize(),function(data){
              if(data.error == 0){
                  var message;
                  if(data.message!=null && data.message!=''){
                      message = data.message;
                  }else{
                      message = "操作成功...";
                  }
                  new PNotify({
                        title: message,
                        type: 'success',
                        delay:3000,
                        hide: true,
                        styling: 'bootstrap3'
                  });
              }else{
                  var message;
                  if(data.message!=null && data.message!=''){
                      message = data.message;
                  }else{
                      message = "发生了一些异常...";
                  }
                  new PNotify({
                        title: message,
                        delay:3000,
                        type: 'error',
                        hide: true,
                        styling: 'bootstrap3'
                   });
              }
          });
		});
	});
</script>
<div class="page-header">
	<h3>
		主题设置
	</h3>
</div>
<!-- /.page-header -->
<!-- PAGE CONTENT BEGINS -->
<div class="row">
<div class="col-xs-12">
<form id="template-form" class="form-horizontal">
    <input type="hidden" name="template" value="${template}">
    <div class="form-group">
        <label for="form-field-1"
            class="col-sm-3 control-label no-padding-right"> 底部 Slogan </label>

        <div class="col-sm-9">
            <input type="text" name="footerSlogan" value="${_res.footerSlogan}"
                class="form-control col-xs-10 col-sm-5" placeholder=""
                id="form-field-1">
        </div>
    </div>

    <div class="form-group">
        <label for="form-field-1"
            class="col-sm-3 control-label no-padding-right"> 标题 </label>

        <div class="col-sm-9">
            <input type="text" name="title" value="${_res.title}"
                class="form-control col-xs-10 col-sm-5" placeholder=""
                id="form-field-1">
        </div>
    </div>

    <div class="form-group">
        <label
            class="col-sm-3 control-label no-padding-right">
            网站&nbsp;Logo </label>
        <div class="col-sm-7">
            <input id="logo" class="form-control" name="avatar" value="${_res.avatar}">
        </div>
        <div class="col-sm-2">
            <input type="file" class="fileUpload icon-upload-alt" name="imgFile" value="上传" />
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