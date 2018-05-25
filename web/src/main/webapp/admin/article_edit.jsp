<%@ page import="com.zrlog.common.Constants" %>
<%@ page session="false" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    request.setAttribute("post", Constants.getArticleUri());
%>
<link rel="stylesheet" href="${basePath}admin/markdown/css/editormd.min.css"/>
<link rel="stylesheet" href="${basePath}admin/summernote/summernote.css"/>
<link rel="stylesheet" href="${basePath}assets/css/video-js.css"/>
<link rel="stylesheet" href="${basePath}assets/css/icheck-blue.css"/>
<script src="${basePath}assets/js/icheck.min.js"></script>
<script>
    var skipFirstRubbishSave = ${skipFirstRubbishSave};
    var article = ${article};
    var uploadUrl = 'api/admin/upload/';
    var editorMdPath = "admin/markdown/lib/";
    var editorLang = "admin/markdown/languages/";
    var summernoteLang = "admin/summernote/lang/";
    $('.icheck').iCheck({
        checkboxClass: 'icheckbox_flat-blue',
        radioClass: 'iradio_flat-blue'
    });
    var editorMdJs = '${basePath}admin/markdown/js/editormd.min.js';
    var summernoteJs = '${basePath}admin/summernote/summernote.min.js';
    var editorEl = $("#editorDiv");
    var editorDivWrapper = $("#editorDivWrapper");
    var editorTheme = editorEl.attr("theme");
</script>
<script src="${basePath}assets/js/screenfull.min.js"></script>
<script src="${basePath}assets/js/jquery.liteuploader.min.js"></script>
<script src="${basePath}assets/js/select2/select2.min.js"></script>
<script src="${basePath}assets/js/jquery.tagsinput.js"></script>
<script src="${basePath}assets/js/turndown.js"></script>
<script src="${basePath}admin/js/summernote.js"></script>
<script src="${basePath}admin/js/mdeditor.js"></script>
<script src="${basePath}admin/js/article_edit.js"></script>
<script src="${basePath}assets/js/video.js"></script>
<style>
    .CodeMirror-scroll {
        box-sizing: content-box;
        height: 100%;
        margin-bottom: -30px;
        margin-right: -30px;
        outline: 0 none;
        overflow: hidden;
        padding-bottom: 30px;
        position: relative;
    }

    .switchery {
        width: 38px;
    }

    .x_content {
        clear: both;
        float: left;
        margin-top: 5px;
        padding: 0;
        position: relative;
        width: 100%;
    }

    #preview-link {
        color: #314659;
    }

    .save-btn-full-screen {
        position: fixed;
        z-index: 100000;
        top: 2px;
        right: 20px;
    }

    .saveToRubbish-btn-full-screen {
        position: fixed;
        z-index: 100000;
        top: 2px;
        right: 120px;
    }

    .editormd-html-preview, .editormd-preview-container {
        padding: 5px;
    }

    #editorDiv {
        width: 100%;
        min-height: 1040px;
        border-radius: 4px;
        background-color: #ffffff;
        border: 0;
    }

    #editorDivWrapper {
        border-radius: 4px;
        border: 1px solid #ccc;
    }

    .note-editor.note-frame {
        border: #e6e9ed;
        margin-bottom: 0;
    }

    .note-editor img {
        max-width: 100%;
    }

    .editormd-preview-theme-dark .markdown-body h1, .editormd-preview-theme-dark .markdown-body h2,
    .editormd-preview-theme-dark.markdown-body h3, .editormd-preview-theme-dark .markdown-body h4 {
        color: #ccc;
    }

    .panel-default > .panel-heading {
        background-color: #ffffff;

    }
    .panel-default > .panel-heading button {
        color: #666;
    }
    .full-screen-hide {

    }
</style>
<div class="page-header">
    <h3>
        ${_res['admin.log.edit']}
    </h3>
