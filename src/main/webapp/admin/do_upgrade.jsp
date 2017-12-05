<%@ page session="false" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="include/menu.jsp"/>
<script type="text/javascript" src="${cacheFile['/admin/js/set_update.js']}"></script>
<script src="${cacheFile['/assets/js/jquery-ui-1.10.3.full.min.js']}"></script>
<script src="${cacheFile['/assets/js/switchery.min.js']}"></script>
<script src="${cacheFile['/assets/js/jquery.smartWizard.js']}"></script>
<script src="${cacheFile['/admin/js/do_upgrade.js']}"></script>
<div class="page-header">
    <h3>
        ${_res['upgradeWizard']}
    </h3>
</div>
<div class="row">
    <div class="col-md-8">
        <div id="wizard_verticle" class="form_wizard wizard_horizontal" style="min-height: 300px;">
            <ul class="list-unstyled wizard_steps anchor">
                <li>
                    <a href="${currentPage}#step-11" class="selected" isdone="1" rel="1">
                        <span class="step_no">1</span>
                        <span class="step_descr">变更日志</span>
                    </a>
                </li>
                <li>
                    <a href="${currentPage}#step-22" class="disabled" isdone="0" rel="2">
                        <span class="step_no">2</span>
                        <span class="step_descr">下载更新</span>
                    </a>
                </li>
                <li>
                    <a href="${currentPage}#step-33" class="disabled" isdone="0" rel="3">
                        <span class="step_no">3</span>
                        <span class="step_descr">执行更新</span>
                    </a>
                </li>
            </ul>
            <div class="stepContainer" style="min-height: 400px;overflow-y:auto;">
                <div id="step-11" class="content" style="display: block;">
                    <div id="changeLog">
                        ${lastVersion.version.changeLog}
                    </div>
                </div>
                <div id="step-22" class="content" style="min-height: 400px;overflow-y:auto;display: none;">
                    <h4 id="processbar-title"></h4>
                    <div data-percent="0%" id="progress" class="progress progress-striped"
                         style="display: none;">
                        <div style="width: 0%;" id="progress2"
                             class="progress-bar progress-bar-success"></div>
                    </div>
                </div>
                <div id="step-33" class="content" style="display: none;min-height: 400px;overflow-y:auto;">
                    <h4>正在执行更新...</h4>
                    <div id="upgrade-process">
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="include/footer.jsp"/>