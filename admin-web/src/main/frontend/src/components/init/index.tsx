import {Content} from "antd/es/layout/layout";
import Card from "antd/es/card";
import {Button, Col, Form, Input} from "antd";
import {getBackendServerUrl, isStaticPage, setBackendServerUrl} from "../../utils/constants";
import Row from "antd/es/grid/row";
import {LoginOutlined} from "@ant-design/icons";
import {classes, LoginBg, StyledLoginPage} from "../login";
import {FunctionComponent} from "react";

const layout = {
    labelCol: {span: 8},
    wrapperCol: {span: 14},
};

type InitProps = {
    onSubmit: () => void;
}

const Init: FunctionComponent<InitProps> = ({onSubmit}) => {

    //const navigate = useNavigate();

    return (
        <StyledLoginPage>
            <Content className={classes.content}>
                <Card className={classes.card} cover={<LoginBg/>} styles={{
                    body: {
                        margin: 16,
                    },
                }}>
                    <Form
                        {...layout}
                        initialValues={{
                            "backendServerUrl": getBackendServerUrl()
                        }}
                    >
                        {isStaticPage() && <Form.Item label={"Getaway URL"}
                                                      name={"backendServerUrl"}
                                                      rules={[{required: true}]}>
                            <Input onChange={(e) => setBackendServerUrl(e.target.value)}/>
                        </Form.Item>}
                        <Row style={{alignItems: "center", display: "flex"}}>
                            <Col xxl={24} xs={24}>
                                <Button disabled={false} onClick={() => {
                                    onSubmit()
                                }} onSubmit={() => {
                                    onSubmit();
                                }} type="primary" style={{minWidth: 108}}
                                        htmlType="submit">
                                    <LoginOutlined/> {"Next"}
                                </Button>
                            </Col>
                        </Row>
                    </Form>
                </Card>
            </Content>
        </StyledLoginPage>
    )
}

export default Init