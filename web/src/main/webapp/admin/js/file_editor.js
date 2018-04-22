$(function () {
    var editor;
    $("#saveFile").click(function () {
        $("#content").val(editor.getValue());
        $.post("api/admin/template/saveFile", $("#saveFileForm").serialize(), function (data) {
            var message = "";
            if (!data.error) {
                message = lang.updateSuccess;
            } else {
                message = lang.updateError;
            }
            data.message = message;
            notify(data);
        });
    });
    $.get("api/admin/template/files?path=" + path, function (e) {
        var files = e.files;
        var selectEl = "#form-field-select-6";
        for (var i = 0; i < files.length; i++) {
            $("<option></option>").text(files[i]).val(files[i]).appendTo($(selectEl));
        }
        if ($(selectEl + " option").length > 0) {
            $(selectEl).children('option:first').attr("selected", "selected");
            loadFile($(selectEl).children('option:selected').val());
        }
        $(selectEl).change(function () {
            loadFile($(this).children('option:selected').val());
        });
    });


    function loadFile(file) {
        var path = $("#basePath").val() + file;
        $.get("api/admin/template/loadFile?file=" + path, function (data) {
            $("#editing").text("> " + path);
            $("#file").val(path);
            $("div").remove(".CodeMirror");
            $("#code").text(data['fileContent']);
            editor = CodeMirror.fromTextArea(document.getElementById("code"), {
                lineNumbers: true
            });
            var val = file;
            if (m = /.+\.([^.]+)$/.exec(val)) {
                var info = CodeMirror.findModeByExtension(m[1]);
                if (info) {
                    mode = info.mode;
                    spec = info.mime;
                }
            } else if (/\//.test(val)) {
                var info = CodeMirror.findModeByMIME(val);
                if (info) {
                    mode = info.mode;
                    spec = val;
                }
            } else {
                mode = spec = val;
            }
            if (spec === 'application/x-jsp') {
                spec = 'text/html';
            }
            if (mode) {
                editor.setOption("mode", spec);
            } else {
                alert("Could not find a mode corresponding to " + val);
            }
            $(".CodeMirror").focus();
        });
    }
});