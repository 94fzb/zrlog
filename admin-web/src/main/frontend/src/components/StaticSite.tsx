import { FunctionComponent, useState } from "react";
import { AdminCommonProps } from "../type";
import { FloatButton, message } from "antd";
import { useAxiosBaseInstance } from "../base/AppBase";
import { LoadingOutlined, SyncOutlined } from "@ant-design/icons";

type StaticSiteData = {
    synced: boolean;
};

type StaticSiteState = {
    synced: boolean;
    syncing: boolean;
};

const StaticSite: FunctionComponent<AdminCommonProps<StaticSiteData>> = ({ data }) => {
    const [state, setState] = useState<StaticSiteState>({
        synced: data.synced,
        syncing: false,
    });

    const axiosBaseInstance = useAxiosBaseInstance();

    const [messageApi, messageContextHolder] = message.useMessage({
        maxCount: 3,
    });

    return (
        <>
            {messageContextHolder}
            <FloatButton
                style={{
                    display: state.synced ? "none" : "inherit",
                }}
                onClick={async () => {
                    setState({
                        synced: false,
                        syncing: true,
                    });
                    const { data } = await axiosBaseInstance.post("/api/admin/static-site/startSync");
                    if (data.data as StaticSiteData) {
                        if (data.data.error) {
                            messageApi.error("同步失败 ->" + data.data.message);
                            setState({
                                synced: false,
                                syncing: false,
                            });
                        } else {
                            if (data.data.synced) {
                                messageApi.success("同步完成");
                                setState({
                                    synced: data.data.synced,
                                    syncing: false,
                                });
                                window.location.reload();
                            } else {
                                messageApi.info("同步未完成");
                                setState({
                                    synced: false,
                                    syncing: false,
                                });
                            }
                        }
                    }
                }}
                icon={state.syncing ? <LoadingOutlined /> : <SyncOutlined />}
            ></FloatButton>
        </>
    );
};

export default StaticSite;
