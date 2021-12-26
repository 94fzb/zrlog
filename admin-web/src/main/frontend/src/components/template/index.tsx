import Title from "antd/lib/typography/Title";
import Divider from "antd/es/divider";
import Row from "antd/es/grid/row";
import Col from "antd/es/grid/col";
import {Badge, Card} from "antd";
import {CheckOutlined, DeleteOutlined, EyeOutlined, SettingOutlined} from "@ant-design/icons";
import Meta from "antd/es/card/Meta";
import Button from "antd/es/button";
import {useEffect, useState} from "react";
import {getRes} from "../../utils/constants";
import axios from "axios";
import {Link} from "react-router-dom";

type TemplateEntry = {
    template: string,
    deleteAble: boolean,
    use: boolean,
    name: string,
    previewImage: string,
    preview: boolean,
    digest: string,
}

type TemplateState = {
    loading: boolean,
    templates: TemplateEntry[]
}

const Template = () => {

    const [templateState, setTemplateState] = useState<TemplateState>({
        loading: true,
        templates: []
    })

    const load = () => {
        axios.get("/api/admin/template").then(({data}) => {
            setTemplateState({
                templates: data.data,
                loading: false,
            })
        })
    }

    const preview = (template: string) => {
        axios.post("/api/admin/template/preview?template=" + template).then(() => {
            window.open(document.baseURI, '_blank');
            load();
        })
    }

    const apply = (template: string) => {
        axios.post("/api/admin/template/apply?template=" + template).then(() => {
            load();
        })
    }

    const deleteTemplate = (template: string) => {
        axios.post("/api/admin/template/delete?template=" + template).then(() => {
            load();
        })
    }

    useEffect(() => {
        load()
    }, []);


    const getActions = (template: TemplateEntry) => {
        const links = [];
        links.push(
            <div onClick={() => preview(template.template)}>
                <EyeOutlined key="preview"/>
            </div>,
            <Link to={'./template-config?template=' + template.template}>
                <SettingOutlined key="setting"/>
            </Link>,
            <div onClick={() => apply(template.template)}>
                <CheckOutlined/>
            </div>
        )
        if (template.deleteAble) {
            links.push(<Link to={"#"} onClick={() => deleteTemplate(template.template)}><DeleteOutlined
                key="delete"/></Link>);
        }
        return links;
    }


    return (
        <>
            <Title level={4}>{getRes()['admin.template.manage']}</Title>
            <Divider/>
            <Row gutter={[16, 16]}>
                {templateState.templates.map((template) => {
                    return (
                        <Col md={6} xxl={4} xs={24}>
                            <Badge.Ribbon
                                text={template.use ? getRes()['admin.theme.inUse'] : template.preview ? getRes()['admin.theme.inPreview'] : ""}
                                style={{
                                    fontSize: 16,
                                    display: template.use || template.preview ? "" : "none"
                                }}>
                                <Card
                                    cover={
                                        <img
                                            style={{width: "100%", minHeight: 250}}
                                            alt={template.name}
                                            title={template.name}
                                            src={template.previewImage}
                                        />
                                    }
                                    actions={getActions(template)}
                                >
                                    <Meta
                                        title={template.name}
                                        description={template.digest}
                                    />
                                </Card>
                            </Badge.Ribbon>
                        </Col>
                    )
                })}
            </Row>
            <Divider/>
            <Link to='./template-center'>
                <Button type={"primary"}>{getRes()['admin.theme.download']}</Button>
            </Link>
        </>)
}

export default Template;
