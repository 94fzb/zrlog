$(function () {
    var uploadUrl = 'api/admin/upload/';
    var mdEditor;
    $("#content").val(article['content']);

    $(".select2_single").select2({
        minimumResultsForSearch: -1,
        allowClear: true,
        dropdownParent: $("#type-select-parent")
    });

    $("#right_col").on('resize', function () {
        thumbnailImgResize();
    });

    function thumbnailImgResize() {
        var rightWidth = $("#right_col").width();
        if (rightWidth < 660) {
            $("#thumbnail-img").width(rightWidth - 10);
            $("#thumbnail-img").height(((rightWidth - 10) / 660) * 192);
            $("#camera-icon").css("margin-top", 20);
        } else {
            $("#camera-icon").css("margin-top", 80);
        }
    }

    jQuery(window).resize(thumbnailImgResize);
    var timer = setInterval(thumbnailImgResize, 100);

    var keywordsEl = $("#keywords");
    keywordsEl.tagsInput({
        height: '68px',
        width: 'auto'
    });
    keywordsEl.importTags($("#keywordsVal").val());
    refreshKeywords();

    function zeroPad(num, places) {
        var zero = places - num.toString().length + 1;
        return Array(+(zero > 0 && zero)).join("0") + num;
    }

    var editormdTheme = $("#markdown").attr("editormdTheme");
    var dark = editormdTheme === 'dark';
    mdEditor = editormd("editormd", {
        width: "100%",
        height: 840,
        path: editorMdPath,
        toolbarIcons: function () {
            return ["undo", "redo", "|", "bold", "del", "italic", "quote", "|", "h1", "h2", "h3", "h4", "|", "list-ul", "list-ol", "hr", "|", "link", "reference-link", "image", "file", "video", "code", "preformatted-text", "code-block", "table", "emoji", "html-entities", "pagebreak", "|", "goto-line", "watch", "fullscreen", "search", "copyPreviewHtml", "|", "help", "info"]
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
            var content = mdEditor.getPreviewedHTML();
            $("#content").val(content);
            if (content === '') {
                $("#editormd").addClass("has-error");
                $("#editormd").css("border-color", "#a94442");
            } else {
                $("#editormd").removeClass("has-error");
                $("#editormd").css("border-color", "#ccc");
            }
        },
        onload: function () {
            $("#content").val(mdEditor.getPreviewedHTML());
            var keyMap = {
                "Ctrl-S": function () {
                    save(true, false);
                }
            };
            this.addKeyMap(keyMap);
            $("#fileDialog").on("click", function () {
                mdEditor.executePlugin("fileDialog", "../plugins/file-dialog/file-dialog");
            });
            $("#videoDialog").on("click", function () {
                mdEditor.executePlugin("videoDialog", "../plugins/video-dialog/video-dialog");
            });
            $("#copPreviewHtmlToClipboard").on("click", function () {
                function copyToClipboard(html) {
                    var $temp = $("<input>");
                    $("body").append($temp);
                    $temp.val(html).select();
                    document.execCommand("copy");
                    $temp.remove();
                }

                copyToClipboard('<div class="markdown-body" style="padding:0">' + mdEditor.getPreviewedHTML() + "</div>");
                var e = {"message": lang.copPreviewHtmlToClipboardSuccess, "error": 0};
                notify(e, "info");
            });
            setInterval(function () {
                save(true, true);
            }, 1000 * 6);
        },
        onfullscreen: function () {
            $("#editormd").css("z-index", "9999");
            mdEditor.width("100%");
            if (screenfull.enabled) {
                screenfull.request();
            }
        },

        onfullscreenExit: function () {
            $("#editormd").css("z-index", 0);
            mdEditor.width("100%");
            if (screenfull.enabled) {
                screenfull.exit();
            }
        }

    });

    $(".editormd-markdown-textarea").attr("name", "markdown");
    $(".editormd-html-textarea").removeAttr("name");

    function checkPreviewLink() {
        if ($("#id").val() === null || $("#id").val() === '') {
            $("#preview").attr("disable", "disable");
        } else {
            updatePreviewLink($("#id").val());
        }
    }

    checkPreviewLink();

    function updatePreviewLink(id) {
        $("#preview-link").attr("href", "admin/article/preview?id=" + id);
        $("#preview").removeClass("btn-");
        $("#preview-link").show();
    }

    function tips(data) {
        if (data.error === 0) {
            $("#id").val(data.id);
            $("#alias").val(data.alias);
            $("#digest").val(data.digest);
            if (data.thumbnail !== null) {
                fillThumbnail(data.thumbnail);
            }
            updatePreviewLink(data.id);
            if (window.location.href.indexOf('?') === -1) {
                window.history.replaceState({}, "", window.location.pathname + "?id=" + data.id + window.location.hash);
            }
        }
        notify(data);
    }

    var saving = false;
    var lastChangeRequestBody;

    function validator(el) {
        //仅在2个输入框都不为空的情况，标记为又文本需要输入
        if ($("#title").val() === '' && $("#content").val() === '') {
            $("#title-parent").removeClass("has-error");
            $("#editormd").removeClass("has-error");
            return false;
        }
        if ($("#title").val() === '') {
            $("#title-parent").addClass("has-error");
        }
        if ($("#content").val() === '') {
            $("#editormd").addClass("has-error");
            $("#editormd").css("border-color", "#a94442");
        }
        return el.find(".has-error").length === 0;
    }

    $("#title").on("change keyup paste click", function () {
        if ($(this).val() !== '') {
            $("#title-parent").removeClass("has-error");
        } else {
            if ($("#content").val() === '') {
                $("#editormd").removeClass("has-error");
                $("#title-parent").removeClass("has-error");
                $("#editormd").css("border-color", "");
            } else {
                $("#title-parent").addClass("has-error");
            }

        }
    });

    function save(rubbish, timer) {
        //如果是还在保存文章状态，跳过保存
        if (saving) {
            notify(lang.saving, "warn");
            return;
        }
        refreshKeywords();
        var body = getFormRequestBody("#article-form");
        var tLastChangeRequestBody = JSON.stringify(body);
        var changed = tLastChangeRequestBody !== lastChangeRequestBody;
        if (validator($("#article-form")) && (!timer || changed)) {
            body['rubbish'] = rubbish;
            var url;
            if ($("#id").val() !== '') {
                url = "api/admin/article/update";
            } else {
                url = "api/admin/article/create";
            }
            saving = true;
            if (!skipFirstRubbishSave) {
                $.ajax({
                        url: url,
                        data: JSON.stringify(body),
                        method: "POST",
                        dataType: "json",
                        contentType: "application/json",
                        success: function (data) {
                            saving = false;
                            var date = new Date();
                            if (!data.error) {
                                data.message = (timer ? lang.auto : "") + (rubbish ? lang.rubbish : "") + " " + lang.saveSuccess + " " + zeroPad(date.getHours(), 2) + ":" + zeroPad(date.getMinutes(), 2) + ":" + zeroPad(date.getSeconds(), 2);
                            }
                            tips(data);
                            lastChangeRequestBody = JSON.stringify(getFormRequestBody("#article-form"));
                        },
                        error: function (xhr, err) {
                            saving = false;
                            tips({"error": 1, "message": formatErrorMessage(xhr, err)});
                        }
                    }
                )
            } else {
                skipFirstRubbishSave = false;
                saving = false;
                lastChangeRequestBody = JSON.stringify(getFormRequestBody("#article-form"));
            }
        } else {
            lastChangeRequestBody = JSON.stringify(getFormRequestBody("#article-form"));
        }
    }

    $(document.body).on('click', '#unCheckedTag .tag2', function (e) {
        $("#keywords_tagsinput").find('span[val=' + $(this).text() + ']').remove();
        keywordsEl.importTags($(this).text());
        $(this).remove();
        e.preventDefault();
        refreshKeywords();
    });
    $(document.body).on('click', "#keywords_tagsinput .tag2 a", function () {
        var text = $(this).siblings().text().trim();
        $("#unCheckedTag").find('span[val=' + text + ']').remove();
        $(this).parent().remove();
        $("#unCheckedTag").append('<span class="tag2" val="' + text + '"><i class="fa fa-tag">' + text + '</i></span>');
        refreshKeywords();
        return false;
    });

    function refreshKeywords() {
        var ts = $("#keywords_tagsinput .tag2").children("span");
        var tagArr = [];
        for (var i = 0; i < ts.length; i++) {
            tagArr[i] = $(ts[i]).text().trim();
        }
        if (tagArr.length > 0) {
            $("#keywordsVal").val(tagArr.join(","));
        } else {
            $("#keywordsVal").val("");
        }
    }

    $("#saveToRubbish").click(function () {
        skipFirstRubbishSave = false;
        save(true, false)
    });

    $("#save").click(function () {
        skipFirstRubbishSave = false;
        save(false, false);
    });

    $('#thumbnail-upload').liteUploader({
        script: 'api/admin/upload/thumbnail?dir=thumbnail'
    }).on('lu:success', function (e, response) {
        if (response.error) {
            alert(response.message);
        } else {
            fillThumbnail(response.url);
        }
    });

    function fillThumbnail(url) {
        $("#thumbnail-img").css('background-image', "url('" + url + "')");
        var w = gup("w", url);
        var h = gup("h", url);
        if (h) {
            if (w < 660) {
                h = 660.0 / w * h;
            }
            $("#thumbnail-img").height(h);
            $("#thumbnail").val(url);
            $("#camera-icon").hide();
        }
    }

    var thumbnailImg = $("input[name=\"thumbnail\"]").val();
    if (thumbnailImg !== "") {
        fillThumbnail(thumbnailImg);
    }

});

function gup(name, url) {
    if (!url) url = location.href;
    name = name.replace(/[\[]/, "\\\[").replace(/[\]]/, "\\\]");
    var regexS = "[\\?&]" + name + "=([^&#]*)";
    var regex = new RegExp(regexS);
    var results = regex.exec(url);
    return results === null ? null : results[1];
}