<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="include/menu.jsp"/>
<script src="assets/js/jquery.liteuploader.min.js"></script>
<script type="text/javascript" src="assets/js/eModal.min.js"></script>
<script type="text/javascript">
	function open2(id,catalog,title){
		var options = {
			"url":"admin/log/editFrame?logId="+id,
			"title":decodeURI(title),
			"size":"edit"
		}
		eModal.iframe(options);
	}
</script>
<script>
	$(document).ready(function() {
		$('#id-input-file-3').ace_file_input({
			style:'well',
			btn_choose:'${_res.dropableUpload}',
			btn_change:null,
			no_icon:'icon-cloud-upload',
			droppable:true,
			thumbnail:'small'//large | fit
			//,icon_remove:null//set null, to hide remove/reset button
			/**,before_change:function(files, dropped) {
				//Check an example below
				//or examples/file-upload.html
				return true;
			}*/
			/**,before_remove : function() {
				return true;
			}*/
			,
			preview_error : function(filename, error_code) {
				//name of the file that failed
				//error_code values
				//1 = 'FILE_LOAD_FAILED',
				//2 = 'IMAGE_LOAD_FAILED',
				//3 = 'THUMBNAIL_FAILED'
				//alert(error_code);
			}

		}).on('change', function(){
			//console.log($(this).data('ace_input_files'));
			//console.log($(this).data('ace_input_method'));
		});
	});
</script>

	<div class="page-header">
		<h1>
			主题
			<small>
				<i class="icon-double-angle-right"></i>
				管理主题
			</small>
		</h1>
	</div>
	<div class="row">
	<div class="col-xs-12">
		<div class="table-responsive">
			<table class="table table-striped table-bordered table-hover" id="sample-table-1">
				<thead>
					<tr>
						<th>主题路径</th>

						<th class="hidden-480">名称</th>

						<th>
							<i class="icon-user bigger-110 hidden-480"></i>
							作者
						</th>
						<th>
							简介
						</th>

						<th>版本</th>
						<th>编辑</th>
						<th>预览</th>
						<th>应用</th>
					</tr>
				</thead>

				<tbody>
					<c:forEach items="${templates}" var="template">
					 <tr>
						<td>
						<c:choose>
							<c:when test="${template.template eq webs.template}">
								<b> ${template.template} 使用中 </b>
							</c:when>
							<c:otherwise>
								${template.template}
							</c:otherwise>
						</c:choose>
						</td>
						<td>${template.name }</td>
						<td><a href="${template.url }" target="_blank">${template.author }</a></td>
						<td>${template.digest }</td>
						<td>${template.version }</td>

						<td>
							<div class="visible-md visible-lg btn-group">
								<a href="admin/black?menu=1&include=editor/edit&path=${template.template}&editType=主题">
								<button class="btn btn-xs">
									<i class="icon-pencil bigger-120"></i>
								</button>
								</a>

							</div>
						</td>
						<td>
							<div class="visible-md visible-lg btn-group">
								<a target="_blank" href="admin/template/preview?template=${template.template}&resultType=html">
								<button class="btn btn-xs btn-primary">
									<i class="icon-eye-open bigger-120"></i>
								</button>
								</a>
							</div>
						</td>

						<td>
							<div class="visible-md visible-lg btn-group">
								<a href="admin/template/apply?template=${template.template}&resultType=html">
								<button class="btn btn-xs btn-success">
									<i class="icon-cog bigger-120"></i>
								</button>
								</a>

							</div>
						</td>
					</tr>

					</c:forEach>
				</tbody>
			</table>
		</div><!-- /.table-responsive -->
	<div class="ace-file-input ace-file-multiple">
	<input multiple="" type="file" id="id-input-file-3">
	</div>
	<a><button class="btn btn-info"><i class="icon-upload"></i>本地上传</button></a>
	<a href="admin/template_center"><button class="btn btn-info"><i class="icon-cloud-download"></i>下载</button></a>
	</div><!-- /span -->
	</div>
</div><!-- /row -->
<jsp:include page="include/footer.jsp"/>