import styled from "styled-components";

export const StyledEditormd = styled("div")`

    .cm-editor.cm-focused {
        outline: none !important;
        box-shadow: none !important;
    }

    .preview {
        overflow: auto;
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

    .editor-dark .markdown-body table th,
    .editor-dark .markdown-body table td {
        border: 1px solid rgba(198, 198, 198, 0.5);
    }

    .editor-dark .markdown-body table tr {
        background-color: #212529 !important;
        border-top: 1px solid rgba(198, 198, 198, 0.5);
    }

    .editor-dark .markdown-body table tr:nth-child(2n) {
        background-color: #212529 !important;
    }

    .editor-dark .markdown-body pre {
        background-color: #1f1f1f !important;
    }
`;
