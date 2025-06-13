import {
    ApiFilled,
    ApiOutlined,
    AppstoreFilled,
    AppstoreOutlined,
    CommentOutlined,
    ContainerFilled,
    ContainerOutlined,
    DashboardFilled,
    DashboardOutlined,
    EditFilled,
    EditOutlined,
    HomeOutlined,
    SettingFilled,
    SettingOutlined,
} from "@ant-design/icons";
import { Col, Layout, Menu, Row, Typography } from "antd";
import { Link, useLocation } from "react-router-dom";

import AdminLoginedRouter from "../routers/admin-logined-router";
import { getRes } from "../utils/constants";
import React, { CSSProperties, ReactElement, useEffect, useLayoutEffect, useState } from "react";
import axios from "axios";
import EnvUtils from "../utils/env-utils";

import type { MenuProps } from "antd";
import UserInfo from "./user-info";

type MenuItem = Required<MenuProps>["items"][number];

type MenuEntry = {
    link: string;
    selectIcon: ReactElement;
    icon: ReactElement;
    text: string;
};

const { Header, Content, Footer, Sider } = Layout;
const { Text } = Typography;

type BasicInfo = {
    userName: string;
    header: string;
};

export type IndexLayoutState = {
    basicInfoLoading: boolean;
    basicInfo: BasicInfo;
    upgrade: boolean;
    newVersion: string;
    versionType: string;
};

