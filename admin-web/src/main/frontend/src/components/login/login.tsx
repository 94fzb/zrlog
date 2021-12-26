import {useState} from 'react'
import {LoginOutlined} from '@ant-design/icons';
import {Button, Col, Form, Input, Layout} from 'antd';
import Card from "antd/es/card";
import {message} from "antd/es";
import Title from "antd/es/typography/Title";
import './login.less'
import Row from "antd/es/grid/row";
import Constants, {getRes} from "../../utils/constants";
import axios from "axios";

const md5 = require('md5');

const layout = {
    labelCol: {span: 8},
    wrapperCol: {span: 8},
};

const {Content, Footer} = Layout;

type LoginState = {
    userName: string,
    password: string
}

const Login = () => {

    const [logging, setLogging] = useState<boolean>(false);
    const [loginState, setLoginState] = useState<LoginState>({userName: "", password: ""})

    const setValue = (value: LoginState) => {
        setLoginState(value);
    }

    const onFinish = (allValues: any) => {
        const loginForm = {
            userName: allValues.userName,
            password: md5(allValues.password),
            https: window.location.protocol === "https:"
        };
        setLogging(true);
        axios.post("/api/admin/login", loginForm).then(({data}) => {
            if (data.error) {
                message.error(data.message).then(() => {
                    //ignore
                });
            } else {
                const query = new URLSearchParams(window.location.search);
                if (query.get("redirectFrom") !== null && query.get("redirectFrom") !== '') {
                    const jumpTo = decodeURIComponent(query.get("redirectFrom") + "");
                    //need reload page, for iframe (maybe)
                    if (jumpTo.startsWith(new URL(document.baseURI).pathname + "admin/plugins/download")) {
                        window.location.href = jumpTo;
                    } else {
                        Constants.getHistory().push(jumpTo);
                    }
                } else {
                    Constants.getHistory().push("./index");
                }
            }
        }).finally(() => {
            setLogging(false);
        })
    };

    return (
        <Layout>
            <Content style={{minWidth: "100%"}}>
                <Card className='login-container'
                      cover={
                          <div className='dimback'>
                              <div
                                  className='dim'
                                  style={{
                                      backgroundImage: "url('./admin/vendors/images/login-bg.jpg')",
                                      height: "120px"
                                  }}>
                              </div>
                              <Title level={3}
                                     style={{color: "#fff"}}>{getRes()['userNameAndPassword']}</Title>
                          </div>}
                      style={{textAlign: "center"}}>
                    <Form
                        {...layout}
                        initialValues={getRes().defaultLoginInfo}
                        onFinish={(values) => onFinish(values)}
                        onValuesChange={(_k, v) => setValue(v)}
                    >
                        <Form.Item
                            label={getRes().userName}
                            name="userName"
                            rules={[{required: true}]}
                        >
                            <Input value={loginState.userName}/>
                        </Form.Item>

                        <Form.Item
                            label={getRes().password}
                            name="password"
                            rules={[{required: true}]}
                        >
                            <Input.Password value={loginState.password}/>
                        </Form.Item>

                        <Row style={{alignItems: 'center', display: "flex"}}>
                            <Col xxl={24} xs={24}>
                                <Button loading={logging} type="primary" htmlType='submit'>
                                    <LoginOutlined/> {getRes().login}
                                </Button>
                            </Col>
                        </Row>
                    </Form>
                </Card>
            </Content>
            <Footer
                style={{textAlign: 'center'}}>{getRes().copyrightCurrentYear} {getRes().websiteTitle}.
                All Rights Reserved.</Footer>
        </Layout>
    )
}

export default Login;
