<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<jsp:include page="include/menu.jsp"/>
		
						<div class="page-header">
							<h1>用户设置
								<small>
									<i class="icon-double-angle-right"></i>
									密码变更
								</small>
							</h1>
						</div><!-- /.page-header -->

							<div class="col-xs-12">
								<!-- PAGE CONTENT BEGINS -->

								<form role="form" action="${url}/admin/changePassword" id="user" class="form-horizontal" method="post">
									<div class="form-group"> 
										<label for="form-field-1" class="col-sm-3 control-label no-padding-right"> 旧密码 </label>

										<div class="col-sm-9">
											<input type="text" name="oldPassword" value="" class="col-xs-10 col-sm-5" placeholder="" id="form-field-1">
										</div>
									</div>
									
									<div class="form-group">
										<label for="form-field-1" class="col-sm-3 control-label no-padding-right"> 新密码 </label>

										<div class="col-sm-9">
											<input type="text" name="newPassword" value="" class="col-xs-10 col-sm-5" placeholder="" id="form-field-1">
											
										</div>
									</div>
									
									<div class="form-group">
										<label for="form-field-1" class="col-sm-3 control-label no-padding-right"> 确认密码 </label>

										<div class="col-sm-9">
										<input type="text" name="newPassword2" value="" class="col-xs-10 col-sm-5" placeholder="" id="form-field-1">
											
										</div>
									</div>
									<div class="space-4"></div>

									<div class="clearfix form-actions">
										<div class="col-md-offset-3 col-md-9">
											<button id="submit" type="submit" class="btn btn-info">
												<i class="icon-ok bigger-110"></i>
												提交
											</button>
											 
										</div>
									</div>

								</form>
								<!-- PAGE CONTENT ENDS -->
							</div><!-- /.col -->

					</div>
	<div style="display:none"></div>
	<body> 
</html>