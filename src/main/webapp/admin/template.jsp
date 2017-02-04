<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="include/menu.jsp"/>
<script src="assets/js/jquery.liteuploader.min.js"></script>
<script src="admin/js/template.js"></script>
<style>
.thumbnail {
    text-align: center;
    background-image: url(assets/images/premium-bg.png);
    margin-bottom: 15px;
}
.thumbnail .caption {
    padding: 9px;
    color: #333;
}
</style>
<div class="page-header">
	<h3>
	${_res['admin.theme.manage']}
	</h3>
</div>
<div class="row">
<div>
	<div class="col-sm-9"></div>
        <div class="form-group col-sm-3">
	    <input type="file" class="fileUpload" class="form-control" name="file" value="上传" />
	    <br/>
</div>
<div>
<div class="rows">
	<c:forEach items="${templates}" var="template">
	 <div class="col-md-3 col-xs-12 widget widget_tally_box">
         <div class="x_panel ui-ribbon-container fixed_height_390">
           <c:choose>
            <c:when test="${template.template eq webs.template}">
                <div class="ui-ribbon-wrapper">
                 <div class="ui-ribbon">
                   ${_res['admin.theme.inUse']}
                 </div>
               </div>
            </c:when>
           </c:choose>
           <div class="x_title">
             <h2>${template.name }</h2>
             <div class="clearfix"></div>
           </div>
           <div class="x_content">
            <c:forEach items="${template.previewImages}" var="image">
                <img style="width: 244px; height: 230px;" src="${image}">
            </c:forEach>
            <p>${template.digest }</p>
            <div class="divider"></div>
            <div class="caption">
             <div class="tools tools-bottom text-center" style="font-size: 14px;">
                <!--<a href="${template.url }" target="_blank" title="${template.author }">${_res['admin.theme.user']}:<i class="fa fa-user"></i></a>-->
                <a href="admin/blank?menu=1&include=file_editor&path=${template.template}&editType=template"><i class="fa fa-pencil"></i></a>
                <a target="_blank" href="admin/template/preview?template=${template.template}&resultType=html"><i class="fa fa-eye"></i></a>
                <a href="admin/template/config?template=${template.template}"><i class="fa fa-cog"></i></a>
                <a class="apply-btn" href="#" template="${template.template}"><i class="fa fa-check"></i></a>
              </div>
              </div>
           </div>
         </div>
       </div>
	</c:forEach>
	</div>
	</div>
	<hr/>
</div><!-- /span -->
</div>
<hr/>
<div class="row">
<div class="col-md-2">
<a href="admin/template_center"><button class="btn btn-info"><i class="fa fa-cloud-download"></i>${_res['admin.theme.download']}</button></a>
</div>
</div>
</div><!-- /row -->
<jsp:include page="include/footer.jsp"/>