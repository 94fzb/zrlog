$(function () {
    var editor;
    window.htmlEditorDestroy = function () {
        if (editor) {
            editorEl.summernote('destroy');
        }
        editorEl.empty();
    };

    window.htmlEditorCreate = function () {
        $.getScript(summernoteJs, function () {
            htmlEditorDestroy();
            markdownEditorDestroy();
            if (currentLang === "zh") {
                $.getScript(summernoteLang + "summernote-zh-CN.js", function () {
                    init("zh-CN");
                });
            } else {
                init("en-US");
            }

            function uploadImage(image) {
                var data = new FormData();
                data.append("imgFile", image);
                $.ajax({
                    url: uploadUrl + "?dir=image",
                    cache: false,
                    contentType: false,
                    processData: false,
                    data: data,
                    type: "post",
                    success: function (data) {
                        var image = $('<img>').attr('src', data.url);
                        image.attr("style", "max-width:100%");
                        editorEl.summernote("insertNode", image[0]);
                    },
                    error: function (data) {
                        console.log(data);
                    }
                });
            }

            function init(_lang) {
                var _height = editorEl.height();
                editor = editorEl.summernote({
                    height: _height,
                    lang: _lang,
                    focus: true,
                    placeholder: _res['editorPlaceholder'],
                    callbacks: {
                        onImageUpload: function (image) {
                            uploadImage(image[0]);
                        }
                    },
                    hint: {
                        words: [],
                        match: /\b(\w{1,})$/,
                        search: function (keyword, callback) {
                            callback($.grep(this.words, function (item) {
                                return item.indexOf(keyword) === 0;
                            }));
                        }
                    }
                });
                editorEl.summernote('code', article.content);
                editorEl.on('summernote.change', function (we, content) {
                    if (editorEl.summernote('isEmpty')) {
                        content = "";
                    }
                    var markdown = new TurndownService().turndown(content);
                    if (markdown == null || markdown === "") {
                        markdown = content;
                    }
                    contentChange(content, markdown);

                });
                $(".btn-fullscreen").click(function () {
                    if ($(this).hasClass("active")) {
                        goFullScreen();
                        $(".note-toolbar-wrapper").remove();
                    } else {
                        exitFullScreen();
                    }
                });
                $(".note-editable").addClass("markdown-body");
                editorEl.hide();
            }
        })
    };
});
