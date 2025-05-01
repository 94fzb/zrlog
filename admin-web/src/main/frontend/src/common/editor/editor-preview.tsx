import EnvUtils from "../../utils/env-utils";
import {StyledHighlightDark} from "./highlight/styled-highlight-dark";
import {StyledHighlightDefault} from "./highlight/styled-highlight-default";
import {marked} from "marked";
import {FunctionComponent, useEffect, useState} from "react";
import "katex/dist/katex.min.css";
import {renderTex} from "./katex/katex-helpers";

export const getPreviewStr = (markdownValue: string) => {
    const el = document.getElementById(previewId);
    if (el) {
        return el.innerHTML;
    }
    const text = marked(markdownValue) as string;
    return renderTex(text);
}

export type  EditorPreviewProps = {
    markdownValue: string;
}

const previewId = "editor-preview";

const EditorPreview: FunctionComponent<EditorPreviewProps> = ({markdownValue}) => {

    const [previewText, setPreviewText] = useState<string>(getPreviewStr(markdownValue));


    useEffect(() => {
        setPreviewText(getPreviewStr(markdownValue));
    }, [markdownValue])

    return EnvUtils.isDarkMode() ? <StyledHighlightDark id={previewId} dangerouslySetInnerHTML={{__html: previewText}}
                                                        className={"markdown-body"}/> :
        <StyledHighlightDefault id={previewId} dangerouslySetInnerHTML={{__html: previewText}}
                                className={"markdown-body"}/>
}

export default EditorPreview;