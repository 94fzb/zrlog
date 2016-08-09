<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="include/menu.jsp"/>
<script src="assets/js/jquery.liteuploader.min.js"></script>
<script src="admin/js/template.js"></script>

<div class="page-header">
	<h3>
	${_res['admin.theme.manage']}
	</h3>
</div>
<div class="row">
	<div class="col-sm-9"></div>
<div class="form-group col-sm-3">
	<input type="file" class="fileUpload" name="imgFile" value="上传" />
</div>
<div class="col-xs-12">
	<div class="table-responsive">
		<table class="table table-striped table-bordered table-hover" id="sample-table-1">
			<thead>
				<tr>
					<th>${_res['admin.theme.path']}</th>
					<th class="hidden-480">${_res['admin.theme.name']}</th>
					<th><i class="fa fa-user bigger-110 hidden-480"></i>${_res['admin.theme.user']}</th>
					<th>${_res['admin.theme.digest']}</th>
					<th>${_res['admin.theme.version']}</th>
					<th>${_res['admin.theme.edit']}</th>
					<th>${_res['admin.theme.preview']}</th>
					<th>${_res['admin.theme.setting']}</th>
					<th>${_res['admin.theme.apply']}</th>
				</tr>
			</thead>

			<tbody>
				<c:forEach items="${templates}" var="template">
				 <tr>
					<td>
					<c:choose>
						<c:when test="${template.template eq webs.template}">
							<b> ${template.template}&nbsp;${_res['admin.theme.inUse']} </b>
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
							<a href="admin/blank?menu=1&include=editor/edit&path=${template.template}&editType=${_res['admin.theme']}">
							<button class="btn btn-xs">
								<i class="fa fa-pencil bigger-120"></i>
							</button>
							</a>

						</div>
					</td>
					<td>
						<div class="visible-md visible-lg btn-group">
							<a target="_blank" href="admin/template/preview?template=${template.template}&resultType=html">
							<button class="btn btn-xs btn-primary">
								<i class="fa fa-eye bigger-120"></i>
							</button>
							</a>
						</div>
					</td>
					<td>
						<div class="visible-md visible-lg btn-group">
							<a href="admin/template/configPage?template=${template.template}">
							<button class="btn btn-xs btn-primary">
								<i class="fa fa-cog bigger-120"></i>
							</button>
							</a>

						</div>
					</td>

					<td>
						<div class="visible-md visible-lg btn-group">
							<a href="admin/template/apply?template=${template.template}&resultType=html">
							<button class="btn btn-xs btn-success">
								<i class="fa fa-check bigger-120"></i>
							</button>
							</a>

						</div>
					</td>
				</tr>

				</c:forEach>
			</tbody>
		</table>
	</div><!-- /.table-responsive -->
<a href="admin/template_center"><button class="btn btn-info"><i class="fa fa-cloud-download"></i>${_res['admin.theme.download']}</button></a>
</div><!-- /span -->
</div>
</div><!-- /row -->
<jsp:include page="include/footer.jsp"/>