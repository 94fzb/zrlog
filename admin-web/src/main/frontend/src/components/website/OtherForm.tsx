import Title from "antd/es/typography/Title";
import Divider from "antd/es/divider";
import Form from "antd/es/form";
import TextArea from "antd/es/input/TextArea";
import Button from "antd/es/button";
import { getRes, removeRes } from "../../utils/constants";
import { useState } from "react";
import axios from "axios";
import { App } from "antd";

import { Other } from "./index";

const layout = {
    labelCol: { span: 8 },
    wrapperCol: { span: 16 },
};

const OtherForm = ({ data }: { data: Other }) => {
    const [form, setForm] = useState<any>(data);
    const { message } = App.useApp();

    const websiteFormFinish = (changedValues: any) => {
        axios.post("/api/admin/website/other", changedValues).then(({ data }) => {
            if (!data.error) {
                message.success(data.message).then(() => {
                    removeRes();
                    window.location.reload();
                });
            }
        });
    };

    if (form === undefined) {
        return <></>;
    }

    return (
        <>
            <Title level={4}>ICP，网站统计等信息</Title>
            <Divider />
            <Form
                {...layout}
                initialValues={form}
                onValuesChange={(_k, v) => setForm(v)}
                onFinish={(k) => websiteFormFinish(k)}
            >
                <Form.Item name="icp" label="ICP备案信息">
                    <TextArea />
                </Form.Item>
                <Form.Item name="webCm" label="网站统计">
                    <TextArea rows={7} />
                </Form.Item>
                <Divider />
                <Button type="primary" htmlType="submit">
                    {getRes().submit}
                </Button>
            </Form>
        </>
    );
};

export default OtherForm;
