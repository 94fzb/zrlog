<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="include/menu.jsp"/>
<script type="text/javascript" src="admin/js/set_update.js"></script>
<div class="page-header">
	<h1>
		${_res['admin.upgrade.manage']}
	</h1>
</div>

<div class="row">
<div class="col-xs-12">
<form role="form" action="admin/website/upgrade" class="form-horizontal" id="upgradeAjax" checkbox="upgradeCanPreview">
	<div class="form-group">
		<label for="form-field-1"
			class="col-sm-3 control-label no-padding-right"> ${_res['admin.upgrade.autoCheckCycle']} </label>

		<div class="col-sm-3">
			<span class="col-sm-9">
			<select name="autoUpgradeVersion" class="form-control" style="height:28px">
				<option <c:if test="${webs.autoUpgradeVersion == 86400}">selected="selected"</c:if> value="86400">${_res['admin.upgrade.cycle.oneDay']}</option>
				<option <c:if test="${webs.autoUpgradeVersion == 604800}">selected="selected"</c:if> value="604800">${_res['admin.upgrade.cycle.oneWeek']}</option>
				<option <c:if test="${webs.autoUpgradeVersion == 1296000}">selected="selected"</c:if> value="1296000">${_res['admin.upgrade.cycle.halfMonth']}</option>
				<option <c:if test="${webs.autoUpgradeVersion == -1}">selected="selected"</c:if> value="-1">${_res['admin.upgrade.cycle.never']}</option>
			</select>
			</span>
		</div>
	</div>
	<div class="form-group">
		<label class="col-sm-3 control-label no-padding-right" for="form-field-1"> ${_res['admin.upgrade.canPreview']} </label>

		<div class="col-sm-9">
			<span class="col-sm-1">
					<input type="hidden" value="off" id="upgradeCanPreview">
					<input type="checkbox" value="off" <c:if test="${webs.upgradeCanPreview eq 'on'}">checked="checked"</c:if> id="gritter-light" class="ace ace-switch ace-switch-5" name="upgradeCanPreview"> <span class="lbl"></span>
			</span>
		</div>
	</div>
	<div class="space-4"></div>

	<div class="clearfix form-actions">
		<div class="col-md-offset-3 col-md-9">
			<button id="upgrade" type="button" class="btn btn-info">
				<i class="icon-ok bigger-110"></i> ${_res.submit}
			</button>
		</div>
	</div>

</form>
</div>
</div>

<jsp:include page="include/footer.jsp"/>