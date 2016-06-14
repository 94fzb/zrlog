var change=0;
var uploadUrl='admin/log/upload';
var mdEditor;
$(function(){
	var checkedSkin = $("body").get(0).getAttribute("class");
	var dark = (checkedSkin == 'skin-1');
	mdEditor = editormd("editormd", {
		width: "100%",
		height: 400,
		path : 'admin/markdown/lib/',
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
		imageFormats : ["jpg", "jpeg", "gif", "png","ico", "bmp", "webp"],
		imageUploadURL : uploadUrl,
		theme        : dark ? "dark" : "default",
		previewTheme : dark ? "dark" : "default",
		editorTheme  : dark ? "pastel-on-dark" : "default",

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
		 onfullscreen : function() {
			$("#editormd").css("z-index","9999")
		 },

		 onfullscreenExit : function() {
			$("#editormd").css("z-index",0)
		 }

	});
	$(".editormd-markdown-textarea").attr("name","mdContent");
	$(".editormd-html-textarea").removeAttr("name");

	function validationPost(){
		$("input[name='table-align']").remove();
		$("#content").val(mdEditor.getPreviewedHTML());
		if($("#title").val()=="" || $("#content").val()==""){
			$.gritter.add({
				title: '文章的标题和内容都不能为空...',
				class_name: 'gritter-error' + (!$('#gritter-light').get(0).checked ? ' gritter' : ''),
			});
			return false;
		}
		return true;
	}

	function autoSave(){
		if(change && validationPost()){
			$.post('admin/log/createOrUpdate?rubbish=1',$('#addorPre').serialize(),function(data){
				if(data.add || data.update){
					var date=new Date();
					$.gritter.add({
						title: "自动保存成功 "+date.getHours()+":"+date.getMinutes() +" "+date.getSeconds(),
						class_name: 'gritter-success' + (!$('#gritter-light').get(0).checked ? ' gritter' : '')
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
	/*自动翻译功能*/
	/*$("#translate").click(function(){
		$.post('post/api/translate',{"key":$("#title").val()},function(data){
			var d=$.parseJSON(data);
			if($("#result").length==0){
				$("#translate").after("<input name='alias' id='result' value='"+d.translate+"'>");
			}
			else{
				$("#result").attr("value",d.translate);
			}
		});
	});*/

	$("#saveToRubbish").click(function(){
		if(validationPost()){
			$.post('admin/log/createOrUpdate?rubbish=1',$('#addorPre').serialize(),function(data){
				if(data.add || data.update){
					$.gritter.add({
						title: '保存成功...',
						class_name: 'gritter-info' + (!$('#gritter-light').get(0).checked ? ' gritter' : ''),
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
			$.post('admin/log/createOrUpdate',$('#addorPre').serialize(),function(data){
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
