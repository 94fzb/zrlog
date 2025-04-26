import {useEffect, useState} from "react";
import {LoginOutlined} from "@ant-design/icons";
import {Button, Col, Form, Input, Layout, message} from "antd";
import Card from "antd/es/card";
import Row from "antd/es/grid/row";
import {getColorPrimary, getRes} from "../../utils/constants";
import axios from "axios";
import {useNavigate} from "react-router-dom";
import Title from "antd/es/typography/Title";
import {ssData} from "../../index";
import PWAHandler from "../../PWAHandler";
import {removeAllCaches} from "../../cache";
import styled from "styled-components";

const md5 = require("md5");

const PREFIX = "login";

const classes = {
    bg: `${PREFIX}-bg`,
    title: `${PREFIX}-title`,
    card: `${PREFIX}-card`,
    content: `${PREFIX}-content`,
    copyrightTips: `${PREFIX}-copyrightTips`
}

const StyledLoginPage = styled(Layout)({
    height: "100vh",
    [`& .${classes.bg}`]: {
        objectFit: "cover",
        opacity: 0.6,
        filter: "blur(1px)",
        width: "100%",
        display: "flex",
        flexDirection: "column",
        borderRadius: "8px 8px 0 0",
        alignItems: "center",
        textAlign: "center",
        backgroundSize: "cover",
        height: "180px",
    },
    [`& .${classes.title}`]: {
        color: "#fff",
        background: getColorPrimary(),
        margin: 0,
        width: "100%",
        position: "absolute",
        bottom: 0,
        whiteSpace: "nowrap",
        overflow: "hidden",
        textOverflow: "ellipsis",
        paddingLeft: 12,
        paddingRight: 12,
    },
    [`& .${classes.card}`]: {
        textAlign: "center",
        width: "100%",
        marginRight: "auto",
        marginLeft: "auto",
        maxWidth: "660px",
    },
    [`& .${classes.content}`]: {
        minWidth: "100%",
        display: "flex",
        alignItems: "center",
        paddingRight: 24,
        paddingLeft: 24,
    },
    [`& .${classes.copyrightTips}`]: {
        textAlign: "center",
        whiteSpace: "nowrap",
        overflow: "hidden",
        textOverflow: "ellipsis",
    }
})

const layout = {
    labelCol: {span: 8},
    wrapperCol: {span: 8},
};

const {Content, Footer} = Layout;

type LoginState = {
    userName: string;
    password: string;
};

const Index = ({offline}: { offline: boolean }) => {
    const [logging, setLogging] = useState<boolean>(false);
    const [loginState, setLoginState] = useState<LoginState>({
        userName: "",
        password: "",
    });

    const [messageApi, contextHolder] = message.useMessage({maxCount: 3});

    const navigate = useNavigate();

    const setValue = (value: LoginState) => {
        setLoginState(value);
    };

    const onFinish = async (allValues: any) => {
        setLogging(true);
        const loginForm = {
            userName: allValues.userName,
            password: md5(allValues.password),
            https: window.location.protocol === "https:",
        };
        try {
            const {data} = await axios.post("/api/admin/login", loginForm);
            if (data.error) {
                await messageApi.error(data.message);
            } else if (data.error == 0) {
                const query = new URLSearchParams(window.location.search);
                if (ssData) {
                    ssData.key = data.data.key;
                }
                const redirectFrom = query.get("redirectFrom") as string;
                if (redirectFrom !== null && redirectFrom !== "") {
                    const jumpTo = decodeURIComponent(redirectFrom);
                    //处理非 https iframe 跳转登录问题
                    if (jumpTo.startsWith("/admin/plugins/")) {
                        window.location.href = window.location.protocol + "//" + window.location.host + jumpTo;
                    } else {
                        navigate(jumpTo.replace("/admin", ""), {replace: true});
                    }
                } else {
                    navigate("/index", {replace: true});
                }
            } else {
                await messageApi.error("未知错误");
            }
        } finally {
            setLogging(false);
        }
    };

    useEffect(() => {
        removeAllCaches();
    }, []);

    return (
        <PWAHandler>
            {contextHolder}
            <StyledLoginPage>
                <Content className={classes.content}>
                    <Card
                        cover={
                            <div style={{textAlign: "center", position: "relative"}}>
                                <img
                                    alt={"bg"}
                                    src={`data:image/png;base64,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`}
                                    className={classes.bg}
                                />
                                <Title level={3} className={classes.title}>
                                    {getRes()["userNameAndPassword"]}
                                </Title>
                            </div>
                        }
                        className={classes.card}
                        styles={{
                            body: {
                                margin: 16,
                            },
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
                                    <Button disabled={offline} loading={logging} type="primary" style={{minWidth: 108}}
                                            htmlType="submit">
                                        <LoginOutlined/> {getRes().login}
                                    </Button>
                                </Col>
                            </Row>
                        </Form>
                    </Card>
                </Content>
                <Footer className={classes.copyrightTips}>
                    {getRes().copyrightCurrentYear} {getRes().websiteTitle}. All Rights Reserved.
                </Footer>
            </StyledLoginPage>
        </PWAHandler>
    );
};

export default Index;
