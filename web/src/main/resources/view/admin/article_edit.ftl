<link rel="stylesheet" href="${basePath}admin/markdown/css/editormd.min.css"/>
<link rel="stylesheet" href="${basePath}admin/summernote/summernote-bs4.css"/>
<link rel="stylesheet" href="${basePath}assets/css/video-js.css"/>
<link rel="stylesheet" href="${basePath}assets/css/icheck-blue.css"/>
<link rel="stylesheet" href="${basePath}admin/css/article_edit.css"/>
<script src="${basePath}assets/js/icheck.min.js"></script>
<script>
    var skipFirstRubbishSave = ${skipFirstRubbishSave?c};
    var article = ${article};
    var uploadUrl = 'api/admin/upload/';
    var editorMdPath = "admin/markdown/lib/";
    var katexPath = "assets/js/katex/";
    var editorLang = "admin/markdown/languages/";
    var summernoteLang = "admin/summernote/lang/";
    var editorMdJs = '${basePath}admin/markdown/js/editormd.min.js';
    var summernoteJs = '${basePath}admin/summernote/summernote-bs4.min.js';
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
<div class="page-header">
    <h3>
    ${_res['admin.log.edit']}
    </h3>
</div>
<form class="form-horizontal form-label-left" id="article-form" style="max-width: 1795px">
    <input type="hidden" id="id" name="id">
    <input type="hidden" name="editorType">
    <textarea placeholder="${_res.editorPlaceholder}" style="display: none;"></textarea>
    <div class="row">
        <div class="col-md-12 col-sm-12 col-xs-12" style="padding-bottom: 10px;">
            <div class="text-right">
                <a id="preview-link" target="_blank" style="display:none">
                    <button class="btn btn-outline-dark" id="preview" type="button">
                        <i class="fa fa-eye bigger-110"></i>
                    ${_res['preview']}
                    </button>
                </a>
                <button class="btn btn-outline-primary" id="saveToRubbish" type="button">
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
                    <div class="col-md-6 col-xs-12" id="title-parent">
                        <input required name="title" id="title" maxlength="254" value=""
                               class="form-control"
                               type="text" placeholder="${_res['inputArticleTitle']}"/>
                    </div>
                    <div class="col-md-3 col-xs-12 full-screen-hide">
                        <div class="input-group">
                            <div class="input-group-prepend">
                                <span class="input-group-text">/${post}</span>
                            </div>
                            <input type="text" id="alias" name="alias" class="form-control"
                                   placeholder="${_res['inputArticleAlias']}"
                                   aria-describedby="validationTooltipUsernamePrepend">
                        </div>
                    </div>
                    <div class="col-md-3 col-xs-12 full-screen-hide">
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
                    <div id="editorDiv" theme="${website.editorMdTheme!''}"></div>
                </div>
            </div>
        </div>
        <div class="col-md-12 col-lg-3 col-xs-12 full-screen-hide">
            <div class="row">
                <div class="form-group col-xs-12">
                    <#if website['article_thumbnail_status']?? && website['article_thumbnail_status'] == '1'>
                        <div class="x_panel">
                            <div id="thumbnail-img" title="${_res['writeCover']}"
                                 style="background-color:rgba(0,0,0,.075);"
                                 class="thumbnail-img img-responsive WriteCover-wrapper WriteCover-previewWrapper">
                                <i id="camera-icon" class="WriteCover-uploadIcon fa fa-camera fa-3"></i>
                                <input type="file" id="thumbnail-upload" name="imgFile" class="WriteCover-uploadInput">
                                <input type="hidden" name="thumbnail" id="thumbnail">
                            </div>
                        </div>
                    </#if>
                    <div class="x_panel">
                        <div class="x_title">
                            <h2>${_res['admin.setting']}</h2>
                            <div class="clearfix"></div>
                        </div>
                        <div class="x_content">
                            <div class="row">
                                <div class="col-md-7">
                                    <label><span style="font-size: 16px">${_res['commentAble']}</span>
                                        <input type="checkbox" name="canComment"
                                        <#if log?? && log['canComment']>checked="checked"</#if>
                                               class="js-switch" style="display: none;" data-switchery="true">

                                    </label>
                                </div>
                                <div class="col-md-5">
                                    <label><span style="font-size: 16px">${_res['recommendable']}</span>
                                        <input type="checkbox" name="recommended"
                                               <#if log?? && log.recommended>checked="checked"</#if> class="js-switch"
                                               style="display: none;" data-switchery="true">

                                    </label>
                                </div>
                            </div>
                            <div class="row">
                                <div class="col-md-6" id="privateCheckbox">
                                    <label><span style="font-size: 16px">${_res['private']}</span>
                                        <input type="checkbox" name="privacy"
                                               <#if log?? && log['privacy']>checked="checked"</#if>
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
                            <#list init.types as type>
                                <div style="font-size: 16px" class="row">
                                    <div class="col-lg-12">
                                        <input class="icheck flat" name="typeId" type="radio"
                                               <#if log?? && type.id == log.typeId>checked="checked"</#if>
                                               value="${type.id}"><span
                                            style="padding-left: 5px">${type.typeName}</span>
                                    </div>
                                </div>
                            </#list>
                        </div>
                    </div>
                    <div class="x_panel">
                        <div class="x_title">
                            <h2>${_res['tag']}</h2>
                            <div class="clearfix"></div>
                        </div>
                        <div class="x_content">
                            <input class="form-control" placeholder="${_res['tagTips']}" type="hidden" name="keywords"
                                   id="keywordsVal" size="60" maxlength="60"/>
                            <div class="tagsinput" id="keywords"></div>
                            <div class="tagsinput" id="unCheckedTag" style="max-height: 145px;width: 100%">
                                <#list init.tags as tags>
                                    <span class="tag2" val="${tags.text}"><i style="padding-right: 5px"
                                                                             class="fa fa-tag"></i>${tags.text}</span>
                                </#list>
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
                                      style="width:100%; height:200px; z-index: 9999;"></textarea>
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