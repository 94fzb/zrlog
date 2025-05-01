import {FunctionComponent, useState} from "react";
import {Button, Input, message, Modal} from "antd";
import BaseDragger from "../../BaseDragger";
import {LoadingOutlined} from "@ant-design/icons";
import MarkdownHelp from "./markdown-help";
import TableBody from "./table-body";
import CodeBody from "./code-body";
import {DialogType} from "../editor.types";

type EditorDialogProps = {
    title: string;
    type: DialogType
    onOk?: (mdStr: string) => void;
    onClose?: () => void;
    getContainer?: () => HTMLElement;
}

type EditorDialogState = {
    value: string,
    desc: string,
    uploading: boolean
}

const EditorDialog: FunctionComponent<EditorDialogProps> = ({title, type, onOk, onClose, getContainer}) => {

    const [state, setState] = useState<EditorDialogState>({
        uploading: false,
        value: "",
        desc: "",
    })

    const [messageApi, contextHolder] = message.useMessage({maxCount: 3, getContainer: getContainer});

    const getAccept = () => {
        if (type === "image") {
            return "image/*"
        }
        if (type === "video") {
            return "video/*"
        }
        return ""
    }

    const getBody = () => {
        if (type === "help") {
            return <MarkdownHelp/>
        }
        if (type === "code") {
            return <CodeBody getContainer={getContainer} onChange={(v) => {
                setState((prevState) => {
                    return {
                        ...prevState,
                        value: v
                    }
                })
            }}/>
        }
        if (type === "table") {
            return <TableBody onChange={(e) => {
                setState((prevState) => {
                    return {
                        ...prevState,
                        value: e
                    }
                })
            }}/>
        }
        if (type === "link") {
            return <div style={{display: "flex", flexFlow: "column", gap: 8}}>
                <div style={{display: "flex", justifyContent: "flex-start", gap: 12, alignItems: "center"}}>
                    <div style={{display: "flex"}}>地址</div>
                    <Input style={{minHeight: 36, flex: 1, width: "100%", display: "flex"}} value={state.value}
                           onChange={(e) => {
                               setState((prevState) => {
                                   return {
                                       ...prevState,
                                       value: e.target.value
                                   }
                               })
                           }}/>
                </div>
                <div style={{display: "flex", justifyContent: "flex-start", gap: 12, alignItems: "center"}}>
                    <div style={{display: "flex"}}>描述</div>
                    <Input style={{minHeight: 36, flex: 1, width: "100%", display: "flex"}} value={state.desc}
                           onChange={(e) => {
                               setState((prevState) => {
                                   return {
                                       ...prevState,
                                       desc: e.target.value
                                   }
                               })
                           }}></Input>
                </div>
            </div>
        }
        return (
            <div style={{display: "flex", flexFlow: "column", gap: 12}}>
                <div style={{display: "flex", justifyContent: "flex-start", gap: 12, alignItems: "center"}}>
                    <div style={{display: "flex"}}>地址</div>
                    <Input style={{minHeight: 36, flex: 1, width: "100%", display: "flex"}} value={state.value}
                           onChange={(e) => {
                               setState((prevState) => {
                                   return {
                                       ...prevState,
                                       value: e.target.value
                                   }
                               })
                           }}/>
                    <BaseDragger accept={getAccept()} style={{flex: "100%", minWidth: 96}}
                                 action={"api/admin/upload?dir=" + type}
                                 name={"imgFile"}
                                 disabled={state.uploading}
                                 onError={() => {
                                     setState((prevState) => {
                                         return {
                                             ...prevState,
                                             uploading: false
                                         }
                                     })
                                 }}
                                 onSuccess={(e) => {
                                     setState((prevState) => {
                                         return {
                                             ...prevState,
                                             value: e.data.url,
                                             uploading: false
                                         }
                                     })
                                 }} onProgress={() => {
                        setState((prevState) => {
                            return {
                                ...prevState,
                                uploading: true
                            }
                        })
                    }}>
                        <Button type={"text"} style={{background: "inherit", border: "none"}}>
                            {state.uploading && <LoadingOutlined/>} {state.uploading ? "上传中" : "本地上传"}
                        </Button>
                    </BaseDragger>
                </div>

                <div style={{display: "flex", justifyContent: "flex-start", gap: 12, alignItems: "center"}}>
                    <div style={{display: "flex"}}>描述</div>
                    <Input style={{minHeight: 36, flex: 1, width: "100%", display: "flex"}} value={state.desc}
                           onChange={(e) => {
                               setState((prevState) => {
                                   return {
                                       ...prevState,
                                       desc: e.target.value
                                   }
                               })
                           }}></Input>
                    <div style={{minWidth: 96}}></div>
                </div>
            </div>
        )
    }


    return <Modal open={true} width={{
        xs: '90%',
        sm: '80%',
        md: '70%',
        lg: '60%',
        xl: '50%',
        xxl: '40%',
    }} getContainer={getContainer} title={title} onOk={() => {
        if (onOk) {
            if (type === "table" || type === "code") {
                if (state.value === "") {
                    messageApi.error("不能为空")
                    return;
                }
                onOk(state.value);
                return;
            }
            if (state.value === "") {
                messageApi.error("地址不能为空")
                return;
            }
            if (type === "image") {
                onOk("![" + state.desc + "](" + state.value + ")\n");
            } else {
                if (state.desc === "") {
                    messageApi.error("描述不能为空")
                    return;
                }
                onOk("[" + state.desc + "](" + state.value + ")");
            }
        }
    }} onClose={() => {
        if (onClose) {
            onClose();
        }
    }} onCancel={() => {
        if (onClose) {
            onClose();
        }
    }}>
        {contextHolder}
        {getBody()}
    </Modal>
}

export default EditorDialog;