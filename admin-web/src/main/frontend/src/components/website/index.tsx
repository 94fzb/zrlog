import { Tabs } from "antd";
import Title from "antd/es/typography/Title";
import Row from "antd/es/grid/row";
import Col from "antd/es/grid/col";
import Divider from "antd/es/divider";
import Index, { TemplateEntry } from "../template";
import { getColorPrimary, getRes } from "../../utils/constants";
import BlogForm from "./BlogForm";
import BasicForm from "./BasicForm";
import OtherForm from "./OtherForm";
import UpgradeSettingForm from "./UpgradeSettingForm";
import { Link } from "react-router-dom";

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
    const tab = new URLSearchParams(window.location.search).get("tab");
    const activeKey = tab ? tab : "basic";

    const buildLink = (key: string, text: string) => {
        return (
            <Link
                to={"/website?tab=" + key}
                replace={true}
                style={{ color: activeKey === key ? getColorPrimary() : "inherit" }}
            >
                {text}
            </Link>
        );
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
                        label: buildLink("basic", "基本信息"),
                        children: (
                            <Row>
                                <Col md={12} xs={24}>
                                    <BasicForm data={data.basic} />
                                </Col>
                            </Row>
                        ),
                    },
                    {
                        key: "blog",
                        label: buildLink("blog", "博客设置"),
                        children: (
                            <Row>
                                <Col md={12} xs={24}>
                                    <BlogForm data={data.blog} />
                                </Col>
                            </Row>
                        ),
                    },
                    {
                        key: "template",
                        label: buildLink("template", getRes()["admin.template.manage"]),
                        children: <Index data={data.templates} />,
                    },
                    {
                        key: "other",
                        label: buildLink("other", "其他设置"),
                        children: (
                            <Row>
                                <Col md={12} xs={24}>
                                    <OtherForm data={data.other} />
                                </Col>
                            </Row>
                        ),
                    },
                    {
                        key: "upgrade",
                        label: buildLink("upgrade", getRes()["admin.upgrade.manage"]),
                        children: <UpgradeSettingForm data={data.upgrade} />,
                    },
                ]}
                //onChange={(e) => handleTabClick(e)}
            />
        </>
    );
};

export default WebSite;
