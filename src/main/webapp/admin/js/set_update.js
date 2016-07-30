
	$(function(){
		$(".btn-info").click(function(){
			var formId=$(this).attr("id")+"Ajax";
			if($("#"+formId).attr("checkBox")){
				var checkBoxName=$("#"+formId).attr("checkBox");
				if($("[name='"+checkBoxName+"']").size() && $("[name='"+checkBoxName+"']")[0].checked==true){
					$("#"+checkBoxName).attr("name",checkBoxName).attr("value","on");
				}
				else{
					$("#"+checkBoxName).attr("name",checkBoxName).attr("value","off");
				}
				
			}
			var uri;
			if($("#"+formId).attr("action")!=null){
				uri = $("#"+formId).attr("action");
			}else{
				uri = 'admin/website/update'
			}
			$.post(uri,$("#"+formId).serialize(),function(data){
				if(data.success){
					$.gritter.add({
						title: '  操作成功...',
						class_name: 'gritter-success' + (!$('#gritter-light').get(0).checked ? ' gritter-light' : ''),
					});
					location.href = location.href;
				}else{
					$.gritter.add({
						title: '  发生了一些异常...',
						class_name: 'gritter-error' + (!$('#gritter-light').get(0).checked ? ' gritter-light' : ''),
					});
				}
			});
		});
	});