<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<jsp:include page="include/menu.jsp"/>
<script type="text/javascript" src="admin/js/set_update.js"></script>
<div class="page-header">
	<h3>密码变更</h3>
</div><!-- /.page-header -->
<div class="row">
<div class="col-xs-12">
	<!-- PAGE CONTENT BEGINS -->
	<form role="form" action="api/admin/changePassword" id="updatePasswordAjax" class="form-horizontal" method="post">
		<div class="form-group">
			<label for="form-field-1" class="col-sm-3 control-label no-padding-right"> 旧密码 * </label>

			<div class="col-sm-5">
				<input type="password" name="oldPassword" value="" required="" class="form-control col-xs-12 col-sm-6" placeholder="" id="orlPassword">
			</div>
		</div>

		<div class="form-group">
			<label for="form-field-1" class="col-sm-3 control-label no-padding-right"> 新密码 * </label>

			<div class="col-sm-5">
				<input type="password" name="newPassword" value="" class="form-control col-xs-12 col-sm-6" required=""  placeholder="" id="newPassword1">

			</div>
		</div>

		<div class="form-group">
			<label for="form-field-1" class="col-sm-3 control-label no-padding-right"> 确认密码 * </label>

			<div class="col-sm-5">
			<input type="password" name="newPassword2" value="" class="form-control col-xs-12 col-sm-6" required=""  placeholder="" id="newPassword2">

			</div>
		</div>
		<div class="ln_solid"></div>

		<div class="form-group">
			<div class="col-md-offset-3 col-md-9">
				<button id="updatePassword" type="button" class="btn btn-info">
					<i class="fa fa-check bigger-110"></i>
					提交
				</button>

			</div>
		</div>

	</form>
	<!-- PAGE CONTENT ENDS -->
</div><!-- /.col -->
</div>
<jsp:include page="include/footer.jsp" />