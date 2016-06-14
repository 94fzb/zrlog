<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/"+"admin/";
request.setAttribute("url", request.getScheme()+"://"+request.getHeader("host")+request.getContextPath());
request.setAttribute("suburl", request.getRequestURL().substring(basePath.length()));
%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<link rel="stylesheet" href="admin/markdown/css/editormd.min.css" />
<link rel="stylesheet" href="assets/css/jquery.gritter.css" />
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
</style>
<script src="admin/markdown/js/editormd.min.js"></script>
<script src="admin/js/editor.js"></script>
<script src="assets/js/jquery.gritter.min.js"></script>
<script src="assets/js/bootbox.min.js"></script>
<input id="gritter-light" checked="" type="checkbox" class="ace ace-switch ace-switch-5" />
<div class="row">
	<div class="col-xs-12">
<form target="_blank" class="form-horizontal" role="form" id="addorPre" method="post" action="admin/log/preview">
	<textarea id="markdown" style="display: none;">${log.mdContent}</textarea>
	<input type="hidden" id="logId" name="logId" value="${log.logId}">
	<textarea placeholder="${_res.editorPlaceholder}" id="content" name="content" style="display: none;">${log.content}</textarea>

	<input name="title" id="title" size="60" maxlength="60"  value="${log.title}" class="col-xs-7" type="text" placeholder="请输入文章标题"></input>
	<div class="col-xs-2">
	<select name="typeId" class="form-control" style="height:28px">
	  <c:forEach items="${init.types}" var="type">
		<option <c:if test="${type.id eq log.typeId}">selected="selected"</c:if> value="${type.id}">${type.typeName}</option>
	  </c:forEach>
	</select>
	</div>
	<input id="alias" type="text" class="col-xs-3"  placeholder="请输入别名"  name="alias" value="${log.alias}">

	<div id="editormd"></div>
	<input value="${log.keywords}" class="col-xs-6" placeholder="设置关键字，用逗号隔开，建议不超过5个" type="text" name="keywords" id="inp" size="60" maxlength="60" />
	<div class="tags" id="tag" style="width: 100%">
	<c:forEach items="${init.tags}" var="tags">
		<span class="tag"><i class="icon-tag"></i> ${tags.text}</span>
	</c:forEach>
	</div>
	<hr/>
	<div class="col-xs-4">
		<label>
			<input class="ace ace-switch ace-switch-6" type="checkbox" checked="checked" name="canComment">
			<span class="lbl">&nbsp;发布评论</span>
		</label>
	</div>
	<div class="col-xs-4">
		<label>
			<input class="ace ace-switch ace-switch-6" type="checkbox" <c:if test="${log.recommended}">checked="checked"</c:if>  name="recommended">
			<span class="lbl">&nbsp;推荐</span>
		</label>
	</div>
	<div class="col-xs-4">
		<label>
			<input class="ace ace-switch ace-switch-6" type="checkbox" <c:if test="${log['private']}">checked="checked"</c:if> name="private">
			<span class="lbl">&nbsp;不公开</span>
		</label>
	</div>
	<br/>
	<hr/>
	<div class="widget-box collapsed">
		<div data-action="collapse" class="widget-header widget-header-small  header-color-blue">
			<div  class="widget-toolbar inline">
				摘要<a href="#"> <i class="icon-chevron-down"></i>
				</a>
			</div>
		</div>

		<div class="widget-body">
			<div class="widget-body-inner" style="display: none;">
				<textarea name="digest"  placeholder="一段好的摘要，能为你的读者提供一个非常好的引导。" cols="100" rows="3" style="width:100%; height:180x; z-index: 9999;">${log.digest}</textarea>
			</div>
		</div>
	</div>
	<div class="clearfix form-actions">
		<div class="col-md-offset-3 col-md-9">
			<button class="btn btn-info" id="saveToRubbish" type="button">
				<i class="icon-pencil bigger-110"></i>
				存为草稿
			</button>
			<button class="btn btn-info" id="createOrUpdate" type="button">
				<i class="icon-save bigger-110"></i>
				保存
			</button>
			<button class="btn" type="button" id="preview">
				<i class="icon-eye-open bigger-110"></i>
				预览
			</button>
		</div>
	</div>
</form>
</div>
</div>