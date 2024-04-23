import { apiBasePath } from "../../index";
import { CameraOutlined, DeleteFilled } from "@ant-design/icons";
import Image from "antd/es/image";
import Dragger from "antd/es/upload/Dragger";
import { FunctionComponent } from "react";
import { UploadChangeParam } from "antd/es/upload";
import { App } from "antd";
import { getColorPrimary, getRes } from "../../utils/constants";

type ThumbnailUploadProps = {
    onChange?: (e: string) => void;
    thumbnail?: string;
};

const ThumbnailUpload: FunctionComponent<ThumbnailUploadProps> = ({ onChange, thumbnail }) => {
    const { message } = App.useApp();

    const onUploadChange = async (info: UploadChangeParam) => {
        const { status } = info.file;
        if (status === "done") {
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
            style={{ overflow: "hidden", minHeight: 102, maxHeight: 256 }}
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
                        {getRes()["articleCover"]}
                    </p>
                </>
            )}
            {thumbnail != null && thumbnail !== "" && (
                <div style={{ position: "relative" }}>
                    <Image
                        style={{ borderRadius: 8, position: "relative" }}
                        preview={false}
                        id="thumbnail"
                        src={thumbnail}
                        wrapperStyle={{ position: "relative" }}
                    />
                    <div
                        style={{
                            position: "absolute",
                            right: 0,
                            top: 0,
                            borderRadius: "0 8px",
                            padding: 12,
                            background: getColorPrimary() + "5e",
                            color: "white",
                            fontSize: 20,
                        }}
                        onClick={(e) => {
                            if (onChange) {
                                onChange("");
                            }
                            e.stopPropagation();
                        }}
                    >
                        <DeleteFilled />
                    </div>
                </div>
            )}
        </Dragger>
    );
};
export default ThumbnailUpload;
