<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
request.setAttribute("url", request.getScheme()+"://"+request.getHeader("host")+request.getContextPath());
%>
<!DOCTYPE html>
<html>
  	<base href="<%=basePath%>">
	<head>
		<meta charset="utf-8" />
		<title>${init.webSite.title} - 请先登录</title>
		<link href="assets/css/bootstrap.min.css" rel="stylesheet" />
		<link rel="stylesheet" href="assets/css/font-awesome.min.css" />
		<link rel="stylesheet" href="assets/css/ace.min.css" />
		<link rel="stylesheet" href="assets/css/ace-rtl.min.css" />
		
		<link rel="shortcut icon" href="${url}/favicon.ico" />
	</head>

	<body class="login-layout">
		<div class="main-container">
			<div class="main-content">
				<div class="row">
					<div class="col-sm-10 col-sm-offset-1">
						<div class="login-container">
							<div class="center">
								<h1>
									<span class="white">${init.webSite.title}</span>
								</h1>
							</div>

							<div class="space-6"></div>
							<div class="position-relative">
								<div id="login-box" class="login-box visible widget-box no-border">
									<div class="widget-body">
										<div class="widget-main">
											<h4 class="header blue lighter bigger">
												<i class="icon-coffee green"></i>
												请输入用户名和密码
											</h4>

											<div class="space-6"></div>

											<form action="${url}/admin/login" method="post">
												<input type="hidden" name="redirectFrom" value="${param.redirectFrom}">
												<fieldset>
													<label class="block clearfix">
														<span class="block input-icon input-icon-right">
															<input type="text" class="form-control" name="userName" placeholder="用户名" />
															<i class="icon-user"></i>
														</span>
													</label>

													<label class="block clearfix">
														<span class="block input-icon input-icon-right">
															<input type="password" class="form-control" name="password" placeholder="密码" />
															<i class="icon-lock"></i>
														</span>
													</label>

													<div class="space"></div>
													<div class="alert alert-danger" <c:if test="${empty errorMsg }">style="display: none;"</c:if> >
														${errorMsg}														 
													</div>
 
													<div class="clearfix">
														<label class="inline">
															<input type="checkbox" name="rememberMe" class="ace" />
															<span class="lbl">&nbsp;记住我</span>
														</label>

														<button type="submit" class="width-35 pull-right btn btn-sm btn-primary">
															<i class="icon-key"></i>
															登陆
														</button>
													</div>
													
													<div class="space-4"></div>
												</fieldset>
											</form>


										</div><!-- /widget-main -->
									</div><!-- /widget-body -->
								</div><!-- /login-box -->

								 
							</div><!-- /position-relative -->
						</div>
					</div><!-- /.col -->
				</div><!-- /.row -->
			</div>
		</div><!-- /.main-container -->

		 
	<div style="display:none"></div>
</body>
</html>
