import EnvUtils from "../../utils/env-utils";
import { StyledHighlightDark } from "./highlight/styled-highlight-dark";
import { StyledHighlightDefault } from "./highlight/styled-highlight-default";
import { FunctionComponent } from "react";

export type EditorPreviewProps = {
    htmlContent: string;
};

const previewId = "editor-preview";

const EditorPreview: FunctionComponent<EditorPreviewProps> = ({ htmlContent }) => {
    return EnvUtils.isDarkMode() ? (
        <StyledHighlightDark
            id={previewId}
            dangerouslySetInnerHTML={{ __html: htmlContent }}
            className={"markdown-body"}
        />
    ) : (
        <StyledHighlightDefault
            id={previewId}
            dangerouslySetInnerHTML={{ __html: htmlContent }}
            className={"markdown-body"}
        />
    );
};

export default EditorPreview;
