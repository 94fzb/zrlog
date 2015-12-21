<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<jsp:include page="header.jsp" />
<div data-target="#step-container" class="row-fluid" id="fuelux-wizard">
	<ul class="wizard-steps">
		<li class="" data-target="#step1"><span class="step">1</span> <span
			class="title">数据库信息</span></li>

		<li data-target="#step2" class=""><span class="step">2</span> <span
			class="title">网站信息</span></li>

		<li data-target="#step3" class="active"><span class="step">3</span>
			<span class="title">完成</span></li>
	</ul>
</div>
<div id="step-container"
	class="step-content row-fluid position-relative">

	<div id="step3" class="step-pane active">
		<div class="main">
			<div class="center">
				<h3 class="green">安装完成!</h3>
				<a href="${basePath }">点击查看</a>
			</div>
		</div>

	</div>
</div>
<jsp:include page="footer.jsp" />
