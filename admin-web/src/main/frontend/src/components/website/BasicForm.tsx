import Title from "antd/es/typography/Title";
import Divider from "antd/es/divider";
import Form from "antd/es/form";
import Input from "antd/es/input";
import TextArea from "antd/es/input/TextArea";
import Button from "antd/es/button";
import { getRes, removeRes } from "../../utils/constants";
import { useState } from "react";
import axios from "axios";
import { App } from "antd";
import { Basic } from "./index";

const layout = {
    labelCol: { span: 8 },
    wrapperCol: { span: 16 },
};

const BasicForm = ({ data }: { data: Basic }) => {
    const [form, setForm] = useState<any>(data);

    const { message } = App.useApp();
    const websiteFormFinish = (changedValues: any) => {
        axios.post("/api/admin/website/basic", changedValues).then(({ data }) => {
            if (!data.error) {
                message.success(data.message).then(() => {
                    removeRes();
                    window.location.reload();
                });
            }
        });
    };

    return (
        <>
            <Title level={4}>认真输入，有助于网站被收录</Title>
            <Divider />
            <Form
                {...layout}
                initialValues={form}
                onValuesChange={(_k, v) => setForm(v)}
                onFinish={(k) => websiteFormFinish(k)}
            >
                <Form.Item name="title" label="网站标题" rules={[{ required: true }]}>
                    <Input placeholder="请输入网站标题" />
                </Form.Item>
                <Form.Item name="second_title" label="网站副标题">
                    <Input placeholder="请输入网站副标题" />
                </Form.Item>
                <Form.Item name="keywords" label="网站关键词">
                    <Input placeholder="请输入网站关键词" />
                </Form.Item>
                <Form.Item name="description" label="网站描述">
                    <TextArea rows={5} />
                </Form.Item>
                <Divider />
                <Button type="primary" htmlType="submit">
                    {getRes().submit}
                </Button>
            </Form>
        </>
    );
};

export default BasicForm;
