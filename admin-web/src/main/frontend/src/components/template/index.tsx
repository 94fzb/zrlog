import Title from "antd/es/typography/Title";
import Divider from "antd/es/divider";
import Row from "antd/es/grid/row";
import Col from "antd/es/grid/col";
import { Badge, Card } from "antd";
import { CheckOutlined, CloudDownloadOutlined, DeleteOutlined, EyeOutlined, SettingOutlined } from "@ant-design/icons";
import Meta from "antd/es/card/Meta";
import Button from "antd/es/button";
import { useEffect, useState } from "react";
import { getBackendServerUrl, getRealRouteUrl, getRes } from "../../utils/constants";
import { Link } from "react-router-dom";
import Popconfirm from "antd/es/popconfirm";
import { useAxiosBaseInstance } from "../../base/AppBase";
import { getCsrData } from "../../api";

export type TemplateEntry = {
    template: string;
    deleteAble: boolean;
    use: boolean;
    name: string;
    shortTemplate: string;
    previewImage: string;
    adminPreviewImage: string;
    preview: boolean;
    digest: string;
};

const Template = ({ data }: { data: TemplateEntry[] }) => {
    const [templateState, setTemplateState] = useState<TemplateEntry[]>(data);

    const axiosInstance = useAxiosBaseInstance();

    const load = () => {
        getCsrData("/template", axiosInstance).then(({ data }) => {
            setTemplateState(data);
        });
    };

    const preview = (shortTemplate: string) => {
        axiosInstance.post("/api/admin/template/preview?shortTemplate=" + shortTemplate).then(() => {
            window.open(document.baseURI, "_blank");
            load();
        });
    };

    const apply = (shortTemplate: string) => {
        axiosInstance.post("/api/admin/template/apply?shortTemplate=" + shortTemplate).then(() => {
            load();
        });
    };

    const deleteTemplate = (shortTemplate: string) => {
        axiosInstance.post("/api/admin/template/delete?shortTemplate=" + shortTemplate).then(() => {
            load();
        });
    };

    useEffect(() => {
        setTemplateState(data);
    }, [data]);

    const getActions = (template: TemplateEntry) => {
        const links = [];
        links.push(
            <div onClick={() => preview(template.shortTemplate)}>
                <EyeOutlined key="preview" />
            </div>,
            <Link to={getRealRouteUrl("/template-config?shortTemplate=" + template.shortTemplate)}>
                <SettingOutlined key="setting" />
            </Link>,
            <div onClick={() => apply(template.shortTemplate)}>
                <CheckOutlined />
            </div>
        );
        if (template.deleteAble) {
            links.push(
                <Popconfirm
                    title={getRes()["deleteTips"]}
                    onConfirm={() => {
                        deleteTemplate(template.shortTemplate);
                    }}
                >
                    <DeleteOutlined key="delete" />
                </Popconfirm>
            );
        }
        return links;
    };

    return (
        <>
            <Title level={4}>{getRes()["admin.template.manage"]}</Title>
            <Divider />
            <Row gutter={[16, 16]}>
                {templateState.map((template) => {
                    return (
                        <Col md={6} xxl={4} xs={24}>
                            <Badge.Ribbon
                                text={
                                    template.use
                                        ? getRes()["admin.theme.inUse"]
                                        : template.preview
                                        ? getRes()["admin.theme.inPreview"]
                                        : ""
                                }
                                style={{
                                    fontSize: 16,
                                    display: template.use || template.preview ? "" : "none",
                                }}
                            >
                                <Card
                                    cover={
                                        <img
                                            style={{ width: "100%", minHeight: 250 }}
                                            alt={template.name}
                                            title={template.name}
                                            src={getBackendServerUrl() + template.adminPreviewImage.substring(1)}
                                        />
                                    }
                                    actions={getActions(template)}
                                >
                                    <Meta title={template.name} description={template.digest} />
                                </Card>
                            </Badge.Ribbon>
                        </Col>
                    );
                })}
            </Row>
            <Divider />
            <Link to={getRealRouteUrl(`/template-center?host=${window.location.host}`)}>
                <Button icon={<CloudDownloadOutlined />} type={"primary"}>
                    {getRes()["admin.theme.download"]}
                </Button>
            </Link>
        </>
    );
};

export default Template;
