$(function(){
    var editor;
    $("#saveFile").click(function(){
        $("#code").val(editor.getValue());
        $.post("api/admin/template/saveFile", $("#saveFileForm").serialize(),function(data){
            if(data.status){
              new PNotify({
                  title: '操作成功...',
                  type: 'success',
                  delay:3000,
                  hide: true,
                  styling: 'bootstrap3'
              });
            }else{
              new PNotify({
                  title: '发生了一些异常...',
                  type: 'error',
                  delay:3000,
                  hide: true,
                  styling: 'bootstrap3'
              });
            }
        });
    });

    loadFile($("#form-field-select-6").children('option:selected').val());
    $("#form-field-select-6").change(function(){
        loadFile($(this).children('option:selected').val());
    });
    function loadFile(file){
        $.get("api/admin/template/loadFile?file="+file,function(data){
            $("div").remove(".CodeMirror");
            $("#code").text(data.fileContent);
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
              if (mode) {
                editor.setOption("mode", spec);
              } else {
                alert("Could not find a mode corresponding to " + val);
              }
        });
    }
});