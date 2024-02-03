import { apiBasePath } from "../../index";
import { CameraOutlined } from "@ant-design/icons";
import Image from "antd/es/image";
import Dragger from "antd/es/upload/Dragger";
import { FunctionComponent } from "react";
import { UploadChangeParam } from "antd/es/upload";
import jquery from "jquery";
import { App } from "antd";

type ThumbnailUploadProps = {
    onChange?: (e: string) => void;
    thumbnail?: string;
};

const ThumbnailUpload: FunctionComponent<ThumbnailUploadProps> = ({ onChange, thumbnail }) => {
    const { message } = App.useApp();

    const gup = (name: string, url: string) => {
        // eslint-disable-next-line
        const results = new RegExp("[?&]" + name + "=([^&#]*)").exec(url);
        if (!results) {
            return undefined;
        }
        return results[1] || undefined;
    };

    const setThumbnailHeight = (url: string) => {
        const height = Number.parseInt(gup("h", url) + "");
        if (height) {
            const originW = Number.parseInt(jquery("#thumbnail").width() + "");
            const w = Number.parseInt(gup("w", url) + "");
            jquery("#thumbnail").width((w / originW) * Math.max(height, 256));
        }
    };

    const onUploadChange = async (info: UploadChangeParam) => {
        const { status } = info.file;
        if (status === "done") {
            setThumbnailHeight(info.file.response.data.url);
            if (onChange) {
                onChange(info.file.response.data.url);
            }
        } else if (status === "error") {
            message.error(`${info.file.name} file upload failed.`);
        }
    };

    return (
        <Dragger
            accept={"image/*"}
            style={{ overflow: "hidden", maxHeight: 256 }}
            action={apiBasePath + "upload/thumbnail?dir=thumbnail"}
            name="imgFile"
            onChange={(e) => onUploadChange(e)}
        >
            {(thumbnail === undefined || thumbnail === null || thumbnail === "") && (
                <>
                    <p
                        className="ant-upload-drag-icon"
                        style={{
                            padding: `16px 0`,
                            margin: 0,
                            display: "flex",
                            justifyContent: "center",
                            alignItems: "center",
                        }}
                    >
                        <CameraOutlined style={{ fontSize: "28px" }} />
                    </p>
                    <p
                        className="ant-upload-text"
                        style={{
                            padding: `8px 0 8px 0`,
                            margin: 0,
                        }}
                    >
                        拖拽或点击，上传文章封面
                    </p>
                </>
            )}
            {thumbnail != null && thumbnail !== "" && (
                <Image style={{ borderRadius: 8 }} preview={false} id="thumbnail" src={thumbnail} />
            )}
        </Dragger>
    );
};
export default ThumbnailUpload;
