<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String scheme = com.fzb.blog.web.util.WebTools.getRealScheme(request);
String basePath = scheme+"://"+request.getServerName()+":"+request.getServerPort()+path+"/"+"admin/";
request.setAttribute("url", scheme+"://"+request.getHeader("host")+request.getContextPath());
%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<link rel="stylesheet" href="${cacheFile['/admin/markdown/css/editormd.min.css']}" />
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
.form-control {
height: 38px;
}

</style>
<script src="${cacheFile['/admin/markdown/js/editormd.min.js']}"></script>
<c:if test='${lang eq "en"}'>
<script src="${cacheFile['/admin/markdown/languages/en.js']}"></script>
</c:if>
<script src="${cacheFile['/assets/js/select2/select2.min.js']}"></script>
<script src="${cacheFile['/admin/js/article_editor.js']}"></script>
<div class="row">
<div class="col-md-1" style="float:right">
    <a id="preview-link" target="_blank" style="display:none">
        <button class="btn btn-block" id="preview" type="button">
            <i class="fa fa-eye bigger-110"></i>
            预览
        </button>
    </a>
</div>
<div class="x_content">
<form target="_blank" class="form-horizontal form-label-left" id="article-form">
	<textarea editormdTheme='${webs.editorMdTheme}' id="markdown" style="display: none;">${log.mdContent}</textarea>
	<input type="hidden" id="logId" name="logId" value="${log.logId}">
	<textarea placeholder="${_res.editorPlaceholder}" id="content" name="content" style="display: none;">${log.content}</textarea>
	<div class="form-group">
	<div class="col-xs-7">
	<input  name="title" id="title" size="60" maxlength="60"  value="${log.title}" class="form-control" type="text" placeholder="请输入文章标题"></input>
	</div>
	<div class="col-xs-2">
	<select name="typeId" class="form-control select2_single">
	  <c:forEach items="${init.types}" var="type">
		<option <c:if test="${type.id eq log.typeId}">selected="selected"</c:if> value="${type.id}">${type.typeName}</option>
	  </c:forEach>
	</select>
	</div>
	<div class="col-xs-3">
	<input id="alias" type="text" class="form-control"  placeholder="请输入别名"  name="alias" value="${log.alias}">
	</div>
	</div>

	<div class="col-xs-12">
	<div id="editormd"></div>
	</div>
	<div class="form-group">
	<div class=" col-xs-6">
	<input value="${log.keywords}" class="form-control" placeholder="设置关键字，用逗号隔开，建议不超过5个" type="text" name="keywords" id="inp" size="60" maxlength="60" />
	</div>
	<div class="col-xs-12">
	<div class="tagsinput" id="tag" style="width: 100%">
	<c:forEach items="${init.tags}" var="tags">
		<span class="tag2"><i class="fa fa-tag"></i>${tags.text}</span>
	</c:forEach>
	</div>
	</div>
	</div>
	<div class="form-group">
	<div class="col-xs-4">
		<label>
			<input type="checkbox" name="canComment" <c:if test="${log == null}">checked="checked"</c:if> <c:if test="${log['canComment']}">checked="checked"</c:if> class="js-switch" style="display: none;" data-switchery="true">
			发布评论
		</label>
	</div>
	<div class="col-xs-4">
		<label>
			<input type="checkbox" name="recommended" <c:if test="${log.recommended}">checked="checked"</c:if> class="js-switch" style="display: none;" data-switchery="true">
			推荐
		</label>
	</div>
	<div class="col-xs-4">
		<label>
			<input type="checkbox" name="private" <c:if test="${log['private']}">checked="checked"</c:if> class="js-switch" style="display: none;" data-switchery="true">
			不公开
		</label>
	</div>
	</div>
	<div class="form-group col-xs-12">
		<div class="x_panel">
		  <div class="x_title">
			<h2>摘要</h2>
			<ul class="nav navbar-right panel_toolbox">
			  <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a></li>
			</ul>
			<div class="clearfix"></div>
		  </div>
		  <div class="x_content">
		  <div class="form-group">
		  	<textarea name="digest" class="form-control"  placeholder="一段好的摘要，能为你的读者提供一个非常好的引导。" cols="100" rows="3" style="width:100%; height:180x; z-index: 9999;">${log.digest}</textarea>
		  </div>
		  </div>
		</div>

	</div>
	<div class="form-group col-xs-12">
		<div class="col-md-offset-5 col-md-6">
			<button class="btn btn-primary" id="saveToRubbish" type="button">
				${_res['saveAsDraft']}
			</button>
			<button class="btn btn-info" id="createOrUpdate" type="button">
				<i class="fa fa-save bigger-110"></i>
				${_res['save']}
			</button>
		</div>
	</div>
</form>
</div>
</div>
