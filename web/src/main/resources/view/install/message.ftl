<#include "header.ftl"/>
<div id="step-container" class=" stepContainer row-fluid position-relative">
    <div id="step2">
        <div class="main">
            <form method="post" action="${basePath}install/install" class="form-horizontal" data-toggle="validator">
                <div class="center">
                    <h3 class="green">${_res.installInputWebSiteInfo}</h3>
                </div>
                <hr>
                <div class="form-group row">
                    <label class="control-label col-xs-12 col-sm-3 no-padding-right">${_res.installAdmin}:</label>

                    <div class="col-xs-6 col-sm-3">
                        <div class="clearfix">
                            <input type="text" value="admin" class="form-control" name="username" required>
                        </div>
                    </div>
                </div>

                <div class="space-8"></div>

                <div class="form-group row">
                    <label class="control-label col-xs-12 col-sm-3 no-padding-right">${_res.installAdminPassword}:</label>

                    <div class="col-xs-6 col-sm-3">
                        <div class="clearfix">
                            <input type="password" autocomplete="off" class="form-control" name="password" required>
                        </div>
                    </div>
                </div>

                <div class="space-8"></div>
                <div class="form-group row">
                    <label class="control-label col-xs-12 col-sm-3 no-padding-right">${_res.installAdminEmail}:</label>

                    <div class="col-xs-6 col-sm-3">
                        <div class="clearfix">
                            <input type="text" class="form-control" name="email">
                        </div>
                    </div>
                </div>

                <div class="space-8"></div>
                <div class="form-group row">
                    <label class="control-label col-xs-12 col-sm-3 no-padding-right">${_res.installWebSiteTitle}:</label>

                    <div class="col-xs-8 col-sm-4">
                        <div class="clearfix">
                            <input type="text" value="" placeholder="${_res.installWebSiteTitleTip}"
                                   class="form-control"
                                   name="title" required>
                        </div>
                    </div>
                </div>

                <div class="space-8"></div>
                <div class="form-group row">
                    <label class="control-label col-xs-12 col-sm-3 no-padding-right">${_res.installWebSiteSecond}:</label>

                    <div class="col-xs-8 col-sm-4">
                        <div class="clearfix">
                            <input type="text" value="" class="form-control" name="second_title">
                        </div>
                    </div>
                </div>

                <div class="space-8"></div>
                <div class="form-group">
                    <div class="col-xs-12 col-sm-10">
                        <div class="row-fluid wizard-actions">
                            <button data-last="Finish " class="btn btn-success btn-next">
                                ${_res.installNextStep}&nbsp;<i class="fa fa-arrow-right fa fa-on-right"></i>
                            </button>
                        </div>
                    </div>
                </div>
            </form>
        </div>
    </div>
</div>
<#include "footer.ftl"/>
