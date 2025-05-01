import { DownOutlined, KeyOutlined, LogoutOutlined, SoundOutlined, UserOutlined } from "@ant-design/icons";
import { Badge, MenuProps, Modal, Typography } from "antd";
import { Link } from "react-router-dom";

import Dropdown from "antd/es/dropdown";
import Image from "antd/es/image";
import Constants, { getBackendServerUrl, getRealRouteUrl, getRes, isStaticPage } from "../utils/constants";
import Divider from "antd/es/divider";
import { BasicUserInfo } from "../type";
import { tryBlock } from "../utils/helpers";

const { Text } = Typography;

const UserInfo = ({ data, offline }: { data: BasicUserInfo; offline: boolean }) => {
    const [modal, contextHolder] = Modal.useModal();

    const adminSettings = (res: Record<string, never>): MenuProps["items"] => {
        let base = [
            {
                key: "1",
                label: (
                    <Link to={getRealRouteUrl("/user")} onClick={(e) => tryBlock(e, modal)}>
                        <UserOutlined />
                        <Text style={{ paddingLeft: "5px", paddingRight: 16 }}>{res["admin.user.info"]}</Text>
                    </Link>
                ),
            },
            {
                key: "2",
                label: (
                    <Link to={getRealRouteUrl("/user-update-password")} onClick={(e) => tryBlock(e, modal)}>
                        <KeyOutlined />
                        <Text style={{ paddingLeft: "5px", paddingRight: 16 }}>{res["admin.changePwd"]}</Text>
                    </Link>
                ),
            },
            {
                key: "-",
                label: (
                    <Divider style={{ marginTop: "5px", marginBottom: "5px", userSelect: "none", cursor: "none" }} />
                ),
            },
        ];
        if (!offline) {
            base.push({
                key: "3",
                label: (
                    <a
                        href={getBackendServerUrl() + "admin/logout" + (isStaticPage() ? "?sp=true" : "")}
                        onClick={(e) => tryBlock(e, modal)}
                    >
                        <LogoutOutlined />
                        <Text style={{ paddingLeft: "5px", paddingRight: 16 }}>{res["admin.user.logout"]}</Text>
                    </a>
                ),
            });
        }
        if (data.lastVersion?.upgrade) {
            base = [
                {
                    key: "99",
                    label: (
                        <Link to={getRealRouteUrl("/upgrade")} onClick={(e) => tryBlock(e, modal)}>
                            <Badge dot={true}>
                                <SoundOutlined />
                                <Text style={{ paddingLeft: "6px" }}>
                                    {res["newVersion"]} - ({data.lastVersion.version.version}#
                                    {data.lastVersion.version.type})
                                </Text>
                            </Badge>
                        </Link>
                    ),
                },
                ...base,
            ];
        }
        return base;
    };

    const items = adminSettings(getRes());

    return (
        <>
            {contextHolder}
            <Dropdown menu={{ items }} placement="bottom">
                <div
                    style={{
                        color: "#ffffff",
                        height: "64px",
                        borderRadius: 0,
                        marginRight: 16,
                    }}
                >
                    <Image
                        preview={false}
                        fallback={Constants.getFillBackImg()}
                        className={"userAvatarImg"}
                        src={data.header}
                        style={{ cursor: "pointer", width: 40, height: 40 }}
                    />
                    <Badge dot={data.lastVersion?.upgrade}>
                        <Text
                            style={{
                                color: "#ffffff",
                                paddingLeft: 8,
                            }}
                        >
                            {data.userName}
                        </Text>
                    </Badge>
                    <DownOutlined />
                </div>
            </Dropdown>
        </>
    );
};

export default UserInfo;
