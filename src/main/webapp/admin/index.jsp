<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="f" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/"+"manage/";

%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="include/menu.jsp"/>
						<div class="page-header">
							<h1>
								控制台
								<small>
									<i class="icon-double-angle-right"></i>
									 查看
								</small>
							</h1>
						</div><!-- /.page-header -->
								 <div class="alert alert-block alert-success">
									<button data-dismiss="alert" class="close" type="button">
										<i class="icon-remove"></i>
									</button>

									<i class="icon-ok green"></i>

									欢迎使用
									<strong class="green">
										zrlog 日志管理系统
										<small>(v${zrlog.version})</small>
									</strong>
									,轻量级好用的日志管理系统.
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
