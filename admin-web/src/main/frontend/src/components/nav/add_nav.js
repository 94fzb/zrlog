import React from "react";
import {BaseResourceComponent} from "../base-resource-component";
import {Button, Col, Form, Input, InputNumber, Modal} from "antd";
import Row from "antd/es/grid/row";
import axios from "axios";

const layout = {
    labelCol: {span: 4},
    wrapperCol: {span: 20},
};

export class AddNav extends BaseResourceComponent {

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
        axios.post("/api/admin/nav/add", this.state.addForm).then(() => {
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
                                    label='导航名称'
                                    style={{marginBottom: 8}}
                                    name="navName"
                                    rules={[{required: true, message: ''}]}>
                                    <Input/>
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
