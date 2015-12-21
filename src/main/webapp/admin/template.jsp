<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="include/menu.jsp"/>
						<div class="page-header">
							<h1>
								模板
								<small>
									<i class="icon-double-angle-right"></i>
									管理模板
								</small>
							</h1>
						</div>
						<div class="col-xs-12">
							<div class="table-responsive">
								<table class="table table-striped table-bordered table-hover" id="sample-table-1">
									<thead>
										<tr>
											<th>模板地址</th>
											
											<th class="hidden-480">名称</th>

											<th>
												<i class="icon-time bigger-110 hidden-480"></i>
												作者
											</th>
											<th>
												<i class="icon-time bigger-110 hidden-480"></i>
												简介
											</th>

											<th>版本</th>
											<th>编辑</th>
											<th>应用</th>
										</tr>
									</thead>

									<tbody>
										<c:forEach items="${templates}" var="template">
										 <tr>

											<td>
												${template.template}
											</td>
											<td>${template.name }</td>
											<td class="hidden-480">${template.author }</td>
											<td>${template.digest }</td>
											<td>${template.version }</td>

											<td>
												<div class="visible-md visible-lg hidden-sm hidden-xs btn-group">
													<a href="${url}/admin/black?menu=1&include=plugins/file/edit&path=${template.template}&editType=主题"><button class="btn btn-xs btn-success">
														<i class="icon-edit bigger-120"></i>
													</button>
													</a>
													 
												</div>
											</td>
											
											<td>
												<div class="visible-md visible-lg hidden-sm hidden-xs btn-group">
													<a href="${url}/admin/website/update?template=${template.template}&resultType=html"><button class="btn btn-xs btn-success">
														<i class="icon-ok bigger-120"></i>
													</button>
													</a>
													 
												</div>
											</td>
										</tr>
												 
										</c:forEach>
									</tbody>
								</table>
							</div><!-- /.table-responsive -->
						<a href="${url }/admin/template_center"><button class="btn btn-info">下载</button></a>
						</div><!-- /span -->
					</div><!-- /row -->
	<div style="display:none"></div>
	<body> 
</html>