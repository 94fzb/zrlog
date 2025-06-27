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


const CodemirrorMarkedEditor: FunctionComponent<MyEditorMdWrapperProps> = ({
                                                                               height,
                                                                               markdown,
                                                                               onChange,
                                                                               loadSuccess
                                                                           }) => {

    const [markdownValue, setMarkdownValue] = useState<string>(markdown ? markdown : "");

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
            '<div class="markdown-body" style="padding:0">' + marked(markdownValue) + "</div>"
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
            const oldLength = markdownValue.length;
            setMarkdownValue(markdownValue + mdStr)
            setTimeout(() => {
                setAnchorLocation(oldLength + cursorPosition - 1)
            }, 100);
        }} onCopy={() => {
            doCopy();
        }}/>
        <div style={{height: height, display: "flex"}}>
            <CodeMirror
                placeholder={getRes()["editorPlaceholder"]}
                value={markdownValue}
                height="100%"
                width={"100%"}
                theme={EnvUtils.isDarkMode() ? "dark" : "light"}
                extensions={[markdownLanguage, EditorView.lineWrapping]}
                onCreateEditor={(view) => {
                    viewRef.current = view;
                }}
                onChange={async (val) => {
                    setMarkdownValue(val)
                    const content = await marked(markdownValue);
                    onChange({
                        markdown: val,
                        content: content,
                    })

                }}
                style={{minWidth: "50%", width: "50%", borderRight: getBorder()}}
            />
            <div dangerouslySetInnerHTML={{__html: marked(markdownValue)}} style={{padding: 2}}/>
        </div>
    </StyledEditormd>
}

export default CodemirrorMarkedEditor;