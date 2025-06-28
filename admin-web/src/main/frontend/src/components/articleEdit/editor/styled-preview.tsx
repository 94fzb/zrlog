import styled from "styled-components";

export const StyledPreview = styled("div")`

    .markdown-body strong {
        font-weight: 700;
    }

    .markdown-body h1 {
        margin: 0.67em 0;
    }

    .markdown-body img {
        border: 0;
    }

    .markdown-body hr {
        -moz-box-sizing: content-box;
        box-sizing: content-box;
        height: 0;
    }

    .markdown-body input {
        color: inherit;
        margin: 0;
        line-height: normal;
    }

    .markdown-body html input[disabled] {
        cursor: default;
    }

    .markdown-body input[type="checkbox"] {
        -moz-box-sizing: border-box;
        box-sizing: border-box;
        padding: 0;
    }

    .markdown-body * {
        -moz-box-sizing: border-box;
        box-sizing: border-box;
    }

    .markdown-body a {
        background: 0 0;
        color: #4183c4;
        text-decoration: none;
    }

    .markdown-body a:active,
    .markdown-body a:hover {
        outline: 0;
        text-decoration: underline;
    }

    .markdown-body hr {
        margin: 15px 0;
        overflow: hidden;
        background: 0 0;
        border: 0;
        border-bottom: 1px solid #ddd;
    }

    .markdown-body h1,
    .markdown-body h2 {
        padding-bottom: 0.3rem;
        border-bottom: 1px solid #eee;
    }

    .markdown-body blockquote {
        margin: 0;
    }

    .markdown-body ol ol,
    .markdown-body ul ol {
        list-style-type: lower-roman;
    }

    .markdown-body ol ol ol,
    .markdown-body ol ul ol,
    .markdown-body ul ol ol,
    .markdown-body ul ul ol {
        list-style-type: lower-alpha;
    }

    .markdown-body dd {
        margin-left: 0;
    }

    .markdown-body .octicon {
        line-height: 1;
        display: inline-block;
        text-decoration: none;
        -webkit-font-smoothing: antialiased;
        -moz-osx-font-smoothing: grayscale;
        user-select: none;
    }

    .markdown-body .octicon-link:before {
        content: "\\f05c";
    }

    .markdown-body > :first-child {
        margin-top: 0 !important;
    }

    .markdown-body > :last-child {
        margin-bottom: 0 !important;
    }

    .markdown-body .anchor {
        position: absolute;
        top: 0;
        left: 0;
        display: block;
        padding-right: 6px;
        padding-left: 30px;
        margin-left: -30px;
    }

    .markdown-body .anchor:focus {
        outline: 0;
    }

    .markdown-body h1,
    .markdown-body h2,
    .markdown-body h3,
    .markdown-body h4,
    .markdown-body h5,
    .markdown-body h6 {
        position: relative;
        margin-top: 1rem;
        margin-bottom: 16px;
        font-weight: 700;
        line-height: 1.4;
    }

    .markdown-body h1 .octicon-link,
    .markdown-body h2 .octicon-link,
    .markdown-body h3 .octicon-link,
    .markdown-body h4 .octicon-link,
    .markdown-body h5 .octicon-link,
    .markdown-body h6 .octicon-link {
        display: none;
        color: #000;
        vertical-align: middle;
    }

    .markdown-body h1:hover .anchor,
    .markdown-body h2:hover .anchor,
    .markdown-body h3:hover .anchor,
    .markdown-body h4:hover .anchor,
    .markdown-body h5:hover .anchor,
    .markdown-body h6:hover .anchor {
        padding-left: 8px;
        margin-left: -30px;
        text-decoration: none;
    }

    .markdown-body h1:hover .anchor .octicon-link,
    .markdown-body h2:hover .anchor .octicon-link,
    .markdown-body h3:hover .anchor .octicon-link,
    .markdown-body h4:hover .anchor .octicon-link,
    .markdown-body h5:hover .anchor .octicon-link,
    .markdown-body h6:hover .anchor .octicon-link {
        display: inline-block;
    }

    .markdown-body h1 {
        font-size: 2.25rem;
        line-height: 1.2;
    }

    .markdown-body h1 .anchor {
        line-height: 1;
    }

    .markdown-body h2 {
        font-size: 1.75rem;
        line-height: 1.225;
    }

    .markdown-body h2 .anchor {
        line-height: 1;
    }

    .markdown-body h3 {
        font-size: 1.5rem;
        line-height: 1.43;
    }

    .markdown-body h3 .anchor,
    .markdown-body h4 .anchor {
        line-height: 1.2;
    }

    .markdown-body h4 {
        font-size: 1.25rem;
    }

    .markdown-body h5 .anchor,
    .markdown-body h6 .anchor {
        line-height: 1.1;
    }

    .markdown-body h5 {
        font-size: 1rem;
    }

    .markdown-body h6 {
        font-size: 1rem;
        color: #777;
    }

    .markdown-body blockquote,
    .markdown-body dl,
    .markdown-body ol,
    .markdown-body p,
    .markdown-body pre,
    .markdown-body table,
    .markdown-body ul {
        margin-top: 0;
        margin-bottom: 16px;
    }

    .markdown-body ol,
    .markdown-body ul {
        padding-left: 2rem;
    }

    .markdown-body ol ol,
    .markdown-body ol ul,
    .markdown-body ul ol,
    .markdown-body ul ul {
        margin-top: 0;
        margin-bottom: 0;
    }

    .markdown-body li > p {
        margin-top: 16px;
    }

    .markdown-body dl {
        padding: 0;
    }

    .markdown-body dl dt {
        padding: 0;
        margin-top: 16px;
        font-size: 1rem;
        font-style: italic;
        font-weight: 700;
    }

    .markdown-body dl dd {
        padding: 0 16px;
        margin-bottom: 16px;
    }

    .markdown-body blockquote {
        padding: 0 15px;
        color: #777;
        border-left: 4px solid #ddd;
    }

    .markdown-body blockquote > :first-child {
        margin-top: 0;
    }

    .markdown-body blockquote > :last-child {
        margin-bottom: 0;
    }

    .markdown-body table {
        border-collapse: collapse;
        border-spacing: 0;
        display: block;
        width: 100%;
        overflow: auto;
        word-break: normal;
        word-break: keep-all;
    }

    .markdown-body table th {
        font-weight: 700;
    }

    .markdown-body table td,
    .markdown-body table th {
        padding: 6px 13px;
        border: 1px solid #ddd;
    }

    .markdown-body table tr {
        background-color: #fff;
        border-top: 1px solid #ccc;
    }

    .markdown-body table tr:nth-child(2n) {
        background-color: #f8f8f8;
    }

    .markdown-body img {
        max-width: 100%;
        -moz-box-sizing: border-box;
        box-sizing: border-box;
    }

    .markdown-body code {
        padding: 0.2em 0;
        margin: 0;
        font-size: 85%;
        background-color: rgba(0, 0, 0, 0.04);
        border-radius: 8px;
    }

    .markdown-body code:after,
    .markdown-body code:before {
        letter-spacing: -0.2rem;
        content: "\\00a0";
    }

    .markdown-body pre > code {
        padding: 0;
        margin: 0;
        font-size: 100%;
        word-break: normal;
        white-space: pre;
        background: 0 0;
        border: 0;
    }

    .markdown-body .highlight {
        margin-bottom: 16px;
    }

    .markdown-body .highlight pre,
    .markdown-body pre {
        padding: 16px;
        overflow: auto;
        font-size: 85%;
        background-color: #f7f7f7;
        border-radius: 8px;
    }

    .markdown-body .highlight pre {
        margin-bottom: 0;
        word-break: normal;
    }

    .markdown-body pre code {
        display: inline;
        max-width: initial;
        padding: 0;
        margin: 0;
        overflow: initial;
        line-height: inherit;
        word-wrap: normal;
        background-color: transparent;
        border: 0;
    }

    .markdown-body pre code:after,
    .markdown-body pre code:before {
        content: normal;
    }

    .markdown-body .pl-c {
        color: #969896;
    }

    .markdown-body .pl-c1,
    .markdown-body .pl-mdh,
    .markdown-body .pl-mm,
    .markdown-body .pl-mp,
    .markdown-body .pl-mr,
    .markdown-body .pl-s1 .pl-v,
    .markdown-body .pl-s3,
    .markdown-body .pl-sc,
    .markdown-body .pl-sv {
        color: #0086b3;
    }

    .markdown-body .pl-e,
    .markdown-body .pl-en {
        color: #795da3;
    }

    .markdown-body .pl-s1 .pl-s2,
    .markdown-body .pl-smi,
    .markdown-body .pl-smp,
    .markdown-body .pl-stj,
    .markdown-body .pl-vo,
    .markdown-body .pl-vpf {
        color: #333;
    }

    .markdown-body .pl-ent {
        color: #63a35c;
    }

    .markdown-body .pl-k,
    .markdown-body .pl-s,
    .markdown-body .pl-st {
        color: #a71d5d;
    }

    .markdown-body .pl-pds,
    .markdown-body .pl-s1,
    .markdown-body .pl-s1 .pl-pse .pl-s2,
    .markdown-body .pl-sr,
    .markdown-body .pl-sr .pl-cce,
    .markdown-body .pl-sr .pl-sra,
    .markdown-body .pl-sr .pl-sre,
    .markdown-body .pl-src {
        color: #df5000;
    }

    .markdown-body .pl-mo,
    .markdown-body .pl-v {
        color: #1d3e81;
    }

    .markdown-body .pl-id {
        color: #b52a1d;
    }

    .markdown-body .pl-ii {
        background-color: #b52a1d;
        color: #f8f8f8;
    }

    .markdown-body .pl-sr .pl-cce {
        color: #63a35c;
        font-weight: 700;
    }

    .markdown-body .pl-ml {
        color: #693a17;
    }

    .markdown-body .pl-mh,
    .markdown-body .pl-mh .pl-en,
    .markdown-body .pl-ms {
        color: #1d3e81;
        font-weight: 700;
    }

    .markdown-body .pl-mq {
        color: teal;
    }

    .markdown-body .pl-mi {
        color: #333;
        font-style: italic;
    }

    .markdown-body .pl-mb {
        color: #333;
        font-weight: 700;
    }

    .markdown-body .pl-md,
    .markdown-body .pl-mdhf {
        background-color: #ffecec;
        color: #bd2c00;
    }

    .markdown-body .pl-mdht,
    .markdown-body .pl-mi1 {
        background-color: #eaffea;
        color: #55a532;
    }

    .markdown-body .pl-mdr {
        color: #795da3;
        font-weight: 700;
    }

    .markdown-body kbd {
        display: inline-block;
        padding: 3px 5px;
        line-height: 10px;
        color: #555;
        vertical-align: middle;
        background-color: #fcfcfc;
        border: 1px solid #ccc;
        border-bottom-color: #bbb;
        border-radius: 8px;
        box-shadow: inset 0 -1px 0 #bbb;
    }

    .markdown-body .task-list-item + .task-list-item {
        margin-top: 3px;
    }

    .markdown-body .task-list-item input {
        float: left;
        margin: 0.3em 0 0.25em -1.6rem;
        vertical-align: middle;
    }

    .markdown-body :checked + .radio-label {
        z-index: 1;
        position: relative;
        border-color: #4183c4;
    }


    /*! Pretty printing styles. Used with prettify.js. */

    .pln {
        color: #000;
    }

    @media screen {
        .str {
            color: #080;
        }

        .kwd {
            color: #008;
        }

        .com {
            color: #800;
        }

        .typ {
            color: #606;
        }

        .lit {
            color: #066;
        }

        .clo,
        .opn,
        .pun {
            color: #660;
        }

        .tag {
            color: #008;
        }

        .atn {
            color: #606;
        }

        .atv {
            color: #080;
        }

        .dec,
        .var {
            color: #606;
        }

        .fun {
            color: red;
        }
    }

    @media print, projection {
        .kwd,
        .tag,
        .typ {
            font-weight: 700;
        }

        .str {
            color: #060;
        }

        .kwd {
            color: #006;
        }

        .com {
            color: #600;
            font-style: italic;
        }

        .typ {
            color: #404;
        }

        .lit {
            color: #044;
        }

        .clo,
        .opn,
        .pun {
            color: #440;
        }

        .tag {
            color: #006;
        }

        .atn {
            color: #404;
        }

        .atv {
            color: #060;
        }
    }

    pre.prettyprint {
        padding: 2px;
        border: 1px solid #888;
    }

    ol.linenums {
        margin-top: 0;
        margin-bottom: 0;
    }

    li.L1,
    li.L3,
    li.L5,
    li.L7,
    li.L9 {
        background: #eee;
    }
    
    .markdown-body .editormd-toc-menu ul {
        padding-left: 0;
    }

    .markdown-body .highlight pre,
    .markdown-body pre {
        line-height: 1.6;
    }

    hr.editormd-page-break {
        border: 1px dotted #ccc;
        font-size: 0;
        height: 2px;
    }

    @media only print {
        hr.editormd-page-break {
            background: 0 0;
            border: none;
            height: 0;
        }
    }
`;
