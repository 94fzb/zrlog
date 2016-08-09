<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<jsp:include page="header.jsp" />
	<div id="step1" class="step-pane active">
		<div class="main">
			<form method="post" action="install/testDbConn " id="validation-form"
				class="form-horizontal" novalidate="novalidate">
				<div class="center">
					<h3 class="green">${_res.installInputDbInfo}</h3>
				</div>
				<hr>
				<div class="form-group">
					<label class="control-label col-xs-12 col-sm-3 no-padding-right">${_res.installDbHost}:</label>

					<div class="col-xs-12 col-sm-9">
						<div class="clearfix">
							<input type="text" value="127.0.0.1" class="col-xs-12 col-sm-9"
								name="dbhost">
						</div>
					</div>
				</div>

				<div class="space-8"></div>

				<div class="form-group">
					<label class="control-label col-xs-12 col-sm-3 no-padding-right">${_res.installDbName}:</label>

					<div class="col-xs-12 col-sm-9">
						<div class="clearfix">
							<input type="text" value="zrlog" class="col-xs-12 col-sm-9"
								name="dbname">
						</div>
					</div>
				</div>

				<div class="space-8"></div>
				<div class="form-group">
					<label class="control-label col-xs-12 col-sm-3 no-padding-right">${_res.installDbUserName}:</label>

					<div class="col-xs-12 col-sm-9">
						<div class="clearfix">
							<input type="text" value="root" class="col-xs-12 col-sm-9"
								name="dbuser">
						</div>
					</div>
				</div>

				<div class="space-8"></div>
				<div class="form-group">
					<label class="control-label col-xs-12 col-sm-3 no-padding-right">${_res.installDbPassword}:</label>

					<div class="col-xs-12 col-sm-9">
						<div class="clearfix">
							<input type="password" class="col-xs-12 col-sm-9"
								name="dbpwd">
						</div>
					</div>
				</div>

				<div class="space-8"></div>
				<div class="form-group">
					<label class="control-label col-xs-12 col-sm-3 no-padding-right">${_res.installDbPort}:</label>

					<div class="col-xs-12 col-sm-9">
						<div class="clearfix">
							<input type="text" value="3306" class="col-xs-12 col-sm-9"
								name="port">
						</div>
					</div>
				</div>

				<div class="space-8"></div>
				<div class="form-group">
					<label class="control-label col-xs-12 col-sm-3 no-padding-right">${_res.installNotifyEmail}
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
								${_res.installNextStep} <i class="fa fa-arrow-right fa fa-on-right"></i>
							</button>
						</div>
					</div>
				</div>
			</form>
		</div>
	</div>
</div>
<jsp:include page="footer.jsp" />
