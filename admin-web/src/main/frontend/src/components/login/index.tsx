import { useState } from "react";
import { LoginOutlined } from "@ant-design/icons";
import { Button, Col, Form, Input, Layout, App } from "antd";
import Card from "antd/es/card";
import Row from "antd/es/grid/row";
import { getColorPrimary, getRes } from "../../utils/constants";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import Title from "antd/es/typography/Title";

const md5 = require("md5");

const layout = {
    labelCol: { span: 8 },
    wrapperCol: { span: 8 },
};

const { Content, Footer } = Layout;

type LoginState = {
    userName: string;
    password: string;
};

const Index = () => {
    const [logging, setLogging] = useState<boolean>(false);
    const [loginState, setLoginState] = useState<LoginState>({
        userName: "",
        password: "",
    });

    const { message } = App.useApp();

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
            .then(({ data }) => {
                if (data.error) {
                    message.error(data.message).then(() => {
                        //ignore
                    });
                } else {
                    const query = new URLSearchParams(window.location.search);
                    if (query.get("redirectFrom") !== null && query.get("redirectFrom") !== "") {
                        //need reload page, because basename error
                        window.location.href = decodeURIComponent(query.get("redirectFrom") + "");
                    } else {
                        navigate("/index", { replace: true });
                    }
                }
            })
            .finally(() => {
                setLogging(false);
            });
    };

    return (
        <Layout style={{ height: "100vh" }}>
            <Content
                style={{ minWidth: "100%", display: "flex", alignItems: "center", paddingRight: 24, paddingLeft: 24 }}
            >
                <Card
                    cover={
                        <div style={{ textAlign: "center" }}>
                            <img
                                alt={"bg"}
                                src={`data:image/jpeg;base64,/9j/4QAYRXhpZgAASUkqAAgAAAAAAAAAAAAAAP/sABFEdWNreQABAAQAAAA8AAD/4QMraHR0cDovL25zLmFkb2JlLmNvbS94YXAvMS4wLwA8P3hwYWNrZXQgYmVnaW49Iu+7vyIgaWQ9Ilc1TTBNcENlaGlIenJlU3pOVGN6a2M5ZCI/PiA8eDp4bXBtZXRhIHhtbG5zOng9ImFkb2JlOm5zOm1ldGEvIiB4OnhtcHRrPSJBZG9iZSBYTVAgQ29yZSA1LjMtYzAxMSA2Ni4xNDU2NjEsIDIwMTIvMDIvMDYtMTQ6NTY6MjcgICAgICAgICI+IDxyZGY6UkRGIHhtbG5zOnJkZj0iaHR0cDovL3d3dy53My5vcmcvMTk5OS8wMi8yMi1yZGYtc3ludGF4LW5zIyI+IDxyZGY6RGVzY3JpcHRpb24gcmRmOmFib3V0PSIiIHhtbG5zOnhtcD0iaHR0cDovL25zLmFkb2JlLmNvbS94YXAvMS4wLyIgeG1sbnM6eG1wTU09Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9tbS8iIHhtbG5zOnN0UmVmPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvc1R5cGUvUmVzb3VyY2VSZWYjIiB4bXA6Q3JlYXRvclRvb2w9IkFkb2JlIFBob3Rvc2hvcCBDUzYgKFdpbmRvd3MpIiB4bXBNTTpJbnN0YW5jZUlEPSJ4bXAuaWlkOjE1NDg4QTlERTNCNDExRTdBMTQ2OTgyMzI1N0M4MEQ3IiB4bXBNTTpEb2N1bWVudElEPSJ4bXAuZGlkOjE1NDg4QTlFRTNCNDExRTdBMTQ2OTgyMzI1N0M4MEQ3Ij4gPHhtcE1NOkRlcml2ZWRGcm9tIHN0UmVmOmluc3RhbmNlSUQ9InhtcC5paWQ6MTU0ODhBOUJFM0I0MTFFN0ExNDY5ODIzMjU3QzgwRDciIHN0UmVmOmRvY3VtZW50SUQ9InhtcC5kaWQ6MTU0ODhBOUNFM0I0MTFFN0ExNDY5ODIzMjU3QzgwRDciLz4gPC9yZGY6RGVzY3JpcHRpb24+IDwvcmRmOlJERj4gPC94OnhtcG1ldGE+IDw/eHBhY2tldCBlbmQ9InIiPz7/7gAOQWRvYmUAZMAAAAAB/9sAhAAGBAQEBQQGBQUGCQYFBgkLCAYG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`}
                                style={{
                                    objectFit: "cover",
                                    opacity: 0.6,
                                    filter: "alpha(opacity=60)",
                                    width: "100%",
                                    position: "relative",
                                    display: "flex",
                                    flexDirection: "column",
                                    alignItems: "center",
                                    textAlign: "center",
                                    backgroundSize: "cover",
                                    height: "120px",
                                }}
                            />
                            <Title level={3} style={{ color: "#fff", background: getColorPrimary(), margin: 0 }}>
                                {getRes()["userNameAndPassword"]}
                            </Title>
                        </div>
                    }
                    style={{
                        textAlign: "center",
                        width: "100%",
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
                        <Form.Item label={getRes().userName} name="userName" rules={[{ required: true }]}>
                            <Input value={loginState.userName} />
                        </Form.Item>

                        <Form.Item label={getRes().password} name="password" rules={[{ required: true }]}>
                            <Input.Password value={loginState.password} />
                        </Form.Item>

                        <Row style={{ alignItems: "center", display: "flex" }}>
                            <Col xxl={24} xs={24}>
                                <Button loading={logging} type="primary" htmlType="submit">
                                    <LoginOutlined /> {getRes().login}
                                </Button>
                            </Col>
                        </Row>
                    </Form>
                </Card>
            </Content>
            <Footer style={{ textAlign: "center" }}>
                {getRes().copyrightCurrentYear} {getRes().websiteTitle}. All Rights Reserved.
            </Footer>
        </Layout>
    );
};

export default Index;
