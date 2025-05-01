import { FunctionComponent, useEffect, useState } from "react";
import { Col, Form, Input, message, Modal } from "antd";
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

export type EditTypeProps = {
    record: any;
    editSuccessCall: () => void;
    offline: boolean;
};

const EditType: FunctionComponent<EditTypeProps> = ({ record, editSuccessCall, offline }) => {
    const [showModel, setShowModel] = useState<boolean>(false);
    const [updateForm, setUpdateForm] = useState<any>(record);
    const [messageApi, contextHolder] = message.useMessage({ maxCount: 3 });

    const axiosInstance = useAxiosBaseInstance();
    const handleOk = () => {
        axiosInstance.post("/api/admin/type/update", updateForm).then(async ({ data }) => {
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

    const setValue = (changedValues: any) => {
        setUpdateForm(changedValues);
    };

    useEffect(() => {
        setUpdateForm(record);
    }, [record]);

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
export default EditType;
