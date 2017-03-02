<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<jsp:include page="include/menu.jsp"/>
<div class="row">
<div class="col-xs-12">
<div class="alert alert-block alert-success">
	<p>
		${message}
	</p>
	<p>
		<a href="javascript:history.go(-1);"><button class="btn btn-sm btn-success">${_res.goBack}</button></a>
	</p>
</div>
</div>
</div>
<jsp:include page="include/footer.jsp" />