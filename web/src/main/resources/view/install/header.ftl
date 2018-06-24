<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>${_res.installWizard}</title>
    <link href="${basePath}assets/css/bootstrap.min.css" rel="stylesheet"/>
    <link href="${basePath}assets/css/custom.min.css" rel="stylesheet"/>
    <link href="${basePath}assets/css/custom.colorful.css" rel="stylesheet">
    <link href="${basePath}assets/css/font-awesome.min.css" rel="stylesheet"/>
    <script src="${basePath}assets/js/jquery.min.js"></script>
    <script src="${basePath}assets/js/bootstrap.min.js"></script>
    <script src="${basePath}assets/js/validator.min.js"></script>
    <style>
        .container {
            min-width: 60%;
        }

        .alert {
            width: 100%;
        }
    </style>
</head>

<body style="background:#fff">
<div class="container">
    <div class="x_panel">
        <div class="x_title">
            <div class="widget-header widget-header-blue widget-header-flat">
                <h3 class="lighter">${_res.installWizard}</h3>
            </div>
            <div class="clearfix"></div>
        </div>

        <div class="x_content">
            <div class="col-md-12">
                <div class="row">
                     <#if errorMsg ??>
                         <div class="alert alert-danger">
                             <b><i class="fa fa-info-circle"></i> ${errorMsg}</b>
                         </div>
                     </#if>
                </div>
            </div>
            <#if currentViewName != 'forbidden'>
            <div class="col-md-12">
                <div id="wizard" class="form_wizard wizard_horizontal">
                    <ul class="list-unstyled wizard_steps anchor">
                        <li>
                            <a
                                <#if currentViewName == 'index'>class="selected"</#if>
                                    <#if currentViewName != 'index'>class="disable"</#if> isdone="1" rel="1">
                                <span class="step_no">1</span>
                                <span class="step_descr">${_res.installDatabaseInfo}</span>
                            </a>
                        </li>
                        <li>
                            <a
                            <#if currentViewName == 'message'>class="selected"</#if>
                            <#if currentViewName != 'message'>class="disable"</#if>
                            isdone="0" rel="2">
                                <span class="step_no">2</span>
                                <span class="step_descr">${_res.installWebSiteInfo}</span>
                            </a>
                        </li>
                        <li>
                            <a
                            <#if currentViewName == 'success'>class="selected"</#if>
                            <#if currentViewName != 'success'>class="disable"</#if> isdone="0" rel="3">
                                <span class="step_no">3</span>
                                <span class="step_descr">${_res.installComplete}</span>
                            </a>
                        </li>
                    </ul>
                </div>
            </div>
            </#if>
