<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page import="java.io.File"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getHeader("host")+path+"/";
request.setAttribute("url", request.getScheme()+"://"+request.getHeader("host")+request.getContextPath());
request.setAttribute("suburl", request.getRequestURL().substring(basePath.length()));
%>
<link rel="stylesheet" href="admin/markdown/lib/codemirror/codemirror.min.css">
<link rel="stylesheet" href="admin/markdown/lib/codemirror/addon/dialog/dialog.css">
<link rel="stylesheet" href="admin/markdown/lib/codemirror/addon/search/matchesonscrollbar.css">
<script src="admin/markdown/lib/codemirror/codemirror.min.js"></script>
<script src="admin/markdown/lib/codemirror/modes.min.js"></script>

<script src="admin/markdown/lib/codemirror/addon/dialog/dialog.js"></script>
<script src="admin/markdown/lib/codemirror/addon/search/searchcursor.js"></script>
<script src="admin/markdown/lib/codemirror/addon/search/search.js"></script>
<script src="admin/markdown/lib/codemirror/addon/scroll/annotatescrollbar.js"></script>
<script src="admin/markdown/lib/codemirror/addon/search/matchesonscrollbar.js"></script>
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

<script type="text/javascript">
	$(function(){
		var editor;
		$("#saveFile").click(function(){
			$("#code").val(editor.getValue());
			$.post("admin/template/saveFile", $("#saveFileForm").serialize(),function(data){
				if(data.status){
					$.gritter.add({
						title: '  操作成功...',
						class_name: 'gritter-success' + (!$('#gritter-light').get(0).checked ? ' gritter-light' : ''),
					});
				}else{
					$.gritter.add({
						title: '  发生了一些异常...',
						class_name: 'gritter-error' + (!$('#gritter-light').get(0).checked ? ' gritter-light' : ''),
					});
				}
			});
		});
		

		loadFile($("#form-field-select-6").children('option:selected').val());
		$("#form-field-select-6").change(function(){
			loadFile($(this).children('option:selected').val()); 
		});
		function loadFile(file){
			$.get("admin/template/loadFile?file="+file,function(data){
				$("div").remove(".CodeMirror");
				$("#code").text(data.fileContent);
				editor = CodeMirror.fromTextArea(document.getElementById("code"), {
					  lineNumbers: true
				});
				 var val = file;
				  if (m = /.+\.([^.]+)$/.exec(val)) {
				    var info = CodeMirror.findModeByExtension(m[1]);
				    if (info) {
				      mode = info.mode;
				      spec = info.mime;
				    }
				  } else if (/\//.test(val)) {
				    var info = CodeMirror.findModeByMIME(val);
				    if (info) {
				      mode = info.mode;
				      spec = val;
				    }
				  } else {
				    mode = spec = val;
				  }
				  if (mode) {
				    editor.setOption("mode", spec);
				  } else {
				    alert("Could not find a mode corresponding to " + val);
				  }
			});
		}
	});
</script>
		<div class="page-header">
			<h1>
				文件编辑
				<%-- <small>
					<i class="icon-double-angle-right"></i>
					<%=new String(request.getParameter("editType").getBytes(),"UTF-8") %>
				</small> --%>
			</h1>
		</div><!-- /.page-header -->
		<form id="saveFileForm">
			 <select name="file"  id="form-field-select-6" class="form-control">
				<%!
					 private List<String> fileList=new ArrayList<String>();
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
					/**
				     * 根据文件后缀 查找符合要求文件列表
				     * @param path
				     * @param prefix
				     */
				     fileList.clear();
				     getFilesByPrefix(request.getRealPath(request.getParameter("path")),".jsp",".js",".css",".html");
					// need JRE1.7
					/* String webPath=request.getServletContext().getRealPath("/");
					File[] templates=new File(request.getServletContext().getRealPath("/include/templates/")).listFiles(); */
					String webPath=request.getRealPath("/");
					
					List<String> strFile=new ArrayList<String>();
					for(int i=0;i<fileList.size();i++){
						strFile.add(fileList.get(i).toString().substring(webPath.length()-1).replace('\\', '/'));
					}
					request.setAttribute ("templates", strFile);
					%>
				<c:forEach var="template" items="${templates}">
					<option  value="<c:out value="${template }"/>" <c:if test="${webs.template eq  template}">selected="selected"</c:if>  ><c:out value="${template }" /></option>
				</c:forEach>
				</select><br>
		<textarea id="code" name="content">
		${fileContent}
		</textarea>
		</form>
		<div class="clearfix form-actions">
			<div class="col-md-offset-1">
				<button class="btn btn-info" type="button" id="saveFile">
					<i class="icon-ok bigger-110"></i> 保存
				</button>
 			</div>
		</div>
