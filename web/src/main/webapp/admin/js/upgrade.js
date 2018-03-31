$(function () {
    $(".select2_single").select2({
        minimumResultsForSearch: -1,
        allowClear: true,
        dropdownParent: $("#cycle-select-parent")
    });

    function checkVersion() {
        $.get("api/admin/upgrade/checkNewVersion", function (e) {
            var message = "";
            if (e.upgrade) {
                message = " V" + e.version.version + "-" + e.version.buildId + " (" + e.version.type + ")";
            } else {
                message = lang.notFoundNewVersion;
            }
            new PNotify({
                title: message,
                type: 'info',
                delay: 3000,
                hide: true,
                styling: 'fontawesome'
            });
            if (e.upgrade) {
                window.location.href = location.href;
            }
        });
        return false;
    }

    $("#checkUpgrade").click(function (e) {
        checkVersion();
    });

});
