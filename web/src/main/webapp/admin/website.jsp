<%@ page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script type="text/javascript" src="${basePath}admin/js/set_update.js"></script>
<script type="text/javascript" src="${basePath}assets/js/select2/select2.min.js"></script>
<script src="${basePath}admin/js/upgrade.js"></script>
<div class="page-header">
    <h3>${_res['admin.setting']}</h3>
</div>
<style>
    .tab-pane {
        padding-top: 10px;
    }

    .header {
        padding-left: 10px;
    }
</style>
<script>
    $(function () {
        $(".select2_single").select2({
            minimumResultsForSearch: -1,
            allowClear: true,
            dropdownParent: $("#type-select-parent")
        });

    });
</script>
<!-- /.page-header -->
<div class="tabbable tabs-left">
    <ul class="nav nav-tabs">
        <li class="nav-item"><a class="nav-link active" href="#basic" data-toggle="tab">基本信息</a></li>
        <li class="nav-item"><a class="nav-link" href="#blogTab" data-toggle="tab">博客设置</a></li>
        <li class="nav-item"><a class="nav-link" href="#other" data-toggle="tab">其他设置</a></li>
        <li class="nav-item"><a class="nav-link" href="#errorPageEdit" data-toggle="tab">错误页面编辑</a></li>
        <li class="nav-item"><a class="nav-link" href="#upgradeTab" data-toggle="tab">${_res['admin.upgrade.manage']}</a></li>
    </ul>
    <div class="tab-content row">
        <div class="tab-pane in active col-md-6 col-xs-12 col-sm-12" id="basic">
            <h4 class="header">认真输入，有助于网站被收录</h4>
            <form role="form" class="form-horizontal" id="baseMsgAjax">
                <div class="form-group row">
                    <label class="col-md-3 control-label no-padding-right"> 网站标题 </label>
                    <div class="col-md-9">
                        <input type="text" name="title" value="${webs.title }"
                               class="form-control col-xs-12 col-sm-9" placeholder="请输入网站标题 ">
                    </div>
                </div>

                <div class="form-group row">
                    <label class="col-md-3 control-label no-padding-right"> 网站副标题 </label>

                    <div class="col-md-9">
                        <input type="text" name="second_title"
                               value="${webs.second_title }" class="form-control col-xs-12"
                               placeholder="请求输入网站副标题 ">

                    </div>
                </div>
                <div class="form-group row">
                    <label class="col-md-3 control-label no-padding-right"> 网站关键词 </label>

                    <div class="col-md-9">
                        <input type="text" name="keywords" value="${webs.keywords}"
                               class="form-control col-xs-12" placeholder="请求输入网站关键词 "
                        >
                    </div>
                </div>
                <div class="form-group row">
                    <label for="form-field-2"
                           class="col-md-3 control-label no-padding-right"> 网站描述 </label>

                    <div class="col-md-9">
						<textarea name="description" class="form-control col-xs-12" rows="5"
                                  placeholder="" id="form-field-2">${webs.description}</textarea>
                    </div>
                </div>
                <div class="ln_solid"></div>
                <div class="form-group row">
                    <div class="col-md-offset-3 col-md-9">
                        <button id="baseMsg" type="button" class="btn btn-info">
                            ${_res['submit']}
                        </button>
                    </div>
                </div>

            </form>
        </div>
        <div class="tab-pane col-md-6" id="other">
            <h4 class="header">ICP，网站统计等信息</h4>
            <form role="form" class="form-horizontal" id="otherMsgAjax">
                <div class="form-group row">
                    <label class="col-md-3 control-label no-padding-right"> ICP备案信息 </label>

                    <div class="col-md-9">
						<textarea name="icp" class="form-control" cols="30" rows="3"
                        >${webs.icp}</textarea>
                    </div>
                </div>
                <div class="form-group row">
                    <label class="col-md-3 control-label no-padding-right">IP 黑名单</label>

                    <div class="col-md-9">
                        <textarea name="blackList" class="form-control col-xs-12" cols="30"
                                  rows="8">${webs.blackList}</textarea>
                    </div>
                </div>
                <div class="form-group row">
                    <label class="col-sm-3 control-label no-padding-right"> 网站统计 </label>

                    <div class="col-md-9">
						<textarea name="webCm" class="form-control col-xs-12" cols="30" rows="8"
                        >${webs.webCm}</textarea>
                    </div>
                </div>

                <div class="ln_solid"></div>

                <div class="form-group row">
                    <div class="col-md-offset-3 col-md-9">
                        <button id="otherMsg" type="button" class="btn btn-info">
                            ${_res['submit']}
                        </button>
                    </div>
                </div>

            </form>
        </div>
        <div class="tab-pane col-md-12" id="errorPageEdit">
            <jsp:include page="file_editor.jsp?path=/error/&editType=错误页面"/>
        </div>
        <div class="tab-pane col-md-12" id="upgradeTab">
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
                    <form role="form" action="api/admin/upgrade/setting" class="form-horizontal was-validated" id="upgradeAjax">
                        <div class="form-group row" id="cycle-select-parent">
                            <label class="col-md-3 control-label no-padding-right"> ${_res['admin.upgrade.autoCheckCycle']} </label>

                            <div class="col-md-4" >
			<select name="autoUpgradeVersion" id="cycle-select" class="form-control">
				<!--
				<option <c:if test="${webs.autoUpgradeVersion == 60}">selected="selected"</c:if> value="60">${_res['admin.upgrade.cycle.oneMinute']}</option>
				<option <c:if test="${webs.autoUpgradeVersion == 3600}">selected="selected"</c:if> value="3600">${_res['admin.upgrade.cycle.oneHour']}</option>
				-->
				<option
                        <c:if test="${webs.autoUpgradeVersion == 86400}">selected="selected"</c:if>
                        value="86400">${_res['admin.upgrade.cycle.oneDay']}</option>
				<option
                        <c:if test="${webs.autoUpgradeVersion == 604800}">selected="selected"</c:if>
                        value="604800">${_res['admin.upgrade.cycle.oneWeek']}</option>
				<option
                        <c:if test="${webs.autoUpgradeVersion == 1296000}">selected="selected"</c:if>
                        value="1296000">${_res['admin.upgrade.cycle.halfMonth']}</option>
				<option
                        <c:if test="${webs.autoUpgradeVersion == -1}">selected="selected"</c:if>
                        value="-1">${_res['admin.upgrade.cycle.never']}</option>
			</select>
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
                       <c:if test="${webs.upgradePreview == 1}">checked="checked"</c:if>>
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
        </div>
        <div class="tab-pane col-md-6" id="blogTab" style="padding: 10px">
            <h4 class="header">博客设置</h4>
            <form role="form" class="form-horizontal" id="blogAjax" data-toggle="validator">
                <div class="form-group row">
                    <label class="col-md-3 control-label no-padding-right">会话过期时间（分钟）</label>

                    <div class="col-md-5">
                        <input type="number" min="1" name="session_timeout" class="form-control col-xs-12 col-sm-6"
                               value="${webs.session_timeout}">
                    </div>
                </div>
                <div class="form-group row" id="type-select-parent">
                    <label class="col-md-3 control-label no-padding-right"> 编辑器 </label>

                    <span class="col-md-4">
                    类型<select name="editor_type" class="form-control select2_single">
                        <option
                                <c:if test="${webs.editorType eq 'html'}">selected="selected"</c:if>
                                value="html">html</option>
                        <option
                                <c:if test="${webs.editorType ne 'html'}">selected="selected"</c:if>
                                value="markdown">markdown</option>
                    </select>
                        </span>
                    <span class="col-md-4">
                    主题<select name="editorMdTheme" class="form-control select2_single">
                        <option
                                <c:if test="${webs.editorMdTheme eq 'default'}">selected="selected"</c:if>
                                value="default">默认</option>
                        <option
                                <c:if test="${webs.editorMdTheme eq 'dark'}">selected="selected"</c:if>
                                value="dark">护眼</option>
                    </select>
                    </span>
                </div>
                <div class="form-group row">
                    <label class="col-md-3 control-label no-padding-right"> 静态化文章页 </label>
                    <div class="col-md-9">
						<span class="col-sm-1">
						<label>
							<input type="checkbox" class="js-switch" style="display: none;" data-switchery="true"
                                   name="generator_html_status"
                                   <c:if test="${webs.generator_html_status == 1}">checked="checked"</c:if>>
						</label>
						</span>
                    </div>
                </div>

                <div class="form-group row">
                    <label class="col-md-3 control-label no-padding-right"> 关闭评论 </label>
                    <div class="col-md-9">
						<span class="col-sm-1">
						<label>
							<input type="checkbox" class="js-switch" style="display: none;" data-switchery="true"
                                   name="disable_comment_status"
                                   <c:if test="${webs.disable_comment_status == 1}">checked="checked"</c:if>>
						</label>
						</span>
                    </div>
                </div>

                <div class="form-group row">
                    <label class="col-sm-3 control-label no-padding-right"> 文章封面 </label>
                    <div class="col-sm-5">
						<span class="col-sm-1">
						<label>
							<input type="checkbox" name="article_thumbnail_status" class="js-switch"
                                   style="display: none;"
                                   data-switchery="true"
                                   name="article_thumbnail_status"
                                   <c:if test="${webs.article_thumbnail_status == 1}">checked="checked"</c:if>>
						</label>
						</span>
                    </div>
                </div>
                <div class="form-group row">
                    <label class="col-sm-3 control-label no-padding-right"> 语言 </label>
                    <div class="col-sm-4">
                        <select name="language" class="form-control select2_single">
                            <option
                            <c:if test="${webs.language eq 'zh_CN'}">'selected'='selected'</c:if>
                            value="zh_CN">${_res.languageChinese}</option>
                            <option
                            <c:if test="${webs.language eq 'en_US'}">'selected'='selected'</c:if>
                            value="en_US">${_res.languageEnglish}</option>
                        </select>
                    </div>
                </div>
                <div class="form-group row">
                    <label class="col-sm-3 control-label no-padding-right"> 文章路由 </label>
                    <div class="col-sm-4">
                        <select name="article_route" class="form-control select2_single">
                            <option
                            <c:if test="${webs.article_route eq ''}">'selected'='selected'</c:if>
                            value="">默认</option>
                            <option
                            <c:if test="${webs.article_route eq 'post' || empty webs.article_route}">'selected'='selected'</c:if>
                            value="post">post</option>
                        </select>
                    </div>
                </div>
                <div class="ln_solid"></div>

                <div class="form-group row">
                    <div class="col-md-offset-3 col-md-9">
                        <button id="blog" type="button" class="btn btn-info">
                            ${_res['submit']}
                        </button>
                    </div>
                </div>
            </form>
        </div>
    </div>
</div>
${pageEndTag}