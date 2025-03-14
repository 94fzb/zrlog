import styled from "styled-components";

export const StyledEditormd = styled("div")`
    @charset "UTF-8";
    /*! Editor.md v1.5.0 | editormd.css | Open source online markdown editor. | MIT License | By: Pandao | https://github.com/pandao/editor.md | 2015-06-09 */
    /*! prefixes.scss v0.1.0 | Author: Pandao | https://github.com/pandao/prefixes.scss | MIT license | Copyright (c) 2015 */
    position: relative;

    .editormd-form br,
    .markdown-body hr:after {
        clear: both;
    }

    .editormd {
        width: 100%;
        height: 1240px;
        margin: 0 auto 0;
        text-align: left;
        overflow: hidden;
        position: relative;
    }

    .editormd *,
    .editormd :after,
    .editormd :before {
        -webkit-box-sizing: border-box;
        -moz-box-sizing: border-box;
        box-sizing: border-box;
    }

    .editormd a {
        text-decoration: none;
    }

    .editormd img {
        border: none;
        vertical-align: middle;
    }

    .editormd .editormd-html-textarea,
    .editormd .editormd-markdown-textarea,
    .editormd > textarea {
        width: 0;
        height: 0;
        outline: 0;
        resize: none;
    }

    .editormd .editormd-html-textarea,
    .editormd .editormd-markdown-textarea {
        display: none;
    }

    .editormd button,
    .editormd input[type="text"],
    .editormd input[type="button"],
    .editormd input[type="submit"],
    .editormd select,
    .editormd textarea {
        -webkit-appearance: none;
        -moz-appearance: none;
        -ms-appearance: none;
        appearance: none;
    }

    .editormd ::-webkit-scrollbar {
        height: 10px;
        width: 7px;
        background: rgba(0, 0, 0, 0.1);
    }

    .editormd ::-webkit-scrollbar:hover {
        background: rgba(0, 0, 0, 0.2);
    }

    .editormd ::-webkit-scrollbar-thumb {
        background: rgba(0, 0, 0, 0.3);
        -webkit-border-radius: 6px;
        -moz-border-radius: 6px;
        -ms-border-radius: 6px;
        -o-border-radius: 6px;
        border-radius: 6px;
    }

    .editormd ::-webkit-scrollbar-thumb:hover {
        -webkit-box-shadow: inset 1px 1px 1px rgba(0, 0, 0, 0.25);
        -moz-box-shadow: inset 1px 1px 1px rgba(0, 0, 0, 0.25);
        -ms-box-shadow: inset 1px 1px 1px rgba(0, 0, 0, 0.25);
        -o-box-shadow: inset 1px 1px 1px rgba(0, 0, 0, 0.25);
        box-shadow: inset 1px 1px 1px rgba(0, 0, 0, 0.25);
        background-color: rgba(0, 0, 0, 0.4);
    }

    .editormd-user-unselect {
        -webkit-user-select: none;
        -moz-user-select: none;
        -ms-user-select: none;
        -o-user-select: none;
        user-select: none;
    }

    .editormd-toolbar {
        width: 100%;
        min-height: 37px;
        background: #fff;
        display: none;
        position: absolute;
        top: 0;
        left: 0;
        z-index: 10;
        border-bottom: 1px solid #ddd;
    }

    .editormd-toolbar-container {
        padding: 0 8px;
        min-height: 35px;
        -o-user-select: none;
        user-select: none;
    }

    .editormd-toolbar-container,
    .markdown-body .octicon {
        -webkit-user-select: none;
        -moz-user-select: none;
        -ms-user-select: none;
    }

    .editormd-menu,
    .markdown-body ol,
    .markdown-body td,
    .markdown-body th,
    .markdown-body ul {
        padding: 0;
    }

    .editormd-menu {
        margin: 0;
        list-style: none;
    }

    .editormd-menu > li {
        margin: 0;
        padding: 5px 1px;
        display: inline-block;
        position: relative;
    }

    .editormd-menu > li.divider {
        display: inline-block;
        text-indent: -9999px;
        margin: 0 5px;
        height: 65%;
        border-right: 1px solid #ddd;
    }

    .editormd-menu > li > a {
        outline: 0;
        color: #666;
        display: inline-block;
        min-width: 24px;
        font-size: 16px;
        text-decoration: none;
        text-align: center;
        -webkit-border-radius: 2px;
        -moz-border-radius: 2px;
        -ms-border-radius: 2px;
        -o-border-radius: 2px;
        border-radius: 2px;
        border: 1px solid #fff;
    }

    .editormd-menu > li > a.active,
    .editormd-menu > li > a:hover {
        border: 1px solid #ddd;
        background: #eee;
    }

    .editormd-menu > li > a > .fa {
        text-align: center;
        display: block;
        padding: 5px;
    }

    .editormd-menu > li > a > .editormd-bold {
        padding: 5px 2px;
        display: inline-block;
        font-weight: 700;
    }

    .editormd-menu > li:hover .editormd-dropdown-menu {
        display: block;
    }

    .editormd-menu > li + li > a {
        margin-left: 3px;
    }

    .editormd-dropdown-menu {
        display: none;
        background: #fff;
        border: 1px solid #ddd;
        width: 148px;
        list-style: none;
        position: absolute;
        top: 33px;
        left: 0;
        z-index: 100;
        -webkit-box-shadow: 1px 2px 6px rgba(0, 0, 0, 0.15);
        -moz-box-shadow: 1px 2px 6px rgba(0, 0, 0, 0.15);
        -ms-box-shadow: 1px 2px 6px rgba(0, 0, 0, 0.15);
        -o-box-shadow: 1px 2px 6px rgba(0, 0, 0, 0.15);
        box-shadow: 1px 2px 6px rgba(0, 0, 0, 0.15);
    }

    .editormd-dropdown-menu:after,
    .editormd-dropdown-menu:before {
        width: 0;
        height: 0;
        display: block;
        content: "";
        position: absolute;
        top: -11px;
        left: 8px;
        border: 5px solid transparent;
    }

    .editormd-dropdown-menu:before {
        border-bottom-color: #ccc;
    }

    .editormd-dropdown-menu:after {
        border-bottom-color: #fff;
        top: -10px;
    }

    .editormd-dropdown-menu > li > a {
        color: #666;
        display: block;
        text-decoration: none;
        padding: 8px 10px;
    }

    .editormd-dropdown-menu > li > a:hover {
        background: #f6f6f6;
        transition: all 300ms ease-out;
    }

    .editormd-dropdown-menu > li + li {
        border-top: 1px solid #ddd;
    }

    .editormd-container {
        margin: 0;
        width: 100%;
        height: 100%;
        overflow: hidden;
        padding: 35px 0 0;
        position: relative;
        background: #fff;
        -webkit-box-sizing: border-box;
        -moz-box-sizing: border-box;
        box-sizing: border-box;
    }

    .editormd-dialog {
        color: #666;
        position: fixed;
        z-index: 99999;
        display: none;
        -webkit-border-radius: 8px;
        -moz-border-radius: 8px;
        -ms-border-radius: 8px;
        -o-border-radius: 8px;
        border-radius: 6px;
        -webkit-box-shadow: 0 0 10px rgba(0, 0, 0, 0.3);
        -moz-box-shadow: 0 0 10px rgba(0, 0, 0, 0.3);
        -ms-box-shadow: 0 0 10px rgba(0, 0, 0, 0.3);
        -o-box-shadow: 0 0 10px rgba(0, 0, 0, 0.3);
        box-shadow: 0 0 10px rgba(0, 0, 0, 0.3);
        background: #fff;
        font-size: 14px;
    }

    .editormd-dialog-container {
        position: relative;
        padding: 20px;
        line-height: 1.4;
    }

    .editormd-dialog-container h1 {
        font-size: 24px;
        margin-bottom: 10px;
    }

    .editormd-dialog-container h1 .fa {
        color: #2c7eea;
        padding-right: 5px;
    }

    .editormd-dialog-container h1 small {
        padding-left: 5px;
        font-weight: 400;
        font-size: 12px;
        color: #999;
    }

    .editormd-dialog-container select {
        color: #999;
        padding: 3px 8px;
        border: 1px solid #ddd;
    }

    .editormd-dialog-close {
        position: absolute;
        top: 12px;
        right: 15px;
        font-size: 18px;
        color: #ccc;
        -webkit-transition: color 300ms ease-out;
        -moz-transition: color 300ms ease-out;
        transition: color 300ms ease-out;
    }

    .editormd-dialog-close:hover {
        color: #999;
    }

    .editormd-dialog-header {
        padding: 11px 20px;
        border-bottom: 1px solid #eee;
        -webkit-transition: background 300ms ease-out;
        -moz-transition: background 300ms ease-out;
        transition: background 300ms ease-out;
    }

    .editormd-dialog-header:hover {
        background: #f6f6f6;
    }

    .editormd-dialog-title {
        font-size: 14px;
    }

    .editormd-dialog-footer {
        padding: 10px 0 0;
        text-align: right;
    }

    .editormd-dialog-info {
        width: 420px;
    }

    .editormd-dialog-info h1 {
        font-weight: 400;
    }

    .editormd-dialog-info .editormd-dialog-container {
        padding: 20px 25px 25px;
    }

    .editormd-dialog-info .editormd-dialog-close {
        top: 10px;
        right: 10px;
    }

    .editormd-dialog-info .hover-link:hover,
    .editormd-dialog-info p > a {
        color: #2196f3;
    }

    .editormd-dialog-info .hover-link {
        color: #666;
    }

    .editormd-dialog-info a .fa-external-link {
        display: none;
    }

    .editormd-dialog-info a:hover {
        color: #2196f3;
    }

    .editormd-dialog-info a:hover .fa-external-link {
        display: inline-block;
    }

    .editormd-container-mask,
    .editormd-dialog-mask,
    .editormd-mask {
        display: none;
        width: 100%;
        height: 100%;
        position: absolute;
        top: 0;
        left: 0;
    }

    .editormd-dialog-mask-bg,
    .editormd-mask {
        background: #fff;
        opacity: 0.5;
        filter: alpha(opacity=50);
    }

    .editormd-mask {
        position: fixed;
        background: #000;
        opacity: 0.2;
        filter: alpha(opacity=20);
        z-index: 99998;
    }

    .editormd-container-mask {
        z-index: 20;
        display: block;
        background-color: #fff;
    }

    .editormd-code-block-dialog textarea,
    .editormd-preformatted-text-dialog textarea {
        width: 100%;
        height: 400px;
        margin-bottom: 6px;
        overflow: auto;
        border: 1px solid #eee;
        background: #fff;
        padding: 15px;
        resize: none;
    }

    .editormd-code-toolbar {
        color: #999;
        font-size: 14px;
        margin: -5px 0 10px;
    }

    .editormd-grid-table {
        width: 99%;
        display: table;
        border: 1px solid #ddd;
        border-collapse: collapse;
    }

    .editormd-grid-table-row {
        width: 100%;
        display: table-row;
    }

    .editormd-grid-table-row a {
        font-size: 1.4rem;
        width: 5%;
        height: 36px;
        color: #999;
        text-align: center;
        display: table-cell;
        vertical-align: middle;
        border: 1px solid #ddd;
        text-decoration: none;
        -webkit-transition: background-color 300ms ease-out, color 100ms ease-in;
        -moz-transition: background-color 300ms ease-out, color 100ms ease-in;
        transition: background-color 300ms ease-out, color 100ms ease-in;
    }

    .editormd-grid-table-row a.selected {
        color: #666;
        background-color: #eee;
    }

    .editormd-grid-table-row a:hover {
        color: #777;
        background-color: #f6f6f6;
    }

    .editormd-tab-head {
        list-style: none;
        border-bottom: 1px solid #ddd;
    }

    .editormd-tab-head li {
        display: inline-block;
    }

    .editormd-tab-head li a {
        color: #999;
        display: block;
        padding: 6px 12px 5px;
        text-align: center;
        text-decoration: none;
        margin-bottom: -1px;
        border: 1px solid #ddd;
        -webkit-border-top-left-radius: 3px;
        -moz-border-top-left-radius: 3px;
        -ms-border-top-left-radius: 3px;
        -o-border-top-left-radius: 3px;
        border-top-left-radius: 3px;
        -webkit-border-top-right-radius: 3px;
        -moz-border-top-right-radius: 3px;
        -ms-border-top-right-radius: 3px;
        -o-border-top-right-radius: 3px;
        border-top-right-radius: 3px;
        background: #f6f6f6;
        -webkit-transition: all 300ms ease-out;
        -moz-transition: all 300ms ease-out;
        transition: all 300ms ease-out;
    }

    .editormd-tab-head li a:hover {
        color: #666;
        background: #eee;
    }

    .editormd-tab-head li.active a {
        color: #666;
        background: #fff;
        border-bottom-color: #fff;
    }

    .editormd-tab-head li + li {
        margin-left: 3px;
    }

    .editormd-tab-box {
        padding: 20px 0;
    }

    .editormd-form {
        color: #666;
    }

    .editormd-form label {
        float: left;
        display: block;
        width: 75px;
        text-align: left;
        padding: 7px 0 15px 5px;
        margin: 0 0 2px;
        font-weight: 400;
    }

    .editormd-form iframe {
        display: none;
    }

    .editormd-form input:focus {
        outline: 0;
    }

    .editormd-form input[type="text"],
    .editormd-form input[type="number"] {
        color: #999;
        padding: 8px;
        border: 1px solid #ddd;
        border-radius: 8px;
    }

    .editormd-form input[type="number"] {
        width: 40px;
        display: inline-block;
        padding: 6px 8px;
    }

    .editormd-form input[type="text"] {
        display: inline-block;
        width: 264px;
    }

    .editormd-form .fa-btns {
        display: inline-block;
    }

    .editormd-form .fa-btns a {
        color: #999;
        padding: 7px 10px 0 0;
        display: inline-block;
        text-decoration: none;
        text-align: center;
    }

    .editormd-form .fa-btns .fa {
        font-size: 1.3rem;
    }

    .editormd-form .fa-btns label {
        float: none;
        display: inline-block;
        width: auto;
        text-align: left;
        padding: 0 0 0 5px;
        cursor: pointer;
    }

    .fa-fw,
    .fa-li {
        text-align: center;
    }

    .editormd-dialog-container .editormd-btn,
    .editormd-dialog-container button,
    .editormd-dialog-container input[type="submit"],
    .editormd-dialog-footer .editormd-btn,
    .editormd-dialog-footer button,
    .editormd-dialog-footer input[type="submit"],
    .editormd-form .editormd-btn,
    .editormd-form button,
    .editormd-form input[type="submit"] {
        color: #666;
        min-width: 75px;
        cursor: pointer;
        background: #fff;
        padding: 7px 10px;
        border: 1px solid #ddd;
        -webkit-border-radius: 8px;
        -moz-border-radius: 8px;
        -ms-border-radius: 8px;
        -o-border-radius: 8px;
        border-radius: 8px;
        -webkit-transition: background 300ms ease-out;
        -moz-transition: background 300ms ease-out;
        transition: background 300ms ease-out;
    }

    .editormd-dialog-container .editormd-btn:hover,
    .editormd-dialog-container button:hover,
    .editormd-dialog-container input[type="submit"]:hover,
    .editormd-dialog-footer .editormd-btn:hover,
    .editormd-dialog-footer button:hover,
    .editormd-dialog-footer input[type="submit"]:hover,
    .editormd-form .editormd-btn:hover,
    .editormd-form button:hover,
    .editormd-form input[type="submit"]:hover {
        background: #eee;
    }

    .editormd-dialog-container .editormd-btn + .editormd-btn,
    .editormd-dialog-footer .editormd-btn + .editormd-btn,
    .editormd-form .editormd-btn + .editormd-btn {
        margin-left: 8px;
    }

    .editormd-file-input {
        width: 75px;
        height: 32px;
        margin-left: 8px;
        position: relative;
        display: inline-block;
    }

    .editormd-file-input input[type="file"] {
        width: 75px;
        height: 32px;
        opacity: 0;
        cursor: pointer;
        background: #000;
        display: inline-block;
        position: absolute;
        top: 0;
        right: 0;
    }

    .editormd-file-input input[type="file"]::-webkit-file-upload-button {
        visibility: hidden;
    }

    .editormd-file-input:hover input[type="submit"] {
        background: #eee;
    }

    .editormd .CodeMirror,
    .editormd-preview {
        display: inline-block;
        width: 50%;
        height: 100%;
        vertical-align: top;
        -webkit-box-sizing: border-box;
        -moz-box-sizing: border-box;
        box-sizing: border-box;
        margin: 0;
    }

    .editormd-preview {
        position: absolute;
        top: 35px;
        right: 0;
        overflow: auto;
        line-height: 1.6;
        display: none;
        background: #fff;
    }

    .fa,
    .fa-stack {
        display: inline-block;
    }

    .editormd .CodeMirror {
        z-index: 10;
        float: left;
        border-right: 1px solid #ddd;
        font-size: 14px;
        line-height: 1.6;
        margin-top: 35px;
    }

    .editormd .CodeMirror pre {
        font-size: 14px;
        padding: 0 12px;
    }

    .editormd .CodeMirror-linenumbers {
        padding: 0 5px;
    }

    .editormd .CodeMirror-focused .CodeMirror-selected,
    .editormd .CodeMirror-selected {
        background: #70b7ff;
    }

    .editormd .CodeMirror,
    .editormd .CodeMirror-scroll,
    .editormd .editormd-preview {
        -webkit-overflow-scrolling: touch;
    }

    .editormd .styled-background {
        background-color: #ff7;
    }

    .editormd .CodeMirror-focused .cm-matchhighlight {
        background-image: url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAIAAAACCAYAAABytg0kAAAAFklEQVQI12NgYGBgkKzc8x9CMDAwAAAmhwSbidEoSQAAAABJRU5ErkJggg==);
        background-position: bottom;
        background-repeat: repeat-x;
    }

    .editormd .CodeMirror-empty.CodeMirror-focused {
        outline: 0;
    }

    .editormd .CodeMirror pre.CodeMirror-placeholder {
        color: #999;
    }

    .editormd .cm-trailingspace {
        background-image: url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAQAAAACCAYAAAB/qH1jAAAABmJLR0QA/wD/AP+gvaeTAAAACXBIWXMAAAsTAAALEwEAmpwYAAAAB3RJTUUH3QUXCToH00Y1UgAAACFJREFUCNdjPMDBUc/AwNDAAAFMTAwMDA0OP34wQgX/AQBYgwYEx4f9lQAAAABJRU5ErkJggg==);
        background-position: bottom left;
        background-repeat: repeat-x;
    }

    .editormd .cm-tab {
        background: url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADAAAAAMCAYAAAAkuj5RAAAAAXNSR0IArs4c6QAAAGFJREFUSMft1LsRQFAQheHPowAKoACx3IgEKtaEHujDjORSgWTH/ZOdnZOcM/sgk/kFFWY0qV8foQwS4MKBCS3qR6ixBJvElOobYAtivseIE120FaowJPN75GMu8j/LfMwNjh4HUpwg4LUAAAAASUVORK5CYII=)
            right no-repeat;
    }

    /*! github-markdown-css | The MIT License (MIT) | Copyright (c) Sindre Sorhus <sindresorhus@gmail.com> (sindresorhus.com) | https://github.com/sindresorhus/github-markdown-css */
    @font-face {
        font-family: octicons-anchor;
        src: url(data:font/woff;charset=utf-8;base64,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)
            format("woff");
    }

    .markdown-body {
        -ms-text-size-adjust: 100%;
        -webkit-text-size-adjust: 100%;
        color: #333;
        overflow: hidden;
        font-size: 16px;
        line-height: 1.6;
        word-wrap: break-word;
    }

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

    .editormd-html-preview,
    .editormd-preview-container {
        text-align: left;
        font-size: 14px;
        line-height: 1.6;
        padding: 20px;
        overflow: auto;
        width: 100%;
        //editor statistics
        margin-bottom: 30px;
        background-color: #fff;
    }

    .editormd-html-preview blockquote,
    .editormd-preview-container blockquote {
        color: #666;
        border-left: 4px solid #ddd;
        padding-left: 20px;
        margin-left: 0;
        font-size: 14px;
        font-style: italic;
    }

    .editormd-html-preview p code,
    .editormd-preview-container p code {
        margin-left: 5px;
        margin-right: 4px;
    }

    .editormd-html-preview abbr,
    .editormd-preview-container abbr {
        background: #ffd;
    }

    .editormd-html-preview hr,
    .editormd-preview-container hr {
        height: 1px;
        border: none;
        border-top: 1px solid #ddd;
        background: 0 0;
    }

    .editormd-html-preview code,
    .editormd-preview-container code {
        border: 1px solid #ddd;
        background: #f6f6f6;
        padding: 3px;
        border-radius: 8px;
        font-size: 14px;
    }

    .editormd-html-preview pre,
    .editormd-preview-container pre {
        border: 1px solid #ddd;
        background: #f6f6f6;
        padding: 10px;
        -webkit-border-radius: 8px;
        -moz-border-radius: 8px;
        -ms-border-radius: 8px;
        -o-border-radius: 8px;
        border-radius: 8px;
    }

    .editormd-html-preview pre code,
    .editormd-preview-container pre code {
        padding: 0;
    }

    .editormd-html-preview table thead tr,
    .editormd-preview-container table thead tr {
        background-color: #f8f8f8;
    }

    .editormd-html-preview p.editormd-tex,
    .editormd-preview-container p.editormd-tex {
        text-align: center;
    }

    .editormd-html-preview span.editormd-tex,
    .editormd-preview-container span.editormd-tex {
        margin: 0 5px;
    }

    .editormd-html-preview .emoji,
    .editormd-preview-container .emoji {
        width: 24px;
        height: 24px;
    }

    .editormd-html-preview .katex,
    .editormd-preview-container .katex {
        font-size: 1.4rem;
    }

    .editormd-html-preview .flowchart,
    .editormd-html-preview .sequence-diagram,
    .editormd-preview-container .flowchart,
    .editormd-preview-container .sequence-diagram {
        margin: 0 auto;
        text-align: center;
    }

    .editormd-html-preview .flowchart svg,
    .editormd-html-preview .sequence-diagram svg,
    .editormd-preview-container .flowchart svg,
    .editormd-preview-container .sequence-diagram svg {
        margin: 0 auto;
    }

    .editormd-html-preview .flowchart text,
    .editormd-html-preview .sequence-diagram text,
    .editormd-preview-container .flowchart text,
    .editormd-preview-container .sequence-diagram text {
        font-size: 15px !important;
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

    .editormd-html-preview pre.prettyprint,
    .editormd-preview-container pre.prettyprint {
        padding: 10px;
        border: 1px solid #ddd;
        white-space: pre-wrap;
        word-wrap: break-word;
    }

    .editormd-html-preview ol.linenums,
    .editormd-preview-container ol.linenums {
        color: #999;
        padding-left: 2.5rem;
    }

    .editormd-html-preview ol.linenums li,
    .editormd-preview-container ol.linenums li {
        list-style-type: decimal;
    }

    .editormd-html-preview ol.linenums li code,
    .editormd-preview-container ol.linenums li code {
        border: none;
        background: 0 0;
        padding: 0;
    }

    .editormd-html-preview .editormd-toc-menu,
    .editormd-preview-container .editormd-toc-menu {
        margin: 8px 0 12px;
        display: inline-block;
    }

    .editormd-html-preview .editormd-toc-menu > .markdown-toc,
    .editormd-preview-container .editormd-toc-menu > .markdown-toc {
        position: relative;
        -webkit-border-radius: 4px;
        -moz-border-radius: 4px;
        -ms-border-radius: 4px;
        -o-border-radius: 4px;
        border-radius: 4px;
        border: 1px solid #ddd;
        display: inline-block;
        font-size: 1rem;
    }

    .editormd-html-preview .editormd-toc-menu > .markdown-toc > ul,
    .editormd-preview-container .editormd-toc-menu > .markdown-toc > ul {
        width: 160%;
        min-width: 180px;
        position: absolute;
        left: -1px;
        top: -2px;
        z-index: 100;
        padding: 0 10px 10px;
        display: none;
        background: #fff;
        border: 1px solid #ddd;
        -webkit-border-radius: 4px;
        -moz-border-radius: 4px;
        -ms-border-radius: 4px;
        -o-border-radius: 4px;
        border-radius: 4px;
        -webkit-box-shadow: 0 3px 5px rgba(0, 0, 0, 0.2);
        -moz-box-shadow: 0 3px 5px rgba(0, 0, 0, 0.2);
        -ms-box-shadow: 0 3px 5px rgba(0, 0, 0, 0.2);
        -o-box-shadow: 0 3px 5px rgba(0, 0, 0, 0.2);
        box-shadow: 0 3px 5px rgba(0, 0, 0, 0.2);
    }

    .editormd-html-preview .editormd-toc-menu > .markdown-toc > ul > li ul,
    .editormd-preview-container .editormd-toc-menu > .markdown-toc > ul > li ul {
        width: 100%;
        min-width: 180px;
        border: 1px solid #ddd;
        display: none;
        background: #fff;
        -webkit-border-radius: 4px;
        -moz-border-radius: 4px;
        -ms-border-radius: 4px;
        -o-border-radius: 4px;
        border-radius: 4px;
    }

    .editormd-html-preview .editormd-toc-menu .toc-menu-btn:hover,
    .editormd-html-preview .editormd-toc-menu > .markdown-toc > ul > li a:hover,
    .editormd-preview-container .editormd-toc-menu .toc-menu-btn:hover,
    .editormd-preview-container .editormd-toc-menu > .markdown-toc > ul > li a:hover {
        background-color: #f6f6f6;
    }

    .editormd-html-preview .editormd-toc-menu > .markdown-toc > ul > li a,
    .editormd-preview-container .editormd-toc-menu > .markdown-toc > ul > li a {
        color: #666;
        padding: 6px 10px;
        display: block;
        -webkit-transition: background-color 500ms ease-out;
        -moz-transition: background-color 500ms ease-out;
        transition: background-color 500ms ease-out;
    }

    .editormd-html-preview .editormd-toc-menu > .markdown-toc li,
    .editormd-preview-container .editormd-toc-menu > .markdown-toc li {
        position: relative;
    }

    .editormd-html-preview .editormd-toc-menu > .markdown-toc li > ul,
    .editormd-preview-container .editormd-toc-menu > .markdown-toc li > ul {
        position: absolute;
        top: 32px;
        left: 10%;
        display: none;
        -webkit-box-shadow: 0 3px 5px rgba(0, 0, 0, 0.2);
        -moz-box-shadow: 0 3px 5px rgba(0, 0, 0, 0.2);
        -ms-box-shadow: 0 3px 5px rgba(0, 0, 0, 0.2);
        -o-box-shadow: 0 3px 5px rgba(0, 0, 0, 0.2);
        box-shadow: 0 3px 5px rgba(0, 0, 0, 0.2);
    }

    .editormd-html-preview .editormd-toc-menu > .markdown-toc li > ul:after,
    .editormd-html-preview .editormd-toc-menu > .markdown-toc li > ul:before,
    .editormd-preview-container .editormd-toc-menu > .markdown-toc li > ul:after,
    .editormd-preview-container .editormd-toc-menu > .markdown-toc li > ul:before {
        pointer-events: pointer-events;
        position: absolute;
        left: 15px;
        top: -6px;
        display: block;
        content: "";
        width: 0;
        height: 0;
        border: 6px solid transparent;
        border-width: 0 6px 6px;
        z-index: 10;
    }

    .editormd-html-preview .editormd-toc-menu > .markdown-toc li > ul:before,
    .editormd-preview-container .editormd-toc-menu > .markdown-toc li > ul:before {
        border-bottom-color: #ccc;
    }

    .editormd-html-preview .editormd-toc-menu > .markdown-toc li > ul:after,
    .editormd-preview-container .editormd-toc-menu > .markdown-toc li > ul:after {
        border-bottom-color: #fff;
        top: -5px;
    }

    .editormd-html-preview .editormd-toc-menu ul,
    .editormd-preview-container .editormd-toc-menu ul {
        list-style: none;
    }

    .editormd-html-preview .editormd-toc-menu a,
    .editormd-preview-container .editormd-toc-menu a {
        text-decoration: none;
    }

    .editormd-html-preview .editormd-toc-menu h1,
    .editormd-preview-container .editormd-toc-menu h1 {
        font-size: 16px;
        padding: 5px 0 10px 10px;
        line-height: 1;
        border-bottom: 1px solid #eee;
    }

    .editormd-html-preview .editormd-toc-menu h1 .fa,
    .editormd-preview-container .editormd-toc-menu h1 .fa {
        padding-left: 10px;
    }

    .editormd-html-preview .editormd-toc-menu .toc-menu-btn,
    .editormd-preview-container .editormd-toc-menu .toc-menu-btn {
        color: #666;
        min-width: 180px;
        padding: 5px 10px;
        border-radius: 4px;
        display: inline-block;
        -webkit-transition: background-color 500ms ease-out;
        -moz-transition: background-color 500ms ease-out;
        transition: background-color 500ms ease-out;
    }

    .editormd-html-preview textarea,
    .editormd-onlyread .editormd-toolbar {
        display: none;
    }

    .editormd-html-preview .editormd-toc-menu .toc-menu-btn .fa,
    .editormd-preview-container .editormd-toc-menu .toc-menu-btn .fa {
        float: right;
        padding: 3px 0 0 10px;
        font-size: 1.3rem;
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

    .editormd-html-preview hr.editormd-page-break {
        background: 0 0;
        border: none;
        height: 0;
    }

    .editormd-preview-close-btn {
        color: #fff;
        padding: 4px 6px;
        font-size: 18px;
        -webkit-border-radius: 500px;
        -moz-border-radius: 500px;
        -ms-border-radius: 500px;
        -o-border-radius: 500px;
        border-radius: 500px;
        display: none;
        background-color: #ccc;
        position: absolute;
        top: 25px;
        right: 35px;
        z-index: 19;
        -webkit-transition: background-color 300ms ease-out;
        -moz-transition: background-color 300ms ease-out;
        transition: background-color 300ms ease-out;
    }

    .editormd-preview-close-btn:hover {
        background-color: #999;
    }

    .editormd-preview-active {
        width: 100%;
        padding: 40px;
    }

    .editormd-preview-theme-dark {
        color: #dcdcdc;
        background: #1a1a17;
    }

    .editormd-preview-theme-dark .editormd-preview-container {
        color: #dcdcdc;
        background-color: #1a1a17;
    }

    .editormd-preview-theme-dark .editormd-preview-container pre.prettyprint {
        border: none;
    }

    .editormd-preview-theme-dark .editormd-preview-container blockquote {
        color: inherit;
        padding: 0.5rem;
        background: #222;
        border-color: #333;
    }

    .editormd-preview-theme-dark .editormd-preview-container abbr {
        color: rgb(220, 220, 220);
        padding: 1px 3px;
        -webkit-border-radius: 8px;
        -moz-border-radius: 8px;
        -ms-border-radius: 8px;
        -o-border-radius: 8px;
        border-radius: 8px;
        background: #f90;
    }

    .editormd-preview-theme-dark .editormd-preview-container code {
        color: #fff;
        border: none;
        padding: 1px 3px;
        -webkit-border-radius: 8px;
        -moz-border-radius: 8px;
        -ms-border-radius: 8px;
        -o-border-radius: 8px;
        border-radius: 8px;
        background: #5a9600;
    }

    .editormd-preview-theme-dark .editormd-preview-container table {
        border: none;
    }

    .editormd-preview-theme-dark .editormd-preview-container .fa-emoji {
        color: #b4bf42;
    }

    .editormd-preview-theme-dark .editormd-preview-container .katex {
        color: #fec93f;
    }

    .editormd-preview-theme-dark .editormd-toc-menu > .markdown-toc {
        background: #fff;
        border: none;
    }

    .editormd-preview-theme-dark .editormd-toc-menu > .markdown-toc h1 {
        border-color: #ddd;
    }

    .editormd-preview-theme-dark .markdown-body h1,
    .editormd-preview-theme-dark .markdown-body h2,
    .editormd-preview-theme-dark .markdown-body hr {
        border-color: #222;
    }

    .editormd-preview-theme-dark pre {
        color: #999;
        background-color: #111;
        background-color: rgba(0, 0, 0, 0.4);
    }

    .editormd-preview-theme-dark pre .pln {
        color: #999;
    }

    .editormd-preview-theme-dark li.L1,
    .editormd-preview-theme-dark li.L3,
    .editormd-preview-theme-dark li.L5,
    .editormd-preview-theme-dark li.L7,
    .editormd-preview-theme-dark li.L9 {
        background: 0 0;
    }

    .editormd-preview-theme-dark [class*="editormd-logo"] {
        color: #2196f3;
    }

    .editormd-preview-theme-dark .sequence-diagram text {
        fill: #fff;
    }

    .editormd-preview-theme-dark .sequence-diagram path,
    .editormd-preview-theme-dark .sequence-diagram rect {
        color: #fff;
        fill: #64d1cb;
        stroke: #64d1cb;
    }

    .editormd-preview-theme-dark .flowchart path,
    .editormd-preview-theme-dark .flowchart rect {
        stroke: #a6c6ff;
    }

    .editormd-preview-theme-dark .flowchart rect {
        fill: #a6c6ff;
    }

    .editormd-preview-theme-dark .flowchart text {
        fill: #5879b4;
    }

    @media screen {
        .editormd-preview-theme-dark .str {
            color: #080;
        }

        .editormd-preview-theme-dark .kwd {
            color: #f90;
        }

        .editormd-preview-theme-dark .com {
            color: #444;
        }

        .editormd-preview-theme-dark .typ {
            color: #606;
        }

        .editormd-preview-theme-dark .lit {
            color: #066;
        }

        .editormd-preview-theme-dark .clo,
        .editormd-preview-theme-dark .opn,
        .editormd-preview-theme-dark .pun {
            color: #660;
        }

        .editormd-preview-theme-dark .tag {
            color: #f90;
        }

        .editormd-preview-theme-dark .atn {
            color: #6c95f5;
        }

        .editormd-preview-theme-dark .atv {
            color: #080;
        }

        .editormd-preview-theme-dark .dec,
        .editormd-preview-theme-dark .var {
            color: #008ba7;
        }

        .editormd-preview-theme-dark .fun {
            color: red;
        }
    }

    .editormd-onlyread .CodeMirror {
        margin-top: 0;
    }

    .editormd-onlyread .editormd-preview {
        top: 0;
    }

    .editormd-fullscreen {
        position: fixed;
        top: 0;
        left: 0;
        border: none;
        margin: 0 auto;
    }

    .editormd-theme-dark {
        border-color: #1a1a17;
        background-color: #1a1a17;
    }

    .editormd-theme-dark .CodeMirror {
        border-color: #1a1a17;
        background-color: #1a1a17;
    }

    .editormd-theme-dark .editormd-toolbar {
        background: #141414;
        border-bottom: 1px solid rgba(253, 253, 253, 0.12);
    }

    .editormd-theme-dark .editormd-menu > li > a {
        color: #777;
        border-color: #141414;
    }

    .editormd-theme-dark .editormd-menu > li > a.active,
    .editormd-theme-dark .editormd-menu > li > a:hover {
        border-color: #333;
        background: #333;
    }

    .editormd-theme-dark .editormd-menu > li.divider {
        border-right: 1px solid #111;
    }

    .editormd-theme-dark .CodeMirror {
        border-right: 1px solid #2b2b2b;
    }

    .editormd-preview-theme-dark h1 {
        color: #dcdcdc;
    }

    .editormd-preview-theme-dark h2 {
        color: #dcdcdc;
    }

    .editormd-preview-theme-dark h3 {
        color: #dcdcdc;
    }

    .editormd-preview-theme-dark h4 {
        color: #dcdcdc;
    }

    .editormd-preview-theme-dark h5 {
        color: #dcdcdc;
    }

    .editormd-preview-theme-dark h6 {
        color: #dcdcdc;
    }

    .editor-dark .editormd-dialog {
        background: #141414;
    }

    .editor-dark .editormd-dialog-header {
        border-bottom: 1px solid #2c2827;
    }

    .editor-dark .editormd-dialog-footer {
        border-top: 1px solid #2c2827;
    }

    .editor-dark .editormd-dialog-header:hover {
        border-bottom: 1px solid #2c2827;
    }

    .editor-dark .editormd-container-mask {
        z-index: 20;
        display: block;
        background: #1a1a17;
        border-color: #1a1a17;
    }

    .editor-light .CodeMirror-activeline-background {
        background: #e8f2ff;
    }

    .editor-dark .CodeMirror-activeline-background {
        background: #1a1a17;
    }

    .editor-light .CodeMirror-gutters {
        border-right: 1px solid #ddd;
        background-color: #f7f7f7;
        white-space: nowrap;
    }

    .editor-dark .CodeMirror-gutters {
        border-right: 1px solid #1a1a17;
        background-color: #1a1a17;
        white-space: nowrap;
    }

    .editor-dark .CodeMirror {
        color: #dcdcdc;
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
`;
