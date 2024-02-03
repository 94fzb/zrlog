import { Tabs } from "antd";
import Title from "antd/es/typography/Title";
import Row from "antd/es/grid/row";
import Col from "antd/es/grid/col";
import Divider from "antd/es/divider";
import Index from "../template";
import { getRes } from "../../utils/constants";
import BlogForm from "./BlogForm";
import BasicForm from "./BasicForm";
import OtherForm from "./OtherForm";
import UpgradeSettingForm from "./UpgradeSettingForm";
import { useNavigate } from "react-router-dom";
import { useState } from "react";

const WebSite = () => {
    const navigate = useNavigate();
    const initActiveKey = window.location.hash !== "" ? window.location.hash.substr(1) : "basic";

    const [activeKey, setActiveKey] = useState<string>(initActiveKey);

    const getTabPan = (currentActiveKey: string) => {
        if (activeKey === "basic" && currentActiveKey === "basic") {
            return (
                <Row>
                    <Col md={12} xs={24}>
                        <BasicForm />
                    </Col>
                </Row>
            );
        } else if (activeKey === "blog" && currentActiveKey === "blog") {
            return (
                <Row>
                    <Col md={12} xs={24}>
                        <BlogForm />
                    </Col>
                </Row>
            );
        } else if (activeKey === "template" && currentActiveKey === "template") {
            return <Index />;
        } else if (activeKey === "other" && currentActiveKey === "other") {
            return (
                <Row>
                    <Col md={12} xs={24}>
                        <OtherForm />
                    </Col>
                </Row>
            );
        } else if (activeKey === "upgrade" && currentActiveKey === "upgrade") {
            return <UpgradeSettingForm />;
        } else {
            return <></>;
        }
    };

    const handleTabClick = (key: string) => {
        navigate(`/website#${key}`);
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
