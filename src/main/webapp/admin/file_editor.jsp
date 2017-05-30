<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page import="java.io.File"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String scheme = com.fzb.blog.web.util.WebTools.getRealScheme(request);
String basePath = scheme+"://"+request.getHeader("host")+path+"/";
request.setAttribute("url", scheme+"://"+request.getHeader("host")+request.getContextPath());
%>
<%!
	 private List<String> fileList=new ArrayList<String>();
	 /**
     	 * 根据文件后缀 查找符合要求文件列表
     	 * @param path
     	 * @param prefix
     	 */
	 private void getFilesByPrefix(String path,String... prefix){
		File file[]=new File(path).listFiles();

		for(File f:file)
		{
			if(f.isDirectory() && new File(f.getAbsolutePath()).listFiles()!=null){
				getFilesByPrefix(f.getAbsolutePath(), prefix);
			}
			else{
				for(String pre:prefix){
					if(f.getAbsoluteFile().toString().endsWith(pre)){
						fileList.add(f.getAbsoluteFile().toString());
					}
				}
			}
		}
	}
%>
<%
	fileList.clear();
	getFilesByPrefix(request.getRealPath(request.getParameter("path")),".jsp",".js",".css",".html");
	String webPath=request.getRealPath("/");

	List<String> strFile=new ArrayList<String>();
	for(int i=0;i<fileList.size();i++){
		strFile.add(fileList.get(i).toString().substring(webPath.length()-1).replace('\\', '/'));
	}
	request.setAttribute ("templates", strFile);
	if("template".equals(request.getParameter("editType"))){
	    request.setAttribute("tips","主题编辑功能建议仅用于临时变更");
	}
%>
<link rel="stylesheet" href="${cacheFile['/admin/markdown/lib/codemirror/codemirror.min.css']}">
<link rel="stylesheet" href="${cacheFile['/admin/markdown/lib/codemirror/addon/dialog/dialog.css']}">
<link rel="stylesheet" href="${cacheFile['/admin/markdown/lib/codemirror/addon/search/matchesonscrollbar.css']}">
<script src="${cacheFile['/admin/markdown/lib/codemirror/codemirror.min.js']}"></script>
<script src="${cacheFile['/admin/markdown/lib/codemirror/modes.min.js']}"></script>
<script src="${cacheFile['/admin/markdown/lib/codemirror/addon/dialog/dialog.js']}"></script>
<script src="${cacheFile['/admin/markdown/lib/codemirror/addon/search/searchcursor.js']}"></script>
<script src="${cacheFile['/admin/markdown/lib/codemirror/addon/search/search.js']}"></script>
<script src="${cacheFile['/admin/markdown/lib/codemirror/addon/scroll/annotatescrollbar.js']}"></script>
<script src="${cacheFile['/admin/markdown/lib/codemirror/addon/search/matchesonscrollbar.js']}"></script>
<script src="${cacheFile['/admin/js/file_editor.js']}"></script>
<style type="text/css">
.CodeMirror {
	border-top: 1px solid black;
	border-bottom: 1px solid black;
}
dt {
	font-family: monospace;
	color: #666;
}
</style>
<div class="page-header">
	<h3>
		文件编辑
	</h3>
</div>
<h4 class="text-right">${tips}</h4>
<form id="saveFileForm">
<div class="form-group">
	 <select name="file"  id="form-field-select-6" class="form-control">
		<c:forEach var="template" items="${templates}">
			<option  value="<c:out value="${template }"/>" <c:if test="${webs.template eq  template}">selected="selected"</c:if>  ><c:out value="${template }" /></option>
		</c:forEach>
	</select>
</div>
<div class="form-group">
<textarea id="code" name="content">
${fileContent}
</textarea>
</div>
</form>
<div class="ln_solid"></div>
<div class="form-group">
	<div class="col-md-offset-1">
		<button class="btn btn-info" type="button" id="saveFile">
			<i class="fa fa-check bigger-110"></i> ${_res['submit']}
		</button>
	</div>
</div>
