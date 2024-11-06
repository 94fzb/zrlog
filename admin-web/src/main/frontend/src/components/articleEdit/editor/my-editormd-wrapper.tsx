import { FunctionComponent, ReactNode, useEffect, useState } from "react";
import $ from "jquery";
import Spin from "antd/es/spin";
import { message } from "antd";
import makeAsyncScriptLoader from "react-async-script";
import MyLoadingComponent from "../../my-loading-component";
import EnvUtils from "../../../utils/env-utils";
import { getRes } from "../../../utils/constants";
import { dom, library } from "@fortawesome/fontawesome-svg-core";
import {
    fa2,
    fa3,
    fa4,
    faAlignCenter,
    faAlignJustify,
    faAlignLeft,
    faAlignRight,
    faAnchor,
    faBold,
    faClipboard,
    faClose,
    faEye,
    faEyeSlash,
    faFileCode,
    faFileVideo,
    faImage,
    faInfoCircle,
    faItalic,
    faLink,
    faListOl,
    faListUl,
    faMinus,
    faNewspaper,
    faPaperclip,
    faPhotoFilm,
    faQuestionCircle,
    faQuoteLeft,
    faStrikethrough,
    faTable,
} from "@fortawesome/free-solid-svg-icons";
import { StyledEditormd } from "./styled-editormd";
import axios from "axios";
import { ChangedContent } from "../index.types";
// Add the icons to the library so you can use it in your page
const icons = [
    faBold,
    faStrikethrough,
    fa4,
    faAlignCenter,
    faAlignJustify,
    faAlignLeft,
    faAlignRight,
    faItalic,
    faQuoteLeft,
    faListUl,
    faListOl,
    faMinus,
    faQuestionCircle,
    faAnchor,
    faInfoCircle,
    faClipboard,
    faTable,
    faFileCode,
    faPaperclip,
    faNewspaper,
    faFileVideo,
    fa3,
    fa2,
    faImage,
    faPhotoFilm,
    faLink,
    faClose,
    faEyeSlash,
    faEye,
];
icons.forEach((e) => {
    library.add(e);
});

// This will replace any existing `<i>` elements with `<svg>` and set up a MutationObserver to continue doing this as the DOM changes.
dom.watch();

type MyEditorMdWrapperState = {
    editorLoading: boolean;
    mdEditorScriptLoaded: boolean;
    id: string;
    content: string;
    markdown: string;
};

export type ScriptLoaderProps = {
    asyncScriptOnLoad?: (() => void) | undefined;
    isLoading: boolean;
    error: ReactNode;
};

let editor: any;

const MyEditorMd: FunctionComponent<MyEditorMdWrapperProps> = ({ height, markdown, onChange, loadSuccess }) => {
    const [state, setState] = useState<MyEditorMdWrapperState>({
        mdEditorScriptLoaded: true,
        editorLoading: true,
        content: "",
        markdown: markdown ? markdown : "",
        id: "editor-" + new Date().getTime(),
    });

    const [messageApi, contextHolder] = message.useMessage();

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
                    "reference-link",
                    "image",
                    "file",
                    "video",
                    "|",
                    "preformatted-text",
                    "code-block",
                    "table",
                    "copyPreviewHtml",
                    "watch",
                    "|",
                    "help",
                ];
            },
            imageUploadURL: document.baseURI + "api/admin/upload",
            path: document.baseURI + "admin/vendors/markdown/lib/",
            placeholder: getRes()["editorPlaceholder"],
            markdown: state.markdown,
            onload: function () {
                setTimeout(() => {
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

                        copyToClipboard(
                            '<div class="markdown-body" style="padding:0">' + editor.getPreviewedHTML() + "</div>"
                        );
                        messageApi.info(getRes().copPreviewHtmlToClipboardSuccess);
                    });
                }, 100);

                function uploadFile(file: File | null) {
                    const index = Math.random().toString(10).substr(2, 5) + "-" + Math.random().toString(36).substr(2);

                    const fileName = index + ".png";
                    const formData = new FormData();
                    if (file) {
                        formData.append("imgFile", file, fileName);
                        axios.post("/api/admin/upload?dir=image", formData).then(({ data }) => {
                            const url = data.data.url;
                            editor.insertValue("![x](" + url + ")");
                        });
                    }
                }

                const jqMd = $("#" + state.id);
                if (jqMd && jqMd[0]) {
                    jqMd[0].addEventListener("paste", function (e) {
                        const clipboardData = e.clipboardData;
                        // @ts-ignore
                        const items = clipboardData.items;
                        for (let i = 0; i < items.length; i++) {
                            if (items[i].kind === "file" && items[i].type.match(/^image/)) {
                                // 取消默认的粘贴操作
                                e.preventDefault();
                                // 上传文件
                                uploadFile(items[i].getAsFile());
                                break;
                            }
                        }
                    });
                }
                setDarkMode(editor, EnvUtils.isDarkMode());
                initSuccess(editor);
            },

            onchange: async function () {
                setState({
                    ...state,
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
        onChange({ markdown: state.markdown, content: state.content });
    }, [state.markdown, state.content]);

    useEffect(() => {
        //@ts-ignore
        window.createEditorMDInstance();
        initEditor();
        return () => {
            $(document.getElementById(state.id) as HTMLDivElement).off();
            $(document.body as HTMLBodyElement).off();
        };
    }, []);

    return (
        <StyledEditormd>
            <Spin spinning={state.editorLoading} delay={500} style={{ height: "100%", overflowY: "auto" }}>
                {contextHolder}
                <div
                    id={state.id}
                    className={EnvUtils.isDarkMode() ? "editor-dark" : "editor-light"}
                    style={{ height: height }}
                />
            </Spin>
        </StyledEditormd>
    );
};

type MyEditorMdWrapperProps = {
    height: any;
    onChange: (content: ChangedContent) => Promise<void>;
    markdown?: string;
    loadSuccess?: (editor: any) => void;
};

const MyEditorMdWrapper: FunctionComponent<MyEditorMdWrapperProps> = ({ height, markdown, onChange, loadSuccess }) => {
    const [mdEditorScriptLoaded, setMdEditorScriptLoaded] = useState<boolean>(false);

    const EditMdAsyncScriptLoader = makeAsyncScriptLoader(
        document.baseURI + "admin/vendors/markdown/js/editormd-1.5.5.js"
    )(MyLoadingComponent) as unknown as FunctionComponent<ScriptLoaderProps>;
    if (mdEditorScriptLoaded) {
        return <MyEditorMd height={height} markdown={markdown} loadSuccess={loadSuccess} onChange={onChange} />;
    }
    return (
        <div style={{ height: height }}>
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
