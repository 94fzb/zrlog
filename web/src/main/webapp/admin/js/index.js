var allLang = {
    "zh": {
        "title": "标题",
        "keywords": "关键字",
        "author": "作者",
        "type": "分类",
        "createTime": "创建时间",
        "lastUpdateDate": "最后更新时间",
        "viewCount": "浏览量",
        "rubbish": "草稿",
        "private": "私有",
        "edit": "编辑",
        "view": "浏览",
        "content": "内容",
        "nickName": "昵称",
        "commentUserHomePage": "评论者主页",
        "email": "邮箱",
        "typeName": "分类名称",
        "alias": "别名",
        "mark": "标记",
        "tag": "标签",
        "count": "计数",
        "link": "链接",
        "website": "网站",
        "desc": "描述",
        "order": "排序",
        "navName": "导航名称",
        "yes": "是",
        "no": "否",
        "saving": "正在保存中，请稍等...",
        "auto": "自动",
        "saveSuccess": "保存成功",
        "releaseSuccess": "发布成功",
        "addAttachment": "添加附件",
        "addVideo": "添加视频",
        "copPreviewHtmlToClipboard": "拷贝预览HTML到粘贴板",
        "copPreviewHtmlToClipboardSuccess": "已成功拷贝预览HTML到粘贴板",
        "connectError": "请检查网络是否正常",
        "response404": "请求资源不存在，或已被移出",
        "response500": "程序内部错误",
        "responseJsonError": "响应数据不是JSON",
        "responseTimeout": "响应超时",
        "requestAbort": "请求被放弃",
        "uncaughtError": "未知错误",
        "notFoundNewVersion": "暂时没有新版本",
        "newVersion": "新版本",
        "updateSuccess": "更新成功",
        "updateError": "发生了一些异常",
        "deleteError": "删除失败",
        "close": "关闭"
    },
    "en": {
        "title": "Title",
        "keywords": "Keywords",
        "author": "Author",
        "type": "Type",
        "createTime": "Create Time",
        "lastUpdateDate": "Last UpdateDate",
        "viewCount": "View Count",
        "rubbish": "Rubbish",
        "private": "Private",
        "edit": "Edit",
        "view": "View",
        "content": "Content",
        "nickName": "Nick Name",
        "commentUserHomePage": "Home Page",
        "email": "Email",
        "typeName": "Type Name",
        "alias": "Alias",
        "mark": "Mark",
        "tag": "Tag",
        "count": "Count",
        "link": "Link",
        "website": "WebSite",
        "desc": "Desc",
        "order": "Order",
        "navName": "Nav Name",
        "yes": "Yes",
        "no": "No",
        "saving": "Saving",
        "auto": "Auto",
        "saveSuccess": "save success",
        "releaseSuccess": "release success",
        "addAttachment": "Attachment",
        "addVideo": "Video",
        "copPreviewHtmlToClipboard": "Copy preview html to clipboard",
        "copPreviewHtmlToClipboardSuccess": "Copy preview html to clipboard success",
        "connectError": "Not connected.\nPlease verify your network connection.",
        "response404": "The requested page not found. [404]",
        "response500": "Internal Server Error [500].",
        "responseJsonError": "Requested JSON parse failed.",
        "responseTimeout": "Time out error.",
        "requestAbort": "abort",
        "uncaughtError": "Uncaught Error",
        "notFoundNewVersion": "No new version",
        "newVersion": "New version",
        "updateSuccess": "Update success",
        "updateError": "Some error",
        "deleteError": "Delete error",
        "close": "Close"

    }
};
var currentLang = document.getElementsByTagName("html")[0]["lang"];
var lang = allLang[currentLang];
var mainColor = "#1890ff";

$(function () {
    NProgress.configure({ parent: '#nav_menu' });


    $(".haveRead").click(function (e) {
        var commentId = $(this).attr("id");
        var commentEl = $(this);
        $.ajax({
            url: "api/admin/comment/read",
            method: "POST",
            contentType: "application/json",
            data: JSON.stringify({id: commentId}),
            success: function () {
                commentEl.parent().remove();
                var count = parseInt($("#commentNum").text() - 1);
                if (count <= 0) {
                    $("#commentMessages").remove();
                } else {
                    $("#commentNum").text(count);
                }

            }
        });
        return false;
    });

    route(function (path) {
        loadPage(path);
    });
    route.start(true);

    var lastClick = new Date();
    var i = 0;
    $(".nav_menu").click(function (e) {
        egg(1, e);
    }).dblclick(function (e) {
        egg(2, e);
    });

    function egg(plus, e) {
        if ($(e.target).attr("id") === 'nav_menu' && new Date().getTime() - lastClick.getTime() < 1000) {
            if (i >= 3) {
                $.get("api/admin/website/version", function (e) {
                    e.message = e['changelog'];
                    $("#info-title").text(e.version + " - " + e.buildId);
                    $("#info-body").html(e.changelog);
                    $("#closeBtn").text(lang.close);
                    $("#info").modal('show');
                    i = 0;
                });
            } else {
                i += plus;
            }
        }
        lastClick = new Date();
    }
});

function loadHTML(url, id) {
    $.ajaxSetup({'cache': true});
    $.get(url, function (responseText, textStatus, req) {
        NProgress.done();
        if (textStatus === "error") {
            return $("#" + id).html(formatErrorMessage(req, textStatus));
        } else {
            $("#" + id).html(req.responseText);
            $(".js-switch").each(function (e) {
                new Switchery($(".js-switch").get(e), {color: mainColor})
            })
        }
    });
    after();
}

function after() {
    if ($("#version").length > 0 && window.location.hash === "#upgrade") {
        $("#version").addClass("open");
    } else {
        $("#version").removeClass("open");
    }
}

function clearAllTimeouts() {
    if (typeof clearAllTimeouts.last == 'undefined') {
        clearAllTimeouts.last = setTimeout("||void", 0); // Opera || IE other browsers accept "" or "void"
    }
    var mx = setTimeout("||void", 0);
    for (var i = clearAllTimeouts.last; i <= mx; i++) {
        clearTimeout(i);
    }
    clearAllTimeouts.last = i;
}

function clear(path) {
    clearAllTimeouts();
    var currentEl = $(".side-menu li a[href='admin/index#" + path + "']");
    if (currentEl.length > 0) {
        $(".side-menu li").removeClass("current-page");
        currentEl.parent().parent().find("li").removeClass("active");
        currentEl.parent().addClass("current-page").removeClass("active");
    }
    $("body").find(".tooltip").remove();
    $("body").find(".ui-widget").remove();
    $("body").find(".ui-pnotify").remove();
    $(".note-popover").remove()
}

function loadPage(path) {
    if (window.location.pathname.indexOf("admin/template/download") > 0) {
        NProgress.done();
        return;
    }
    clear(path);
    NProgress.start();
    if (path === '') {
        path = "dashboard";
    }
    if (path === 'article_edit') {
        path = "article/edit";
        if (window.location.search !== '') {
            $("#editPage").attr("href", "admin/index" + window.location.search + "#article_edit");
        }
    }
    if (path === 'template_config') {
        path = "template/config";
    }
    path = "admin/" + path + window.location.search;
    //console.info(path);
    loadHTML(path, "right_col");
}

function reloadPage() {
    if (window.location.hash !== '') {
        loadPage(window.location.hash.substr(1));
    }
}

$(".js-switch").each(function (e) {
    new Switchery(e.get(0))
});