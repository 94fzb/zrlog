import React from 'react'
import {LoginOutlined} from '@ant-design/icons';
import {Button, Col, Form, Input, Layout} from 'antd';
import Card from "antd/es/card";
import axios from "axios";
import {message} from "antd/es";
import Spin from "antd/es/spin";
import {BaseResourceComponent} from "../base-resource-component";
import Title from "antd/es/typography/Title";
import './login.less'
import Row from "antd/es/grid/row";
import Constants from "../../utils/constants";

const md5 = require('md5');

const layout = {
    labelCol: {span: 8},
    wrapperCol: {span: 8},
};

const {Content, Footer} = Layout;

class Login extends BaseResourceComponent {

    loginFrom = React.createRef();

    initState() {
        return {
            logging: false
        }
    }

    setValue(changedValues) {
        this.loginFrom.current.setFieldsValue(changedValues);
    }

    onFinish(allValues) {
        const loginForm = {
            "userName": allValues.userName,
            "password": md5(allValues.password),
            "https": window.location.protocol === "https:"
        };
        this.setState({
            logging: true
        })
        axios.post("/api/admin/login", loginForm).then(({data}) => {
            if (data.error) {
                message.error(data.message);
            } else {
                const query = new URLSearchParams(this.props.location.search);
                if (query.get("redirectFrom") !== null && query.get("redirectFrom") !== '') {
                    window.location.href = decodeURIComponent(query.get("redirectFrom"));
                } else {
                    Constants.getHistory().push("/admin/index");
                }
            }
        }).finally(() => {
            this.setState({
                logging: false
            })
        })
    };

    fetchResSuccess(res) {
        const {defaultLoginInfo} = res;
        if (defaultLoginInfo !== undefined) {
            this.setValue(defaultLoginInfo);
        }
    }

    getSecondTitle() {
        return this.state.res['login'];
    }

    render() {
        return (
            <Spin delay={this.getSpinDelayTime()} spinning={this.state.resLoading}>
                <Layout>
                    <Content style={{minWidth: "100%"}}>
                        <Card className='login-container'
                              cover={
                                  <div className='dimback'>
                                      <div
                                          className='dim'
                                          style={{
                                              backgroundImage: "url('/admin/vendors/images/login-bg.jpg')",
                                              height: "120px"
                                          }}>
                                      </div>
                                      <Title level={3}
                                             style={{color: "#fff"}}>{this.state.res['userNameAndPassword']}</Title>
                                  </div>}
                              style={{textAlign: "center"}}>
                            <Form
                                ref={this.loginFrom}
                                {...layout}
                                onFinish={(values) => this.onFinish(values)}
                                onValuesChange={(k, v) => this.setValue(k, v)}
                            >
                                <Form.Item
                                    label={this.state.res.userName}
                                    name="userName"
                                    rules={[{required: true}]}
                                >
                                    <Input/>
                                </Form.Item>

                                <Form.Item
                                    label={this.state.res.password}
                                    name="password"
                                    rules={[{required: true}]}
                                >
                                    <Input.Password/>
                                </Form.Item>

                                <Row type="flex" style={{alignItems: 'center'}}>
                                    <Col xxl={24} xs={24}>
                                        <Button loading={this.state.logging} type="primary" enterbutton='true'
                                                htmlType='submit'>
                                            <LoginOutlined/> {this.state.res.login}
                                        </Button>
                                    </Col>
                                </Row>
                            </Form>
                        </Card>
                    </Content>
                    <Footer
                        style={{textAlign: 'center'}}>{this.state.res.copyrightCurrentYear} {this.state.res.websiteTitle}.
                        All Rights Reserved.</Footer>
                </Layout>
            </Spin>
        )
    }
}

export default Login;
