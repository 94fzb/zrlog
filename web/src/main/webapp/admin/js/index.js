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
        "deleteError": "删除失败"
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
        "deleteError": "Delete error"

    }
};
var lang = allLang[document.getElementsByTagName("html")[0]["lang"]];
var mainColor = "#1890ff";

$(function () {
    $('#menu_toggle').on('click', function () {
        var classArr = $("body").get(0).getAttribute("class").split(" ");
        var naver = "nav-md";
        for (var i = 0; i < classArr.length; i++) {
            if (classArr[i].indexOf("nav") !== -1) {
                if (classArr[i] === naver) {
                    naver = "nav-sm"
                }
                break;
            }
        }
        $.ajax({
            url: 'api/admin/website/update',
            contentType: "application/json",
            data: JSON.stringify({"admin_dashboard_naver": naver}),
            method: "POST",
            success: function (data) {
                if (!data.error) {
                }
            }
        })
        ;
    });
    $(".language").click(function (e) {
        var language = $(this).attr("id");
        $.ajax({
            url: 'api/admin/website/update',
            contentType: "application/json",
            data: JSON.stringify({"language": language}),
            method: "POST",
            success: function () {
                location.href = window.location.href;
                location.reload();
            }
        });
    });
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
                $("#commentNum").text(parseInt($("#commentNum").text() - 1));
            }
        });
        return false;
    });

    route(function (path) {
        loadPage(path);
    });
    route.start(true);
});

function loadHTML(url, id) {
    $.ajaxSetup({'cache': true});
    $("#" + id).load(url, function (responseText, textStatus, req) {
        NProgress.done();
        if (textStatus === "error") {
            return $("#" + id).html(formatErrorMessage(req, textStatus));
        } else {
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
}

function loadPage(path) {
    if (window.location.pathname.indexOf("admin/template/download") > 0) {
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