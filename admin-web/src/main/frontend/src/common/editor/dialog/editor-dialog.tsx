import { FunctionComponent, useState } from "react";
import { message, Modal } from "antd";
import MarkdownHelp from "./markdown-help";
import TableBody from "./table-body";
import CodeBody from "./code-body";
import { DialogType } from "../editor.types";
import UploadBody from "./upload-body";
import LinkBody from "./link-body";

type EditorDialogProps = {
    title: string;
    type: DialogType;
    onOk?: (mdStr: string) => void;
    onClose?: () => void;
    getContainer?: () => HTMLElement;
};

type EditorDialogState = {
    value: string;
};

const EditorDialog: FunctionComponent<EditorDialogProps> = ({ title, type, onOk, onClose, getContainer }) => {
    const [state, setState] = useState<EditorDialogState>({
        value: "",
    });

    const [messageApi, contextHolder] = message.useMessage({ maxCount: 3, getContainer: getContainer });

    const getBody = () => {
        if (type === "help") {
            return <MarkdownHelp />;
        }
        if (type === "code") {
            return (
                <CodeBody
                    getContainer={getContainer}
                    onChange={(v) => {
                        setState(() => {
                            return {
                                value: v,
                            };
                        });
                    }}
                />
            );
        }
        if (type === "table") {
            return (
                <TableBody
                    onChange={(e) => {
                        setState(() => {
                            return {
                                value: e,
                            };
                        });
                    }}
                />
            );
        }
        if (type === "link") {
            return (
                <LinkBody
                    onChange={(value) => {
                        setState({ value: value });
                    }}
                />
            );
        }
        return (
            <UploadBody
                type={type}
                onChange={(value) => {
                    setState({ value: value });
                }}
            />
        );
    };

    return (
        <Modal
            open={true}
            width={{
                xs: "90%",
                sm: "80%",
                md: "70%",
                lg: "60%",
                xl: "50%",
                xxl: "40%",
            }}
            getContainer={getContainer}
            title={title}
            onOk={() => {
                if (type === "help") {
                    return;
                }
                if (onOk) {
                    if (type === "table" || type === "code") {
                        if (state.value === "") {
                            messageApi.error("内容不能为空");
                            return;
                        }
                        onOk(state.value);
                        return;
                    } else {
                        if (state.value === "") {
                            messageApi.error("地址不能为空");
                            return;
                        }
                        onOk(state.value);
                    }
                }
            }}
            onClose={() => {
                if (onClose) {
                    onClose();
                }
            }}
            onCancel={() => {
                if (onClose) {
                    onClose();
                }
            }}
        >
            {contextHolder}
            {getBody()}
        </Modal>
    );
};

export default EditorDialog;
