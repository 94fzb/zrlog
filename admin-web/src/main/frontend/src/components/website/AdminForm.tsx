import Title from "antd/es/typography/Title";
import Divider from "antd/es/divider";
import Form from "antd/es/form";
import Input from "antd/es/input";
import Switch from "antd/es/switch";
import { getRes, removeRes } from "../../utils/constants";
import Select from "antd/es/select";
import Button from "antd/es/button";
import { useEffect, useState } from "react";
import axios from "axios";
import { App, ColorPicker } from "antd";
import { Admin } from "./index";
import FaviconUpload from "./FaviconUpload";

const layout = {
    labelCol: { span: 8 },
    wrapperCol: { span: 16 },
};

const { Option } = Select;

const BlogForm = ({ data }: { data: Admin }) => {
    const [form, setForm] = useState<Admin>(data);
    const { message } = App.useApp();

    const websiteFormFinish = (changedValues: Admin) => {
        axios.post("/api/admin/website/admin", { ...form, ...changedValues }).then(({ data }) => {
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
                <Title level={4}>管理界面设置</Title>
                <Divider />
                <Form.Item name="session_timeout" label="管理界面会话超时" rules={[{ required: true }]}>
                    <Input
                        suffix="分钟"
                        style={{ maxWidth: "120px" }}
                        max={99999}
                        type={"number"}
                        min={5}
                        placeholder=""
                    />
                </Form.Item>
                <Form.Item name="language" label={getRes()["language"]}>
                    <Select style={{ maxWidth: "100px" }}>
                        <Option value="zh_CN">{getRes()["languageChinese"]}</Option>
                        <Option value="en_US">{getRes()["languageEnglish"]}</Option>
                    </Select>
                </Form.Item>
                <Form.Item valuePropName="checked" name="admin_darkMode" label={getRes()["admin.dark.mode"]}>
                    <Switch size={"small"} />
                </Form.Item>
                <Form.Item label={getRes()["admin.color.primary"]}>
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
                <Form.Item name="favicon_png_pwa_192_base64" label={`${getRes()["favicon"]} PWA (192px)`}>
                    <FaviconUpload
                        url={form.favicon_png_pwa_192_base64}
                        onChange={(e) => {
                            setForm({ ...form, favicon_png_pwa_192_base64: e ? e : "" });
                        }}
                    />
                </Form.Item>
                <Form.Item name="favicon_png_pwa_512_base64" label={`${getRes()["favicon"]} PWA (512px)`}>
                    <FaviconUpload
                        url={form.favicon_png_pwa_512_base64}
                        onChange={(e) => {
                            setForm({ ...form, favicon_png_pwa_512_base64: e ? e : "" });
                        }}
                    />
                </Form.Item>
                <Title level={4}>文章/编辑器设置</Title>
                <Divider />

                <Form.Item name="article_auto_digest_length" label="文章自动摘要最大长度">
                    <Input
                        suffix="字"
                        style={{ maxWidth: "120px" }}
                        max={99999}
                        type={"number"}
                        min={-1}
                        placeholder=""
                    />
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
