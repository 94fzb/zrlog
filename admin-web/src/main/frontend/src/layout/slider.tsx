import React, { CSSProperties, ReactElement, useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { getRes } from "../utils/constants";
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
    SettingFilled,
    SettingOutlined,
} from "@ant-design/icons";
import { Menu, MenuProps } from "antd";
import EnvUtils from "../utils/env-utils";
import { useLocation } from "react-router";

type MenuItem = Required<MenuProps>["items"][number];

type MenuEntry = {
    link: string;
    selectIcon: ReactElement;
    icon: ReactElement;
    text: string;
};

const SliderMenu = () => {
    const itemStyle = {
        margin: 0,
        borderRadius: 0,
        width: "100%",
        color: "#FFF",
    };

    const location = useLocation();

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

    const getSelectMenu = () => {
        if (location.pathname === "") {
            return "index";
        } else {
            return location.pathname.replace("/admin", "");
        }
    };

    const defaultSelectMenu = getSelectMenu();

    const [selectMenu, setSelectMenu] = useState<string>(defaultSelectMenu);

    useEffect(() => {
        setSelectMenu(getSelectMenu());
    }, [location]);

    return <Menu selectedKeys={[selectMenu]} items={items} theme={EnvUtils.isDarkMode() ? "light" : "dark"} />;
};
export default SliderMenu;