const IndexLayout = () => {
    const itemStyle = {
        margin: 0,
        borderRadius: 0,
        width: "100%",
        color: "#FFF",
    };

    const location = useLocation();
    const [selectMenu, setSelectMenu] = useState<string>("");

    const getIcon = (entry: MenuEntry) => {
        if (location.pathname === entry.link) {
            return entry.selectIcon;
        }
        if (entry.link !== "#more") {
            return entry.icon;
        }
        if (location.pathname === "/link" || location.pathname === "/nav" || location.pathname === "/article-type") {
            return entry.selectIcon;
        }
        return entry.icon;
    };

    function getItem(entry: MenuEntry, key: React.Key | null, children: MenuItem[], style: CSSProperties): MenuItem {
        const label = (
            <Link to={entry.link} style={{ color: "inherit" }}>
                {getIcon(entry)}
                <span className="menu-title">{entry.text}</span>
            </Link>
        );
        if (children.length > 0) {
            return {
                key,
                children,
                label,
                style,
            } as MenuItem;
        }
        return {
            key,
            label,
            style,
        } as MenuItem;
    }

    const items: MenuItem[] = [
        getItem(
            {
                text: getRes().dashboard,
                link: "/index",
                selectIcon: <DashboardFilled style={{ fontSize: 24 }} />,
                icon: <DashboardOutlined style={{ fontSize: 24 }} />,
            },
            "/index",
            [],
            itemStyle
        ),
        getItem(
            {
                text: getRes()["admin.log.edit"],
                link: "/article-edit",
                selectIcon: <EditFilled style={{ fontSize: 24 }} />,
                icon: <EditOutlined style={{ fontSize: 24 }} />,
            },
            "/article-edit",
            [],
            itemStyle
        ),
        getItem(
            {
                text: getRes()["blogManage"],
                link: "/article",
                selectIcon: <ContainerFilled style={{ fontSize: 24 }} />,
                icon: <ContainerOutlined style={{ fontSize: 24 }} />,
            },
            "/article",
            [],
            itemStyle
        ),
        getItem(
            {
                text: getRes()["admin.comment.manage"],
                link: "/comment",
                selectIcon: <CommentOutlined style={{ fontSize: 24 }} />,
                icon: <CommentOutlined style={{ fontSize: 24 }} />,
            },
            "/comment",
            [],
            itemStyle
        ),
        getItem(
            {
                text: getRes()["admin.plugin.manage"],
                link: "/plugin",
                selectIcon: <ApiFilled style={{ fontSize: 24 }} />,
                icon: <ApiOutlined style={{ fontSize: 24 }} />,
            },
            "/plugin",
            [],
            itemStyle
        ),
        getItem(
            {
                text: getRes()["admin.setting"],
                link: "/website",
                selectIcon: <SettingFilled style={{ fontSize: 24 }} />,
                icon: <SettingOutlined style={{ fontSize: 24 }} />,
            },

            "/website",
            [],
            itemStyle
        ),
        getItem(
            {
                text: getRes()["admin.more"],
                link: "#more",
                selectIcon: <AppstoreFilled style={{ fontSize: 24 }} />,
                icon: <AppstoreOutlined style={{ fontSize: 24 }} />,
            },
            "more",
            [
                getItem(
                    {
                        text: getRes()["admin.type.manage"],
                        link: "/article-type",
                        selectIcon: <span />,
                        icon: <span />,
                    },
                    "/article-type",
                    [],
                    itemStyle
                ),
                getItem(
                    {
                        text: getRes()["admin.link.manage"],
                        link: "/link",
                        selectIcon: <span />,
                        icon: <span />,
                    },
                    "/link",
                    [],
                    itemStyle
                ),
                getItem(
                    {
                        text: getRes()["admin.nav.manage"],
                        link: "/nav",
                        selectIcon: <span />,
                        icon: <span />,
                    },
                    "/nav",
                    [],
                    itemStyle
                ),
            ],
            { ...itemStyle }
        ),
    ];

    const [layoutState, setLayoutState] = useState<IndexLayoutState>({
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
        axios.get("/api/admin/user/basicInfo").then(({ data }) => {
            if (data.data.lastVersion.version) {
                setLayoutState({
                    ...layoutState,
                    basicInfoLoading: false,
                    basicInfo: data.data,
                    upgrade: data.data.lastVersion.upgrade,
                    newVersion: data.data.lastVersion.version.version,
                    versionType: data.data.lastVersion.version.type,
                });
            } else {
                setLayoutState({
                    ...layoutState,
                    basicInfoLoading: false,
                    basicInfo: data.data,
                });
            }
        });
    };

    useEffect(() => {
        loadInfo();
    }, []);

    useLayoutEffect(() => {
        if (location.pathname === "") {
            setSelectMenu("index");
            return;
        }
        setSelectMenu(location.pathname);
    }, [location]);

    // @ts-ignore
    return (
        <>
            <Header
                style={{
                    backgroundColor: EnvUtils.isDarkMode() ? "#1f1f1f" : "#011529",
                }}
            >
                <a
                    href={document.baseURI}
                    id="logo"
                    target="_blank"
                    title={getRes()["websiteTitle"]}
                    rel="noopener noreferrer"
                >
                    <HomeOutlined />
                </a>
                <UserInfo layoutState={layoutState} />
            </Header>
            <Row>
                <Col style={{ minHeight: "100vh" }} id="sider">
                    <Sider
                        width={70}
                        style={{ minHeight: "100vh", backgroundColor: EnvUtils.isDarkMode() ? "#1f1f1f" : "#001529" }}
                    >
                        <Menu
                            selectedKeys={[selectMenu]}
                            items={items}
                            theme={EnvUtils.isDarkMode() ? "light" : "dark"}
                        />
                    </Sider>
                </Col>
                <Col style={{ flex: 1, width: 100 }}>
                    <Layout style={{ minHeight: "100vh" }}>
                        <Content>
                            <AdminLoginedRouter />
                        </Content>
                        <Footer>
                            <Row>
                                <Col xs={24} md={12}>
                                    <div
                                        className="ant-layout-footer-copyright"
                                        dangerouslySetInnerHTML={{
                                            __html: getRes().copyrightTips + ". All Rights Reserved.",
                                        }}
                                    />
                                </Col>
                                <Col xs={0} md={12}>
                                    <Text style={{ float: "right" }}>Version {getRes().currentVersion}</Text>
                                </Col>
                            </Row>
                        </Footer>
                    </Layout>
                </Col>
            </Row>
        </>
    );
};

export default IndexLayout;
