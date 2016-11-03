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
	<div class="col-sm-9"></div>
<div class="form-group col-sm-3">
	<input type="file" class="fileUpload" name="imgFile" value="上传" />
</div>
	<c:forEach items="${templates}" var="template">
	<div class="col-lg-3 col-sm-4">
		<div class="thumbnail" style="height:350px">
		  <div class="image view view-first" style="height:280px">
			<c:forEach items="${template.previewImages}" var="image">
				<img style="width: 100%; display: block;" src="${image}">
			</c:forEach>
			<div class="mask">
				<div style="height:210px">
				  <p>${template.digest }</p>
				</div>
			  <div class="tools tools-bottom">
				<!--<a href="${template.url }" target="_blank" title="${template.author }">${_res['admin.theme.user']}:<i class="fa fa-user"></i></a>-->
				<a href="admin/blank?menu=1&include=editor/edit&path=${template.template}&editType=${_res['admin.theme']}"><i class="fa fa-pencil"></i></a>
				<a target="_blank" href="admin/template/preview?template=${template.template}&resultType=html"><i class="fa fa-eye"></i></a>
				<a href="admin/template/configPage?template=${template.template}"><i class="fa fa-cog"></i></a>
				<a href="admin/template/apply?template=${template.template}&resultType=html"><i class="fa fa-check"></i></a>
			  </div>
			</div>
		  </div>
		  <div class="caption">
			<h3>${template.name }
			<c:choose>
				<c:when test="${template.template eq webs.template}">
					 - (${_res['admin.theme.inUse']})
				</c:when>
            </c:choose></h3>
		  </div>
		</div>
	  </div>
	</c:forEach>
	</div>
	<hr/>
<a href="admin/template_center"><button class="btn btn-info"><i class="fa fa-cloud-download"></i>${_res['admin.theme.download']}</button></a>
</div><!-- /span -->
</div>
</div><!-- /row -->
<jsp:include page="include/footer.jsp"/>