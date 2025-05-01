import { CameraOutlined, DeleteFilled, LoadingOutlined } from "@ant-design/icons";
import Image from "antd/es/image";
import { FunctionComponent, useState } from "react";
import { getColorPrimary, getRes } from "../../utils/constants";
import BaseDragger from "../../common/BaseDragger";
import { message } from "antd";

type ThumbnailUploadProps = {
    onChange?: (e: string) => void;
    thumbnail?: string;
};

const ThumbnailUpload: FunctionComponent<ThumbnailUploadProps> = ({ onChange, thumbnail }) => {
    const [uploading, setUploading] = useState<boolean>(false);

    const [messageApi, contextHolder] = message.useMessage({ maxCount: 3 });

    return (
        <BaseDragger
            onSuccess={({ data }) => {
                setUploading(false);
                if (onChange) {
                    onChange(data.url);
                }
            }}
            disabled={uploading}
            onProgress={() => {
                setUploading(true);
            }}
            onError={(e) => {
                messageApi.error(e.message);
                setUploading(false);
            }}
            accept={"image/*"}
            style={{ overflow: "hidden", minHeight: 102, maxHeight: 256 }}
            action={"/api/admin/upload/thumbnail?dir=thumbnail"}
            name="imgFile"
        >
            {contextHolder}
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
                        {uploading && <LoadingOutlined />} {getRes()["uploadTips"]}
                    </p>
                </>
            )}
            {thumbnail != null && thumbnail !== "" && (
                <div style={{ position: "relative" }}>
                    <Image
                        style={{ borderRadius: 0, position: "relative" }}
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
        </BaseDragger>
    );
};
export default ThumbnailUpload;
