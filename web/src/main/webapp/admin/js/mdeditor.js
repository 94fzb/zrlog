$(function () {
    var editor;

    window.markdownEditorDestroy = function () {
        if (editor) {
            editorEl.empty();
        }
    };

    var tips = "<li id='tips' style='display: none'>" + _res['upload'] + "...</li>";

    window.markdownImageUploadStart = function () {
        $("#tips").show();
    };

    window.markdownImageUploadComplete = function (url) {
        editor.insertValue("![](" + url + ")");
        $("#tips").hide();
    };


    window.markdownEditorCreate = function () {
        $.getScript(editorMdJs, function () {
            htmlEditorDestroy();
            markdownEditorDestroy();
            var dark = editorTheme === 'dark';
            if (currentLang === 'en') {
                editormd.loadScript(editorLang + 'en', function () {
                    editor.lang = editormd.defaults.lang;
                });
            }
            editor = editormd(editorEl.attr("id"), {
                path: editorMdPath,
                toolbarIcons: function () {
                    return ["bold", "del", "italic", "quote", "|", "h1", "h2", "h3", "h4", "h5", "|", "list-ul", "list-ol", "hr", "pagebreak", "|", "link", "reference-link", "image", "file", "video", "|", "preformatted-text", "code-block", "table", "emoji", "|", "watch", "fullscreen", "copyPreviewHtml", "|", "info", "help"]
                },
                toolbarCustomIcons: {
                    file: '<a href="javascript:;" id="fileDialog"  title=' + lang.addAttachment + ' unselectable="on"><i class="fa fa-paperclip" unselectable="on"></i></a>',
                    video: '<a href="javascript:;" id="videoDialog"  title="' + lang.addVideo + '" unselectable="on"><i class="fa fa-file-video-o" unselectable="on"></i></a>',
                    copyPreviewHtml: '<a href="javascript:;" id="copPreviewHtmlToClipboard"  title="' + lang.copPreviewHtmlToClipboard + '" unselectable="on"><i class="fa fa-clipboard" unselectable="on"></i></a>'
                },
                codeFold: true,
                appendMarkdown: article['markdown'],
                searchReplace: true,
                htmlDecode: "pre",
                emoji: true,
                taskList: true,
                tocm: false,         // Using [TOCM]
                tex: true,                   // 开启科学公式TeX语言支持，默认关闭
                flowChart: true,             // 开启流程图支持，默认关闭
                sequenceDiagram: true,       // 开启时序/序列图支持，默认关闭,
                dialogMaskOpacity: 0,    // 设置透明遮罩层的透明度，全局通用，默认值为0.1
                dialogMaskBgColor: "#000", // 设置透明遮罩层的背景颜色，全局通用，默认为#fff
                imageUpload: true,
                imageFormats: imageExts,
                imageUploadURL: uploadUrl,
                theme: dark ? "dark" : "default",
                previewTheme: dark ? "dark" : "default",
                editorTheme: dark ? "pastel-on-dark" : "default",

                onchange: function () {
                    contentChange(editor.getPreviewedHTML(), editor.getMarkdown());
                },
                onload: function () {
                    $("#content").val(editor.getPreviewedHTML());
                    $("#fileDialog").on("click", function () {
                        editor.executePlugin("fileDialog", "../plugins/file-dialog/file-dialog");
                    });
                    $("#videoDialog").on("click", function () {
                        editor.executePlugin("videoDialog", "../plugins/video-dialog/video-dialog");
                    });
                    $("#copPreviewHtmlToClipboard").on("click", function () {
                        function copyToClipboard(html) {
                            var $temp = $("<input>");
                            $("body").append($temp);
                            $temp.val(html).select();
                            document.execCommand("copy");
                            $temp.remove();
                        }

                        copyToClipboard('<div class="markdown-body" style="padding:0">' + editor.getPreviewedHTML() + "</div>");
                        var e = {"message": lang.copPreviewHtmlToClipboardSuccess, "error": 0};
                        notify(e, "info");
                    });
                    $(".CodeMirror-gutters").height($(".CodeMirror-scroll").height() + 20);
                    $(".editormd-menu").append(tips);
                },
                onfullscreen: function () {
                    editor.width("100%");
                    $(".full-screen-hide").hide();
                    goFullScreen();
                },

                onfullscreenExit: function () {
                    $(".full-screen-hide").show();
                    editor.width("100%");
                    exitFullScreen();
                }

            });
            $(".editormd-markdown-textarea").removeAttr("name");
        })
    };

});

function uploadFile(file) {
    var index = Math.random().toString(10).substr(2, 5) + '-' + Math.random().toString(36).substr(2);
    var fileName = index + '.png';
    var fileInfo = {
        id: index,
        name: fileName
    };
    markdownImageUploadStart(fileInfo);

    var formData = new FormData();
    formData.append('imgFile', file, fileName);
    $.ajax({
        method: 'post',
        url: uploadUrl + "?dir=image",
        data: formData,
        contentType: false,
        processData: false,
        success: function (data) {
            var url = data.url;
            markdownImageUploadComplete(url);
        },
        error: function (error) {
            alert(error);
        }
    });
}

document.getElementById('editorDivWrapper').addEventListener('paste', function (e) {
    if (editorType === "markdown") {
        var clipboardData = e.clipboardData;
        var items = clipboardData.items;
        for (var i = 0; i < items.length; i++) {
            if (items[i].kind === 'file' && items[i].type.match(/^image/)) {
                // 取消默认的粘贴操作
                e.preventDefault();
                // 上传文件
                uploadFile(items[i].getAsFile());
                break;
            }
        }
    }
});