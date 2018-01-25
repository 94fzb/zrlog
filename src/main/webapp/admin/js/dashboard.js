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
                location.href = window.location.href
            }
        });
    })
});