<script type="text/javascript" src="${basePath}admin/js/set_update.js"></script>
<script src="${basePath}assets/js/jquery.liteuploader.min.js"></script>
<script>
    $(document).ready(function () {
        $('.fileUpload').liteUploader({
            script: 'api/admin/upload/?dir=image'
        }).on('lu:success', function (e, response) {
            $('.file-name').attr("data-title", response.url)
            $("#logo").val(response.url);
            $("a .remove").remove();
        });
    });
</script>
<style>
    input {
        height: 36px;
        border: 1px solid #ccc;
    }

    input[type="file"] {
        display: none;
    }

    .custom-file-upload {
        border: 1px solid #ccc;
        display: inline-block;
        padding: 6px 12px;
        cursor: pointer;
    }
</style>
<div class="page-header">
    <h3>
        ${_res['admin.user.info']}
    </h3>
</div>
<div class="row">
    <div class="col-md-6 col-xs-12 col-sm-12">
        <form role="form" method="post" data-toggle="validator" class="form-horizontal" id="userAjax"
              action="api/admin/update">
            <div class="form-group row">
                <label class="col-md-3 control-label no-padding-right"> ${_res.userName} </label>

                <div class="col-md-6">
                    <input type="text" name="userName" value="${user.userName!'' }"
                           class="form-control" required placeholder="">
                </div>
            </div>
            <div class="form-group row">
                <label class="col-md-3 control-label no-padding-right"> ${_res['email']} </label>

                <div class="col-md-6">
                    <input type="email" name="email" value="${user.email!'' }"
                           class="form-control" placeholder="">
                </div>
            </div>
            <div class="form-group row">
                <label class="col-md-3 control-label no-padding-right">${_res['headPortrait']}</label>
                <div class="col-md-9">
                    <input id="logo" class="col-md-7" name="header" value="${user.header!''}"/>
                    <label for="fileUpload" class="custom-file-upload">
                        ${_res['upload']}
                    </label>
                    <input id="fileUpload" type="file" class="col-md-5 fileUpload" name="imgFile" value=""/>
                </div>
            </div>
            <div class="ln_solid"></div>
            <div class="form-group row">
                <div class="col-md-offset-3 col-md-9">
                    <button id="user" type="button" class="btn btn-info">
                        ${_res['submit']}
                    </button>
                </div>
            </div>
        </form>
    </div>
</div>
${pageEndTag}