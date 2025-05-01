import { Content } from "antd/es/layout/layout";
import Card from "antd/es/card";
import { Button, Col, Form, Input } from "antd";
import { getBackendServerUrl, getColorPrimary } from "../../utils/constants";
import Row from "antd/es/grid/row";
import { LoginOutlined } from "@ant-design/icons";
import { classes, LoginBg, StyledLoginPage } from "../login";
import { FunctionComponent, useState } from "react";
import zh_CN from "antd/es/locale/zh_CN";
import en_US from "antd/es/locale/en_US";

const layout = {
    labelCol: { span: 8 },
    wrapperCol: { span: 14 },
};

type InitProps = {
    lang: string;
    onSubmit: (backendServerUrl: string) => void;
};

const Init: FunctionComponent<InitProps> = ({ onSubmit, lang }) => {
    const getNextDesc = () => {
        if (lang === "zh_CN") {
            return zh_CN.Tour?.Next;
        }
        return en_US.Tour?.Next;
    };

    const getGetaWayUrlDesc = () => {
        if (lang === "zh_CN") {
            return "网关 URL";
        }
        return "Getaway URL";
    };

    const defaultUrl = getBackendServerUrl() != "/" ? getBackendServerUrl() : window.location.origin;
    const [url, setUrl] = useState<string>(defaultUrl);

    const onOk = () => {
        if (url.endsWith("/")) {
            onSubmit(url);
            return;
        }
        onSubmit(url + "/");
    };

    return (
        <StyledLoginPage mainColor={getColorPrimary()}>
            <Content className={classes.content}>
                <Card
                    className={classes.card}
                    cover={<LoginBg />}
                    styles={{
                        body: {
                            margin: 16,
                        },
                    }}
                >
                    <Form
                        {...layout}
                        initialValues={{
                            backendServerUrl: url,
                        }}
                        onFinish={() => {
                            onOk();
                        }}
                        onValuesChange={(e) => {
                            setUrl(e["backendServerUrl"]);
                        }}
                    >
                        <Form.Item label={getGetaWayUrlDesc()} name={"backendServerUrl"} rules={[{ required: true }]}>
                            <Input />
                        </Form.Item>
                        <Row style={{ alignItems: "center", display: "flex" }}>
                            <Col xxl={24} xs={24}>
                                <Button type="primary" style={{ minWidth: 108 }} htmlType="submit">
                                    <LoginOutlined /> {getNextDesc()}
                                </Button>
                            </Col>
                        </Row>
                    </Form>
                </Card>
            </Content>
        </StyledLoginPage>
    );
};

export default Init;
