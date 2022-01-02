import {FunctionComponent, useEffect, useState} from "react";
import './my-editormd.css';
import $ from 'jquery';
import Spin from "antd/es/spin";
import {message} from "antd";
import makeAsyncScriptLoader from "react-async-script";
import {MyLoadingComponent} from "../../my-loading-component";
import EnvUtils from "../../../utils/env-utils";
import {getRes} from "../../../utils/constants";
import {ArticleEntry} from "../article-edit";

const editorMdId = 'editor';

type MyEditorMdWrapperState = {
    editorLoading: boolean,
    mdEditorScriptLoaded: boolean,
}

export type ChangedContent = {
    content?: string,
    markdown?: string,
}

const MyEditorMd: FunctionComponent<MyEditorMdWrapperProps> = ({
                                                                   onfullscreen,
                                                                   onfullscreenExit,
                                                                   markdown,
                                                                   autoSaveToRubbish,
                                                                   loadSuccess
                                                               }) => {


    const [state, setState] = useState<MyEditorMdWrapperState>({
        mdEditorScriptLoaded: true,
        editorLoading: true,
    })

    useEffect(() => {
        const dark = EnvUtils.isDarkMode();

        function setDarkMode(editor: any, dark: boolean) {
            editor.setTheme(dark ? "dark" : "default");
            editor.setPreviewTheme(dark ? "dark" : "default");
            editor.setEditorTheme(dark ? "pastel-on-dark" : "default");
        }

        function initSuccess() {
            $(".CodeMirror-gutters").css("left", "0px");
            setState({
                ...state,
                editorLoading: false
            })
            loadSuccess();
        }

        // eslint-disable-next-line no-undef,no-unused-vars
        //@ts-ignore
        const editor = editormd(editorMdId, $, {
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
                return ["bold", "del", "italic", "quote", "|", "h2", "h3", "h4", "|", "list-ul", "list-ol",
                    "hr", "pagebreak", "|", "link", "reference-link", "image", "file", "video", "|", "preformatted-text",
                    "code-block", "table", "copyPreviewHtml", "|", "fullscreen", "info", "help"]
            },
            imageUploadURL: document.baseURI + "api/admin/upload",
            path: document.baseURI + "admin/vendors/markdown/lib/",
            width: "100%",
            height: "1240px",
            placeholder: getRes()['editorPlaceholder'],
            markdown: markdown,
            onload: function () {
                $("#fileDialog").on("click", function () {
                    editor.executePlugin("fileDialog", "file-dialog/file-dialog");
                });
                $("#videoDialog").on("click", function () {
                    editor.executePlugin("videoDialog", "video-dialog/video-dialog");
                });
                $("#copPreviewHtmlToClipboard").on("click", function () {
                    function copyToClipboard(html: any) {
                        const temp = $("<input>");
                        $("body").append(temp);
                        temp.val(html).select();
                        document.execCommand("copy");
                        temp.remove();
                    }

                    copyToClipboard('<div class="markdown-body" style="padding:0">' + editor.getPreviewedHTML() + "</div>");
                    message.info(getRes().copPreviewHtmlToClipboardSuccess);
                });

                function uploadFile(file: string | Blob | null) {
                    const index = Math.random().toString(10).substr(2, 5) + '-' + Math.random().toString(36).substr(2);
                    const fileName = index + '.png';
                    const formData = new FormData();
                    // @ts-ignore
                    formData.append('imgFile', file, fileName);
                    $.ajax({
                        method: 'post',
                        url: document.baseURI + "/api/admin/upload?dir=image",
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
                        // @ts-ignore
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
                setTimeout(initSuccess, 100);
            },

            onchange: function () {
                const changed = {
                    markdown: this.getMarkdown(),
                    content: this.getPreviewedHTML()
                }
                autoSaveToRubbish(changed, 1000);
            },
            onfullscreen: function () {
                onfullscreen(editor);
                editor.width("100%");
                setDarkMode(editor, true);
            },

            onfullscreenExit: function () {
                onfullscreenExit();
                editor.width("100%");
                setDarkMode(editor, dark);
            }
        });
    }, [])

    return (
        <Spin spinning={state.editorLoading}>
            <div id={editorMdId} style={{borderRadius: 2}}/>
        </Spin>);
}


type MyEditorMdWrapperProps = {
    onfullscreen: (editor: any) => void,
    onfullscreenExit: () => void,
    autoSaveToRubbish: (content: ChangedContent | ArticleEntry, delay: number) => void,
    markdown?: string,
    loadSuccess: () => void
}

const MyEditorMdWrapper: FunctionComponent<MyEditorMdWrapperProps> = ({
                                                                          onfullscreen,
                                                                          markdown,
                                                                          autoSaveToRubbish,
                                                                          onfullscreenExit,
                                                                          loadSuccess
                                                                      }) => {
    const [mdEditorScriptLoaded, setMdEditorScriptLoaded] = useState<boolean>(false);

    const EditMdAsyncScriptLoader = makeAsyncScriptLoader(document.baseURI + "/admin/vendors/markdown/js/editormd.min.js")(MyLoadingComponent);
    if (mdEditorScriptLoaded) {
        return (
            <MyEditorMd markdown={markdown}
                        loadSuccess={loadSuccess}
                        autoSaveToRubbish={autoSaveToRubbish} onfullscreen={onfullscreen}
                        onfullscreenExit={onfullscreenExit}/>
        )
    }
    return (
        <EditMdAsyncScriptLoader
            asyncScriptOnLoad={() => {
                setMdEditorScriptLoaded(true);
            }} isLoading={false} error={undefined}/>
    );
}

export default MyEditorMdWrapper;
