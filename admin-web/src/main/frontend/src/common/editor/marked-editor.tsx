import CodeMirror, { EditorSelection, EditorView } from "@uiw/react-codemirror";
import { FunctionComponent, useEffect, useRef, useState } from "react";
import { MarkdownEditorProps } from "./editor.types";
import EnvUtils from "../../utils/env-utils";
import { StyledEditor } from "./styles/styled-editor";
import EditorToolBar from "./editor-tool-bar";
import { getRes } from "../../utils/constants";
import useMessage from "antd/es/message/useMessage";
import { getBorder } from "./editor-helpers";
import { StyledPreview } from "./styles/styled-preview";
import { languages } from "@codemirror/language-data";
import { markdown } from "@codemirror/lang-markdown";

import PasteUpload from "./paste-upload";
import ScrollSync from "./scroll-sync";
import EditorPreview from "./editor-preview";
import { markdownToHtml } from "./utils/marked-utils";

type MarkdownEditorState = {
    markdownValue: string;
    content: string;
    preview: boolean;
};

const MarkedEditor: FunctionComponent<MarkdownEditorProps> = ({
    height,
    value,
    onChange,
    content,
    loadSuccess,
    getContainer,
}) => {
    const [state, setState] = useState<MarkdownEditorState>({
        markdownValue: value ? value : "",
        preview: window.innerWidth > 600,
        content: content,
    });

    const editorRef = useRef<EditorView | null>(null);
    const previewRef = useRef<HTMLDivElement | null>(null);

    const [messageApi, contextHolder] = useMessage({ maxCount: 3 });

    const insertTextAtCursor = (text: string, cursorPosition: number) => {
        const view = editorRef.current;
        if (!view) return;

        const pos = view.state.selection.main.head; // 当前光标位置

        view.dispatch({
            changes: { from: pos, insert: text },
            selection: EditorSelection.cursor(pos + cursorPosition),
            scrollIntoView: true,
        });
        view.focus(); // 确保光标可见
    };

    function copyToClipboard(html: string) {
        const temp = document.createElement("input") as HTMLInputElement;
        document.body.append(temp);
        temp.value = html;
        temp.select();
        document.execCommand("copy", false);
        temp.remove();
    }

    const doCopy = async () => {
        copyToClipboard('<div class="markdown-body" style="padding:0">' + state.content + "</div>");
        messageApi.info(getRes().copPreviewHtmlToClipboardSuccess);
    };

    useEffect(() => {
        if (loadSuccess) {
            loadSuccess(null);
        }
    }, []);

    useEffect(() => {
        const md = state.markdownValue;
        markdownToHtml(md).then((html) => {
            const changeValues = {
                content: html,
                markdown: md,
            };
            setState((prevState) => {
                return {
                    ...prevState,
                    markdownValue: md,
                    content: html,
                };
            });
            onChange(changeValues);
        });
    }, [state.markdownValue]);

    return (
        <StyledEditor style={{ paddingBottom: 30 }}>
            {editorRef.current && (
                <PasteUpload
                    onUploadSuccess={(imgUrl) => {
                        const content = "![](" + imgUrl + ")\n";
                        insertTextAtCursor(content, content.length);
                    }}
                    editorView={editorRef.current.contentDOM as HTMLElement}
                />
            )}
            <div className={EnvUtils.isDarkMode() ? "editor-dark" : "editor-light"} style={{ overflow: "hidden" }}>
                {contextHolder}
                <EditorToolBar
                    getContainer={getContainer}
                    onChange={(mdStr, cursorPosition) => {
                        insertTextAtCursor(mdStr, cursorPosition);
                    }}
                    onCopy={async () => {
                        await doCopy();
                    }}
                    onEditorModeChange={(preview) => {
                        setState((prevState) => {
                            return {
                                ...prevState,
                                preview: preview,
                            };
                        });
                    }}
                    preview={state.preview}
                />
                <div style={{ height: height, display: "flex" }}>
                    <CodeMirror
                        basicSetup={{ searchKeymap: false }}
                        placeholder={getRes()["editorPlaceholder"]}
                        value={state.markdownValue}
                        height={height}
                        width={"100%"}
                        onUpdate={(viewUpdate) => {
                            if (viewUpdate.viewportChanged) {
                                /*setState((prevState) => {
                                return {
                                    ...prevState,
                                    preview: window.innerWidth > 600,
                                }
                            })*/
                            }
                        }}
                        theme={EnvUtils.isDarkMode() ? "dark" : "light"}
                        extensions={[markdown({ codeLanguages: languages }), EditorView.lineWrapping]}
                        onCreateEditor={(view) => {
                            editorRef.current = view;
                        }}
                        onChange={async (val) => {
                            setState((prevState) => {
                                return {
                                    ...prevState,
                                    markdownValue: val,
                                };
                            });
                        }}
                        style={{
                            minWidth: state.preview ? "50%" : "calc(100% - 4px)",
                            width: state.preview ? "50%" : "calc(100% - 4px)",
                            borderRight: getBorder(),
                        }}
                    />
                    <StyledPreview
                        ref={previewRef}
                        className={"preview"}
                        style={{
                            padding: 4,
                            display: state.preview ? "block" : "none",
                            width: "calc(50% - 4px)",
                        }}
                    >
                        <EditorPreview htmlContent={state.content} />
                    </StyledPreview>
                </div>
            </div>
            {editorRef.current && previewRef.current && editorRef.current.scrollDOM && (
                <ScrollSync editorRef={editorRef} previewRef={previewRef} />
            )}
        </StyledEditor>
    );
};

export default MarkedEditor;
