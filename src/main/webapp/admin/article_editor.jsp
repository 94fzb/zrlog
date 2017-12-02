<%@ page session="false" pageEncoding="UTF-8" %>
<%
    String scheme = com.zrlog.web.util.WebTools.getRealScheme(request);
    request.setAttribute("url", scheme + "://" + request.getHeader("host") + request.getContextPath());
%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<link rel="stylesheet" href="${cacheFile['/admin/markdown/css/editormd.min.css']}"/>
<script src="${cacheFile['/admin/markdown/js/editormd.min.js']}"></script>
<c:if test='${lang eq "en"}'>
    <script src="${cacheFile['/admin/markdown/languages/en.js']}"></script>
</c:if>
<script src="${cacheFile['/assets/js/jquery.liteuploader.min.js']}"></script>
<script src="${cacheFile['/assets/js/select2/select2.min.js']}"></script>
<script src="${cacheFile['/admin/js/article_editor.js']}"></script>
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

    .switchery > small {
        width: 15px;
        height: 15px;
    }

    .switchery {
        height: 15px;
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
</style>
<div class="row">
    <div class="x_content">
        <form target="_blank" class="form-horizontal form-label-left" id="article-form">
            <textarea editormdTheme='${webs.editorMdTheme}' id="markdown"
                      style="display: none;">${log.mdContent}</textarea>
            <input type="hidden" id="logId" name="logId" value="${log.logId}">
            <textarea placeholder="${_res.editorPlaceholder}" id="content" name="content"
                      style="display: none;">${log.content}</textarea>
            <c:if test="${'off' ne webSite['article_thumbnail']}">
                <div class="form-group">
                    <div class="WriteCover-wrapper">
                        <div id="thumbnail-img" title="${_res['writeCover']}"
                             style="background: url('${log.thumbnail}')"
                             class="thumbnail-img img-responsive WriteCover-previewWrapper WriteCover-previewWrapper--empty">
                            <i id="camera-icon" class="WriteCover-uploadIcon fa fa-camera fa-3"></i>
                            <input type="file" accept=".jpeg, .jpg, .png" id="thumbnail-upload" name="imgFile" value="${log.thumbnail}"
                                   class="WriteCover-uploadInput">
                            <input type="hidden" name="thumbnail" id="thumbnail">
                        </div>
                    </div>
                </div>
            </c:if>

            <div class="col-md-12 col-sm-12 col-xs-12 col-lg-9">
                <div class="form-group">
                    <div class="row">
                        <div class="col-xs-7">
                            <input required name="title" id="title" maxlength="254" value="${log.title}"
                                   class="form-control"
                                   type="text" placeholder="${_res['inputArticleTitle']}"/>
                        </div>
                        <div class="col-xs-2" id="type-select-parent">
                            <select name="typeId" class="form-control select2_single">
                                <c:forEach items="${init.types}" var="type">
                                    <option
                                            <c:if test="${type.id eq log.typeId}">selected="selected"</c:if>
                                            value="${type.id}">${type.typeName}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-xs-3">
                            <input id="alias" type="text" class="form-control" placeholder="${_res['inputArticleAlias']}" name="alias" value="${log.alias}">
                        </div>
                    </div>
                </div>
                <div id="editormd"></div>
            </div>
            <div class="col-md-12 row col-lg-3">
                <div class="form-group col-xs-12 text-right">
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
                </div>
                <div class="form-group col-xs-12">
                    <div class="row">
                        <div class="col-xs-4">
                            <label><span style="font-size: 15px">${_res['commentAble']}</span>
                                <input type="checkbox" name="canComment"
                                       <c:if test="${log == null}">checked="checked"</c:if>
                                       <c:if test="${log['canComment']}">checked="checked"</c:if> class="js-switch"
                                       style="display: none;" data-switchery="true">

                            </label>
                        </div>
                        <div class="col-xs-4">
                            <label><span style="font-size: 15px">${_res['recommendable']}</span>
                                <input type="checkbox" name="recommended"
                                       <c:if test="${log.recommended}">checked="checked"</c:if> class="js-switch"
                                       style="display: none;" data-switchery="true">

                            </label>
                        </div>
                        <div class="col-xs-4">
                            <label><span style="font-size: 15px">${_res['private']}</span>
                                <input type="checkbox" name="private"
                                       <c:if test="${log['private']}">checked="checked"</c:if> class="js-switch"
                                       style="display: none;" data-switchery="true">

                            </label>
                        </div>
                    </div>
                </div>
                <div class="form-group col-xs-12">
                    <input value="${log.keywords}" class="form-control" placeholder="${_res["tagTips"]}"
                           type="text"
                           name="keywords" id="inp" size="60" maxlength="60"/>
                    <div class="tagsinput" id="tag" style="max-height: 300px;width: 100%">
                        <c:forEach items="${init.tags}" var="tags">
                            <span class="tag2"><i class="fa fa-tag"></i>${tags.text}</span>
                        </c:forEach>
                    </div>
                </div>

                <div class="form-group col-xs-12">
                    <div class="x_panel">
                        <div class="x_title">
                            <h2>${_res['digest']}</h2>
                            <ul class="nav navbar-right panel_toolbox">
                                <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a></li>
                            </ul>
                            <div class="clearfix"></div>
                        </div>
                        <div class="x_content">
                            <div class="form-group">
                            <textarea name="digest" class="form-control" placeholder="${_res['digestTips']}"
                                      cols="100" rows="3"
                                      style="width:100%; height:200px; z-index: 9999;">${log.digest}</textarea>
                            </div>
                        </div>
                    </div>

                </div>
                <div class="form-group col-xs-12">

                    <button class="btn btn-info" id="createOrUpdate" type="button">
                        <i class="fa fa-paper-plane bigger-110"></i>
                        ${_res['save']}
                    </button>
                </div>

            </div>
        </form>
    </div>
</div>
