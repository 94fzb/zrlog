import { useState } from "react";
import { Button, Col, Form, Input, InputNumber, message, Modal } from "antd";
import Row from "antd/es/grid/row";
import { getRes } from "../../utils/constants";
import { useAxiosBaseInstance } from "../../base/AppBase";

const layout = {
    labelCol: { span: 4 },
    wrapperCol: { span: 20 },
};

const AddNav = ({ addSuccessCall, offline }: { offline: boolean; addSuccessCall: () => void }) => {
    const [showModel, setShowModel] = useState<boolean>(false);
    const [form, setForm] = useState<any>();
    const [messageApi, contextHolder] = message.useMessage({ maxCount: 3 });
    const axiosInstance = useAxiosBaseInstance();
    const handleOk = () => {
        axiosInstance.post("/api/admin/nav/add", form).then(async ({ data }) => {
            if (data.error) {
                await messageApi.error(data.message);
                return;
            }

            if (data.error === 0) {
                setShowModel(false);
                addSuccessCall();
            }
        });
    };

    const setValue = (changedValues: any) => {
        setForm(changedValues);
    };

    return (
        <>
            {contextHolder}
            <Button type="primary" disabled={offline} onClick={() => setShowModel(true)} style={{ marginBottom: 8 }}>
                {getRes()["add"]}
            </Button>
            <Modal title={getRes()["add"]} open={showModel} onOk={handleOk} onCancel={() => setShowModel(false)}>
                <Form onValuesChange={(_k, v) => setValue(v)} {...layout}>
                    <Row>
                        <Col span={24}>
                            <Form.Item
                                label={getRes()["admin.link.manage"]}
                                style={{ marginBottom: 8 }}
                                name="url"
                                rules={[{ required: true, message: "" }]}
                            >
                                <Input />
                            </Form.Item>
                        </Col>
                    </Row>
                    <Row>
                        <Col span={24}>
                            <Form.Item
                                label={getRes()["admin.nav.name"]}
                                style={{ marginBottom: 8 }}
                                name="navName"
                                rules={[{ required: true, message: "" }]}
                            >
                                <Input />
                            </Form.Item>
                        </Col>
                    </Row>
                    <Row>
                        <Col span={24}>
                            <Form.Item
                                label={getRes()["order"]}
                                style={{ marginBottom: 8 }}
                                name="sort"
                                rules={[{ required: true, message: "" }]}
                            >
                                <InputNumber />
                            </Form.Item>
                        </Col>
                    </Row>
                </Form>
            </Modal>
        </>
    );
};

export default AddNav;
