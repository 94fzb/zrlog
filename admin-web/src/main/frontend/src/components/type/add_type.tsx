import { useState } from "react";
import { Button, Col, Form, Input, message, Modal } from "antd";
import Row from "antd/es/grid/row";
import TextArea from "antd/es/input/TextArea";
import { getRes } from "../../utils/constants";
import { useAxiosBaseInstance } from "../../base/AppBase";

const layout = {
    labelCol: { span: 4 },
    wrapperCol: { span: 20 },
};

const AddType = ({ addSuccessCall, offline }: { addSuccessCall: () => void; offline: boolean }) => {
    const [showModel, setShowModel] = useState<boolean>(false);
    const [form, setForm] = useState<any>();
    const [messageApi, contextHolder] = message.useMessage({ maxCount: 3 });

    const axiosInstance = useAxiosBaseInstance();
    const handleOk = () => {
        axiosInstance.post("/api/admin/type/add", form).then(async ({ data }) => {
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
                                label={getRes()["admin.type.manage"]}
                                style={{ marginBottom: 8 }}
                                name="typeName"
                                rules={[{ required: true, message: "" }]}
                            >
                                <Input />
                            </Form.Item>
                        </Col>
                    </Row>
                    <Row>
                        <Col span={24}>
                            <Form.Item
                                label={getRes()["alias"]}
                                style={{ marginBottom: 8 }}
                                name="alias"
                                rules={[{ required: true, message: "" }]}
                            >
                                <Input />
                            </Form.Item>
                        </Col>
                    </Row>
                    <Row>
                        <Col span={24}>
                            <Form.Item
                                label={getRes()["introduction"]}
                                style={{ marginBottom: 8 }}
                                name="remark"
                                rules={[{ required: true, message: "" }]}
                            >
                                <TextArea />
                            </Form.Item>
                        </Col>
                    </Row>
                </Form>
            </Modal>
        </>
    );
};
export default AddType;
