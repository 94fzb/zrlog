<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/"+"admin/";
request.setAttribute("url", request.getScheme()+"://"+request.getHeader("host")+request.getContextPath());
request.setAttribute("suburl", request.getRequestURL().substring(basePath.length()));
%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<link rel="stylesheet" href="${url }/admin/markdown/css/editormd.min.css" />
<link rel="stylesheet" href="${url}/assets/css/jquery.gritter.css" />
<script src="${url}/admin/markdown/js/editormd.min.js"></script>
<script src="${url}/assets/js/jquery.gritter.min.js"></script>
<script src="${url}/assets/js/bootbox.min.js"></script>
<script>
	var change=0;
	var uploadUrl='${url}/admin/log/upload';
	var mdEditor;
	$(function(){
		mdEditor = editormd("editormd", {
			width: "100%",
			height: 400,
			path : '${url}/admin/markdown/lib/',
			codeFold : true,
			appendMarkdown :$("#markdown").val(),
			saveHTMLToTextarea : true,
			searchReplace : true,
			htmlDecode : "iframe,pre",
			emoji : true,
			taskList : true,
			tocm            : true,         // Using [TOCM]
			tex : true,                   // 开启科学公式TeX语言支持，默认关闭
			flowChart : true,             // 开启流程图支持，默认关闭
			sequenceDiagram : true,       // 开启时序/序列图支持，默认关闭,
			dialogMaskOpacity : 0,    // 设置透明遮罩层的透明度，全局通用，默认值为0.1
			dialogMaskBgColor : "#000", // 设置透明遮罩层的背景颜色，全局通用，默认为#fff
			imageUpload : true,
			imageFormats : ["jpg", "jpeg", "gif", "png", "bmp", "webp"],
			imageUploadURL : uploadUrl,
			onchange : function() {
				change=1;
			},
			 onload : function() {
				$("#content").val(mdEditor.getPreviewedHTML());
				var keyMap = {
					"Ctrl-S": function(cm) {
						change=1;
						autoSave();
					}
				}
				this.addKeyMap(keyMap);
			 },

		});
		$(".editormd-markdown-textarea").attr("name","mdContent");
		$(".editormd-html-textarea").removeAttr("name");

		function validationPost(){
			$("input[name='table-align']").remove();
			$("#content").val(mdEditor.getPreviewedHTML());
			if($("#title").val()=="" || $("#content").val()==""){
				$.gritter.add({
					title: '文章的标题和内容都不能为空...',
					class_name: 'gritter-error' + (!$('#gritter-light').get(0).checked ? ' gritter-light' : ''),
				});
				return false;
			}
			return true;
		}

		function autoSave(){
			if(change && validationPost()){
				$.post('${url}/admin/log/createOrUpdate?rubbish=1',$('#addorPre').serialize(),function(data){
					if(data.add || data.update){
						var date=new Date();
						$.gritter.add({
							title: "自动保存成功 "+date.getHours()+":"+date.getMinutes() +" "+date.getSeconds(),
							class_name: 'gritter-success' + (!$('#gritter-light').get(0).checked ? ' gritter-light' : '')
						});
						$("#logId").val(data.logId);
						$("#alias").val(data.alias);
					}
				});
			}
			change = 0;
		}
		setInterval(autoSave,1000*6);

		var $tags=$("#inp");
		$(".tag").click(function(e){
			$tags.val($tags.val()+$(this).text()+",");
			$(this).remove();
			e.preventDefault();
		});
		$("#translate").click(function(){
			$.post('${url}/post/api/translate',{"key":$("#title").val()},function(data){
				var d=$.parseJSON(data);
				if($("#result").length==0){
					$("#translate").after("<input name='alias' id='result' value='"+d.translate+"'>");
				}
				else{
					$("#result").attr("value",d.translate);
				}
			});
		});

		$("#saveToRubbish").click(function(){
			if(validationPost()){
				$.post('${url}/admin/log/createOrUpdate?rubbish=1',$('#addorPre').serialize(),function(data){
					if(data.add || data.update){
						$.gritter.add({
							title: '保存成功...',
							class_name: 'gritter-info' + (!$('#gritter-light').get(0).checked ? ' gritter-light' : ''),
						});
						$("#logId").val(data.logId);
						$("#alias").val(data.alias);
					}
				});
			}
		});

		$("#preview").click(function(){
			if(validationPost()){
				document.getElementById("addorPre").submit();
			}
		});

		$("#createOrUpdate").click(function(){
			if(validationPost()){
				$.post('${url}/admin/log/createOrUpdate',$('#addorPre').serialize(),function(data){
					if(data.add || data.update){
						$.gritter.add({
							title: '更新成功...',
							class_name: 'gritter-info' + (!$('#gritter-light').get(0).checked ? ' gritter-light' : ''),
						});
						$("#logId").val(data.logId);
						$("#alias").val(data.alias);
					}
				});
			}
		});

		$("#reset").click(function(){
			bootbox.setDefaults({locale:"zh_CN"});
			bootbox.alert("这将会清空你输入的", function() {
				$('#title').val("");
				$('#tag').val("");
				mdEditor.markdown="";
			});
		});
	});
