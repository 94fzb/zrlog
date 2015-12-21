<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<jsp:include page="header.jsp" />
<div data-target="#step-container" class="row-fluid" id="fuelux-wizard">
	<ul class="wizard-steps">
		<li class="active" data-target="#step1"><span class="step">1</span>
			<span class="title">数据库信息</span></li>

		<li data-target="#step2" class=""><span class="step">2</span> <span
			class="title">网站信息</span></li>

		<li data-target="#step3" class=""><span class="step">3</span> <span
			class="title">完成</span></li>
	</ul>
</div>
<div id="step-container"
	class="step-content row-fluid position-relative">

	<div id="step1" class="step-pane active">
		<div class="main">
			<form method="post" action="install/testDbConn " id="validation-form"
				class="form-horizontal" novalidate="novalidate">
				<div class="center">
					<h3 class="green">填写数据库信息</h3>
				</div>
				<hr>
				<div class="form-group">
					<label class="control-label col-xs-12 col-sm-3 no-padding-right">数据库服务器:</label>

					<div class="col-xs-12 col-sm-9">
						<div class="clearfix">
							<input type="text" value="127.0.0.1" class="col-xs-12 col-sm-9"
								name="dbhost">
						</div>
					</div>
				</div>

				<div class="space-8"></div>

				<div class="form-group">
					<label class="control-label col-xs-12 col-sm-3 no-padding-right">数据库名:</label>

					<div class="col-xs-12 col-sm-9">
						<div class="clearfix">
							<input type="text" value="zrlog" class="col-xs-12 col-sm-9"
								name="dbname">
						</div>
					</div>
				</div>

				<div class="space-8"></div>
				<div class="form-group">
					<label class="control-label col-xs-12 col-sm-3 no-padding-right">数据库用户名:</label>

					<div class="col-xs-12 col-sm-9">
						<div class="clearfix">
							<input type="text" value="root" class="col-xs-12 col-sm-9"
								name="dbuser">
						</div>
					</div>
				</div>

				<div class="space-8"></div>
				<div class="form-group">
					<label class="control-label col-xs-12 col-sm-3 no-padding-right">数据库密码:</label>

					<div class="col-xs-12 col-sm-9">
						<div class="clearfix">
							<input type="password" class="col-xs-12 col-sm-9"
								name="dbpwd">
						</div>
					</div>
				</div>

				<div class="space-8"></div>
				<div class="form-group">
					<label class="control-label col-xs-12 col-sm-3 no-padding-right">数据库端口:</label>

					<div class="col-xs-12 col-sm-9">
						<div class="clearfix">
							<input type="text" value="3306" class="col-xs-12 col-sm-9"
								name="port">
						</div>
					</div>
				</div>

				<div class="space-8"></div>
				<div class="form-group">
					<label class="control-label col-xs-12 col-sm-3 no-padding-right">系统信箱
						Email:</label>

					<div class="col-xs-12 col-sm-9">
						<div class="clearfix">
							<input type="text" class="col-xs-12 col-sm-9" name="dbhost">
						</div>
					</div>
				</div>
				<div class="form-group">
					<div class="col-xs-12 col-sm-10">
						<div class="row-fluid wizard-actions">
							<button data-last="Finish " class="btn btn-success btn-next">
								下一步 <i class="icon-arrow-right icon-on-right"></i>
							</button>
						</div>
					</div>
				</div>
			</form>
		</div>
	</div>
</div>
<jsp:include page="footer.jsp" />
