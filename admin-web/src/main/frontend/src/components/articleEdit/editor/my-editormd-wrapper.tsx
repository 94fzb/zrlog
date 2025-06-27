import {FunctionComponent, ReactNode, useEffect, useState} from "react";
import $ from "jquery";
import Spin from "antd/es/spin";
import {message} from "antd";
import makeAsyncScriptLoader from "react-async-script";
import MyLoadingComponent from "../../my-loading-component";
import EnvUtils from "../../../utils/env-utils";
import {getRes, isDev} from "../../../utils/constants";

import {StyledEditormd} from "./styled-editormd";
import {ChangedContent} from "../index.types";
import {getContextPath} from "../../../utils/helpers";
import EditorDialog from "./EditorDialog";
import {EditorDialogState, MyEditorMdWrapperProps} from "./editor.types";


type MyEditorMdWrapperState = {
    editorLoading: boolean;
    mdEditorScriptLoaded: boolean;
    id: string;
};


export type ScriptLoaderProps = {
    asyncScriptOnLoad?: (() => void) | undefined;
    isLoading: boolean;
    error: ReactNode;
};

const getResourceBaseUrl = () => {
    if (isDev()) {
        return "admin/"
    }
    const url = getRes()["admin_static_resource_base_url"] as string;
    if (url && url.trim().length > 0) {
        return url + "/admin/";
    }
    return getContextPath() + "admin/";
};

let editor: any;

