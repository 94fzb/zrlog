import React from "react";
import {BaseResourceComponent} from "../base-resource-component";
import {Button, Col, Form, Input, Modal} from "antd";
import Row from "antd/es/grid/row";
import TextArea from "antd/lib/input/TextArea";
import axios from "axios";

const layout = {
    labelCol: {span: 4},
    wrapperCol: {span: 20},
};

export class AddType extends BaseResourceComponent {

    state = {visible: false};

    addForm = React.createRef();

    showModal = () => {
        this.setState({
            visible: true,
        });
    }

    handleOk = e => {
        console.log(e);
        this.setState({
            visible: false,
        });
        axios.post("/api/admin/type/add", this.state.addForm).then(e => {
            this.props.tableComponent.fetchData();
        })
    };

    handleCancel = e => {
        console.log(e);
        this.setState({
            visible: false,
        });
    };

    setValue(k, changedValues) {
        this.addForm.current.setFieldsValue(changedValues);
        this.setState({
            addForm: changedValues,
        })
    }

    render() {
        return (
            <>
                <Button type="primary" onClick={this.showModal} style={{marginBottom: 8}}>
                    添加
                </Button>
                <Modal
                    title='添加'
                    visible={this.state.visible}
                    onOk={this.handleOk}
                    onCancel={this.handleCancel}
                >
                    <Form ref={this.addForm}
                          onValuesChange={(k, v) => this.setValue(k, v)}
                          {...layout}>
                        <Row>
                            <Col span={24}>
                                <Form.Item
                                    label='名称分类'
                                    style={{marginBottom: 8}}
                                    name="typeName"
                                    rules={[{required: true, message: ''}]}>
                                    <Input/>
                                </Form.Item>
                            </Col>
                        </Row>
                        <Row>
                            <Col span={24}>
                                <Form.Item
                                    label='别名'
                                    style={{marginBottom: 8}}
                                    name="alias"
                                    rules={[{required: true, message: ''}]}>
                                    <Input/>
                                </Form.Item>
                            </Col>
                        </Row>
                        <Row>
                            <Col span={24}>
                                <Form.Item
                                    label='介绍'
                                    style={{marginBottom: 8}}
                                    name="remark"
                                    rules={[{required: true, message: ''}]}>
                                    <TextArea/>
                                </Form.Item>
                            </Col>
                        </Row>
                    </Form>
                </Modal>
            </>
        );

    }

}
