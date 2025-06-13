import { DownOutlined, KeyOutlined, LogoutOutlined, SoundOutlined, UserOutlined } from "@ant-design/icons";
import { Badge, MenuProps, Typography } from "antd";
import { Link } from "react-router-dom";

import Dropdown from "antd/es/dropdown";
import Image from "antd/es/image";
import Constants, { getRes } from "../utils/constants";

const { Text } = Typography;
import { IndexLayoutState } from "./index-layout";
import Divider from "antd/es/divider";

const UserInfo = ({ layoutState }: { layoutState: IndexLayoutState }) => {
    const adminSettings = (res: Record<string, never>): MenuProps["items"] => {
        let base = [
            {
                key: "1",
                label: (
                    <Link to="/user">
                        <UserOutlined />
                        <Text style={{ paddingLeft: "5px" }}>{res["admin.user.info"]}</Text>
                    </Link>
                ),
            },
            {
                key: "2",
                label: (
                    <Link to="/user-update-password">
                        <KeyOutlined />
                        <Text style={{ paddingLeft: "5px" }}>{res["admin.changePwd"]}</Text>
                    </Link>
                ),
            },
            {
                key: "-",
                label: (
                    <Divider style={{ marginTop: "5px", marginBottom: "5px", userSelect: "none", cursor: "none" }} />
                ),
            },
            {
                key: "3",
                label: (
                    <a href="./admin/logout">
                        <LogoutOutlined />
                        <Text style={{ paddingLeft: "5px" }}>{res["admin.user.logout"]}</Text>
                    </a>
                ),
            },
        ];
        if (layoutState.upgrade) {
            base = [
                {
                    key: "0",
                    label: (
                        <Link to="/upgrade">
                            <Badge dot={layoutState.upgrade}>
                                <SoundOutlined />
                                <Text style={{ paddingLeft: "6px" }}>
                                    {res["newVersion"]} - ({layoutState.newVersion}#{layoutState.versionType})
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
        <Dropdown menu={{ items }} placement="bottom" arrow={{ pointAtCenter: true }}>
            <div
                style={{
                    color: "#ffffff",
                    height: "64px",
                    borderRadius: 0,
                    paddingRight: 16,
                    float: "right",
                }}
                hidden={layoutState.basicInfoLoading}
            >
                <Image
                    preview={false}
                    fallback={Constants.getFillBackImg()}
                    className={"userAvatarImg"}
                    src={layoutState.basicInfo.header}
                    style={{ cursor: "pointer", width: 40, height: 40 }}
                />
                <Badge dot={layoutState.upgrade}>
                    <Text
                        style={{
                            color: "#ffffff",
                            paddingLeft: 8,
                        }}
                    >
                        {layoutState.basicInfo.userName}
                    </Text>
                </Badge>
                <DownOutlined />
            </div>
        </Dropdown>
    );
};

export default UserInfo;
