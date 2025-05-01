import Divider from "antd/es/divider";
import Form from "antd/es/form";
import { Input, message, Row } from "antd";
import Button from "antd/es/button";
import Col from "antd/es/grid/col";
import { getRes } from "../utils/constants";
import { useAxiosBaseInstance } from "../base/AppBase";
import BaseTitle from "../base/BaseTitle";

const layout = {
    labelCol: { span: 8 },
    wrapperCol: { span: 16 },
};

const UserUpdatePassword = ({ offline }: { offline: boolean }) => {
    const [messageApi, contextHolder] = message.useMessage({ maxCount: 3 });

    const axiosInstance = useAxiosBaseInstance();
    const onFinish = (allValues: Record<string, any>) => {
        axiosInstance.post("/api/admin/user/updatePassword", allValues).then(async ({ data }) => {
            if (data.error) {
                await messageApi.error(data.message);
            } else if (data.error === 0) {
                await messageApi.success(data.message);
            }
        });
    };

    return (
        <>
            {contextHolder}
            <BaseTitle title={getRes()["admin.changePwd"]} />
            <Row>
                <Col style={{ maxWidth: 600 }} xs={24}>
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
                        <Button disabled={offline} type="primary" htmlType="submit">
                            {getRes().submit}
                        </Button>
                    </Form>
                </Col>
            </Row>
        </>
    );
};

export default UserUpdatePassword;
