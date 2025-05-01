import { FunctionComponent, useEffect, useState } from "react";
import { Col, Form, Input, InputNumber, message, Modal } from "antd";
import Row from "antd/es/grid/row";
import TextArea from "antd/es/input/TextArea";
import { EditOutlined } from "@ant-design/icons";
import { Link } from "react-router-dom";
import { getColorPrimary, getRes } from "../../utils/constants";
import { useAxiosBaseInstance } from "../../base/AppBase";

const layout = {
    labelCol: { span: 4 },
    wrapperCol: { span: 20 },
};

export type EditLinkProps = {
    record: any;
    editSuccessCall: () => void;
    offline: boolean;
};

const EditLink: FunctionComponent<EditLinkProps> = ({ record, editSuccessCall, offline }) => {
    const [showModel, setShowModel] = useState<boolean>(false);
    const [updateForm, setUpdateForm] = useState<any>(record);
    const [messageApi, contextHolder] = message.useMessage({ maxCount: 3 });
    const axiosInstance = useAxiosBaseInstance();
    const handleOk = () => {
        axiosInstance.post("/api/admin/link/update", updateForm).then(async ({ data }) => {
            if (data.error) {
                await messageApi.error(data.message);
                return;
            }
            if (data.error === 0) {
                setShowModel(false);
                if (editSuccessCall) {
                    editSuccessCall();
                }
            }
        });
    };

    useEffect(() => {
        setUpdateForm(record);
    }, [record]);

    const setValue = (changedValues: any) => {
        setUpdateForm(changedValues);
    };

    return (
        <>
            {contextHolder}
            <Link
                to={"#edit-" + record.id}
                onClick={(e) => {
                    e.preventDefault();
                    e.stopPropagation();
                    if (offline) {
                        return;
                    }
                    setShowModel(true);
                }}
            >
                <EditOutlined style={{ color: getColorPrimary() }} />
            </Link>
            <Modal title={getRes()["edit"]} open={showModel} onOk={handleOk} onCancel={() => setShowModel(false)}>
                <Form initialValues={updateForm} onValuesChange={(_k, v) => setValue(v)} {...layout}>
                    <Form.Item name="id" style={{ display: "none" }}>
                        <Input hidden={true} />
                    </Form.Item>
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
                                label={getRes()["admin.link.name"]}
                                style={{ marginBottom: 8 }}
                                name="linkName"
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
                                name="alt"
                                rules={[{ required: true, message: "" }]}
                            >
                                <TextArea />
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
export default EditLink;
