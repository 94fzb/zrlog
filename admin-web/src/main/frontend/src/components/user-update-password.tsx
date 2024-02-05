import Title from "antd/es/typography/Title";
import Divider from "antd/es/divider";
import Form from "antd/es/form";
import { Input, Row } from "antd";
import Button from "antd/es/button";
import { message } from "antd/es";
import Col from "antd/es/grid/col";
import { getRes } from "../utils/constants";
import axios from "axios";

const layout = {
    labelCol: { span: 8 },
    wrapperCol: { span: 16 },
};

const UserUpdatePassword = () => {
    const [messageApi, contextHolder] = message.useMessage();

    const onFinish = (allValues: Record<string, any>) => {
        axios.post("/api/admin/user/updatePassword", allValues).then(({ data }) => {
            if (data.error) {
                messageApi.error(data.message).then(() => {
                    //ignore
                });
            } else {
                messageApi.success(data.message).then(() => {
                    //ignore
                });
            }
        });
    };

    return (
        <>
            {contextHolder}
            <Title className="page-header" level={3}>
                {getRes()["admin.changePwd"]}
            </Title>
            <Divider />
            <Row>
                <Col md={12} xs={24}>
                    <Form {...layout} onFinish={(value) => onFinish(value)}>
                        <Form.Item
                            name="oldPassword"
                            label={getRes()["admin.oldPassword"]}
                            rules={[{ required: true }]}
                        >
                            <Input.Password />
                        </Form.Item>
                        <Form.Item
                            name="newPassword"
                            label={getRes()["admin.newPassword"]}
                            rules={[{ required: true }]}
                        >
                            <Input.Password />
                        </Form.Item>
                        <Divider />
                        <Button type="primary" htmlType="submit">
                            {getRes().submit}
                        </Button>
                    </Form>
                </Col>
            </Row>
        </>
    );
};

export default UserUpdatePassword;
