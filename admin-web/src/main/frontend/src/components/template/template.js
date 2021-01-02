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

    componentDidMount() {
        super.componentDidMount();
        axios.get("/api/admin/template").then(({data}) => {
            this.setState({
                templates: data.data,
                loading: false,
            })
        })
    }

    getSecondTitle() {
        return this.state.res['admin.template.manage'];
    }

    render() {
        return (<Spin spinning={this.state.loading}>
            <Title level={4}>{this.state.res['admin.template.manage']}</Title>
            <Divider/>
            <Row gutter={[16, 16]}>
                {this.state.templates.map((template) => {
                    return (
                        <Col md={8} xxl={4} xs={24}>
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
                                    actions={[
                                        <Link to={'/admin/template-config?template=' + template.template}>
                                            <SettingOutlined key="setting"/>
                                        </Link>,
                                        <Link target='_blank'
                                              to={"/admin/template/preview?template=" + template.template}>
                                            <EyeOutlined key="preview"/>
                                        </Link>,
                                        <DeleteOutlined disabled={!template.deleteAble} key="delete"/>,
                                        <Link target='_blank'
                                              to={"/admin/template/apply?template=" + template.template}>
                                            <CheckOutlined/>
                                        </Link>
                                    ]}
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