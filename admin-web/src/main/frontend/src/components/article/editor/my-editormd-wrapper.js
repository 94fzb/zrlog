import React from "react";
import {BaseResourceComponent} from "../../base-resource-component";
import './my-editormd.css';
import $ from 'jquery';
import Spin from "antd/es/spin";
import {message} from "antd";
import makeAsyncScriptLoader from "react-async-script";
import {MyLoadingComponent} from "../../../App";
import EnvUtils from "../../../utils/env-utils";

const editorMdId = 'editor';

class MyEditorMd extends BaseResourceComponent {

    initState() {
        return {
            editorLoading: true,
            mdEditorScriptLoaded: false
        }
    }

    componentDidMount() {
        super.componentDidMount();
        let {superThis} = this.props;
        let superMd = this;
        let dark = EnvUtils.isDarkMode();

        function setDarkMode(editor, dark) {
            editor.setTheme(dark ? "dark" : "default");
            editor.setPreviewTheme(dark ? "dark" : "default");
            editor.setEditorTheme(dark ? "pastel-on-dark" : "default");
        }

        // eslint-disable-next-line no-undef,no-unused-vars
        let editor = editormd(editorMdId, $, {
            codeFold: true,
            searchReplace: true,
            htmlDecode: "pre",
            taskList: true,
            tocm: false,
            tex: true,
            flowChart: true,
            sequenceDiagram: true,
            dialogMaskOpacity: 0,
            dialogMaskBgColor: "#000",
            imageUpload: true,
            watch: true,
            toolbarIcons: function () {
                return ["bold", "del", "italic", "quote", "|", "h1", "h2", "h3", "h4", "h5", "|", "list-ul", "list-ol",
                    "hr", "pagebreak", "|", "link", "reference-link", "image", "file", "video", "|", "preformatted-text",
                    "code-block", "table", "|", "fullscreen", "copyPreviewHtml", "|", "info", "help"]
            },
            imageUploadURL: "/api/admin/upload",
            path: "/admin/vendors/markdown/lib/",
            width: "100%",
            height: "1240px",
            placeholder: superThis.state.res['editorPlaceholder'],
            markdown: superThis.state.article.markdown,
            onload: function () {
                $("#fileDialog").on("click", function () {
                    editor.executePlugin("fileDialog", "file-dialog/file-dialog");
                });
                $("#videoDialog").on("click", function () {
                    editor.executePlugin("videoDialog", "video-dialog/video-dialog");
                });
                $("#copPreviewHtmlToClipboard").on("click", function () {
                    function copyToClipboard(html) {
                        const temp = $("<input>");
                        $("body").append(temp);
                        temp.val(html).select();
                        document.execCommand("copy");
                        temp.remove();
                    }

                    copyToClipboard('<div class="markdown-body" style="padding:0">' + editor.getPreviewedHTML() + "</div>");
                    message.info(superMd.state.res.copPreviewHtmlToClipboardSuccess);
                });
                superMd.setState({
                    editorLoading: false
                });

                function uploadFile(file) {
                    const index = Math.random().toString(10).substr(2, 5) + '-' + Math.random().toString(36).substr(2);
                    const fileName = index + '.png';
                    const formData = new FormData();
                    formData.append('imgFile', file, fileName);
                    $.ajax({
                        method: 'post',
                        url: "/api/admin/upload?dir=image",
                        data: formData,
                        contentType: false,
                        processData: false,
                        success: function (data) {
                            const url = data.data.url;
                            editor.insertValue("![](" + url + ")");
                        },
                        error: function (error) {
                            alert(error);
                        }
                    });
                }

                const jqMd = $("#" + editorMdId);
                if (jqMd && jqMd[0]) {
                    jqMd[0].addEventListener('paste', function (e) {
                        const clipboardData = e.clipboardData;
                        const items = clipboardData.items;
                        for (let i = 0; i < items.length; i++) {
                            if (items[i].kind === 'file' && items[i].type.match(/^image/)) {
                                // 取消默认的粘贴操作
                                e.preventDefault();
                                // 上传文件
                                uploadFile(items[i].getAsFile());
                                break;
                            }
                        }
                    });
                }
                setDarkMode(editor, dark);
            },

            onchange: function () {
                const changed = {
                    markdown: this.getMarkdown(),
                    content: this.getPreviewedHTML()
                }
                superThis.autoSaveToRubbish(changed, 3000);
            },
            onfullscreen: function () {
                superThis.onfullscreen();
                editor.width("100%");
                setDarkMode(editor, true);
            },

            onfullscreenExit: function () {
                superThis.onfullscreenExit();
                editor.width("100%");
                setDarkMode(editor, dark);
            }
        });
    }

    render() {
        return (
            <Spin spinning={this.state.editorLoading}>
                <div id={editorMdId} style={{borderRadius: 2}}/>
            </Spin>);
    }

    getSecondTitle() {
        return this.state.res['admin.log.edit'];
    }
}


class MyEditorMdWrapper extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            mdEditorScriptLoaded: false,
        };
    }

    render() {
        const EditMdAsyncScriptLoader = makeAsyncScriptLoader("/admin/vendors/markdown/js/editormd.min.js")(MyLoadingComponent);
        if (this.state.mdEditorScriptLoaded) {
            return (
                <MyEditorMd superThis={this.props.superThis}/>
            )
        }
        return (
            <>
                <EditMdAsyncScriptLoader
                    asyncScriptOnLoad={() => {
                        this.setState({mdEditorScriptLoaded: true});
                    }}
                />
            </>
        );
    }
}

export default MyEditorMdWrapper;
