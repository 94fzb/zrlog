import Title from "antd/es/typography/Title";
import Divider from "antd/es/divider";
import Form from "antd/es/form";
import TextArea from "antd/es/input/TextArea";
import Button from "antd/es/button";
import { getRes } from "../../utils/constants";
import { useEffect, useState } from "react";
import axios from "axios";
import { message } from "antd";

import { Other } from "./index";

const layout = {
    labelCol: { span: 8 },
    wrapperCol: { span: 16 },
};

const OtherForm = ({ data, offline }: { data: Other; offline: boolean }) => {
    const [form, setForm] = useState<any>(data);
    const [messageApi, contextHolder] = message.useMessage({ maxCount: 3 });

    const websiteFormFinish = (changedValues: any) => {
        axios.post("/api/admin/website/other", { ...form, ...changedValues }).then(async ({ data }) => {
            if (data.error) {
                await messageApi.error(data.message);
                return;
            }
            if (data.error === 0) {
                await messageApi.success(data.message);
            }
        });
    };

    useEffect(() => {
        setForm(data);
    }, [data]);

    return (
        <>
            {contextHolder}
            <Title level={4}>ICP，网站统计等信息</Title>
            <Divider />
            <Form
                {...layout}
                initialValues={form}
                onValuesChange={(_k, v) => setForm({ ...form, ...v })}
                onFinish={(k) => websiteFormFinish(k)}
            >
                <Form.Item name="icp" label="ICP备案信息">
                    <TextArea />
                </Form.Item>
                <Form.Item name="webCm" label="网站统计">
                    <TextArea rows={7} />
                </Form.Item>
                <Form.Item name="robotRuleContent" label="robots.txt">
                    <TextArea rows={7} placeholder={"User-agent: *\n" + "Disallow: "} />
                </Form.Item>
                <Divider />
                <Button disabled={offline} type="primary" htmlType="submit">
                    {getRes().submit}
                </Button>
            </Form>
        </>
    );
};

export default OtherForm;
