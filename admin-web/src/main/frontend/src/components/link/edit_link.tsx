import {FunctionComponent, useState} from "react";
import {Col, Form, Input, InputNumber, Modal} from "antd";
import Row from "antd/es/grid/row";
import TextArea from "antd/lib/input/TextArea";
import {EditOutlined} from "@ant-design/icons";
import {Link} from "react-router-dom";
import axios from "axios";

const layout = {
    labelCol: {span: 4},
    wrapperCol: {span: 20},
};

export type EditLinkProps = {
    record: any,
    editSuccessCall: () => void
}


const EditLink: FunctionComponent<EditLinkProps> = ({record, editSuccessCall}) => {

    const [showModel, setShowModel] = useState<boolean>(false);
    const [updateForm, setUpdateForm] = useState<any>(record);

    const handleOk = () => {
        axios.post("/api/admin/link/update", updateForm).then(() => {
            setShowModel(false);
            if (editSuccessCall) {
                editSuccessCall();
            }
        });
    };


    const setValue = (changedValues: any) => {
        setUpdateForm(changedValues);
    }


    return (
        <>
            <Link to={"#"}><EditOutlined onClick={() => setShowModel(true)} style={{marginBottom: 8}}/></Link>
            <Modal
                title='编辑'
                visible={showModel}
                onOk={handleOk}
                onCancel={() => setShowModel(false)}
            >
                <Form initialValues={record}
                      onValuesChange={(_k, v) => setValue(v)}
                      {...layout}>
                    <Form.Item name='id' style={{display: "none"}}>
                        <Input hidden={true}/>
                    </Form.Item>
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
                                label='网站名称'
                                style={{marginBottom: 8}}
                                name="linkName"
                                rules={[{required: true, message: ''}]}>
                                <Input/>
                            </Form.Item>
                        </Col>
                    </Row>
                    <Row>
                        <Col span={24}>
                            <Form.Item
                                label='描述'
                                style={{marginBottom: 8}}
                                name="alt"
                                rules={[{required: true, message: ''}]}>
                                <TextArea/>
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
export default EditLink;