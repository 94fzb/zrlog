<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<jsp:include page="include/menu.jsp"/>
	<div class="page-header">
		<h1>
			${mTitle}
			<small>
				<i class="fa fa-double-angle-right"></i>
				${sTitle}
			</small>
		</h1>
	</div>
	<div class="row">
		<div class="col-xs-12">
<jsp:include page="${param.include }"/>
		</div>
	</div>

<jsp:include page="include/footer.jsp"/>
