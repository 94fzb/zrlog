<script src="${basePath}assets/js/jquery.liteuploader.min.js"></script>
<script src="${basePath}admin/js/template.js"></script>
<style>
    input[type="file"] {
        display: none;
    }

    .custom-file-upload {
        cursor: pointer;
        width: 120px;
    }

    .fix_button {
        width: auto;
    }

    .tools a {
        padding-right: 6px;
    }
</style>
<div class="row">
    <div class="col-md-12  text-right">
        <label for="fileUpload"
               class="custom-file-upload btn btn-primary fix_button">
            <i class="fa fa-cloud-upload" style="padding-right: 5px"></i><span>${_res['upload']}</span>
        </label>
        <input type="file" id="fileUpload" class="fileUpload" name="file" value=""/>
    </div>
</div>
<div class="row">
    <div class="divider"></div>
</div>
<div class="row">
    <#list templates as template>
        <div class="col-md-3 col-xs-12 widget widget_tally_box">
            <div class="x_panel ui-ribbon-container fixed_height_440">
                <#if template.use>
                    <div class="ui-ribbon-wrapper">
                        <div class="ui-ribbon">
                            ${_res['admin.theme.inUse']}
                        </div>
                    </div>
                </#if>
                <#if template.preview>
                    <div class="ui-ribbon-wrapper">
                        <div class="ui-ribbon">
                            ${_res['admin.theme.inPreview']}
                        </div>
                    </div>
                </#if>
                <div class="x_title">
                    <h2>${template.name }</h2>
                    <div class="clearfix"></div>
                </div>
                <div class="x_content">
                    <#list template.previewImages as image>
                        <img style="width: 100%;height: 280px" src="${image}">
                    </#list>
                    <p style="padding-top: 10px">${template.digest }</p>
                    <div class="divider"></div>
                    <div class="caption">
                        <div class="tools tools-bottom text-center" style="font-size: 16px;">
                            <!--<a href="${template.url }" target="_blank" title="${template.author }">${_res['admin.theme.user']}:<i class="fa fa-user"></i></a>-->
                            <a href="admin/index?include=file_editor&path=${template.template}/&editType=template#file_editor"><i
                                    class="fa fa-pencil"></i></a>
                            <a target="_blank"
                               href="admin/template/preview?template=${template.template}"><i
                                    class="fa fa-eye"></i></a>
                            <#if template.configAble>
                                <a href="admin/index?template=${template.template}#template_config"><i
                                        class="fa fa-cog"></i></a>
                            </#if>
                            <#if template.deleteAble><a href="#" class="delete-btn"
                                                        template="${template.template}"><i
                                    class="fa fa-remove"></i></a></#if>
                            <a class="apply-btn" href="#" template="${template.template}"><i
                                    class="fa fa-check"></i></a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </#list>
</div>
<div class="row">
    <div class="divider"></div>
</div>
<div class="row">
    <div class="col-md-12">
        <a href="admin/index#template_center">
            <button class="btn btn-dark col-md-1 fix_button"><i
                    class="fa fa-cloud-download"></i><span>${_res['admin.theme.download']}</span></button>
        </a>
    </div>
</div>