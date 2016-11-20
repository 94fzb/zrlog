<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="f" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="include/menu.jsp"/>
<div class="page-header">
<h3>
${_res.dashboard}
</h3>
</div><!-- /.page-header -->
 <div class="alert alert-block alert-info">
	<i class="fa fa-info">&nbsp;</i>
	${_res['admin.index.welcomeTips']}
	<small>&nbsp;v${zrlog.version} - ${zrlog.buildId} (${zrlog.buildTime})</small>
</div>
<div class="row">
<div class="col-sm-7">
	<div class="widget-box">
		<div class="widget-header">
			<h4>
				<i class="fa fa-server"></i>
				${_res.serverInfo}
			</h4>
		</div>
		<div class="widget-body">
			<div class="widget-main">
				 <table id="sample-table-1" class="table table-striped table-bordered table-hover">
					<thead>
						<tr>
							<th class="hidden-480">${_res.key}</th>

							<th>
								${_res.value}
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
								J2EE 容器信息
							</td>
							<td>${system['server.info']}</td>
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
								操作系统地域/语言
							</td>
							<td>${system['user.country']}/${system['user.language']}</td>
						</tr>
						<tr>
							<td>
								数据库版本
							</td>
							<td>${system['dbServer.version']}</td>
						</tr>


					</tbody>
				</table>
			</div>
		</div>
	</div>
</div>
<div class="col-sm-5">
	<div class="widget-box">
		<div class="widget-header">
			<h4>
				<i class="fa fa-comments"></i>
				${_res['admin.index.outline']}
			</h4>
		</div>
		<div class="widget-body">
			<div class="widget-main">
			<div class="animated flipInY col-lg-6 col-md-6 col-sm-6 col-xs-12">
				<div class="tile-stats">
				  <div class="icon"><i class="fa fa-comments-o"></i></div>
				  <div class="count">${session.toDayCommCount}</div>
				  <h3>今天评论</h3>
				</div>
			  </div>
			  <div class="animated flipInY col-lg-6 col-md-6 col-sm-6 col-xs-12">
				<div class="tile-stats">
				  <div class="icon"><i class="fa fa-comments-o"></i></div>
				  <div class="count">${session.commCount }</div>
				  <h3>总评论</h3>
				</div>
		</div>
			  <div class="animated flipInY col-lg-6 col-md-6 col-sm-6 col-xs-12">
				<div class="tile-stats">
				  <div class="icon"><i class="fa fa-eye"></i></div>
				  <div class="count">${clickCount }</div>
				  <h3>文章总浏览次数</h3>
				</div>
		</div>
		</div>
		</div>
	</div>
</div>
</div>
<jsp:include page="include/footer.jsp"/>
