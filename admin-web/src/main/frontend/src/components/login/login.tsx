import {useState} from "react";
import {LoginOutlined} from "@ant-design/icons";
import {Button, Col, Form, Input, Layout} from "antd";
import Card from "antd/es/card";
import {message} from "antd/es";
import Row from "antd/es/grid/row";
import {getRes} from "../../utils/constants";
import axios from "axios";
import {useNavigate} from "react-router-dom";
import Title from "antd/es/typography/Title";

const md5 = require("md5");

const layout = {
    labelCol: {span: 8},
    wrapperCol: {span: 8},
};

const {Content, Footer} = Layout;

type LoginState = {
    userName: string;
    password: string;
};

const Login = () => {
    const [logging, setLogging] = useState<boolean>(false);
    const [loginState, setLoginState] = useState<LoginState>({
        userName: "",
        password: "",
    });

    const [messageApi, contextHolder] = message.useMessage();


    const navigate = useNavigate();

    const setValue = (value: LoginState) => {
        setLoginState(value);
    };

    const onFinish = (allValues: any) => {
        const loginForm = {
            userName: allValues.userName,
            password: md5(allValues.password),
            https: window.location.protocol === "https:",
        };
        setLogging(true);
        axios
            .post("/api/admin/login", loginForm)
            .then(({data}) => {
                if (data.error) {
                    messageApi.error(data.message).then(() => {
                        //ignore
                    });
                } else {
                    const query = new URLSearchParams(window.location.search);
                    if (query.get("redirectFrom") !== null && query.get("redirectFrom") !== "") {
                        //need reload page, because basename error
                        window.location.href = decodeURIComponent(query.get("redirectFrom") + "");
                    } else {
                        navigate("/index", {replace: true});
                    }
                }
            })
            .finally(() => {
                setLogging(false);
            });
    };

    return (
        <Layout style={{height: "100vh", color: "#314659"}}>
            {contextHolder}
            <Content style={{minWidth: "100%"}}>
                <Card
                    cover={
                        <div style={{background: "rgba(54, 84, 99, 0.7)", textAlign: "center"}}>
                            <div
                                style={{
                                    opacity: 0.6,
                                    filter: "alpha(opacity=60)",
                                    width: "100%",
                                    fontSize: "30px",
                                    position: "relative",
                                    display: "flex",
                                    flexWrap: "wrap",
                                    flexDirection: "column",
                                    alignItems: "center",
                                    textAlign: "center",
                                    backgroundSize: "cover",
                                    background: "no-repeat center",
                                    backgroundImage: "url('./admin/vendors/images/login-bg.jpg')",
                                    height: "120px",
                                }}
                            ></div>
                            <Title level={3} style={{color: "#fff", margin: 0}}>
                                {getRes()["userNameAndPassword"]}
                            </Title>
                        </div>
                    }
                    style={{
                        textAlign: "center",
                        width: "100%",
                        marginTop: "20vh",
                        marginRight: "auto",
                        marginLeft: "auto",
                        maxWidth: "660px",
                    }}
                >
                    <Form
                        {...layout}
                        initialValues={getRes().defaultLoginInfo}
                        onFinish={(values) => onFinish(values)}
                        onValuesChange={(_k, v) => setValue(v)}
                    >
                        <Form.Item label={getRes().userName} name="userName" rules={[{required: true}]}>
                            <Input value={loginState.userName}/>
                        </Form.Item>

                        <Form.Item label={getRes().password} name="password" rules={[{required: true}]}>
                            <Input.Password value={loginState.password}/>
                        </Form.Item>

                        <Row style={{alignItems: "center", display: "flex"}}>
                            <Col xxl={24} xs={24}>
                                <Button loading={logging} type="primary" htmlType="submit">
                                    <LoginOutlined/> {getRes().login}
                                </Button>
                            </Col>
                        </Row>
                    </Form>
                </Card>
            </Content>
            <Footer style={{textAlign: "center"}}>
                {getRes().copyrightCurrentYear} {getRes().websiteTitle}. All Rights Reserved.
            </Footer>
        </Layout>
    );
};

export default Login;
