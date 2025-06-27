import styled from "styled-components";

export const StyledEditormd = styled("div")`

    .cm-editor.cm-focused {
        outline: none !important;
        box-shadow: none !important;
    }

    .cm-scroller {
        font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, 'Noto Sans', sans-serif, 'Apple Color Emoji', 'Segoe UI Emoji', 'Segoe UI Symbol', 'Noto Color Emoji'
    }

    .editor-dark .cm-scroller {
        background-color: #1a1a17;
    }

    .editor-dark .cm-gutters {
        background-color: #141414;
    }

    .editor-dark .preview {
        background-color: #1a1a17;
    }
`;
