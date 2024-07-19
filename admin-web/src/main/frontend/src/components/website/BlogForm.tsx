import Title from "antd/es/typography/Title";
import Divider from "antd/es/divider";
import Form from "antd/es/form";
import Input from "antd/es/input";
import Switch from "antd/es/switch";
import { getRes, removeRes } from "../../utils/constants";
import Button from "antd/es/button";
import { useEffect, useState } from "react";
import axios from "axios";
import { App } from "antd";
import { Blog } from "./index";

const layout = {
    labelCol: { span: 8 },
    wrapperCol: { span: 16 },
};

const BlogForm = ({ data, offline }: { data: Blog; offline?: boolean }) => {
    const [form, setForm] = useState<any>(data);
    const { message } = App.useApp();

    const websiteFormFinish = (changedValues: any) => {
        axios.post("/api/admin/website/blog", { ...form, ...changedValues }).then(({ data }) => {
            if (data.error) {
                message.error(data.message).then();
                return;
            }
            message.success(data.message).then(() => {
                removeRes();
                window.location.reload();
            });
        });
    };

    useEffect(() => {
        setForm(data);
    }, [data]);

    return (
        <>
            <Form
                {...layout}
                initialValues={form}
                onValuesChange={(_k, v) => setForm({ ...form, ...v })}
                onFinish={(v) => websiteFormFinish(v)}
            >
                <Title level={4}>博客设置</Title>
                <Divider />
                <Form.Item name="host" label="博客域名（Host）">
                    <Input style={{ maxWidth: "300px" }} placeholder="留空，程序将读取接收到的 Host 字段" />
                </Form.Item>
                <Form.Item valuePropName="checked" name="generator_html_status" label="静态化文章页">
                    <Switch size={"small"} />
                </Form.Item>
                <Form.Item valuePropName="checked" name="disable_comment_status" label="关闭评论">
                    <Switch size={"small"} />
                </Form.Item>
                <Form.Item valuePropName="checked" name="article_thumbnail_status" label="文章封面">
                    <Switch size={"small"} />
                </Form.Item>
                <Divider />
                <Button type="primary" disabled={offline} htmlType="submit">
                    {getRes().submit}
                </Button>
            </Form>
        </>
    );
};

export default BlogForm;
