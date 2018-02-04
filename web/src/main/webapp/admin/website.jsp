<%@ page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="include/menu.jsp"/>
<script type="text/javascript" src="${cacheFile['/admin/js/set_update.js']}"></script>
<div class="page-header">
    <h3>${_res['admin.website.manage']}</h3>
</div>
<!-- /.page-header -->
<div class="tabbable tabs-left">
    <ul class="nav nav-tabs">
        <li class="active"><a href="#basic" data-toggle="tab">基本信息</a></li>
        <li><a href="#other" data-toggle="tab">其他信息</a></li>
        <li><a href="#errorPageEdit" data-toggle="tab">错误页面编辑</a></li>
        <li><a href="#blackList-tab" data-toggle="tab">IP 黑名单</a></li>
    </ul>
    <div class="tab-content">
        <div class="tab-pane in active col-md-6" id="basic" style="padding: 10px">
            <h4 class="header blue">认真输入，有助于网站被收录</h4>
            <form role="form" class="form-horizontal" id="baseMsgAjax">
                <div class="form-group">
                    <label class="col-sm-3 control-label no-padding-right"> 网站标题 </label>
                    <div class="col-sm-5">
                        <input type="text" name="title" value="${webs.title }"
                               class="form-control col-xs-12 col-sm-6" placeholder="请输入网站标题 ">
                    </div>
                </div>

                <div class="form-group">
                    <label class="col-md-3 control-label no-padding-right"> 网站副标题 </label>

                    <div class="col-md-9">
                        <input type="text" name="second_title"
                               value="${webs.second_title }" class="form-control col-xs-12 col-sm-6"
                               placeholder="请求输入网站副标题 ">

                    </div>
                </div>
                <div class="form-group">
                    <label class="col-md-3 control-label no-padding-right"> 网站关键词 </label>

                    <div class="col-md-9">
                        <input type="text" name="keywords" value="${webs.keywords}"
                               class="form-control col-xs-12 col-sm-6" placeholder="请求输入网站关键词 "
                        >
                    </div>
                </div>
                <div class="form-group">
                    <label for="form-field-2"
                           class="col-md-3 control-label no-padding-right"> 网站描述 </label>

                    <div class="col-md-9">
						<textarea name="description" class="form-control col-xs-12 col-sm-6" rows="5"
                                  placeholder="" id="form-field-2">${webs.description}</textarea>
                    </div>
                </div>
                <div class="ln_solid"></div>
                <div class="form-group">
                    <div class="col-md-offset-3 col-md-9">
                        <button id="baseMsg" type="button" class="btn btn-info">
                            <i class="fa fa-check bigger-110"></i> ${_res['submit']}
                        </button>
                    </div>
                </div>

            </form>
        </div>
        <div class="tab-pane col-md-6" id="other" style="padding: 10px">
            <h4 class="header blue">ICP，网站统计等信息</h4>
            <form role="form" class="form-horizontal" checkBox="pseudo_static_status,article_thumbnail"
                  id="otherMsgAjax">
                <div class="form-group">
                    <label class="col-md-3 control-label no-padding-right"> 会话过期时间（分钟） </label>

                    <div class="col-md-3">
                        <input name="session_timeout" class="form-control col-xs-12 col-sm-6"
                               value="${webs.session_timeout}">
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-md-3 control-label no-padding-right"> 编辑器主题 </label>

                    <span class="col-md-4">
                    <select name="editorMdTheme" class="form-control select2_single">
                        <option
                                <c:if test="${webs.editorMdTheme eq 'default'}">selected="selected"</c:if>
                                value="default">默认</option>
                        <option
                                <c:if test="${webs.editorMdTheme eq 'dark'}">selected="selected"</c:if>
                                value="dark">护眼</option>
                    </select>
                    </span>
                </div>
                <div class="form-group">
                    <label class="col-md-3 control-label no-padding-right"> ICP备案信息 </label>

                    <div class="col-md-9">
						<textarea name="icp" class="form-control col-md-6" cols="30" rows="3"
                        >${webs.icp}</textarea>
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-sm-3 control-label no-padding-right"> 网站统计 </label>

                    <div class="col-md-9">
						<textarea name="webCm" class="form-control col-xs-12 col-sm-6" cols="30" rows="8"
                        >${webs.webCm}</textarea>
                    </div>
                </div>

                <div class="form-group">
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

                <div class="form-group">
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

                <div class="ln_solid"></div>

                <div class="form-group">
                    <div class="col-md-offset-3 col-md-9">
                        <button id="otherMsg" type="button" class="btn btn-info">
                            <i class="fa fa-check bigger-110"></i> ${_res['submit']}
                        </button>
                    </div>
                </div>

            </form>
        </div>
        <div class="tab-pane" id="errorPageEdit" style="padding: 10px">
            <iframe src="admin/blank?include=file_editor&path=/error/&editType=错误页面"
                    scrolling="no" style="border: 0px;" width="100%" height="600px">
            </iframe>
        </div>
        <div class="tab-pane col-md-6" id="blackList-tab" style="padding: 10px">
            <form role="form" class="form-horizontal" id="blackListAjax">
                <h4 class="header blue">IP 黑名单</h4>
                <div class="form-group">
                    <label class="col-md-3 control-label no-padding-right"></label>

                    <div class="col-md-9">
						<textarea name="blackList" class="form-control col-xs-12 col-sm-6" cols="30" rows="8"
                        >${webs.blackList}</textarea>
                    </div>
                </div>
                <div class="ln_solid"></div>
                <div class="form-group">
                    <div class="col-md-offset-3 col-md-9">
                        <button id="blackList" type="button" class="btn btn-info">
                            <i class="fa fa-check bigger-110"></i> ${_res['submit']}
                        </button>
                    </div>
                </div>
            </form>
        </div>
    </div>
</div>
<jsp:include page="include/footer.jsp"/>