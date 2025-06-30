import CodeMirror, {EditorSelection, EditorView} from "@uiw/react-codemirror";
import {FunctionComponent, useEffect, useRef, useState} from "react";
import {MyEditorMdWrapperProps} from "./editor.types";
import {marked} from "marked";
import EnvUtils from "../../../utils/env-utils";
import {StyledEditormd} from "./styled-editormd";
import EditorToolBar from "./editor-tool-bar";
import {getRes} from "../../../utils/constants";
import useMessage from "antd/es/message/useMessage";
import {getBorder} from "./editor-helpers";
import {StyledPreview} from "./styled-preview";
import {languages} from "@codemirror/language-data";
import {markdown} from "@codemirror/lang-markdown";
import hljs from "highlight.js/lib/core";
import java from 'highlight.js/lib/languages/java';
import PasteUpload from "./paste-upload";
import ScrollSync from "./scroll-sync";
import {StyledHighlightDark} from "./highlight/styled-highlight-dark";
import {StyledHighlightDefault} from "./highlight/styled-highlight-default";

type MarkdownEditorState = {
    markdownValue: string;
    preview: boolean;
}

hljs.registerLanguage('java', java);

const MarkedEditor: FunctionComponent<MyEditorMdWrapperProps> = ({
                                                                     height,
                                                                     value,
                                                                     onChange,
                                                                     loadSuccess,
                                                                     getContainer
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

    const editorRef = useRef<EditorView | null>(null);
    const previewRef = useRef<HTMLDivElement | null>(null);

    const [messageApi, contextHolder] = useMessage({maxCount: 3});


    const insertTextAtCursor = (text: string, cursorPosition: number) => {
        const view = editorRef.current;
        if (!view) return;

        const pos = view.state.selection.main.head; // 当前光标位置

        view.dispatch({
            changes: {from: pos, insert: text},
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

    const doCopy = () => {
        copyToClipboard(
            '<div class="markdown-body" style="padding:0">' + marked(state.markdownValue) + "</div>"
        );
        messageApi.info(getRes().copPreviewHtmlToClipboardSuccess);
    }

    const getPreviewBody = () => {
        return <div dangerouslySetInnerHTML={{__html: marked(state.markdownValue)}}
                    className={"markdown-body"}/>
    }

    useEffect(() => {
        if (loadSuccess) {
            loadSuccess(null);
        }
    }, []);


    return <StyledEditormd style={{paddingBottom: 30}}>
        {editorRef.current && <PasteUpload onUploadSuccess={(imgUrl) => {
            const content = "![](" + imgUrl + ")\n"
            insertTextAtCursor(content, content.length);
        }} editorView={editorRef.current.contentDOM as HTMLElement}/>}
        <div className={EnvUtils.isDarkMode() ? "editor-dark" : "editor-light"}
             style={{overflow: "hidden"}}>
            {contextHolder}
            <EditorToolBar getContainer={getContainer} onChange={(mdStr, cursorPosition) => {
                insertTextAtCursor(mdStr, cursorPosition)
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
                    basicSetup={{searchKeymap: false}}
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
                    }
                    }
                    theme={EnvUtils.isDarkMode() ? "dark" : "light"}
                    extensions={[markdown({codeLanguages: languages}), EditorView.lineWrapping]}
                    onCreateEditor={(view) => {
                        editorRef.current = view;
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
                <StyledPreview ref={previewRef} className={"preview"}
                               style={{
                                   padding: 4,
                                   display: state.preview ? "block" : "none",
                                   width: "calc(50% - 4px)"
                               }}>

                    {EnvUtils.isDarkMode() ? <StyledHighlightDark>{getPreviewBody()}</StyledHighlightDark> :
                        <StyledHighlightDefault>{getPreviewBody()} </StyledHighlightDefault>}
                </StyledPreview>
            </div>
        </div>
        {(editorRef.current && previewRef.current && editorRef.current.scrollDOM) &&
            <ScrollSync editorRef={editorRef} previewRef={previewRef}/>}
    </StyledEditormd>
}

export default MarkedEditor;