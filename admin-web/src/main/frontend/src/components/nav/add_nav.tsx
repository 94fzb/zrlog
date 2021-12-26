import {useState} from "react";
import {Button, Col, Form, Input, InputNumber, Modal} from "antd";
import Row from "antd/es/grid/row";
import axios from "axios";

const layout = {
    labelCol: {span: 4},
    wrapperCol: {span: 20},
};

const AddNav = ({addSuccessCall}: { addSuccessCall: () => void }) => {

    const [showModel, setShowModel] = useState<boolean>(false);
    const [form, setForm] = useState<any>();

    const handleOk = () => {
        axios.post("/api/admin/nav/add", form).then(() => {
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
                                label='链接'
                                style={{marginBottom: 8}}
                                name="url"
                                rules={[{required: true, message: ''}]}>
                                <Input/>
                            </Form.Item>
                        </Col>
                    </Row>
                    <Row>
                        <Col span={24}>
                            <Form.Item
                                label='导航名称'
                                style={{marginBottom: 8}}
                                name="navName"
                                rules={[{required: true, message: ''}]}>
                                <Input/>
                            </Form.Item>
                        </Col>
                    </Row>
                    <Row>
                        <Col span={24}>
                            <Form.Item
                                label='排序'
                                style={{marginBottom: 8}}
                                name="sort"
                                rules={[{required: true, message: ''}]}>
                                <InputNumber/>
                            </Form.Item>
                        </Col>
                    </Row>
                </Form>
            </Modal>
        </>
    );
}

export default AddNav;
