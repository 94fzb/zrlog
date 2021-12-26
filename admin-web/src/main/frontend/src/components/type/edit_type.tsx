import {FunctionComponent, useState} from "react";
import {Col, Form, Input, Modal} from "antd";
import Row from "antd/es/grid/row";
import TextArea from "antd/lib/input/TextArea";
import {EditOutlined} from "@ant-design/icons";
import {Link} from "react-router-dom";
import axios from "axios";

const layout = {
    labelCol: {span: 4},
    wrapperCol: {span: 20},
};


export type EditTypeProps = {
    record: any,
    editSuccessCall: () => void
}


const EditType: FunctionComponent<EditTypeProps> = ({record, editSuccessCall}) => {

    const [showModel, setShowModel] = useState<boolean>(false);
    const [updateForm, setUpdateForm] = useState<any>(record);

    const handleOk = () => {
        axios.post("/api/admin/type/update", updateForm).then(() => {
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
export default EditType