</div>
    <form class="form-horizontal form-label-left" id="article-form" style="max-width: 1795px">
        <input type="hidden" id="id" name="id" value="${log.logId}">
        <input type="hidden" name="editorType">
        <textarea placeholder="${_res.editorPlaceholder}" id="content" name="content"
                  style="display: none;"></textarea>
        <textarea id="markdown" name="markdown" style="display: none;"></textarea>
        <div class="row">
        <div class="col-md-12 col-sm-12 col-xs-12" style="padding-bottom: 10px;">
            <div class="text-right">
                <a id="preview-link" target="_blank" style="display:none">
                    <button class="btn btn-black" id="preview" type="button">
                        <i class="fa fa-eye bigger-110"></i>
                        ${_res['preview']}
                    </button>
                </a>
                <button class="btn btn-primary" id="saveToRubbish" type="button">
                    <i class="fa fa-save  bigger-110"></i>
                    ${_res['saveAsDraft']}
                </button>
                <button class="btn btn-info" id="save" type="button" style="margin-right: 0">
                    <i class="fa fa-paper-plane bigger-110"></i>
                    <span id="save_text"></span>
                </button>
            </div>
            </div>
        </div>
        <div class="row">
        <div class="col-md-12 col-sm-12 col-xs-12 col-lg-9">
            <div class="form-group">
                <div class="row">
                    <div class="col-md-7 col-xs-12" id="title-parent" style="padding-bottom: 5px">
                        <input required name="title" id="title" maxlength="254" value="${log.title}"
                               class="form-control"
                               type="text" placeholder="${_res['inputArticleTitle']}"/>
                    </div>
                    <div class="col-md-3 col-xs-12 full-screen-hide" style="padding-bottom: 5px">
                        <div class="input-group">
                            <span class="input-group-addon" id="basic-addon3">/${post}</span>
                            <input placeholder="${_res['inputArticleAlias']}" type="text" class="form-control"
                                   id="alias" name="alias" aria-describedby="basic-addon3" value="${log.alias}">
                        </div>
                    </div>
                    <div class="col-md-2 col-xs-12 full-screen-hide">
                        <div class="text-right">
                            <div id="editorType" class="btn-group" data-toggle="buttons">
                                <label class="btn btn-default" id="editorType_html" data-toggle-class="btn-primary"
                                       data-toggle-passive-class="btn-default">
                                    <input type="radio" value="html" data-parsley-multiple="editorType"> html
                                </label>
                                <label class="btn btn-default" id="editorType_markdown" data-toggle-class="btn-primary"
                                       data-toggle-passive-class="btn-default">
                                    <input type="radio" value="markdown" data-parsley-multiple="editorType"> markdown
                                </label>
                            </div>
                        </div>
                    </div>
                </div>
                <div id="editorDivWrapper">
                    <div id="editorDiv" theme="${webSite.editorMdTheme}"></div>
                </div>
            </div>
        </div>
        <div class="col-md-12 col-lg-3 col-xs-12 full-screen-hide">
            <div class="row">
            <div class="form-group col-xs-12">
                <c:if test="${0 ne webSite['article_thumbnail_status']}">
                    <div class="x_panel">
                        <div id="thumbnail-img" title="${_res['writeCover']}"
                             style="background-color:rgba(0,0,0,.075);<c:if
                                     test="${log!=null and not empty log.thumbnail}">background: url('${log.thumbnail}')</c:if>"
                             class="thumbnail-img img-responsive WriteCover-wrapper WriteCover-previewWrapper">
                            <i id="camera-icon" class="WriteCover-uploadIcon fa fa-camera fa-3"></i>
                            <input type="file" accept=".jpeg, .jpg, .png" id="thumbnail-upload"
                                   name="imgFile"
                                   value="${log.thumbnail}"
                                   class="WriteCover-uploadInput">
                            <input type="hidden" name="thumbnail" value="${log.thumbnail}" id="thumbnail">
                        </div>
                    </div>
                </c:if>
                <div class="x_panel">
                    <div class="x_title">
                        <h2>${_res['admin.setting']}</h2>
                        <div class="clearfix"></div>
                    </div>
                    <div class="x_content">
                        <div class="row">
                            <div class="col-xs-7">
                                <label><span style="font-size: 16px">${_res['commentAble']}</span>
                                    <input type="checkbox" name="canComment"
                                           <c:if test="${log == null}">checked="checked"</c:if>
                                           <c:if test="${log['canComment']}">checked="checked"</c:if>
                                           class="js-switch"
                                           style="display: none;" data-switchery="true">

                                </label>
                            </div>
                            <div class="col-xs-5">
                                <label><span style="font-size: 16px">${_res['recommendable']}</span>
                                    <input type="checkbox" name="recommended"
                                           <c:if test="${log.recommended}">checked="checked"</c:if>
                                           class="js-switch"
                                           style="display: none;" data-switchery="true">

                                </label>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-xs-6" id="privateCheckbox">
                                <label><span style="font-size: 16px">${_res['private']}</span>
                                    <input type="checkbox" name="_private"
                                           <c:if test="${log['private']}">checked="checked"</c:if>
                                           class="js-switch"
                                           style="display: none;" data-switchery="true">

                                </label>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="x_panel" id="type-select-parent">
                    <div class="x_title">
                        <h2>${_res['admin.type.manage']}</h2>
                        <div class="clearfix"></div>
                    </div>
                    <div class="x_content">
                        <c:forEach items="${init.types}" var="type">
                            <div style="font-size: 16px" class="row">
                                <div class="col-lg-12">
                                    <input class="icheck flat" name="typeId" type="radio"
                                           <c:if test="${type.id eq log.typeId}">checked="checked"</c:if>
                                           value="${type.id}"><span style="padding-left: 5px">${type.typeName}</span>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </div>
            </div>
            </div>
        </div>
        <div class="col-md-12 col-lg-3 col-xs-12 full-screen-hide">
            <div class="row">
            <div class="form-group col-xs-12">
                <div class="x_panel">
                    <div class="x_title">
                        <h2>${_res['tag']}</h2>
                        <div class="clearfix"></div>
                    </div>
                    <div class="x_content">
                        <input value="${log.keywords}" class="form-control" placeholder="${_res['tagTips']}"
                               type="hidden" name="keywords" id="keywordsVal" size="60" maxlength="60"/>
                        <div class="tagsinput" id="keywords"></div>
                        <div class="tagsinput" id="unCheckedTag" style="max-height: 240px;width: 100%">
                            <c:forEach items="${init.tags}" var="tags">
                                <span class="tag2" val="${tags.text}"><i style="padding-right: 5px"
                                                                         class="fa fa-tag"></i>${tags.text}</span>
                            </c:forEach>
                        </div>
                    </div>
                </div>
                <div class="x_panel">
                    <div class="x_title">
                        <h2>${_res['digest']}</h2>
                        <div class="clearfix"></div>
                    </div>
                    <div class="x_content">
                        <div class="form-group">
                            <textarea id="digest" name="digest" class="form-control" placeholder="${_res['digestTips']}"
                                      cols="100" rows="3"
                                      style="width:100%; height:200px; z-index: 9999;">${log.digest}</textarea>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        </div>
        </div>
        </div>
    </form>
${pageEndTag}