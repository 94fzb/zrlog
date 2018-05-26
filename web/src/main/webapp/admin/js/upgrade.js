$(function () {
    $("#cycle-select").select2({
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
            if (!e.error) {
                e.message = message;
            }
            notify(e, "info");
            if (e.upgrade) {
                window.location.reload();
            }
        });
        return false;
    }

    $("#checkUpgrade").click(function (e) {
        checkVersion();
    });

});
