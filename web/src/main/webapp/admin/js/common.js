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