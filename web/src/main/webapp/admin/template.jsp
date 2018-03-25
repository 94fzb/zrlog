<%@ page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="include/menu.jsp"/>
<script src="${cacheFile['/assets/js/jquery.liteuploader.min.js']}"></script>
<script src="${cacheFile['/admin/js/template.js']}"></script>
<style>
    input[type="file"] {
        display: none;
    }

    .custom-file-upload {
        cursor: pointer;
    }
</style>
<div class="page-header">
    <h3>
        ${_res['admin.template.manage']}
    </h3>
</div>
<div class="row">
    <div class="form-group col-sm-offset-11 col-sm-1">
        <label for="fileUpload" class="custom-file-upload btn btn-primary btn-round">
            <i class="fa fa-cloud-upload"></i>&nbsp;${_res['upload']}
        </label>

        <input type="file" id="fileUpload" class="fileUpload" name="file" value=""/>
    </div>
</div>
<div class="row">
    <div class="divider"></div>
</div>
<div class="row">
    <c:forEach items="${templates}" var="template">
        <div class="col-md-3 col-xs-12 widget widget_tally_box">
            <div class="x_panel ui-ribbon-container fixed_height_440">
                <c:choose>
                    <c:when test="${template.template eq webs.template}">
                        <div class="ui-ribbon-wrapper">
                            <div class="ui-ribbon">
                                    ${_res['admin.theme.inUse']}
                            </div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <c:if test="${template.template eq previewTemplate}">
                            <div class="ui-ribbon-wrapper">
                                <div class="ui-ribbon">
                                        ${_res['admin.theme.inPreview']}
                                </div>
                            </div>
                        </c:if>
                    </c:otherwise>
                </c:choose>
                <div class="x_title">
                    <h2>${template.name }</h2>
                    <div class="clearfix"></div>
                </div>
                <div class="x_content">
                    <c:forEach items="${template.previewImages}" var="image">
                        <img style="width: 100%" src="${image}">
                    </c:forEach>
                    <p style="padding-top: 10px">${template.digest }</p>
                    <div class="divider"></div>
                    <div class="caption">
                        <div class="tools tools-bottom text-center" style="font-size: 16px;">
                            <!--<a href="${template.url }" target="_blank" title="${template.author }">${_res['admin.theme.user']}:<i class="fa fa-user"></i></a>-->
                            <a href="admin/blank?menu=1&include=file_editor&path=${template.template}&editType=template"><i
                                    class="fa fa-pencil"></i></a>
                            <a target="_blank"
                               href="admin/template/preview?template=${template.template}"><i
                                    class="fa fa-eye"></i></a>
                            <c:if test="${template.configAble}">
                                <a href="admin/template/config?template=${template.template}"><i
                                        class="fa fa-cog"></i></a>
                            </c:if>
                            <c:if test="${template.deleteAble}"><a href="#" class="delete-btn"
                                                                   template="${template.template}"><i
                                    class="fa fa-remove"></i></a></c:if>
                            <a class="apply-btn" href="#" template="${template.template}"><i
                                    class="fa fa-check"></i></a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </c:forEach>
</div>
<div class="row">
    <div class="divider"></div>
</div>
<div class="row">
    <div class="col-md-2">
        <a href="admin/template_center">
            <button class="btn btn-dark btn-round"><i
                    class="fa fa-cloud-download"></i>&nbsp;${_res['admin.theme.download']}</button>
        </a>
    </div>
</div>
<br/>
<jsp:include page="include/footer.jsp"/>