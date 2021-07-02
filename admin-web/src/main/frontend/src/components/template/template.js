import {BaseResourceComponent} from "../base-resource-component";
import Title from "antd/lib/typography/Title";
import Divider from "antd/es/divider";
import Row from "antd/es/grid/row";
import Col from "antd/es/grid/col";
import {Badge, Card, Spin} from "antd";
import {Link} from "react-router-dom";
import {CheckOutlined, DeleteOutlined, EyeOutlined, SettingOutlined} from "@ant-design/icons";
import Meta from "antd/es/card/Meta";
import Button from "antd/es/button";
import React from "react";
import axios from "axios";

class Template extends BaseResourceComponent {

    initState() {
        return {
            loading: true,
            templates: [],
        }
    }

    load() {
        axios.get("/api/admin/template").then(({data}) => {
            this.setState({
                templates: data.data,
                loading: false,
            })
        })
    }

    componentDidMount() {
        super.componentDidMount();
        this.load();
    }

    delete(template) {
        axios.post("/api/admin/template/delete?template=" + template).then(e => {
            this.load();
        })
    }

    preview(template) {
        axios.post("/api/admin/template/preview?template=" + template).then(e => {
            window.open("/", '_blank');
            this.load();
        })
    }

    apply(template) {
        axios.post("/api/admin/template/apply?template=" + template).then(e => {
            this.load();
        })
    }


    getActions(template) {
        const links = [];
        links.push(
            <div onClick={e => this.preview(template.template)}>
                <EyeOutlined key="preview"/>
            </div>,
            <Link to={'/admin/template-config?template=' + template.template}>
                <SettingOutlined key="setting"/>
            </Link>,
            <div onClick={e => this.apply(template.template)}>
                <CheckOutlined/>
            </div>
        )
        if (template.deleteAble) {
            links.push(<Link onClick={e => this.delete(template.template)}><DeleteOutlined key="delete"/></Link>);
        }
        return links;
    }

    getSecondTitle() {
        return this.state.res['admin.template.manage'];
    }

    render() {
        return (<Spin spinning={this.state.resLoading || this.state.loading}>
            <Title level={4}>{this.state.res['admin.template.manage']}</Title>
            <Divider/>
            <Row gutter={[16, 16]}>
                {this.state.templates.map((template) => {
                    return (
                        <Col md={6} xxl={4} xs={24}>
                            <Badge.Ribbon
                                text={template.use ? this.state.res['admin.theme.inUse'] : template.preview ? this.state.res['admin.theme.inPreview'] : ""}
                                style={{
                                    fontSize: 16,
                                    display: template.use || template.preview ? "" : "none"
                                }}>
                                <Card
                                    cover={
                                        <img
                                            style={{width: "100%"}}
                                            alt={template.name}
                                            src={template.previewImage}
                                        />
                                    }
                                    actions={this.getActions(template)}
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
            <Link to='/admin/template-center'>
                <Button type={"primary"}>{this.state.res['admin.theme.download']}</Button>
            </Link>
        </Spin>)
    }
}

export default Template;
