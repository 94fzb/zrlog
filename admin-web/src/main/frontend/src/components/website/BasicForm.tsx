import Title from "antd/es/typography/Title";
import Divider from "antd/es/divider";
import Form from "antd/es/form";
import Input from "antd/es/input";
import TextArea from "antd/es/input/TextArea";
import Button from "antd/es/button";
import { getRes, removeRes } from "../../utils/constants";
import { useEffect, useState } from "react";
import axios from "axios";
import { App } from "antd";
import { Basic } from "./index";
import FaviconUpload from "./FaviconUpload";

const layout = {
    labelCol: { span: 8 },
    wrapperCol: { span: 16 },
};

const BasicForm = ({ data, offline }: { data: Basic; offline: boolean }) => {
    const [form, setForm] = useState<Basic>(data);

    const { message } = App.useApp();
    const websiteFormFinish = (changedValues: Basic) => {
        axios.post("/api/admin/website/basic", { ...form, ...changedValues }).then(async ({ data }) => {
            if (data.error) {
                await message.error(data.message);
                return;
            }
            if (data.error === 0) {
                await message.success(data.message);
                removeRes();
                window.location.reload();
            }
        });
    };

    useEffect(() => {
        setForm(data);
    }, [data]);

    return (
        <>
            <Title level={4}>认真输入，有助于网站被收录</Title>
            <Divider />
            <Form
                {...layout}
                initialValues={form}
                onValuesChange={(_k, v) => setForm({ ...form, ...v })}
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
                <Form.Item name="favicon_ico_base64" label={`${getRes()["favicon"]}`}>
                    <FaviconUpload
                        url={form.favicon_ico_base64}
                        onChange={(e) => {
                            setForm({ ...form, favicon_ico_base64: e ? e : "" });
                        }}
                    />
                </Form.Item>
                <Divider />
                <Button disabled={offline} type="primary" htmlType="submit">
                    {getRes().submit}
                </Button>
            </Form>
        </>
    );
};

export default BasicForm;
