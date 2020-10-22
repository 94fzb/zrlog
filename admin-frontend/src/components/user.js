import React from "react";
import Spin from "antd/lib/spin";
import Title from "antd/es/typography/Title";
import Divider from "antd/es/divider";
import {BaseResourceComponent} from "./base-resource-component";
import Form from "antd/es/form";
import {Button, Input} from "antd";
import Dragger from "antd/es/upload/Dragger";
import Row from "antd/es/grid/row";
import Col from "antd/es/grid/col";
import axios from "axios";
import {message} from "antd/es";
import Image from "antd/es/image";

const layout = {
    labelCol: {span: 2},
    wrapperCol: {span: 8},
};

export class User extends BaseResourceComponent {

    userForm = React.createRef();

    getSecondTitle() {
        return this.state.res['admin.user.info'];
    }


    setValue(changedValues) {
        this.userForm.current.setFieldsValue(changedValues);
        console.info(changedValues);
        this.setState({
            basic: changedValues,
            userLoading: false
        })
    }

    onUploadChange(info) {
        const {status} = info.file;
        if (status === 'done') {
            message.success(`${info.file.response.url} file uploaded successfully.`);
            this.state.basic.header = info.file.response.url;
            this.setValue(this.state.basic);
        } else if (status === 'error') {
            message.error(`${info.file.name} file upload failed.`);
        }
    };

    onFinish() {
        axios.post("/api/admin/user/update", JSON.stringify(this.state.basic)).then(({data}) => {
            if (data.error) {
                message.error(data.message);
            }
            this.fetchData();
        })
    };

    initState() {
        return {
            basic: {},
            userLoading: true
        }
    }

    fetchData() {
        axios.get("/api/admin/user/basicInfo").then(({data}) => {
            this.setValue(data.data)
        })
    }

    componentDidMount() {
        super.componentDidMount();
        this.fetchData();
    }


    render() {

        return (
            <Spin spinning={this.state.resLoading && this.state.userLoading}>
                <Title className='page-header' level={3}>{this.getSecondTitle()}</Title>
                <Divider/>
                <Form onFinish={(values) => this.onFinish(values)}
                      onValuesChange={(k, v) => this.setValue(k, v)}
                      ref={this.userForm} {...layout}>

                    <Row>
                        <Col span={24}>
                            <Form.Item
                                label={this.state.res.userName}
                                name="userName"
                                rules={[{required: true}]}
                            >
                                <Input/>
                            </Form.Item>
                        </Col>
                    </Row>
                    <Row>
                        <Col span={24}>
                            <Form.Item
                                label={this.state.res.email}
                                name="email"
                            >
                                <Input type={"email"}/>
                            </Form.Item>
                        </Col>
                    </Row>
                    <Row>
                        <Col span={24}>
                            <Form.Item
                                label={this.state.res.headPortrait}
                                rules={[{required: true}]}
                            >
                                <Dragger style={{width: "156px"}} multiple={false}
                                         onChange={(e) => this.onUploadChange(e)}
                                         name="imgFile"
                                         action="/api/admin/upload?dir=images">
                                    <Image preview={false} width={128} height={128} src={this.state.basic.header}/>
                                </Dragger>
                            </Form.Item>
                        </Col>
                    </Row>
                    <Form.Item>
                        <Button type="primary" enterButton htmlType='submit'>
                            {this.state.res.submit}
                        </Button>
                    </Form.Item>
                </Form>
            </Spin>
        )
    }
}