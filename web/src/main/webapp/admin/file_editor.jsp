<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page session="false" pageEncoding="UTF-8" %>
<%
    String scheme = com.zrlog.web.util.WebTools.getRealScheme(request);
    request.setAttribute("url", scheme + "://" + request.getHeader("host") + request.getContextPath());
    if ("template".equals(request.getParameter("editType"))) {
        request.setAttribute("tips", "主题编辑建议仅用于临时变更");
    }
    request.setAttribute("filePaths", com.zrlog.web.interceptor.TemplateHelper.getFiles(request.getParameter("path")));
%>
<link rel="stylesheet" href="${basePath}admin/markdown/lib/codemirror/codemirror.min.css"/>
<link rel="stylesheet" href="${basePath}admin/markdown/lib/codemirror/addon/dialog/dialog.css"/>
<link rel="stylesheet" href="${basePath}admin/markdown/lib/codemirror/addon/search/matchesonscrollbar.css"/>
<script src="${basePath}admin/markdown/lib/codemirror/codemirror.min.js"></script>
<script src="${basePath}admin/markdown/lib/codemirror/modes.min.js"></script>
<script src="${basePath}admin/markdown/lib/codemirror/addon/dialog/dialog.js"></script>
<script src="${basePath}admin/markdown/lib/codemirror/addon/search/searchcursor.js"></script>
<script src="${basePath}admin/markdown/lib/codemirror/addon/search/search.js"></script>
<script src="${basePath}admin/markdown/lib/codemirror/addon/scroll/annotatescrollbar.js"></script>
<script src="${basePath}admin/markdown/lib/codemirror/addon/search/matchesonscrollbar.js"></script>
<script src="${basePath}admin/js/file_editor.js"></script>
<style>
    .CodeMirror {
        border: 1px solid #ccc;
        height: 600px;
        border-radius: 4px;
        width: 100%;
    }

    #code {
        border: 1px solid #ccc;
        height: 600px;
        border-radius: 4px;
        width: 100%;
    }

    dt {
        font-family: monospace;
        color: #666;
    }

    option {
        padding-top: 8px;
        height: 30px;

    }

</style>
<div class="page-header">
    <h3>
        ${_res['fileEdit']}
        <small id="editing"></small>
    </h3>
</div>
<h4 class="text-right">${tips}</h4>
<div>
    <form id="saveFileForm">
        <input type="hidden" id="basePath" value="${param.path}">
        <input type="hidden" name="file" id="file">
        <textarea name="content" id="content" hidden></textarea>
        <div class="row">
            <div class="form-group">
                <div class="col-md-10">
                    <textarea id="code"></textarea>
                </div>
                <div class="col-md-2">
                    <select multiple style="height: 600px" id="form-field-select-6"
                            class="form-control">
                        <c:forEach var="filePath" items="${filePaths}">
                            <option value="${filePath}">${filePath}</option>
                        </c:forEach>
                    </select>
                </div>
            </div>
        </div>

        <div class="ln_solid"></div>
        <div class="col-md-offset-1">
            <button class="btn" type="button" id="saveFile" style="color: #fff;background-color: #5bc0de;border-color: #46b8da;">
                 ${_res['submit']}
            </button>
        </div>
    </form>
</div>
${pageEndTag}