const MyEditorMd: FunctionComponent<MyEditorMdWrapperProps> = ({height, markdown, onChange, loadSuccess}) => {
    const [state, setState] = useState<MyEditorMdWrapperState>({
        mdEditorScriptLoaded: true,
        editorLoading: true,
        id: "editor-" + new Date().getTime(),
    });

    const [dialogState, setDialogState] = useState<EditorDialogState>({
        open: false,
        title: "",
        type: "image"
    })

    const [content, setContent] = useState<ChangedContent>({content: "", markdown: markdown});

    const [messageApi, contextHolder] = message.useMessage({maxCount: 3});

    function setDarkMode(editor: any, dark: boolean) {
        editor.setTheme(dark ? "dark" : "default");
        editor.setPreviewTheme(dark ? "dark" : "default");
        editor.setEditorTheme(dark ? "pastel-on-dark" : "default");
    }

    function initSuccess(editor: any) {
        $(".CodeMirror-gutters").css("left", "0px");
        setState({
            ...state,
            editorLoading: false,
        });
        if (loadSuccess) {
            loadSuccess(editor);
        }
    }

    const initEditor = () => {
        // eslint-disable-next-line no-undef,no-unused-vars
        //@ts-ignore
        editor = editormd(state.id, $, {
            codeFold: true,
            searchReplace: true,
            htmlDecode: "pre",
            taskList: true,
            tocm: false,
            tex: true,
            height: height,
            flowChart: true,
            sequenceDiagram: true,
            dialogMaskOpacity: 0,
            dialogMaskBgColor: "#000",
            imageUpload: true,
            watch: window.innerWidth > 600,
            toolbarIcons: function () {
                return [
                    "bold",
                    "del",
                    "italic",
                    "quote",
                    "|",
                    "h2",
                    "h3",
                    "h4",
                    "|",
                    "list-ul",
                    "list-ol",
                    "hr",
                    "pagebreak",
                    "|",
                    "link",
                    "image",
                    "file",
                    "video",
                    "|",
                    "code",
                    "table",
                    "copyPreviewHtml",
                    "watch",
                    "|",
                    "help",
                ];
            },
            path: getResourceBaseUrl() + "vendors/markdown/lib/",
            placeholder: getRes()["editorPlaceholder"],
            markdown: content.markdown,
            onload: function () {
                $("#fileDialog").on("click touchend", function () {
                    setDialogState({
                        open: true,
                        title: "添加文件",
                        type: "file"
                    })
                });
                $("#videoDialog").on("click touchend", function () {
                    setDialogState({
                        open: true,
                        title: "添加视频",
                        type: "video"
                    })
                });
                $("#linkDialog").on("click touchend", function () {
                    setDialogState({
                        open: true,
                        title: "添加链接",
                        type: "link"
                    })
                });
                $("#imageDialog").on("click touchend", function () {
                    setDialogState({
                        open: true,
                        title: "添加图片",
                        type: "image"
                    })
                });
                $("#codeDialog").on("click touchend", function () {
                    setDialogState({
                        open: true,
                        title: "添加代码块",
                        type: "code"
                    })
                });
                $("#tableDialog").on("click touchend", function () {
                    setDialogState({
                        open: true,
                        title: "添加表格",
                        type: "table"
                    })
                });
                $("#helpDialog").on("click touchend", function () {
                    setDialogState({
                        open: true,
                        title: "帮助",
                        type: "help"
                    })
                });
                $("#copPreviewHtmlToClipboard").on("click touchend", function () {
                    function copyToClipboard(html: any) {
                        const temp = $("<input>");
                        $("body").append(temp);
                        temp.val(html).select();
                        document.execCommand("copy");
                        temp.remove();
                    }

                    copyToClipboard(
                        '<div class="markdown-body" style="padding:0">' + editor.getPreviewedHTML() + "</div>"
                    );
                    messageApi.info(getRes().copPreviewHtmlToClipboardSuccess);
                });

                setDarkMode(editor, EnvUtils.isDarkMode());
                initSuccess(editor);
            },

            onchange: async function () {
                setContent({
                    markdown: editor.getMarkdown(),
                    content: editor.getPreviewedHTML(),
                });
            },

            onfullscreen: function () {
                editor.width("100%");
            },
            onfullscreenExit: function () {
                editor.width("100%");
            },
        });
    };

    useEffect(() => {
        onChange(content);
    }, [content]);

    useEffect(() => {
        try {
            //@ts-ignore
            window.createEditorMDInstance();
            initEditor();
        } catch (e) {
            messageApi.error((e as Error).message).then(() => {
                //ignore
            });
        }
        return () => {
            $(document.getElementById(state.id) as HTMLDivElement).off();
            $(document.body as HTMLBodyElement).off();
        };
    }, []);

    return (
        <StyledEditormd>
            <Spin spinning={state.editorLoading} delay={500} style={{height: "100%", overflowY: "auto"}}>
                {contextHolder}
                <div
                    id={state.id}
                    className={EnvUtils.isDarkMode() ? "editor-dark" : "editor-light"}
                    style={{height: height}}
                />
                {dialogState.open && <EditorDialog title={dialogState.title} type={dialogState.type} onOk={(mdStr) => {
                    setDialogState({
                        title: "",
                        type: "image",
                        open: false
                    })
                    editor.insertValue(mdStr);
                }} onClose={() => {
                    setDialogState({
                        title: "",
                        type: "image",
                        open: false
                    })
                }}/>}
            </Spin>
        </StyledEditormd>
    );
};


const MyEditorMdWrapper: FunctionComponent<MyEditorMdWrapperProps> = ({height, markdown, onChange, loadSuccess}) => {
    const [mdEditorScriptLoaded, setMdEditorScriptLoaded] = useState<boolean>(false);

    const EditMdAsyncScriptLoader = makeAsyncScriptLoader(
        getResourceBaseUrl() + "vendors/markdown/js/editormd-1.5.8.js"
    )(MyLoadingComponent) as unknown as FunctionComponent<ScriptLoaderProps>;
    if (mdEditorScriptLoaded) {
        return <MyEditorMd height={height} markdown={markdown} loadSuccess={loadSuccess} onChange={onChange}/>;
    }
    return (
        <div style={{height: height}}>
            <EditMdAsyncScriptLoader
                asyncScriptOnLoad={() => {
                    setMdEditorScriptLoaded(true);
                }}
                isLoading={false}
                error={<></>}
            />
        </div>
    );
};

export default MyEditorMdWrapper;
