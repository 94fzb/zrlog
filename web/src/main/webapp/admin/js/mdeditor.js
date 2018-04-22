var editorEl;
$(function () {
    editorEl = $("#editormd");
    var editormdTheme = editorEl.attr("theme");
    var dark = editormdTheme === 'dark';
    var editor = editormd("editormd", {
        width: "100%",
        height: 840,
        path: editorMdPath,
        toolbarIcons: function () {
            return ["bold", "del", "italic", "quote", "|", "h1", "h2", "h3", "h4", "h5", "|", "list-ul", "list-ol", "hr", "pagebreak", "|", "link", "reference-link", "image", "file", "video", "|", "code", "preformatted-text", "code-block", "table", "emoji", "|", "watch", "fullscreen", "search", "copyPreviewHtml", "|", "help", "info"]
        },
        toolbarCustomIcons: {
            file: '<a href="javascript:;" id="fileDialog"  title=' + lang.addAttachment + ' unselectable="on"><i class="fa fa-paperclip" unselectable="on"></i></a>',
            video: '<a href="javascript:;" id="videoDialog"  title="' + lang.addVideo + '" unselectable="on"><i class="fa fa-file-video-o" unselectable="on"></i></a>',
            copyPreviewHtml: '<a href="javascript:;" id="copPreviewHtmlToClipboard"  title="' + lang.copPreviewHtmlToClipboard + '" unselectable="on"><i class="fa fa-clipboard" unselectable="on"></i></a>'
        },
        codeFold: true,
        appendMarkdown: article['markdown'],
        saveHTMLToTextarea: true,
        searchReplace: true,
        htmlDecode: "iframe,pre",
        emoji: true,
        taskList: true,
        tocm: true,         // Using [TOCM]
        tex: true,                   // 开启科学公式TeX语言支持，默认关闭
        flowChart: true,             // 开启流程图支持，默认关闭
        sequenceDiagram: true,       // 开启时序/序列图支持，默认关闭,
        dialogMaskOpacity: 0,    // 设置透明遮罩层的透明度，全局通用，默认值为0.1
        dialogMaskBgColor: "#000", // 设置透明遮罩层的背景颜色，全局通用，默认为#fff
        imageUpload: true,
        imageFormats: ["jpg", "jpeg", "gif", "png", "ico", "bmp", "webp"],
        imageUploadURL: uploadUrl,
        theme: dark ? "dark" : "default",
        previewTheme: dark ? "dark" : "default",
        editorTheme: dark ? "pastel-on-dark" : "default",

        onchange: function () {
            var content = editor.getPreviewedHTML();
            $("#content").val(content);
            if (content === '') {
                editorEl.addClass("has-error");
                editorEl.css("border-color", "#a94442");
            } else {
                editorEl.removeClass("has-error");
                editorEl.css("border-color", "#ccc");
            }
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
            setInterval(function () {
                saveArticle(true, true);
            }, 1000 * 6);
        },
        onfullscreen: function () {
            editorEl.css("z-index", "9999");
            editor.width("100%");
            $("#save-bar").addClass("save-btn-full-screen").addClass("hidden-xs");
            $("#saveToRubbish-bar").addClass("saveToRubbish-btn-full-screen").addClass("hidden-xs");
            if (screenfull.enabled) {
                screenfull.request();
            }
        },

        onfullscreenExit: function () {
            editorEl.css("z-index", 0);
            $("#save-bar").removeClass("save-btn-full-screen").removeClass("hidden-xs");
            $("#saveToRubbish-bar").removeClass("saveToRubbish-btn-full-screen").removeClass("hidden-xs");
            editor.width("100%");
            if (screenfull.enabled) {
                screenfull.exit();
            }
        }

    });
    $(".editormd-markdown-textarea").attr("name", "markdown");
    $(".editormd-html-textarea").removeAttr("name");
});