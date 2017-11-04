<%@ page session="false" pageEncoding="UTF-8" %>
<jsp:include page="include/menu.jsp"/>
<script type="text/javascript" src="${cacheFile['/admin/js/set_update.js']}"></script>
<script src="${cacheFile['/assets/js/jquery.liteuploader.min.js']}"></script>
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
<div class="page-header">
    <h3>
        ${_res['admin.user.info']}
    </h3>
</div>
<div class="row">
    <div class="col-xs-12">
        <form role="form" method="post" class="form-horizontal" id="userAjax" action="api/admin/update">
            <div class="form-group">
                <label class="col-sm-3 control-label no-padding-right"> ${_res.userName} </label>

                <div class="col-sm-3">
                    <input type="text" name="userName" value="${user.userName }"
                           class="form-control col-xs-12 col-sm-6" placeholder="">
                </div>
            </div>
            <div class="form-group">
                <label class="col-sm-3 control-label no-padding-right"> ${_res['email']} </label>

                <div class="col-sm-3">
                    <input type="text" name="email" value="${user.email }"
                           class="form-control col-xs-12 col-sm-6" placeholder="">
                </div>
            </div>
            <div class="form-group">
                <label class="col-sm-3 control-label no-padding-right">${_res['headPortrait']}</label>
                <div class="col-sm-9">
                    <input id="logo" class="col-xs-6 col-sm-6" name="header" value="${user.header}"/>
                    <input type="file" class="col-xs-6 fileUpload" name="imgFile" value="上传"/>
                </div>
            </div>
            <div class="ln_solid"></div>
            <div class="form-group">
                <div class="col-md-offset-3 col-md-9">
                    <button id="user" type="button" class="btn btn-info">
                        <i class="fa fa-check bigger-110"></i> ${_res['submit']}
                    </button>
                </div>
            </div>
        </form>
    </div>
</div>
<jsp:include page="include/footer.jsp"/>