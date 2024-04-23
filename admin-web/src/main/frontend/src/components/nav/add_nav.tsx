import { useState } from "react";
import { App, Button, Col, Form, Input, InputNumber, Modal } from "antd";
import Row from "antd/es/grid/row";
import axios from "axios";
import { getRes } from "../../utils/constants";

const layout = {
    labelCol: { span: 4 },
    wrapperCol: { span: 20 },
};

const AddNav = ({ addSuccessCall }: { addSuccessCall: () => void }) => {
    const [showModel, setShowModel] = useState<boolean>(false);
    const [form, setForm] = useState<any>();
    const { message } = App.useApp();

    const handleOk = () => {
        axios.post("/api/admin/nav/add", form).then(async ({ data }) => {
            if (data.error) {
                await message.error(data.message);
                return;
            }
            setShowModel(false);
            addSuccessCall();
        });
    };

    const setValue = (changedValues: any) => {
        setForm(changedValues);
    };

    return (
        <>
            <Button type="primary" onClick={() => setShowModel(true)} style={{ marginBottom: 8 }}>
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
                                label="导航名称"
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
                                label="排序"
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
