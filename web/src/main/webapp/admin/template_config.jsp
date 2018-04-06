<%@ page session="false" pageEncoding="UTF-8" %>
<div class="page-header">
    <h3>
        主题设置
        <small> / ${templateInfo.name}</small>
    </h3>
</div>
<div class="row">
    <div class="col-md-6">
        <jsp:include page="${include}.jsp"/>
    </div>
</div>
${pageEndTag}