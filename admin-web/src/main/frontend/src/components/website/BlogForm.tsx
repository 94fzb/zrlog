import Title from "antd/es/typography/Title";
import Divider from "antd/es/divider";
import Form from "antd/es/form";
import Input from "antd/es/input";
import Switch from "antd/es/switch";
import { getRes, removeRes } from "../../utils/constants";
import Select from "antd/es/select";
import Button from "antd/es/button";
import { useState } from "react";
import axios from "axios";
import { App, ColorPicker } from "antd";
import { Blog } from "./index";

const layout = {
    labelCol: { span: 8 },
    wrapperCol: { span: 16 },
};

const { Option } = Select;

const BlogForm = ({ data }: { data: Blog }) => {
    const [form, setForm] = useState<any>(data);
    const { message } = App.useApp();

    const websiteFormFinish = (changedValues: any) => {
        axios.post("/api/admin/website/blog", { ...form, ...changedValues }).then(({ data }) => {
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
            <Title level={4}>博客设置</Title>
            <Divider />
            <Form
                {...layout}
                initialValues={form}
                onValuesChange={(_k, v) => setForm({ ...form, v })}
                onFinish={(v) => websiteFormFinish(v)}
            >
                <Form.Item name="session_timeout" label="会话过期时间" rules={[{ required: true }]}>
                    <Input
                        suffix="分钟"
                        style={{ maxWidth: "120px" }}
                        max={99999}
                        type={"number"}
                        min={5}
                        placeholder=""
                    />
                </Form.Item>
                <Form.Item valuePropName="checked" name="generator_html_status" label="静态化文章页">
                    <Switch size={"small"} />
                </Form.Item>
                <Form.Item valuePropName="checked" name="disable_comment_status" label="关闭评论">
                    <Switch size={"small"} />
                </Form.Item>
                <Form.Item valuePropName="checked" name="admin_darkMode" label="护眼模式">
                    <Switch size={"small"} />
                </Form.Item>
                <Form.Item label="主题">
                    <div style={{ display: "flex", justifyContent: "flex-start", alignItems: "center" }}>
                        <ColorPicker
                            value={form["admin_color_primary"]}
                            onChange={(color) => {
                                setForm({ ...form, admin_color_primary: color.toHexString() });
                            }}
                        />
                        <span style={{ paddingLeft: 8 }}>{form["admin_color_primary"]}</span>
                    </div>
                </Form.Item>
                <Form.Item valuePropName="checked" name="article_thumbnail_status" label="文章封面">
                    <Switch size={"small"} />
                </Form.Item>
                <Form.Item name="language" label={getRes()["language"]}>
                    <Select style={{ maxWidth: "100px" }}>
                        <Option value="zh_CN">{getRes()["languageChinese"]}</Option>
                        <Option value="en_US">{getRes()["languageEnglish"]}</Option>
                    </Select>
                </Form.Item>
                <Divider />
                <Button type="primary" htmlType="submit">
                    {getRes().submit}
                </Button>
            </Form>
        </>
    );
};

export default BlogForm;
