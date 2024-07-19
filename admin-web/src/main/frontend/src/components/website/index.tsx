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
import AdminForm from "./AdminForm";

export interface Basic {
    second_title: string;
    title: string;
    keywords: string;
    description: string;
    favicon_ico_base64: string;
}

export interface Admin {
    session_timeout: number;
    disable_comment_status: boolean;
    article_thumbnail_status: boolean;
    language: string;
    admin_darkMode: boolean;
    admin_color_primary: string;
    favicon_png_pwa_192_base64: string;
    favicon_png_pwa_512_base64: string;
}

export interface Blog {
    host: string;
    generator_html_status: boolean;
}

export interface Other {
    icp: string;
    webCm: string;
    robotRuleContent: string;
}

export interface Upgrade {
    autoUpgradeVersion: number;
    upgradePreview: boolean;
}

const WebSite = ({
    data,
    offline,
}: {
    data: Basic | Admin | Upgrade | Other | Blog | TemplateEntry[];
    offline: boolean;
}) => {
    let activeKey = window.location.pathname.replace("/admin/website", "").replace("/", "");
    if (activeKey === "") {
        activeKey = "basic";
    }
    const buildLink = (key: string, text: string) => {
        const toUrl = key === "basic" ? "/website" : "/website/" + key;
        return (
            <Link to={toUrl} replace={true} style={{ color: activeKey === key ? getColorPrimary() : "inherit" }}>
                {text}
            </Link>
        );
    };

    const getItemBody = () => {
        if (activeKey === "basic") {
            return (
                <Row>
                    <Col xs={24} style={{ maxWidth: 600 }}>
                        <BasicForm offline={offline} data={data as Basic} />
                    </Col>
                </Row>
            );
        } else if (activeKey === "blog") {
            return (
                <Row>
                    <Col xs={24} style={{ maxWidth: 600 }}>
                        <BlogForm offline={offline} data={data as Blog} />
                    </Col>
                </Row>
            );
        } else if (activeKey === "admin") {
            return (
                <Row>
                    <Col xs={24} style={{ maxWidth: 600 }}>
                        <AdminForm offline={offline} data={data as Admin} />
                    </Col>
                </Row>
            );
        } else if (activeKey === "template") {
            return <Index data={data as TemplateEntry[]} />;
        } else if (activeKey === "other") {
            return (
                <Row>
                    <Col xs={24} style={{ maxWidth: 600 }}>
                        <OtherForm offline={offline} data={data as Other} />
                    </Col>
                </Row>
            );
        } else if (activeKey === "upgrade") {
            return <UpgradeSettingForm offline={offline} data={data as Upgrade} />;
        }
        return <></>;
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
                        label: buildLink("basic", getRes()["admin.basic.manage"]),
                        children: getItemBody(),
                    },
                    {
                        key: "blog",
                        label: buildLink("blog", getRes()["admin.blog.manage"]),
                        children: getItemBody(),
                    },
                    {
                        key: "admin",
                        label: buildLink("admin", getRes()["admin.admin.manage"]),
                        children: getItemBody(),
                    },
                    {
                        key: "template",
                        label: buildLink("template", getRes()["admin.template.manage"]),
                        children: getItemBody(),
                    },
                    {
                        key: "other",
                        label: buildLink("other", getRes()["admin.other.manage"]),
                        children: getItemBody(),
                    },
                    {
                        key: "upgrade",
                        label: buildLink("upgrade", getRes()["admin.upgrade.manage"]),
                        children: getItemBody(),
                    },
                ]}
            />
        </>
    );
};

export default WebSite;
