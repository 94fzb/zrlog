<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<jsp:include page="header.jsp" />
<div id="step2">
	<div class="main">
		<form method="post" action="${basePath}install/installZrlog"
			id="validation-form" class="form-horizontal" novalidate="novalidate">
			<div class="center">
				<h3 class="green">${_res.installInputWebSiteInfo}</h3>
			</div>
			<hr>
			<div class="form-group">
				<label class="control-label col-xs-12 col-sm-3 no-padding-right">${_res.installAdmin}:</label>

				<div class="col-xs-6 col-sm-3">
					<div class="clearfix">
						<input type="text" value="admin" class="form-control"
							name="username">
					</div>
				</div>
			</div>

			<div class="space-8"></div>

			<div class="form-group">
				<label class="control-label col-xs-12 col-sm-3 no-padding-right">${_res.installAdminPassword}:</label>

				<div class="col-xs-6 col-sm-3">
					<div class="clearfix">
						<input type="password" autocomplete="off" class="form-control" name="password">
					</div>
				</div>
			</div>

			<div class="space-8"></div>
			<div class="form-group">
				<label class="control-label col-xs-12 col-sm-3 no-padding-right">${_res.installAdminEmail}:</label>

				<div class="col-xs-6 col-sm-3">
					<div class="clearfix">
						<input type="text" value="admin@example.com"
							class="form-control" name="email">
					</div>
				</div>
			</div>

			<div class="space-8"></div>
			<div class="form-group">
				<label class="control-label col-xs-12 col-sm-3 no-padding-right">${_res.installWebSiteTitle}:</label>

				<div class="col-xs-8 col-sm-4">
					<div class="clearfix">
						<input type="text" value="" placeholder="${_res.installWebSiteTitleTip}" class="form-control"
							name="title">
					</div>
				</div>
			</div>

			<div class="space-8"></div>
			<div class="form-group">
				<label class="control-label col-xs-12 col-sm-3 no-padding-right">${_res.installWebSiteSecond}:</label>

				<div class="col-xs-8 col-sm-4">
					<div class="clearfix">
						<input type="text" value="" class="form-control"
							name="second_title">
					</div>
				</div>
			</div>

			<div class="space-8"></div>
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
<jsp:include page="footer.jsp" />
