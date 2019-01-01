var imageExts = ["jpg", "jpeg", "gif", "png", "ico", "bmp", "webp"];
var editorEl = $("#editorDiv");
var editorDivWrapper = $("#editorDivWrapper");
var editorTheme = editorEl.attr("theme");
var editForm = $("#article-form");
var editorType;

$(function () {
    $('.icheck').iCheck({
        checkboxClass: 'icheckbox_flat-blue',
        radioClass: 'iradio_flat-blue'
    });
    $("#thumbnail-upload").attr("accept", imageExts.join(","));

    window.contentChange = function (content, markdown) {
        article.content = content;
        article.markdown = markdown;
        validator(editForm);
    };
    $("#id").val(article.logId);
    $("#title").val(article.title);
    $("#alias").val(article.alias);
    $("#keywordsVal").val(article.keywords);
    $("#digest").val(article.digest);
    $("#thumbnail").val(article.thumbnail);
    $("#thumbnail-img").css("background-image", article.thumbnail);

    contentChange(article.content, article.markdown);
    editorTypeSelect(article['editorType']);


    setInterval(function () {
        saveArticle(true, true);
    }, 1000 * 6);

    $("#editorType .btn").click(function () {
        editorTypeSelect($(this).find("input").val());
    });

    function editorTypeSelect(type) {
        if (type === "html") {
            htmlEditorCreate();
        } else {
            markdownEditorCreate();
        }
        $("#editorType .btn").removeClass("active").removeClass("btn-secondary");
        $("#editorType_" + type).addClass("active").addClass("btn-secondary");
        $("input[name='editorType']").val(type);
        editorType = type;
    }

    $(".select2_single").select2({
        minimumResultsForSearch: -1,
        allowClear: true,
        dropdownParent: $("#type-select-parent")
    });

    var keywordsEl = $("#keywords");
    var isPrivateCheckBoxEl = $("input[name='privacy']");
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
            var aliasEl = $("#alias");
            if (aliasEl.val() === "") {
                aliasEl.val(data.alias);
            }
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
        //仅在3个输入框都不为空的情况，标记为又文本需要输入
        if (titleIsEmpty() && contentIsEmpty() && typeIsEmpty()) {
            $("#title-parent").removeClass("has-error");
            editorDivWrapper.removeClass("has-error");
            editorDivWrapper.css("border-color", "#ccc");
            $("#type-select-parent").removeClass("has-error");
            $("#type-select-parent").css("border-color", "");
            return false;
        }
        if (titleIsEmpty()) {
            $("#title-parent").addClass("has-error");
        } else {
            $("#title-parent").removeClass("has-error");
        }
        if (contentIsEmpty()) {
            editorDivWrapper.addClass("has-error");
            editorDivWrapper.css("border-color", "#a94442");
        } else {
            editorDivWrapper.removeClass("has-error");
            editorDivWrapper.css("border-color", "#ccc");
        }
        if (typeIsEmpty()) {
            $("#type-select-parent").addClass("has-error");
            $("#type-select-parent").css("border-color", "#a94442");
        } else {
            $("#type-select-parent").removeClass("has-error");
            $("#type-select-parent").css("border-color", "");
        }
        return el.find(".has-error").length === 0;
    }

    function contentIsEmpty() {
        return article.content === '' || article.content === undefined;
    }

    function typeIsEmpty() {
        return $("input[name='typeId']:checked").val() === undefined;
    }

    function titleIsEmpty() {
        return $("#title").val() === '';
    }

    $("#title").on("change keyup paste click", function () {
        validator(editForm);
    });

    var mobileHide = "d-none d-sm-block";

    window.goFullScreen = function () {
        $("#save").addClass("save-btn-full-screen").addClass(mobileHide);
        $("#saveToRubbish").addClass("saveToRubbish-btn-full-screen").addClass(mobileHide);
        if (screenfull.enabled) {
            screenfull.request();
        }
    };

    window.exitFullScreen = function () {
        $("#save").removeClass("save-btn-full-screen").removeClass(mobileHide);
        $("#saveToRubbish").removeClass("saveToRubbish-btn-full-screen").removeClass(mobileHide);
        if (screenfull.enabled) {
            screenfull.exit();
        }
    };

    window.saveArticle = function (rubbish, timer) {
        //如果是还在保存文章状态，跳过保存
        if (saving) {
            notify({"message": lang.saving}, "warn");
            return;
        }
        refreshKeywords();
        var body = getArticleRequestBody();
        var tLastChangeRequestBody = JSON.stringify(body);
        var changed = tLastChangeRequestBody !== lastChangeRequestBody;
        if (validator(editForm) && (!timer || changed)) {
            body['rubbish'] = rubbish;
            var url;
            if ($("#id").val() !== '') {
                url = "api/admin/article/update";
            } else {
                url = "api/admin/article/create";
            }
            saving = true;
            if (!skipFirstRubbishSave) {
                exitTips();
                $.ajax({
                        url: url,
                        data: JSON.stringify(body),
                        method: "POST",
                        dataType: "json",
                        timeout: 30000,
                        contentType: "application/json",
                        success: function (data) {
                            var date = new Date();
                            if (!data.error) {
                                data.message = (timer ? lang.auto : "") + (rubbish ? lang.rubbish : "") + (currentLang === 'en' ? " " : "") + (isPrivateCheckBoxEl.is(":checked") || timer || rubbish ? lang.saveSuccess : lang.releaseSuccess) + " " + zeroPad(date.getHours(), 2) + ":" + zeroPad(date.getMinutes(), 2) + ":" + zeroPad(date.getSeconds(), 2);
                                exitNotTips();
                            }
                            preTips(data);
                            lastChangeRequestBody = JSON.stringify(getArticleRequestBody());
                        },
                        error: function (xhr, err) {
                            preTips({"error": 1, "message": formatErrorMessage(xhr, err)});
                        },
                        always: function (xhr, err) {
                            preTips({"error": 1, "message": formatErrorMessage(xhr, err)});
                        }
                    }
                )
            } else {
                skipFirstRubbishSave = false;
                saving = false;
                lastChangeRequestBody = JSON.stringify(getArticleRequestBody());
            }
        } else {
            lastChangeRequestBody = JSON.stringify(getArticleRequestBody());
        }
    };

    function getArticleRequestBody() {
        var body = getFormRequestBody("#article-form");
        body['content'] = article.content;
        body['markdown'] = article.markdown;
        return body;
    }

    function preTips(message) {
        if (saving) {
            saving = false;
            tips(message);
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
        $("#unCheckedTag").append('<span class="tag2" val="' + text + '"><i style="padding-right: 5px" class="fa fa-tag"></i>' + text + '</span>');
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
        saveArticle(true, false)
    });

    $("#save").click(function () {
        skipFirstRubbishSave = false;
        saveArticle(false, false);
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

    var thumbnailHieght = 192 / 660 * $("#thumbnail-img").width();

    function defaultThumbnail() {
        $("#thumbnail-img").height(thumbnailHieght);
        $("#camera-icon").css("margin-top", thumbnailHieght / 2 - 18);
    }

    function fillThumbnail(url) {
        $("#thumbnail-img").css('background-image', "url('" + url + "')").css("background-size", "cover");
        var w = gup("w", url);
        var h = gup("h", url);
        if (h) {
            h = $("#thumbnail-img").width() / w * h;
            $("#thumbnail-img").height(h);
            $("#thumbnail").val(url);
        } else {
            defaultThumbnail();
        }
        if (url) {
            $("#camera-icon").hide();
        }
    }


    var thumbnailImg = $("input[name=\"thumbnail\"]").val();
    if (thumbnailImg !== "") {
        fillThumbnail(thumbnailImg);
    } else {
        defaultThumbnail();
    }

    $("body").keydown(function (e) {
        if (e.ctrlKey && e.which === 13 || e.which === 10) {
            saveArticle(false, false);
        } else {
            if (!(String.fromCharCode(event.which).toLowerCase() === 's' && event.ctrlKey) && !(event.which === 19)) {
                return true
            }
            event.preventDefault();
            saveArticle(true, false);
            return false;
        }
    });

    function saveBtnText(_private) {
        if (_private) {
            $("#save_text").text(_res['save']);
        } else {
            $("#save_text").text(_res['release']);
        }
    }

    saveBtnText(isPrivateCheckBoxEl.is(':checked'));

    $(isPrivateCheckBoxEl).click(function () {
        saveBtnText($(this).is(':checked'));
    });

});

function gup(name, url) {
    if (!url) url = location.href;
    name = name.replace(/[\[]/, "\\\[").replace(/[\]]/, "\\\]");
    var regexS = "[\\?&]" + name + "=([^&#]*)";
    var regex = new RegExp(regexS);
    var results = regex.exec(url);
    return results === null ? null : results[1];
}


function confirmExit() {
    return "You have attempted to leave this page. Are you sure?";
}

function exitTips() {
    window.onbeforeunload = confirmExit;
}

function exitNotTips() {
    window.onbeforeunload = null;
}