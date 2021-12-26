import {useState} from "react";
import {Button, Col, Form, Input, Modal} from "antd";
import Row from "antd/es/grid/row";
import TextArea from "antd/lib/input/TextArea";
import axios from "axios";

const layout = {
    labelCol: {span: 4},
    wrapperCol: {span: 20},
};

const AddType = ({addSuccessCall}: { addSuccessCall: () => void }) => {

    const [showModel, setShowModel] = useState<boolean>(false);
    const [form, setForm] = useState<any>();

    const handleOk = () => {
        axios.post("/api/admin/type/add", form).then(() => {
            setShowModel(false);
            addSuccessCall();
        })
    };

    const setValue = (changedValues: any) => {
        setForm(changedValues)
    }
    return (
        <>
            <Button type="primary" onClick={() => setShowModel(true)} style={{marginBottom: 8}}>
                添加
            </Button>
            <Modal
                title='添加'
                visible={showModel}
                onOk={handleOk}
                onCancel={() => setShowModel(false)}
            >
                <Form onValuesChange={(_k, v) => setValue(v)}
                      {...layout}>
                    <Row>
                        <Col span={24}>
                            <Form.Item
                                label='名称分类'
                                style={{marginBottom: 8}}
                                name="typeName"
                                rules={[{required: true, message: ''}]}>
                                <Input/>
                            </Form.Item>
                        </Col>
                    </Row>
                    <Row>
                        <Col span={24}>
                            <Form.Item
                                label='别名'
                                style={{marginBottom: 8}}
                                name="alias"
                                rules={[{required: true, message: ''}]}>
                                <Input/>
                            </Form.Item>
                        </Col>
                    </Row>
                    <Row>
                        <Col span={24}>
                            <Form.Item
                                label='介绍'
                                style={{marginBottom: 8}}
                                name="remark"
                                rules={[{required: true, message: ''}]}>
                                <TextArea/>
                            </Form.Item>
                        </Col>
                    </Row>
                </Form>
            </Modal>
        </>
    );
}
export default AddType;