</script>

<form target="_blank" class="form-horizontal" role="form" id="addorPre" method="post" action="${url }/admin/log/preview">
	<textarea id="markdown" style="display: none;">${log.mdContent}</textarea>
	<input type="hidden" id="logId" name="logId" value="${log.logId}">
	<textarea id="content" name="content" style="display: none;">${log.content}</textarea>
	<input name="title" id="title" size="60" maxlength="60"  value="${log.title}" class="col-xs-7" type="text" placeholder="请输入文章标题"></input>
	<div class="col-xs-2">
	<select name="typeId" id="form-field-select-6" class="form-control">
	  <c:forEach items="${init.types}" var="type">
		<option <c:if test="${type.id eq log.typeId}">selected="selected"</c:if> value="${type.id}">${type.typeName}</option>
	  </c:forEach>
	</select>
	</div>
	<input id="alias" type="text" class="col-xs-3"  placeholder="请输入别名"  name="alias" value="${log.alias}">
	<div class="col-sm-12" style="z-index: 9999">
		<div id="editormd"></div>
	</div>
	<hr/>
	<input value="${log.keywords}" class="col-xs-7" type="text" name="keywords" id="inp" size="60" maxlength="60" /><hr/>
	<div class="tags" id="tag" style="width: 100%">
	<c:forEach items="${init.tags}" var="tags">
		<span class="tag">${tags.text}</span>
	</c:forEach>
	</div>
	<hr/>
	<div class="col-xs-4">
		<label>
			<input class="ace ace-switch ace-switch-6" type="checkbox" checked="checked" name="canComment">
			<span class="lbl">&nbsp;发布评论</span>
		</label>
	</div>
	<div class="col-xs-4">
		<label>
			<input class="ace ace-switch ace-switch-6" type="checkbox" <c:if test="${log.recommended}">checked="checked"</c:if>  name="recommended">
			<span class="lbl">&nbsp;推荐</span>
		</label>
	</div>
	<div class="col-xs-4">
		<label>
			<input class="ace ace-switch ace-switch-6" type="checkbox" <c:if test="${log['private']}">checked="checked"</c:if> name="private">
			<span class="lbl">&nbsp;不公开</span>
		</label>
	</div>
	<br/>
	<hr/>
	<div class="widget-box collapsed">
		<div data-action="collapse" class="widget-header widget-header-small  header-color-blue">
			<div  class="widget-toolbar inline">
				<a href="#"> <i class="icon-chevron-down"></i>
				</a>
			</div>
		</div>

		<div class="widget-body">
			<div class="widget-body-inner" style="display: none;">
				<textarea name="digest" cols="100" rows="8" style="width:100%; height:180x; z-index: 9999;">${log.digest}</textarea>
			</div>
		</div>
	</div>
	<div class="clearfix form-actions">
		<div class="col-md-offset-3 col-md-9">
			<button class="btn btn-info" id="saveToRubbish" type="button">
				<i class="icon-ok bigger-110"></i>
				存为草稿
			</button>
			<button class="btn btn-info" id="createOrUpdate" type="button">
				<i class="icon-ok bigger-110"></i>
				提交
			</button>
			<button class="btn" type="button" id="preview">
				<i class="icon-zoom-in bigger-110"></i>
				预览
			</button>
		</div>
	</div>
</form>