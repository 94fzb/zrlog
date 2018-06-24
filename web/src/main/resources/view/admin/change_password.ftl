<script type="text/javascript" src="${basePath}admin/js/set_update.js"></script>
<div class="page-header">
    <h3>${_res['admin.changePassword']}</h3>
</div>
<div class="row">
    <div class="col-md-6 col-xs-12 col-sm-12">
        <form role="form" action="api/admin/changePassword" data-toggle="validator" id="updatePasswordAjax"
              class="form-horizontal"
              method="post">
            <div class="form-group row">
                <label class="col-md-3 control-label no-padding-right"> ${_res['admin.oldPassword']}</label>

                <div class="col-md-5">
                    <input type="password" name="oldPassword" value="" required=""
                           class="form-control col-xs-12" placeholder="" id="orlPassword">
                </div>
            </div>

            <div class="form-group row">
                <label class="col-md-3 control-label no-padding-right"> ${_res['admin.newPassword']}</label>
                <div class="col-md-5">
                    <input type="password" name="newPassword" value="" class="form-control col-xs-12"
                           required="" placeholder="" id="newPassword1">

                </div>
            </div>
            <div class="ln_solid"></div>

            <div class="form-group row">
                <div class="col-md-offset-3 col-md-9">
                    <button id="updatePassword" type="button" class="btn btn-info">

                        ${_res['submit']}
                    </button>

                </div>
            </div>

        </form>
    </div>
</div>
${pageEndTag}