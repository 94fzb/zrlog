import { Tabs } from "antd";
import Title from "antd/es/typography/Title";
import Row from "antd/es/grid/row";
import Col from "antd/es/grid/col";
import Divider from "antd/es/divider";
import Index, { TemplateEntry } from "../template";
import { getRes } from "../../utils/constants";
import BlogForm from "./BlogForm";
import BasicForm from "./BasicForm";
import OtherForm from "./OtherForm";
import UpgradeSettingForm from "./UpgradeSettingForm";
import { useNavigate } from "react-router-dom";
import { useState } from "react";

export interface Data {
    basic: Basic;
    blog: Blog;
    other: Other;
    upgrade: Upgrade;
    templates: TemplateEntry[];
}

export interface Basic {
    second_title: string;
    title: string;
    keywords: string;
    description: string;
}

export interface Blog {
    session_timeout: number;
    generator_html_status: boolean;
    disable_comment_status: boolean;
    article_thumbnail_status: boolean;
    language: string;
    article_route: string;
    admin_darkMode: boolean;
    admin_color_primary: string;
}

export interface Other {
    icp: string;
    webCm: string;
}

export interface Upgrade {
    autoUpgradeVersion: number;
    upgradePreview: boolean;
}

const WebSite = ({ data }: { data: Data }) => {
    const navigate = useNavigate();
    const tab = new URLSearchParams(window.location.search).get("tab");
    const initActiveKey = tab ? tab : "basic";

    const [activeKey, setActiveKey] = useState<string>(initActiveKey);

    const getTabPan = (currentActiveKey: string) => {
        if (activeKey === "basic" && currentActiveKey === "basic") {
            return (
                <Row>
                    <Col md={12} xs={24}>
                        <BasicForm data={data.basic} />
                    </Col>
                </Row>
            );
        } else if (activeKey === "blog" && currentActiveKey === "blog") {
            return (
                <Row>
                    <Col md={12} xs={24}>
                        <BlogForm data={data.blog} />
                    </Col>
                </Row>
            );
        } else if (activeKey === "template" && currentActiveKey === "template") {
            return <Index data={data.templates} />;
        } else if (activeKey === "other" && currentActiveKey === "other") {
            return (
                <Row>
                    <Col md={12} xs={24}>
                        <OtherForm data={data.other} />
                    </Col>
                </Row>
            );
        } else if (activeKey === "upgrade" && currentActiveKey === "upgrade") {
            return <UpgradeSettingForm data={data.upgrade} />;
        } else {
            return <></>;
        }
    };

    const handleTabClick = (key: string) => {
        navigate(`/website?tab=${key}`);
        setActiveKey(key);
    };

    return (
        <>
            <Title className="page-header" level={3}>
                {getRes()["admin.setting"]}
            </Title>
            <Divider />
            <Tabs
                activeKey={activeKey}
                items={[
                    {
                        key: "basic",
                        label: "基本信息",
                        children: getTabPan("basic"),
                    },
                    {
                        key: "blog",
                        label: "博客设置",
                        children: getTabPan("blog"),
                    },
                    {
                        key: "template",
                        label: getRes()["admin.template.manage"],
                        children: getTabPan("template"),
                    },
                    {
                        key: "other",
                        label: "其他设置",
                        children: getTabPan("other"),
                    },
                    {
                        key: "upgrade",
                        label: getRes()["admin.upgrade.manage"],
                        children: getTabPan("upgrade"),
                    },
                ]}
                onChange={(e) => handleTabClick(e)}
            />
        </>
    );
};

export default WebSite;
