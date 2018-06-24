function getFormRequestBody(fromSelector) {
    var formFields = $(fromSelector).serializeArray();
    var requestBody = {};
    for (var i = 0; i < formFields.length; i++) {
        var el = $(fromSelector).find("input[name='" + formFields[i]['name'] + "']");
        if (el.attr("type") === "checkbox") {
            requestBody[formFields[i]['name']] = formFields[i]['value'] !== undefined;
        } else {
            if (!formFields[i]['value']) {
                requestBody[formFields[i]['name']] = null;
            } else {
                requestBody[formFields[i]['name']] = formFields[i]['value'];
            }
        }
    }
    var checkBoxes = $(fromSelector).find("input[type='checkbox']");
    for (var i = 0; i < checkBoxes.length; i++) {
        var name = $(checkBoxes[i]).attr("name");
        if (name !== '' && !requestBody[name]) {
            requestBody[name] = false;
        }
    }
    return requestBody;
}

function formatErrorMessage(jqXHR, exception) {
    if (jqXHR.status === 0) {
        return (lang.connectError);
    } else if (jqXHR.status === 404) {
        return (lang.response404);
    } else if (jqXHR.status === 500) {
        return (lang.response500);
    } else if (exception === 'parsererror') {
        return (lang.responseJsonError);
    } else if (exception === 'timeout') {
        return (lang.responseTimeout);
    } else if (exception === 'abort') {
        return (lang.requestAbort);
    } else {
        return (lang.uncaughtError + '\n' + jqXHR.responseText);
    }
}

/**
 *
 * @param response
 * @param type 可选值 warn/info/success 默认 success
 */
function notify(response, type) {
    PNotify.removeAll();
    if (response.error) {
        if (response.stack) {
            console.info(response.stack);
        }
        new PNotify({
            title: response.message,
            type: 'error',
            delay: 3000,
            hide: true,
            styling: 'fontawesome'
        });
    } else {
        if (!type) {
            type = "success";
        }
        new PNotify({
            title: response.message,
            type: type,
            delay: 3000,
            hide: true,
            styling: 'fontawesome'
        });
    }
}

function getParameterByName(name) {
    name = name.replace(/[\[]/, "\\\[").replace(/[\]]/, "\\\]");
    var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
        results = regex.exec(location.search);
    return results == null ? "": decodeURIComponent(results[1]);
}