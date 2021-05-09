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
import Constants from "../utils/constants";

const layout = {
    labelCol: {span: 8},
    wrapperCol: {span: 16},
};

class User extends BaseResourceComponent {

    userForm = React.createRef();

    getSecondTitle() {
        return this.state.res['admin.user.info'];
    }


    setValue(changedValues) {
        this.userForm.current.setFieldsValue(changedValues);
        this.setState({
            basic: changedValues,
            userLoading: false
        })
    }

    onUploadChange(info) {
        const {status} = info.file;
        if (status === 'done') {
            this.state.basic.header = info.file.response.data.url;
            this.setValue(this.state.basic);
        } else if (status === 'error') {
            message.error(`${info.file.name} file upload failed.`);
        }
    };

    onFinish() {
        axios.post("/api/admin/user/update", this.state.basic).then(({data}) => {
            if (data.error) {
                message.error(data.message);
            } else {
                message.info(data.message);
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
            <Spin delay={this.getSpinDelayTime()} spinning={this.state.resLoading && this.state.userLoading}>
                <Title className='page-header' level={3}>{this.getSecondTitle()}</Title>
                <Divider/>
                <Row>
                    <Col md={12} xs={24}>
                        <Form onFinish={(values) => this.onFinish(values)}
                              onValuesChange={(k, v) => this.setValue(k, v)}
                              ref={this.userForm} {...layout}>
                            <Form.Item
                                label={this.state.res.userName}
                                name="userName"
                                rules={[{required: true}]}
                            >
                                <Input/>
                            </Form.Item>

                            <Form.Item
                                label={this.state.res.email}
                                name="email"
                            >
                                <Input type={"email"}/>
                            </Form.Item>

                            <Form.Item
                                label={this.state.res.headPortrait}
                                rules={[{required: true}]}
                            >
                                <Dragger style={{width: "128px", height: "128px"}} multiple={false}
                                         onChange={(e) => this.onUploadChange(e)}
                                         name="imgFile"
                                         action="/api/admin/upload?dir=image">
                                    <Image fallback={Constants.getFillBackImg()} preview={false}
                                           height={128}
                                           width={128}
                                           src={this.state.basic.header}/>
                                </Dragger>
                            </Form.Item>
                            <Divider/>
                            <Form.Item>
                                <Button type="primary" enterbutton='true' htmlType='submit'>
                                    {this.state.res.submit}
                                </Button>
                            </Form.Item>
                        </Form>
                    </Col>
                </Row>
            </Spin>
        )
    }
}

export default User;
