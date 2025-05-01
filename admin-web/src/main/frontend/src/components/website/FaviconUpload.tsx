import { CameraOutlined, DeleteFilled } from "@ant-design/icons";
import Image from "antd/es/image";
import Dragger from "antd/es/upload/Dragger";
import { FunctionComponent } from "react";
import { RcFile } from "antd/es/upload";
import { getColorPrimary } from "../../utils/constants";

type ThumbnailUploadProps = {
    onChange?: (e: string | null) => void;
    url?: string;
};

const FaviconUpload: FunctionComponent<ThumbnailUploadProps> = ({ onChange, url }) => {
    const handleBeforeUpload = async (file: RcFile): Promise<any> => {
        return new Promise((_resolve, reject) => {
            const reader = new FileReader();
            reader.readAsDataURL(file);
            reader.onload = () => {
                const base64 = reader.result as string;
                if (onChange) {
                    onChange(base64);
                }
                reject(); // 阻止文件上传
            };
        });
    };

    return (
        <Dragger
            accept={"image/*"}
            height={64}
            beforeUpload={(file) => handleBeforeUpload(file)}
            style={{ overflow: "hidden", height: 64, width: 64 }}
        >
            {(url === undefined || url === null || url === "") && (
                <p
                    className="ant-upload-drag-icon"
                    style={{
                        padding: `18px 0`,
                        margin: 0,
                        display: "flex",
                        justifyContent: "center",
                        alignItems: "center",
                    }}
                >
                    <CameraOutlined style={{ fontSize: "28px" }} />
                </p>
            )}
            {url != null && url !== "" && (
                <div style={{ position: "relative", height: "100%" }}>
                    <Image
                        style={{ borderRadius: 8, position: "relative" }}
                        preview={false}
                        src={url}
                        wrapperStyle={{ position: "relative" }}
                    />
                    <div
                        style={{
                            position: "absolute",
                            right: 0,
                            top: 0,
                            borderRadius: "0 8px",
                            padding: 4,
                            background: getColorPrimary() + "5e",
                            color: "white",
                            fontSize: 16,
                        }}
                        onClick={(e) => {
                            if (onChange) {
                                onChange(null);
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
export default FaviconUpload;
