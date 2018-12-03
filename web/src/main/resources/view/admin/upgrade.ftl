<script type="text/javascript" src="${basePath}admin/js/set_update.js"></script>
<script src="${basePath}assets/js/select2/select2.min.js"></script>
<script src="${basePath}assets/js/switchery.min.js"></script>
<script src="${basePath}admin/js/upgrade.js"></script>
<div class="page-header">
    <h3>
        ${_res['admin.upgrade.manage']}
    </h3>
</div>
<div class="row" style="margin-bottom: 15px">
    <div class="col-md-12">
        <div class="text-right">
            <button id="checkUpgrade" type="button" class="btn btn-blank">
                <i class="fa fa-refresh bigger-110"></i> ${_res.checkUpgrade}
            </button>
        </div>
    </div>
</div>
<div class="row">
    <div class="col-md-6 col-xs-12 col-sm-12">
        <form role="form" action="api/admin/upgrade/setting" class="form-horizontal" id="upgradeAjax">
            <div class="form-group row">
                <label class="col-md-3 control-label no-padding-right"> ${_res['admin.upgrade.autoCheckCycle']} </label>

                <div class="col-md-4">
			<span class="col-sm-12" id="cycle-select-parent">
			<select name="autoUpgradeVersion" class="form-control select2_single">
				<option
                        <c:if test="${website.autoUpgradeVersion == 86400}">selected="selected"</c:if>
                        value="86400">${_res['admin.upgrade.cycle.oneDay']}</option>
				<option
                        <c:if test="${website.autoUpgradeVersion == 604800}">selected="selected"</c:if>
                        value="604800">${_res['admin.upgrade.cycle.oneWeek']}</option>
				<option
                        <c:if test="${website.autoUpgradeVersion == 1296000}">selected="selected"</c:if>
                        value="1296000">${_res['admin.upgrade.cycle.halfMonth']}</option>
				<option
                        <c:if test="${website.autoUpgradeVersion == -1}">selected="selected"</c:if>
                        value="-1">${_res['admin.upgrade.cycle.never']}</option>
			</select>
			</span>
                </div>
            </div>
            <div class="form-group row">
                <label class="col-md-3 control-label no-padding-right"
                > ${_res['admin.upgrade.canPreview']} </label>

                <div class="col-md-4">
			<span class="col-sm-12">
            <label>
                <input type="checkbox" class="form-control js-switch" style="display: none;"
                       data-switchery="upgradePreview"
                       name="upgradePreview"
                       <c:if test="${website.upgradePreview == 1}">checked="checked"</c:if>>
            </label>
            </span>
                </div>
            </div>
            <div class="ln_solid"></div>

            <div class="form-group">
                <div class="col-md-offset-3 col-md-9">
                    <button id="upgrade" type="button" class="btn btn-info">
                        ${_res.submit}
                    </button>
                </div>
            </div>

        </form>
    </div>
</div>
${pageEndTag}