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
        var tab = localStorage.getItem("currentTab");
        if (tab == null) {
            tab = "#basicTab";
        }
        $("#website-tab a[href='" + tab + "']").tab('show');

        $(".select2_single").select2({
            minimumResultsForSearch: -1,
            allowClear: true,
            dropdownParent: $("#type-select-parent")
        });
        $(".nav-link").click(function () {
            localStorage.setItem("currentTab", $(this).attr("href")+"");
        });

    });
</script>
<div id="website-tab" class="tabbable tabs-left">
    <ul class="nav nav-tabs">
        <li class="nav-item"><a class="nav-link" href="#basicTab" data-toggle="tab">基本信息</a></li>
        <li class="nav-item"><a class="nav-link" href="#blogTab" data-toggle="tab">博客设置</a></li>
        <li class="nav-item"><a class="nav-link" href="#templateTab"
                                data-toggle="tab">${_res['admin.template.manage']}</a>
        </li>
        <li class="nav-item"><a class="nav-link" href="#otherTab" data-toggle="tab">其他设置</a></li>
        <li class="nav-item"><a class="nav-link" href="#errorPageEditTab" data-toggle="tab">错误页面编辑</a></li>
        <li class="nav-item"><a class="nav-link" href="#upgradeTab"
                                data-toggle="tab">${_res['admin.upgrade.manage']}</a></li>
    </ul>
    <div class="tab-content row">
        <div class="tab-pane in col-md-6 col-xs-12 col-sm-12" id="basicTab">
            <h4 class="header">认真输入，有助于网站被收录</h4>
            <form role="form" class="form-horizontal" id="baseMsgAjax">
                <div class="form-group row">
                    <label class="col-md-3 control-label no-padding-right"> 网站标题 </label>
                    <div class="col-md-9">
                        <input type="text" name="title" value="${website.title!''}"
                               class="form-control col-xs-12 col-sm-9"
                               placeholder="请输入网站标题 ">
                    </div>
                </div>

                <div class="form-group row">
                    <label class="col-md-3 control-label no-padding-right"> 网站副标题 </label>

                    <div class="col-md-9">
                        <input type="text" name="second_title"
                               value="${website.second_title!''}" class="form-control col-xs-12"
                               placeholder="请求输入网站副标题 ">

                    </div>
                </div>
                <div class="form-group row">
                    <label class="col-md-3 control-label no-padding-right"> 网站关键词 </label>

                    <div class="col-md-9">
                        <input type="text" name="keywords" value="${website.keywords!''}"
                               class="form-control col-xs-12" placeholder="请求输入网站关键词 "
                        >
                    </div>
                </div>
                <div class="form-group row">
                    <label for="form-field-2"
                           class="col-md-3 control-label no-padding-right"> 网站描述 </label>

                    <div class="col-md-9">
						<textarea name="description" class="form-control col-xs-12" rows="5"
                                  placeholder="" id="form-field-2">${website.description!''}</textarea>
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
        <div class="tab-pane col-md-6" id="otherTab">
            <h4 class="header">ICP，网站统计等信息</h4>
            <form role="form" class="form-horizontal" id="otherMsgAjax">
                <div class="form-group row">
                    <label class="col-md-3 control-label no-padding-right"> ICP备案信息 </label>

                    <div class="col-md-9">
						<textarea name="icp" class="form-control" cols="30" rows="3"
                        >${website.icp!''}</textarea>
                    </div>
                </div>
                <div class="form-group row">
                    <label class="col-md-3 control-label no-padding-right">IP 黑名单</label>

                    <div class="col-md-9">
                        <textarea name="blackList" class="form-control col-xs-12" cols="30"
                                  rows="8">${website.blackList!''}</textarea>
                    </div>
                </div>
                <div class="form-group row">
                    <label class="col-sm-3 control-label no-padding-right"> 网站统计 </label>

                    <div class="col-md-9">
						<textarea name="webCm" class="form-control col-xs-12" cols="30" rows="8"
                        >${website.webCm!''}</textarea>
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
        <#assign path="/error/">
        <#assign editType="错误页面">
        <div class="tab-pane col-md-12" id="errorPageEditTab">
            <#include "file_editor.ftl"/>
        </div>
        <div class="tab-pane col-md-12" id="templateTab">
            <#include "template.ftl"/>
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
                    <form role="form" action="api/admin/upgrade/setting" class="form-horizontal was-validated"
                          id="upgradeAjax">
                        <div class="form-group row" id="cycle-select-parent">
                            <label class="col-md-3 control-label no-padding-right"> ${_res['admin.upgrade.autoCheckCycle']} </label>

                            <div class="col-md-4">
                                <select name="autoUpgradeVersion" id="cycle-select" class="form-control">
                                    <option
                        <#if website.autoUpgradeVersion == '86400'>selected="selected"</#if>
                        value="86400">${_res['admin.upgrade.cycle.oneDay']}</option>
                                    <option
                        <#if website.autoUpgradeVersion == '604800'>selected="selected"</#if>
                        value="604800">${_res['admin.upgrade.cycle.oneWeek']}</option>
                                    <option
                        <#if website.autoUpgradeVersion == '1296000'>selected="selected"</#if>
                        value="1296000">${_res['admin.upgrade.cycle.halfMonth']}</option>
                                    <option
                        <#if website.autoUpgradeVersion == '-1'>selected="selected"</#if>
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
                       <#if website.upgradePreview?? &&   website.upgradePreview == '1'>checked="checked"</#if>>
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
                               value="${website.session_timeout!''}">
                    </div>
                </div>
                <div class="form-group row" id="type-select-parent">
                    <label class="col-md-3 control-label no-padding-right"> 编辑器 </label>

                    <span class="col-md-4">
                    类型<select name="editor_type" class="form-control select2_single">
                        <option
                                <#if  website.editor_type?? && website.editor_type == 'html'>selected="selected"</#if>
                                value="html">html</option>
                        <option
                                <#if website.editor_type?? && website.editor_type != 'html'>selected="selected"</#if>
                                value="markdown">markdown</option>
                    </select>
                        </span>
                    <span class="col-md-4">
                    主题<select name="editorMdTheme" class="form-control select2_single">
                        <option
                                <#if website.editorMdTheme?? && website.editorMdTheme == 'default'>selected="selected"</#if>
                                value="default">默认</option>
                        <option
                                <#if website.editorMdTheme?? && website.editorMdTheme == 'dark'>selected="selected"</#if>
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
                                   <#if website.generator_html_status?? && website.generator_html_status == '1'>checked="checked"</#if>>
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
                                   <#if website.disable_comment_status?? && website.disable_comment_status == '1'>checked="checked"</#if>>
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
                                   <#if website.article_thumbnail_status?? &&  website.article_thumbnail_status == '1'>checked="checked"</#if>>
						</label>
						</span>
                    </div>
                </div>
                <div class="form-group row">
                    <label class="col-sm-3 control-label no-padding-right"> 语言 </label>
                    <div class="col-sm-4">
                        <select name="language" class="form-control select2_single">
                            <option
                            <#if website.language?? && website.language == 'zh_CN'>'selected'='selected'</#if>
                            value="zh_CN">${_res.languageChinese}</option>
                            <option
                            <#if website.language?? && website.language == 'en_US'>'selected'='selected'</#if>
                            value="en_US">${_res.languageEnglish}</option>
                        </select>
                    </div>
                </div>
                <div class="form-group row">
                    <label class="col-sm-3 control-label no-padding-right"> 文章路由 </label>
                    <div class="col-sm-4">
                        <select name="article_route" class="form-control select2_single">
                            <option
                            <#if website.article_route?? && website.article_route == ''>'selected'='selected'</#if>
                            value="">默认</option>
                                <option
                            <#if website.article_route?? && website.article_route == 'post'> selected'='selected'</#if>
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