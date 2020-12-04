import React from "react";
import makeAsyncScriptLoader from "react-async-script";
import {BaseResourceComponent} from "../base-resource-component";
import 'font-awesome/css/font-awesome.min.css';
import './my-editormd.css';
import $ from 'jquery';

class MyEditorMd extends BaseResourceComponent {

    componentDidMount() {
        super.componentDidMount();
        let {config} = this.props;
        let {
            id, width, height, path, theme, previewTheme, editorTheme, codeFold, syncScrolling,
            saveHTMLToTextarea, searchReplace, watch, htmlDecode, toolbar, previewCodeHighlight, emoji,
            taskList, tocm, tex, flowChart, sequenceDiagram, dialogLockScreen, dialogShowMask, dialogDraggable,
            dialogMaskOpacity, dialogMaskBgColor, imageUpload, imageFormats, imageUploadURL,
        } = config;

        let {markdown, editorPlaceholder} = this.props;

        try {
            // eslint-disable-next-line no-undef,no-unused-vars
            let editor = editormd(id, $, {
                width: width,
                height: height,
                path: path,
                theme: theme,
                placeholder: editorPlaceholder,
                previewTheme: previewTheme,
                editorTheme: editorTheme,
                markdown: markdown,
                codeFold: codeFold,
                syncScrolling: syncScrolling,
                saveHTMLToTextarea: saveHTMLToTextarea,    // 保存 HTML 到 Textarea
                searchReplace: searchReplace,
                watch: watch,                // 关闭实时预览
                htmlDecode: htmlDecode,            // 开启 HTML 标签解析，为了安全性，默认不开启
                toolbar: toolbar,             //关闭工具栏
                previewCodeHighlight: previewCodeHighlight, // 关闭预览 HTML 的代码块高亮，默认开启
                emoji: emoji,
                taskList: taskList,
                tocm: tocm,         // Using [TOCM]
                tex: tex,                   // 开启科学公式TeX语言支持，默认关闭
                flowChart: flowChart,             // 开启流程图支持，默认关闭
                sequenceDiagram: sequenceDiagram,       // 开启时序/序列图支持，默认关闭,
                dialogLockScreen: dialogLockScreen,   // 设置弹出层对话框不锁屏，全局通用，默认为true
                dialogShowMask: dialogShowMask,     // 设置弹出层对话框显示透明遮罩层，全局通用，默认为true
                dialogDraggable: dialogDraggable,    // 设置弹出层对话框不可拖动，全局通用，默认为true
                dialogMaskOpacity: dialogMaskOpacity,    // 设置透明遮罩层的透明度，全局通用，默认值为0.1
                dialogMaskBgColor: dialogMaskBgColor, // 设置透明遮罩层的背景颜色，全局通用，默认为#fff
                imageUpload: imageUpload,
                imageFormats: imageFormats,
                imageUploadURL: imageUploadURL,
                onload: function () {
                    // eslint-disable-next-line no-undef
                    const jquery = $;
                    jquery("#fileDialog").on("click", function () {
                        editor.executePlugin("fileDialog", "file-dialog/file-dialog");
                    });
                    jquery("#videoDialog").on("click", function () {
                        editor.executePlugin("videoDialog", "video-dialog/video-dialog");
                    });
                    jquery("#copPreviewHtmlToClipboard").on("click", function () {
                        function copyToClipboard(html) {
                            const temp = jquery("<input>");
                            jquery("body").append(temp);
                            temp.val(html).select();
                            document.execCommand("copy");
                            temp.remove();
                        }

                        copyToClipboard('<div class="markdown-body" style="padding:0">' + editor.getPreviewedHTML() + "</div>");
                    });
                },
            });
        } catch (e) {

        }
    }

    render() {
        let {config} = this.props;
        return (<div id={config.id}/>);
    }
}


const LoadingElement = props => {
    return <div/>;
}


export class MyEditorMdWrapper extends React.PureComponent {
    constructor(props) {
        super(props);
        this.state = {
            mdEditorScriptLoaded: false,
        };
    }

    getEditorConfig = () => {
        return {
            id: 'editor',
            codeFold: true,
            path: "/admin/vendors/markdown/lib/",
            searchReplace: true,
            htmlDecode: "pre",
            emoji: true,
            taskList: true,
            tocm: false,         // Using [TOCM]
            tex: true,                   // 开启科学公式TeX语言支持，默认关闭
            flowChart: true,             // 开启流程图支持，默认关闭
            sequenceDiagram: true,       // 开启时序/序列图支持，默认关闭,
            dialogMaskOpacity: 0,    // 设置透明遮罩层的透明度，全局通用，默认值为0.1
            dialogMaskBgColor: "#000", // 设置透明遮罩层的背景颜色，全局通用，默认为#fff
            imageUpload: true,
            imageFormats: [],
            imageUploadURL: "/api/admin/upload",
            width: "100%",
            height: "1240px",
        }
    }

    render() {
        const EditMdAsyncScriptLoader = makeAsyncScriptLoader("/admin/vendors/markdown/js/editormd.min.js")(LoadingElement);
        if (this.state.mdEditorScriptLoaded) {
            console.info(this.props.markdown)
            return (
                <>
                    <MyEditorMd editorPlaceholder={this.props.editorPlaceholder} config={this.getEditorConfig()}
                                markdown={this.props.markdown}/>
                </>
            )
        }
        return (
            <>
                <EditMdAsyncScriptLoader
                    asyncScriptOnLoad={() => {
                        this.setState({mdEditorScriptLoaded: true});
                    }}
                />
            </>
        );
    }
}