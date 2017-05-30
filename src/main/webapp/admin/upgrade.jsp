<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="include/menu.jsp"/>
<script type="text/javascript" src="${cacheFile['/admin/js/set_update.js']}"></script>
<script type="text/javascript" src="${cacheFile['/admin/js/upgrade.js']}"></script>
<script src="${cacheFile['/assets/js/select2/select2.min.js']}"></script>
<script src="${cacheFile['/assets/js/switchery.min.js']}"></script>
<div class="page-header">
	<h3>
		${_res['admin.upgrade.manage']}
	</h3>
	<div class="text-right">
        <button id="checkUpgrade" type="button" class="btn btn-blank">
            <i class="fa fa-refresh bigger-110"></i> ${_res.checkUpgrade}
        </button>
    </div>
</div>
<div class="row">
<div class="col-xs-12">
<form role="form" action="api/admin/upgrade/setting" class="form-horizontal" id="upgradeAjax" checkBox="upgradeCanPreview">
	<div class="form-group">
		<label for="form-field-1"
			class="col-sm-3 control-label no-padding-right"> ${_res['admin.upgrade.autoCheckCycle']} </label>

		<div class="col-sm-3">
			<span class="col-sm-9">
			<select name="autoUpgradeVersion" class="form-control select2_single">
				<!--
				<option <c:if test="${webs.autoUpgradeVersion == 60}">selected="selected"</c:if> value="60">${_res['admin.upgrade.cycle.oneMinute']}</option>
				<option <c:if test="${webs.autoUpgradeVersion == 3600}">selected="selected"</c:if> value="3600">${_res['admin.upgrade.cycle.oneHour']}</option>
				-->
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

		<div class="col-sm-5">
			<span class="col-sm-1">
            <input type="hidden" id="upgradeCanPreview" value="off">
            <label>
                <input type="checkbox" class="form-control js-switch" style="display: none;" data-switchery="true" name="upgradeCanPreview"
                <c:if test="${webs.upgradeCanPreview eq 'on'}">checked="checked"</c:if> value="off">
            </label>
            </span>
		</div>
	</div>
	<div class="ln_solid"></div>

	<div class="form-group">
		<div class="col-md-offset-3 col-md-9">
			<button id="upgrade" type="button" class="btn btn-info">
				<i class="fa fa-check bigger-110"></i> ${_res.submit}
			</button>
		</div>
	</div>

</form>
</div>
</div>

<jsp:include page="include/footer.jsp"/>