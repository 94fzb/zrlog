import React from "react";
import {BaseResourceComponent} from "../base-resource-component";
import {Col, Form, Input, InputNumber, Modal} from "antd";
import axios from "axios";
import Row from "antd/es/grid/row";
import TextArea from "antd/lib/input/TextArea";
import {EditOutlined} from "@ant-design/icons";
import {Link} from "react-router-dom";

const layout = {
    labelCol: {span: 4},
    wrapperCol: {span: 20},
};

export class EditLink extends BaseResourceComponent {

    updateForm = React.createRef();

    initState() {
        return {
            visible: false,
        }
    }


    showModal = () => {
        this.setState({
            visible: true,
        });
        this.setValue(this.props.col);
    }

    handleOk = e => {
        //console.log(e);
        this.setState({
            visible: false,
        });
        axios.post("/api/admin/link/update", this.state.updateForm).then(e => {
            this.props.tableComponent.fetchData();
        })
    };

    handleCancel = e => {
        //console.log(e);
        this.setState({
            visible: false,
        });
    };

    setValue(k, changedValues) {
        console.log(changedValues);
        if (this.updateForm.current != null) {
            this.updateForm.current.setFieldsValue(changedValues);
        }
        this.setState({
            updateForm: changedValues,
        });
    }


    render() {
        return (
            <>
                <Link><EditOutlined onClick={this.showModal} style={{marginBottom: 8}}/></Link>
                <Modal
                    title='编辑'
                    visible={this.state.visible}
                    onOk={this.handleOk}
                    onCancel={this.handleCancel}
                >
                    <Form initialValues={this.props.col} ref={this.updateForm}
                          onValuesChange={(k, v) => this.setValue(k, v)}
                          {...layout}>
                        <Form.Item name='id' style={{display: "none"}}>
                            <Input hidden={true}/>
                        </Form.Item>
                        <Row>
                            <Col span={24}>
                                <Form.Item
                                    label='链接'
                                    style={{marginBottom: 8}}
                                    name="url"
                                    rules={[{required: true, message: ''}]}>
                                    <Input/>
                                </Form.Item>
                            </Col>
                        </Row>
                        <Row>
                            <Col span={24}>
                                <Form.Item
                                    label='网站名称'
                                    style={{marginBottom: 8}}
                                    name="linkName"
                                    rules={[{required: true, message: ''}]}>
                                    <Input/>
                                </Form.Item>
                            </Col>
                        </Row>
                        <Row>
                            <Col span={24}>
                                <Form.Item
                                    label='描述'
                                    style={{marginBottom: 8}}
                                    name="alt"
                                    rules={[{required: true, message: ''}]}>
                                    <TextArea/>
                                </Form.Item>
                            </Col>
                        </Row>
                        <Row>
                            <Col span={24}>
                                <Form.Item
                                    label='排序'
                                    style={{marginBottom: 8}}
                                    name="sort"
                                    rules={[{required: true, message: ''}]}>
                                    <InputNumber/>
                                </Form.Item>
                            </Col>
                        </Row>
                    </Form>
                </Modal>
            </>
        );

    }

}
