import Divider from "antd/es/divider";
import Form from "antd/es/form";
import Input from "antd/es/input";
import TextArea from "antd/es/input/TextArea";
import Button from "antd/es/button";
import { getRes, removeRes } from "../../utils/constants";
import { useEffect, useState } from "react";
import { message } from "antd";
import { Basic } from "./index";
import FaviconUpload from "./FaviconUpload";
import Title from "antd/es/typography/Title";
import { useAxiosBaseInstance } from "../../base/AppBase";

const layout = {
    labelCol: { span: 8 },
    wrapperCol: { span: 16 },
};

const BasicForm = ({ data, offline }: { data: Basic; offline: boolean }) => {
    const [form, setForm] = useState<Basic>(data);

    const [loading, setLoading] = useState<boolean>(false);

    const [messageApi, contextHolder] = message.useMessage({ maxCount: 3 });
    const axiosInstance = useAxiosBaseInstance();
    const websiteFormFinish = async (changedValues: Basic) => {
        if (loading) {
            return;
        }
        try {
            setLoading(true);
            const { data } = await axiosInstance.post("/api/admin/website/basic", { ...form, ...changedValues });
            setLoading(false);
            if (data.error) {
                await messageApi.error(data.message);
                return;
            }
            if (data.error === 0) {
                await messageApi.success(data.message);
                removeRes();
                window.location.reload();
            } else {
                await messageApi.error(data.message);
            }
        } catch (e) {
            setLoading(false);
            await messageApi.error((e as Error).message);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        setForm(data);
    }, [data]);

    return (
        <>
            {contextHolder}
            <Title level={4}>{getRes()["admin.basic.manage"]}</Title>
            <Divider />
            <Form
                {...layout}
                initialValues={form}
                onValuesChange={(_k, v) => setForm({ ...form, ...v })}
                onFinish={async (k) => await websiteFormFinish(k)}
            >
                <Form.Item name="title" label="网站标题" rules={[{ required: true }]}>
                    <Input placeholder="请输入网站标题" showCount={true} maxLength={30} />
                </Form.Item>
                <Form.Item name="second_title" label="网站副标题">
                    <Input placeholder="请输入网站副标题" showCount={true} maxLength={30} />
                </Form.Item>
                <Form.Item name="keywords" label="网站关键词">
                    <Input showCount={true} placeholder="请输入网站关键词" maxLength={40} />
                </Form.Item>
                <Form.Item name="description" label={getRes()["websiteDesc"]}>
                    <TextArea showCount={true} rows={5} maxLength={160} />
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
                <Button loading={loading} disabled={offline} type="primary" htmlType="submit">
                    {getRes().submit}
                </Button>
            </Form>
        </>
    );
};

export default BasicForm;
