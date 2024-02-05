import { DownOutlined, KeyOutlined, LogoutOutlined, SoundOutlined, UserOutlined } from "@ant-design/icons";
import { Badge, MenuProps, Typography } from "antd";
import { Link } from "react-router-dom";

import Dropdown from "antd/es/dropdown";
import Image from "antd/es/image";
import Constants, { getRes } from "../utils/constants";
import Divider from "antd/es/divider";
import { useEffect, useState } from "react";
import axios from "axios";

const { Text } = Typography;

type BasicInfo = {
    userName: string;
    header: string;
};

export type UserInfoState = {
    basicInfoLoading: boolean;
    basicInfo: BasicInfo;
    upgrade: boolean;
    newVersion: string;
    versionType: string;
};

const UserInfo = () => {
    const [state, setState] = useState<UserInfoState>({
        basicInfoLoading: true,
        basicInfo: {
            userName: "",
            header: "",
        },
        upgrade: false,
        newVersion: "",
        versionType: "",
    });

    const loadInfo = () => {
        axios.get("/api/admin/user").then(({ data }) => {
            if (data.data.lastVersion.version) {
                setState({
                    ...state,
                    basicInfoLoading: false,
                    basicInfo: data.data,
                    upgrade: data.data.lastVersion.upgrade,
                    newVersion: data.data.lastVersion.version.version,
                    versionType: data.data.lastVersion.version.type,
                });
            } else {
                setState({
                    ...state,
                    basicInfoLoading: false,
                    basicInfo: data.data,
                });
            }
        });
    };

    useEffect(() => {
        loadInfo();
    }, []);

    if (state.basicInfoLoading) {
        return <></>;
    }

    const adminSettings = (res: Record<string, never>): MenuProps["items"] => {
        let base = [
            {
                key: "1",
                label: (
                    <Link to="/user">
                        <UserOutlined />
                        <Text style={{ paddingLeft: "5px", paddingRight: 16 }}>{res["admin.user.info"]}</Text>
                    </Link>
                ),
            },
            {
                key: "2",
                label: (
                    <Link to="/user-update-password">
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
            {
                key: "3",
                label: (
                    <a href="./admin/logout">
                        <LogoutOutlined />
                        <Text style={{ paddingLeft: "5px", paddingRight: 16 }}>{res["admin.user.logout"]}</Text>
                    </a>
                ),
            },
        ];
        if (state.upgrade) {
            base = [
                {
                    key: "0",
                    label: (
                        <Link to="/upgrade">
                            <Badge dot={state.upgrade}>
                                <SoundOutlined />
                                <Text style={{ paddingLeft: "6px" }}>
                                    {res["newVersion"]} - ({state.newVersion}#{state.versionType})
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
                    marginRight: 16,
                    float: "right",
                }}
                hidden={state.basicInfoLoading}
            >
                <Image
                    preview={false}
                    fallback={Constants.getFillBackImg()}
                    className={"userAvatarImg"}
                    src={state.basicInfo.header}
                    style={{ cursor: "pointer", width: 40, height: 40 }}
                />
                <Badge dot={state.upgrade}>
                    <Text
                        style={{
                            color: "#ffffff",
                            paddingLeft: 8,
                        }}
                    >
                        {state.basicInfo.userName}
                    </Text>
                </Badge>
                <DownOutlined />
            </div>
        </Dropdown>
    );
};

export default UserInfo;
