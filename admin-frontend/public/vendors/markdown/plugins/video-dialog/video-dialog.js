/*!
 * Image (upload) dialog plugin for Editor.md
 *
 * @file        image-dialog.js
 * @author      pandao
 * @version     1.2.0
 * @updateTime  2015-03-07
 * {@link       https://github.com/pandao/editor.md}
 * @license     MIT
 */

(function () {

    var factory = function (exports) {

        var pluginName = "video-dialog";

        exports.fn.videoDialog = function () {

            var _this = this;
            var cm = this.cm;
            var lang = this.lang;
            var editor = this.editor;
            var settings = this.settings;
            var cursor = cm.getCursor();
            var selection = cm.getSelection();
            var fileLang = lang.dialog.video;
            var classPrefix = this.classPrefix;
            var iframeName = classPrefix + "image-iframe";
            var dialogName = classPrefix + pluginName, dialog;

            cm.focus();

            if (editor.find("." + dialogName).length < 1) {
                var guid = (new Date).getTime();
                var action = settings.imageUploadURL + "?guid=" + guid + "&dir=file";

                if (settings.crossDomainUpload) {
                    action += "&callback=" + settings.uploadCallbackURL + "&dialog_id=editormd-image-dialog-" + guid;
                }

                var dialogContent = ((settings.imageUpload) ? "<form action=\"" + action + "\" target=\"" + iframeName + "\" method=\"post\" enctype=\"multipart/form-data\" class=\"" + classPrefix + "form\">" : "<div class=\"" + classPrefix + "form\">") +
                    ((settings.imageUpload) ? "<iframe name=\"" + iframeName + "\" id=\"" + iframeName + "\" guid=\"" + guid + "\"></iframe>" : "") +
                    "<label>" + fileLang.url + "</label>" +
                    "<input type=\"text\" data-url />" + (function () {
                        return (settings.imageUpload) ? "<div class=\"" + classPrefix + "file-input\">" +
                            "<input type=\"file\" name=\"imgFile\" />" +
                            "<input type=\"submit\" value=\"" + fileLang.uploadButton + "\" />" +
                            "</div>" : "";
                    })() +
                    "<br/>" +
                    "<label>" + fileLang.alt + "</label>" +
                    "<input type=\"text\" value=\"" + selection + "\" data-alt />" +
                    "<br/>" +
                    "<label>" + fileLang.link + "</label>" +
                    "<input type=\"text\" value=\"http://\" data-link />" +
                    "<br/>" +
                    ((settings.imageUpload) ? "</form>" : "</div>");

                //var imageFooterHTML = "<button class=\"" + classPrefix + "btn " + classPrefix + "image-manager-btn\" style=\"float:left;\">" + imageLang.managerButton + "</button>";  

                dialog = this.createDialog({
                    title: fileLang.title,
                    width: (settings.imageUpload) ? 465 : 380,
                    height: 250,
                    name: dialogName,
                    content: dialogContent,
                    mask: settings.dialogShowMask,
                    drag: settings.dialogDraggable,
                    lockScreen: settings.dialogLockScreen,
                    maskStyle: {
                        opacity: settings.dialogMaskOpacity,
                        backgroundColor: settings.dialogMaskBgColor
                    },
                    buttons: {
                        enter: [lang.buttons.enter, function () {
                            var url = this.find("[data-url]").val();
                            var alt = this.find("[data-alt]").val();
                            var link = this.find("[data-link]").val();

                            if (url === "") {
                                alert(fileLang.imageURLEmpty);
                                return false;
                            }

                            if (alt === "") {
                                alt = url;
                            }
                            //按大部分视频16：9换算高度不靠谱（移动端展示），还是得100%呀
                            var video = '<video controlsList="nodownload" class="video-js vjs-default-skin vjs-16-9" controls' +
                                ' preload="auto" width="100%" height="320px"' +
                                ' data-setup=\'{"fluid": true}\'>' +
                                ' <source src="' + (url) + '">' +
                                '</video><br/>';
                            if (link === "" || link === "http://") {
                                cm.replaceSelection(video);
                            }
                            else {
                                cm.replaceSelection(video);
                            }
                            if (alt === "") {
                                cm.setCursor(cursor.line, cursor.ch + 2);
                            }

                            this.hide().lockScreen(false).hideMask();

                            return false;
                        }],

                        cancel: [lang.buttons.cancel, function () {
                            this.hide().lockScreen(false).hideMask();

                            return false;
                        }]
                    }
                });

                dialog.attr("id", classPrefix + "image-dialog-" + guid);

                if (!settings.imageUpload) return;

                var fileInput = dialog.find("[name=\"\imgFile\"]");

                fileInput.bind("change", function () {
                    var fileName = fileInput.val();

                    if (fileName === "") {
                        alert(fileLang.uploadFileEmpty);
                    }
                    else {
                        if (typeof (dialog.loading) == "function") dialog.loading(true);

                        var submitHandler = function () {

                            var uploadIframe = document.getElementById(iframeName);

                            uploadIframe.onload = function () {
                                if (typeof (dialog.loading) == "function") dialog.loading(false);

                                var json = uploadIframe.contentWindow.document.body.innerHTML.replace(/<[^>]+>/g, "");
                                json = (typeof JSON.parse !== "undefined") ? JSON.parse(json) : eval("(" + json + ")");
                                if (json.error === 0) {
                                    dialog.find("[data-url]").val(json.url);
                                }
                                else {
                                    alert(json.message);
                                }

                                return false;
                            };
                        };

                        dialog.find("[type=\"submit\"]").bind("click", submitHandler).trigger("click");

                    }

                    return false;
                });
            }

            dialog = editor.find("." + dialogName);
            dialog.find("[type=\"text\"]").val("");
            dialog.find("[type=\"file\"]").val("");
            dialog.find("[data-link]").val("http://");

            this.dialogShowMask(dialog);
            this.dialogLockScreen();
            dialog.show();

        };

    };

    // CommonJS/Node.js
    if (typeof require === "function" && typeof exports === "object" && typeof module === "object") {
        module.exports = factory;
    }
    else if (typeof define === "function")  // AMD/CMD/Sea.js
    {
        if (define.amd) { // for Require.js

            define(["editormd"], function (editormd) {
                factory(editormd);
            });

        } else { // for Sea.js
            define(function (require) {
                var editormd = require("./../../editormd");
                factory(editormd);
            });
        }
    }
    else {
        factory(window.editormd);
    }

})();
