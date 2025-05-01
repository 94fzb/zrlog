import { FunctionComponent, useState } from "react";
import { EditorDialogState } from "./editor.types";
import EditorDialog from "./dialog/editor-dialog";
import EditorIcon from "./editor-icon";
import { getBorder } from "./editor-helpers";

type EditorToolBarProps = {
    onChange: (val: string, cursorPosition: number) => void;
    onCopy?: () => void;
    preview: boolean;
    onEditorModeChange: (preview: boolean) => void;
    getContainer?: () => HTMLElement;
};

const EditorToolBarDivider = () => {
    return (
        <span
            style={{
                borderRight: getBorder(),
                height: "65%",
                width: 1,
                userSelect: "none",
                display: "inline-block",
            }}
        >
            &nbsp;
        </span>
    );
};

const EditorToolBar: FunctionComponent<EditorToolBarProps> = ({
    onChange,
    onCopy,
    preview,
    onEditorModeChange,
    getContainer,
}) => {
    const [dialogState, setDialogState] = useState<EditorDialogState>({
        open: false,
        title: "",
        type: "image",
    });

    return (
        <>
            {dialogState.open && (
                <EditorDialog
                    getContainer={getContainer}
                    title={dialogState.title}
                    type={dialogState.type}
                    onOk={(mdStr) => {
                        setDialogState({
                            title: "",
                            type: "image",
                            open: false,
                        });
                        onChange(mdStr, mdStr.length);
                    }}
                    onClose={() => {
                        setDialogState({
                            title: "",
                            type: "image",
                            open: false,
                        });
                    }}
                />
            )}
            <div
                style={{
                    display: "flex",
                    gap: 2,
                    paddingRight: 8,
                    flexWrap: "wrap",
                    paddingLeft: 8,
                    alignItems: "center",
                    boxSizing: "border-box",
                    borderBottom: getBorder(),
                }}
            >
                <EditorIcon
                    name={"bold"}
                    onClick={() => {
                        onChange("****", 2);
                    }}
                />
                <EditorIcon
                    name={"strikethrough"}
                    onClick={() => {
                        onChange("~~~~", 2);
                    }}
                />
                <EditorIcon
                    name={"italic"}
                    onClick={() => {
                        onChange("**", 1);
                    }}
                />
                <EditorIcon
                    name={"quote-left"}
                    onClick={() => {
                        onChange("> ", 2);
                    }}
                />
                <EditorToolBarDivider />
                <EditorIcon
                    name={"2"}
                    onClick={() => {
                        onChange("## ", 3);
                    }}
                />
                <EditorIcon
                    name={"3"}
                    onClick={() => {
                        onChange("### ", 4);
                    }}
                />
                <EditorIcon
                    name={"4"}
                    onClick={() => {
                        onChange("#### ", 5);
                    }}
                />
                <EditorToolBarDivider />
                <EditorIcon
                    name={"list-ul"}
                    onClick={() => {
                        onChange("- ", 2);
                    }}
                />
                <EditorIcon
                    name={"list-ol"}
                    onClick={() => {
                        onChange("1. ", 3);
                    }}
                />

                <EditorIcon
                    name={"minus"}
                    onClick={() => {
                        const mdStr = "\n------------\n\n ";
                        onChange(mdStr, mdStr.length);
                    }}
                />
                <EditorToolBarDivider />
                <EditorIcon
                    name={"link"}
                    onClick={() => {
                        setDialogState({
                            open: true,
                            title: "添加链接",
                            type: "link",
                        });
                    }}
                />
                <EditorIcon
                    name={"image"}
                    onClick={() => {
                        setDialogState({
                            open: true,
                            title: "添加图片",
                            type: "image",
                        });
                    }}
                />

                <EditorIcon
                    name={"file-video"}
                    onClick={() => {
                        setDialogState({
                            open: true,
                            title: "添加视频",
                            type: "video",
                        });
                    }}
                />
                <EditorIcon
                    name={"paperclip"}
                    onClick={() => {
                        setDialogState({
                            open: true,
                            title: "添加文件",
                            type: "file",
                        });
                    }}
                />
                <EditorToolBarDivider />
                <EditorIcon
                    name={"file-code"}
                    onClick={() => {
                        setDialogState({
                            open: true,
                            title: "添加代码块",
                            type: "code",
                        });
                    }}
                />
                <EditorIcon
                    name={"table"}
                    onClick={() => {
                        setDialogState({
                            open: true,
                            title: "添加表格",
                            type: "table",
                        });
                    }}
                />
                <EditorIcon name={"clipboard"} onClick={onCopy} />
                {preview ? (
                    <EditorIcon
                        key={preview + ""}
                        name={"eye-slash"}
                        onClick={() => {
                            onEditorModeChange(false);
                        }}
                    />
                ) : (
                    <EditorIcon
                        key={preview + ""}
                        name={"eye"}
                        onClick={() => {
                            onEditorModeChange(true);
                        }}
                    />
                )}
                <EditorToolBarDivider />
                <EditorIcon
                    name={"question-circle"}
                    onClick={() => {
                        setDialogState({
                            open: true,
                            title: "帮助",
                            type: "help",
                        });
                    }}
                />
            </div>
        </>
    );
};

export default EditorToolBar;
