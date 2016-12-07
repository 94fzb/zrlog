$(function(){
	$('#menu_toggle').on('click', function() {
		var classArr = $("body").get(0).getAttribute("class").split(" ");
		var naver = "nav-md"
		for(var i=0;i<classArr.length;i++){
			if(classArr[i].indexOf("nav")!=-1){
				if(classArr[i] == naver){
					naver = "nav-sm"
				}
				break;
			}
		}
		$.post('api/admin/website/update',{"admin_dashboard_naver":naver},function(data){
			if(data.success){
				//ignore
			}
		});
	});
	$(".language").click(function(e){
		var language = $(this).attr("id");
		$.post('api/admin/website/update',{"language":language},function(data){
			window.location.href=window.location.href
		});
	})
});