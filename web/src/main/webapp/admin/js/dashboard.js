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
});

function loadHTML(url, id) {
    $.ajaxSetup({'cache': true});
    $("#" + id).load(url, function (e) {
        NProgress.done();
        $(".js-switch").each(function (e) {
            new Switchery($(".js-switch").get(e), {color: "#26B99A"})
        })
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
    if ($(".side-menu li a[href='admin/index#" + path + "']").length > 0) {
        $(".side-menu li").removeClass("current-page");
        $(".side-menu li a[href='admin/index#" + path + "']").parent().addClass("current-page");
        $(".side-menu li a[href='admin/index#" + path + "']").parent().removeClass("active");
    }
    $("body").find(".tooltip").remove();
    $("body").find(".ui-widget").remove();
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

route(function (path) {
    loadPage(path);
});
route.start(true);
$(".js-switch").each(function (e) {
    new Switchery(e.get(0))
});