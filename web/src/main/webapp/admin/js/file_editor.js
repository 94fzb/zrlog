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
    if ($("#form-field-select-6 option").length > 0) {
        $("#form-field-select-6").children('option:first').attr("selected", "selected");
        loadFile($("#form-field-select-6").children('option:selected').val());
    }
    $("#form-field-select-6").change(function () {
        loadFile($(this).children('option:selected').val());
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