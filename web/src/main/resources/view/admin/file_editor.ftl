<script>
    var path = '${path!''}';
    if (path === '') {
        path = getParameterByName("path");
    }
    $("#basePath").val(path);
</script>
<link rel="stylesheet" href="${basePath}admin/markdown/lib/codemirror/codemirror.min.css"/>
<link rel="stylesheet" href="${basePath}admin/markdown/lib/codemirror/addon/dialog/dialog.css"/>
<link rel="stylesheet" href="${basePath}admin/markdown/lib/codemirror/addon/search/matchesonscrollbar.css"/>
<script src="${basePath}admin/markdown/lib/codemirror/codemirror.min.js"></script>
<script src="${basePath}admin/markdown/lib/codemirror/modes.min.js"></script>
<script src="${basePath}admin/markdown/lib/codemirror/addon/dialog/dialog.js"></script>
<script src="${basePath}admin/markdown/lib/codemirror/addon/search/searchcursor.js"></script>
<script src="${basePath}admin/markdown/lib/codemirror/addon/search/search.js"></script>
<script src="${basePath}admin/markdown/lib/codemirror/addon/scroll/annotatescrollbar.js"></script>
<script src="${basePath}admin/markdown/lib/codemirror/addon/search/matchesonscrollbar.js"></script>
<script src="${basePath}admin/js/file_editor.js"></script>
<style>
    .CodeMirror {
        border: 1px solid #ccc;
        height: 600px;
        width: 100%;
    }

    #code {
        border: 1px solid #ccc;
        height: 600px;
        width: 100%;
    }

    dt {
        font-family: monospace;
        color: #666;
    }

    option {
        padding-top: 8px;
        height: 30px;

    }

</style>
<div class="page-header">
    <h3>
    ${_res['fileEdit']}
        <small id="editing"></small>
    </h3>
</div>
<div>
    <form id="saveFileForm">
        <input type="hidden" id="basePath">
        <input type="hidden" name="file" id="file">
        <textarea name="content" id="content" hidden></textarea>

        <div class="form-group row">
            <div class="col-md-10">
                <textarea id="code"></textarea>
            </div>
            <div class="col-md-2">
                <select multiple style="height: 600px" id="form-field-select-6" class="form-control">
                </select>
            </div>
        </div>

        <div class="ln_solid"></div>
        <div class="col-md-offset-1">
            <button class="btn btn-info" type="button" id="saveFile">
            ${_res['submit']}
            </button>
        </div>
    </form>
</div>