<script type="text/javascript" src="${basePath}admin/js/set_update.js"></script>
<script src="${basePath}assets/js/switchery.min.js"></script>
<script src="${basePath}assets/js/jquery.smartWizard.js"></script>
<script src="${basePath}admin/js/do_upgrade.js"></script>
<style>
    a.btn-primary {
        color: #fff;
    }
</style>
<div class="page-header">
    <h3>
    ${_res['upgradeWizard']}
    </h3>
</div>
<div class="row">
    <div class="col-md-6 col-xs-12 col-sm-12">
        <div id="wizard_verticle" class="form_wizard wizard_horizontal">
            <ul class="list-unstyled wizard_steps anchor">
                <li>
                    <a href="#step-11" class="selected" isdone="1" rel="1">
                        <span class="step_no">1</span>
                        <span class="step_descr">变更日志</span>
                    </a>
                </li>
                <li>
                    <a href="#step-22" class="disabled" isdone="0" rel="2">
                        <span class="step_no">2</span>
                        <span class="step_descr">下载更新</span>
                    </a>
                </li>
                <li>
                    <a href="#step-33" class="disabled" isdone="0" rel="3">
                        <span class="step_no">3</span>
                        <span class="step_descr">执行更新</span>
                    </a>
                </li>
            </ul>
            <div class="stepContainer" style="min-height: 400px;overflow-y:auto;">
                <div id="step-11" class="content" style="display: block;">
                    <div id="changeLog">
                        <#if lastVersion?? && lastVersion.upgrade>
                            ${lastVersion.version.changeLog!''}
                        </#if>
                    </div>
                </div>
                <div id="step-22" class="content" style="min-height: 400px;overflow-y:auto;display: none;">
                    <h4 id="processbar-title"></h4>
                    <div class="progress">
                        <div class="progress-bar progress-bar-striped" id="downloadFileProcessBar" role="progressbar"
                             style="width: 0" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100"></div>
                    </div>
                </div>
                <div id="step-33" class="content" style="display: none;min-height: 400px;overflow-y:auto;">
                    <h4>正在执行更新...</h4>
                    <div id="upgrade-process" style="min-height: 400px;">
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
${pageEndTag}