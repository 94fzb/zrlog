import CodeMirror, {EditorSelection, EditorView} from "@uiw/react-codemirror";
import {FunctionComponent, useEffect, useRef, useState} from "react";
import {MyEditorMdWrapperProps} from "./editor.types";
import {markdownLanguage} from "@codemirror/lang-markdown";
import {marked} from "marked";
import EnvUtils from "../../../utils/env-utils";
import {StyledEditormd} from "./styled-editormd";
import EditorToolBar from "./editor-tool-bar";
import $ from "jquery";
import {getRes} from "../../../utils/constants";
import useMessage from "antd/es/message/useMessage";
import {getBorder} from "./editor-helpers";

type MarkdownEditorState = {
    markdownValue: string;
    preview: boolean;
}


const MarkedEditor: FunctionComponent<MyEditorMdWrapperProps> = ({
                                                                     height,
                                                                     markdown,
                                                                     onChange,
                                                                     loadSuccess
                                                                 }) => {

    const [state, setState] = useState<MarkdownEditorState>({
        markdownValue: markdown ? markdown : "",
        preview: window.innerWidth > 600,
    });

    const viewRef = useRef<EditorView | null>(null);

    const [messageApi, contextHolder] = useMessage();


    const setAnchorLocation = (index: number) => {
        viewRef.current?.dispatch({
            selection: EditorSelection.cursor(index),
            scrollIntoView: true
        });
    }


    function copyToClipboard(html: any) {
        const temp = $("<input>");
        $("body").append(temp);
        temp.val(html).select();
        document.execCommand("copy");
        temp.remove();
    }

    const doCopy = () => {
        copyToClipboard(
            '<div class="markdown-body" style="padding:0">' + marked(state.markdownValue) + "</div>"
        );
        messageApi.info(getRes().copPreviewHtmlToClipboardSuccess);
    }


    useEffect(() => {
        if (loadSuccess) {
            loadSuccess(null);
        }
    }, []);


    return <StyledEditormd>
        {contextHolder}
        <EditorToolBar onChange={(mdStr, cursorPosition) => {
            const oldLength = state.markdownValue.length;
            setState((prevState) => {
                return {
                    ...prevState,
                    markdownValue: prevState.markdownValue + mdStr,
                }
            })
            setTimeout(() => {
                setAnchorLocation(oldLength + cursorPosition - 1)
            }, 100);
        }} onCopy={() => {
            doCopy();
        }} onEditorModeChange={(preview) => {
            setState((prevState) => {
                return {
                    ...prevState,
                    preview: preview
                }
            })
        }} preview={state.preview}/>
        <div style={{height: height, display: "flex"}}>
            <CodeMirror
                placeholder={getRes()["editorPlaceholder"]}
                value={state.markdownValue}
                height="100%"
                width={"100%"}
                onUpdate={(viewUpdate) => {
                    if (viewUpdate.viewportChanged) {
                        setState((prevState) => {
                            return {
                                ...prevState,
                                preview: window.innerWidth > 600,
                            }
                        })
                    }
                }
                }
                theme={EnvUtils.isDarkMode() ? "dark" : "light"}
                extensions={[markdownLanguage, EditorView.lineWrapping]}
                onCreateEditor={(view) => {
                    viewRef.current = view;
                }}
                onChange={async (val) => {
                    setState((prevState) => {
                        return {
                            ...prevState,
                            markdownValue: val,
                        }
                    })
                    const content = await marked(val);
                    onChange({
                        markdown: val,
                        content: content,
                    })

                }}
                style={{
                    minWidth: state.preview ? "50%" : "100%",
                    width: state.preview ? "50%" : "100%",
                    borderRight: getBorder()
                }}
            />
            <div dangerouslySetInnerHTML={{__html: marked(state.markdownValue)}}
                 style={{padding: 2, display: state.preview ? "block" : "none", width: "100%"}}/>
        </div>
    </StyledEditormd>
}

export default MarkedEditor;