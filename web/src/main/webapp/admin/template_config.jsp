<%@ page session="false" pageEncoding="UTF-8" %>
<style>
    input {
        height: 36px;
        border: 1px solid #ccc;
    }

    input[type="file"] {
        display: none;
    }

    .custom-file-upload {
        border: 1px solid #ccc;
        display: inline-block;
        padding: 6px 12px;
        cursor: pointer;
    }
</style>
<div class="page-header">
    <h3>
        主题设置
        <small> / ${templateInfo.name}</small>
    </h3>
</div>
<div class="row">
    <div class="col-md-6 col-xs-12 col-sm-12">
        <jsp:include page="${include}.jsp"/>
    </div>
</div>
${pageEndTag}