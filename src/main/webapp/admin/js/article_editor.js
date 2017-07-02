var change=0;
var uploadUrl='api/admin/upload/';
var mdEditor;
$(function(){
    $(".select2_single").select2({
      minimumResultsForSearch: -1,
      allowClear: true
    });
    function zeroPad(num, places) {
      var zero = places - num.toString().length + 1;
      return Array(+(zero > 0 && zero)).join("0") + num;
    }

	var editormdTheme = $("#markdown").attr("editormdTheme");
	var dark = editormdTheme == 'dark';
	mdEditor = editormd("editormd", {
		width: "100%",
		height: 400,
		path : editorMdPath,
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
		imageFormats : ["zip","rar","tar.gz","jpg", "jpeg", "gif", "png","ico", "bmp", "webp"],
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

    function checkPreviewLink(){
        if($("#logId").val()==null || $("#logId").val()==''){
            $("#preview").attr("disable","disable");
        }else{
            updatePreviewLink($("#logId").val());
        }
    }

    checkPreviewLink();

    function updatePreviewLink(id){
        $("#preview-link").attr("href","admin/article/preview?id="+id);
        $("#preview").removeClass("btn-")
        $("#preview-link").show();
    }

    function tips(data,message){
        if(data.error==0){
            new PNotify({
                  title: message,
                  type: 'success',
                  hide: true,
                  delay:3000,
                  styling: 'bootstrap3'
              });
            $("#logId").val(data.logId);
            $("#alias").val(data.alias);
            updatePreviewLink(data.logId);
        }else{
            new PNotify({
                  title: data.message,
                  type: 'error',
                  hide: true,
                  delay:3000,
                  styling: 'bootstrap3'
              });
        }
    }

	function validationPost(){
		$("input[name='table-align']").remove();
		$("#content").val(mdEditor.getPreviewedHTML());
		if($("#title").val()=="" || $("#content").val()==""){
			new PNotify({
				  title: '文章的标题和内容都不能为空...',
                  delay:3000,
				  type: 'warn',
				  hide: true,
				  styling: 'bootstrap3'
			  });
			return false;
		}
		return true;
	}

	function autoSave(){
		if(change && validationPost()){
			$.post('api/admin/article/createOrUpdate?rubbish=1',$('#article-form').serialize(),function(data){
                var date=new Date();
                tips(data,"自动保存成功 "+zeroPad(date.getHours(),2)+":"+zeroPad(date.getMinutes(),2) +":"+zeroPad(date.getSeconds(),2))
			});
		}
		change = 0;
	}
	setInterval(autoSave,1000*6);

	var $tags=$("#inp");
	$(".tag2").click(function(e){
		$tags.val($tags.val()+$(this).text()+",");
		$(this).remove();
		e.preventDefault();
	});

	$("#saveToRubbish").click(function(){
		if(validationPost()){
			$.post('api/admin/article/createOrUpdate?rubbish=1',$('#article-form').serialize(),function(data){
                tips(data,"保存成功...")
			});
		}
	});

	$("#createOrUpdate").click(function(){
		if(validationPost()){
			$.post('api/admin/article/createOrUpdate',$('#article-form').serialize(),function(data){
				tips(data,"保存成功...")
			});
		}
	});
});
