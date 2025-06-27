import CodeMirror, {EditorSelection, EditorView} from "@uiw/react-codemirror";
import {FunctionComponent, useEffect, useRef, useState} from "react";
import {MyEditorMdWrapperProps} from "./editor.types";
import {marked} from "marked";
import EnvUtils from "../../../utils/env-utils";
import {StyledEditormd} from "./styled-editormd";
import EditorToolBar from "./editor-tool-bar";
import $ from "jquery";
import {getRes} from "../../../utils/constants";
import useMessage from "antd/es/message/useMessage";
import {getBorder} from "./editor-helpers";
import {StyledPreview} from "./styled-preview";
import {languages} from "@codemirror/language-data";
import {markdown} from "@codemirror/lang-markdown";
import "highlight.js/styles/default.css"; // 或任意你喜欢的主题
import hljs from "highlight.js/lib/core";
import java from 'highlight.js/lib/languages/java';

type MarkdownEditorState = {
    markdownValue: string;
    preview: boolean;
}

hljs.registerLanguage('java', java);

const MarkedEditor: FunctionComponent<MyEditorMdWrapperProps> = ({
                                                                     height,
                                                                     value,
                                                                     onChange,
                                                                     loadSuccess
                                                                 }) => {

    const renderer = new marked.Renderer();

    renderer.code = function ({text, lang}) {
        const validLang = lang && hljs.getLanguage(lang) ? lang : '';
        if (validLang) {
            const highlighted = hljs.highlight(text, {language: validLang}).value;
            return `<pre><code class="hljs language-${validLang}">${highlighted}</code></pre>`;
        } else {
            const highlighted = hljs.highlightAuto(text).value;
            return `<pre><code class="hljs language-java">${highlighted}</code></pre>`;
        }
    };


    marked.setOptions({
        gfm: true,
        breaks: true,
        renderer
    }); // ✅ 这样确保类型对得上

    const [state, setState] = useState<MarkdownEditorState>({
        markdownValue: value ? value : "",
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
        <div className={EnvUtils.isDarkMode() ? "editor-dark" : "editor-light"} style={{overflow: "hidden"}}>
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
                    height={height}
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
                    extensions={[markdown({codeLanguages: languages}), EditorView.lineWrapping,]}
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
                <StyledPreview className={"preview"}
                               style={{padding: 4, display: state.preview ? "block" : "none", width: "100%"}}>
                    <div dangerouslySetInnerHTML={{__html: marked(state.markdownValue)}}
                         className={"markdown-body"}/>
                </StyledPreview>
            </div>
        </div>
    </StyledEditormd>
}

export default MarkedEditor;