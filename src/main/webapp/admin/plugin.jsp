<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="include/menu.jsp"/>
						<div class="page-header">
							<h1>
								插件
								<small>
									<i class="icon-double-angle-right"></i>
									管理插件
								</small>
							</h1>
						</div><!-- /.page-header -->

							<div class="col-xs-12">
 
										<div class="table-responsive">
											<table class="table table-striped table-bordered table-hover" id="sample-table-1">
												<thead>
													<tr>
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
														<th>安装</th>
														<th>启动</th>
														<th>停止</th>
														<th>卸载</th>
													</tr>
												</thead>

												<tbody>
													<c:forEach items="${plugins}" var="plugin">
													 <tr>

														<td>
															${plugin.plugin}
														</td>
														<td class="hidden-480">${plugin.author }</td>
														<td>${plugin.digest }</td>
														<td>${plugin.version }</td>
														<td>
															<div class="visible-md visible-lg hidden-sm hidden-xs btn-group">
																<a href="${url}/admin/plugin/install?name=${plugin.plugin}&step=config&resultType=html"><button class="btn btn-xs btn-success">
																	<i class="icon-ok bigger-120"></i>
																</button>
																</a>
																 
															</div>
														</td>
														
														<td>
															<div class="visible-md visible-lg hidden-sm hidden-xs btn-group">
																<a href="${url}/admin/plugin/start?name=${plugin.plugin}&resultType=html"><button class="btn btn-xs btn-success">
																	<i class="icon-ok bigger-120"></i>
																</button>
																</a>
																 
															</div>
														</td>
														
														<td>
															<div class="visible-md visible-lg hidden-sm hidden-xs btn-group">
																<a href="${url}/admin/plugin/stop?name=${plugin.plugin}&resultType=html"><button class="btn btn-xs btn-success">
																	<i class="icon-ok bigger-120"></i>
																</button>
																</a>
																 
															</div>
														</td>
														
														<td>
															<div class="visible-md visible-lg hidden-sm hidden-xs btn-group">
																<a href="${url}/admin/plugin/unstall?name=${plugin.plugin}&resultType=html"><button class="btn btn-xs btn-danger">
																	<i class="icon-trash bigger-120"></i>
																</button>
																</a>
																 
															</div>
														</td>
													</tr>
															 
													</c:forEach>
												</tbody>
											</table>
										</div><!-- /.table-responsive -->
									<a href="${url }/admin/plugin_center"><button class="btn btn-info">下载</button></a>
									</div><!-- /span -->
	<div style="display:none"></div>
	<body> 
</html>