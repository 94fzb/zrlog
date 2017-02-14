<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<jsp:include page="header.jsp" />
    <div class="row red">
	<div class="col-md-12">
        <h3><i class="fa fa-info"></i> ${_res.installPrompt}</h3>
        <ul>
            <li><h4>${_res.installWarn1}</h4></li>
            <li><h4>${_res.installWarn2}</h4></li>
        </ul>
    </div>
    </div>
	<div id="step1">
		<div class="main">
			<form method="post" action="${basePath}install/testDbConn " id="validation-form"
				class="form-horizontal" novalidate="novalidate">
				<div class="center">
					<h3 class="green">${_res.installInputDbInfo}</h3>
				</div>
				<div class="space-8"></div>
				<div class="form-group">
					<label class="control-label col-xs-12 col-sm-3 no-padding-right">${_res.installDbHost}:</label>

					<div class="col-xs-6 col-sm-3">
						<div class="clearfix">
							<input type="text" value="127.0.0.1" class="form-control"
								name="dbhost">
						</div>
					</div>
				</div>

				<div class="space-8"></div>

				<div class="form-group">
					<label class="control-label col-xs-12 col-sm-3 no-padding-right">${_res.installDbName}:</label>

					<div class="col-xs-6 col-sm-3">
						<div class="clearfix">
							<input type="text" value="zrlog" class="form-control"
								name="dbname">
						</div>
					</div>
				</div>

				<div class="space-8"></div>
				<div class="form-group">
					<label class="control-label col-xs-12 col-sm-3 no-padding-right">${_res.installDbUserName}:</label>

					<div class="col-xs-6 col-sm-3">
						<div class="clearfix">
							<input type="text" value="root" class="form-control"
								name="dbuser">
						</div>
					</div>
				</div>

				<div class="space-8"></div>
				<div class="form-group">
					<label class="control-label col-xs-12 col-sm-3 no-padding-right">${_res.installDbPassword}:</label>

					<div class="col-xs-6 col-sm-3">
						<div class="clearfix">
							<input type="password" class="form-control"
								name="dbpwd">
						</div>
					</div>
				</div>

				<div class="space-8"></div>
				<div class="form-group">
					<label class="control-label col-xs-12 col-sm-3 no-padding-right">${_res.installDbPort}:</label>

					<div class="col-xs-2 col-sm-1">
						<div class="clearfix">
							<input type="text" value="3306" class="form-control"
								name="port">
						</div>
					</div>
				</div>

				<div class="space-8"></div>
				<div class="form-group">
					<label class="control-label col-xs-12 col-sm-3 no-padding-right">${_res.installNotifyEmail}
						Email:</label>

					<div class="col-xs-8 col-sm-4">
						<div class="clearfix">
							<input type="text" class="form-control col-xs-12 col-sm-9" name="dbhost">
						</div>
					</div>
				</div>
				<div class="form-group">
					<div class="col-xs-12 col-sm-10">
						<div class="row-fluid wizard-actions">
							<button data-last="Finish " class="btn btn-success btn-next">
								${_res.installNextStep}<i class="fa fa-arrow-right"></i>
							</button>
						</div>
					</div>
				</div>
			</form>
		</div>
	</div>
</div>
<jsp:include page="footer.jsp" />
