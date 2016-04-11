<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="include/menu.jsp"/>
			<div class="page-header">
				<h1>
					主题
					<small>
						<i class="icon-double-angle-right"></i>
						管理主题
					</small>
				</h1>
			</div>
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
										<a href="${url}/admin/black?menu=1&include=editor/edit&path=${template.template}&editType=主题">
										<button class="btn btn-xs">
											<i class="icon-pencil bigger-120"></i>
										</button>
										</a>

									</div>
								</td>
								<td>
									<div class="visible-md visible-lg btn-group">
										<a target="_blank" href="${url}/admin/template/preview?template=${template.template}&resultType=html">
										<button class="btn btn-xs btn-primary">
											<i class="icon-zoom-in bigger-120"></i>
										</button>
										</a>
									</div>
								</td>

								<td>
									<div class="visible-md visible-lg btn-group">
										<a href="${url}/admin/template/apply?template=${template.template}&resultType=html">
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
			<a href="${url }/admin/template_center"><button class="btn btn-info"><i class="icon-download"></i>下载</button></a>
			</div><!-- /span -->
		</div><!-- /row -->
		<div style="display:none"></div>
	<body> 
</html>