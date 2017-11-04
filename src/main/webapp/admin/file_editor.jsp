<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page session="false" pageEncoding="UTF-8" %>
<%
    String scheme = com.zrlog.web.util.WebTools.getRealScheme(request);
    request.setAttribute("url", scheme + "://" + request.getHeader("host") + request.getContextPath());
    if ("template".equals(request.getParameter("editType"))) {
        request.setAttribute("tips", "主题编辑功能建议仅用于临时变更");
    }
    request.setAttribute("filePaths", com.zrlog.web.interceptor.TemplateHelper.getFiles(request.getParameter("path")));
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
<div class="page-header">
    <h3>
        文件编辑
    </h3>
</div>
<h4 class="text-right">${tips}</h4>
<form id="saveFileForm">
    <div class="form-group">
        <select name="file" id="form-field-select-6" class="form-control">
            <c:forEach var="filePath" items="${filePaths}">
                <option value="${filePath}">${filePath}</option>
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
