<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="f" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/"+"manage/";

%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="include/menu.jsp"/>

					<div id="ace-settings-container" class="ace-settings-container">
						<div id="ace-settings-btn" class="btn btn-app btn-xs btn-warning ace-settings-btn">
							<i class="icon-cog bigger-150"></i>
						</div>

						<div id="ace-settings-box" class="ace-settings-box">
							<div>
								<div class="pull-left">
									<select class="hide" id="skin-colorpicker" style="display: none;">
										<option value="#438EB9" data-skin="default">#438EB9</option>
										<option value="#222A2D" data-skin="skin-1">#222A2D</option>
										<option value="#D0D0D0" data-skin="skin-3">#D0D0D0</option>
									</select>
								</div>
								<span>&nbsp; 选择皮肤&nbsp; </span>
							</div>

							<div>
								<input type="checkbox" <c:if test="${webs.admin_dashboard_inside_container ne ''}">checked="checked"</c:if> id="ace-settings-add-container" class="ace ace-checkbox-2">
								<label for="ace-settings-add-container" class="lbl">
									切换窄屏
								</label>
							</div>
						</div>
					</div>
						<div class="page-header">
							<h1>
								控制台
								<small>
									<i class="icon-double-angle-right"></i>
									 查看
								</small>
							</h1>
						</div><!-- /.page-header -->
								 <div class="alert alert-block alert-info">
									<i class="icon-info">&nbsp;</i>
									欢迎使用
									<strong>
										zrlog 日志管理系统<small>(v${zrlog.version})</small> - 可能是最好用的开源Java博客系统.
									</strong>

								</div>
								<table id="sample-table-1" class="table table-striped table-bordered table-hover">
												<thead>
													<tr>
														<th class="hidden-480">名称</th>

														<th>
															值
														</th>
													</tr>
												</thead>
												
												<tbody>
													 <tr>
														<td>
															运行环境
														</td>
														<td>${system['java.vm.name']}</td>
													</tr>
													 <tr>
														<td>
															运行环境版本
														</td>
														<td>${system['java.runtime.version']}</td>
													</tr>
													<tr>
														<td>
															运行路径
														</td>
														<td>${system['zrlog.runtime.path']}</td>
													</tr>
													<tr>
														<td>
															虚拟机提供者
														</td>
														<td>${system['java.vm.vendor']}</td>
													</tr>
													<tr>
														<td>
															操作系统
														</td>
														<td>${system['os.name']} - ${system['os.arch']} - ${system['os.version']}</td>
													</tr>
													<tr>
														<td>
															操作系统时区
														</td>
														<td>${system['user.timezone']}</td>
													</tr>
													<tr>
														<td>
															操作系统语言
														</td>
														<td>${system['user.language']}</td>
													</tr>
													
															 
												</tbody>
											</table>
								
								<div class="infobox infobox-green  ">
										<div class="infobox-icon">
											<i class="icon-comments"></i>
										</div>

										<div class="infobox-data">
											<span class="infobox-data-number">${session.commCount }</span>
											<div class="infobox-content">${session.toDayCommCount}个评论</div>
										</div>
										<div class="stat stat-success"><f:formatNumber value="${session.toDayCommCount/(session.commCount-session.toDayCommCount)}" type="percent"/></div>
									</div>
									<div class="infobox infobox-orange2  ">
											<div class="infobox-data">
												<span class="infobox-data-number">${clickCount }</span>
												<div class="infobox-content">页面查看次数</div>
											</div>
											 
										</div>
<jsp:include page="include/footer.jsp"/>
