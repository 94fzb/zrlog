$(function () {
    PNotify.prototype.options.delay = 3000;

    function validator(el) {
        el.validator('validate');
        return el.find(".has-error").length === 0;
    }

    $(".btn-info").click(function () {
        var formId = $(this).attr("id") + "Ajax";
        var formEl = $("#" + formId);
        if (!validator(formEl)) {
            return false;
        }
        var uri;
        if (formEl.attr("action")) {
            uri = formEl.attr("action");
        } else {
            uri = 'api/admin/website/update'
        }
        $.ajax({
            url: uri,
            method: "POST",
            data: JSON.stringify(getFormRequestBody("#" + formId)),
            contentType: "application/json",
            success: function (data) {
                var message;
                if (data.error === 0) {
                    if (data.message) {
                        message = data.message;
                    } else {
                        message = lang.updateSuccess;
                    }
                    new PNotify({
                        title: message,
                        type: 'success',
                        delay: 3000,
                        hide: true,
                        styling: 'fontawesome'
                    });
                } else {
                    if (data.message) {
                        message = data.message;
                    } else {
                        message = lang.updateError;
                    }
                    new PNotify({
                        title: message,
                        delay: 3000,
                        type: 'error',
                        hide: true,
                        styling: 'fontawesome'
                    });
                }
            }
        });
    });
});