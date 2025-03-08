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
import { ColorPicker, message } from "antd";
import { Admin } from "./index";
import FaviconUpload from "./FaviconUpload";
import { getItems_per_page } from "index";
import { colorPickerBgColors } from "../../utils/helpers";

const layout = {
    labelCol: { span: 8 },
    wrapperCol: { span: 16 },
};

const { Option } = Select;

const BlogForm = ({ data, offline }: { data: Admin; offline: boolean }) => {
    const [form, setForm] = useState<Admin>(data);
    const [messageApi, contextHolder] = message.useMessage({ maxCount: 3 });

    const websiteFormFinish = (changedValues: Admin) => {
        axios.post("/api/admin/website/admin", { ...form, ...changedValues }).then(async ({ data }) => {
            if (data.error) {
                await messageApi.error(data.message);
                return;
            }
            if (data.error === 0) {
                await messageApi.success(data.message);
                removeRes();
                window.location.reload();
            }
        });
    };

    useEffect(() => {
        setForm(data);
    }, [data]);

    // @ts-ignore
    return (
        <>
            {contextHolder}
            <Form
                {...layout}
                initialValues={form}
                onValuesChange={(_k, v) => setForm({ ...form, ...v })}
                onFinish={(v) => websiteFormFinish(v)}
            >
                <Title level={4}>{getRes()["admin.admin.manage"]}</Title>
                <Divider />
                <Form.Item name="admin_static_resource_base_url" label="管理页静态资源（URL）">
                    <Input style={{ maxWidth: "300px" }} placeholder="留空，及禁用" />
                </Form.Item>
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
                    <Select style={{ maxWidth: "120px" }}>
                        <Option value="zh_CN">{getRes()["languageChinese"]}</Option>
                        <Option value="en_US">{getRes()["languageEnglish"]}</Option>
                    </Select>
                </Form.Item>
                <Form.Item valuePropName="checked" name="admin_darkMode" label={getRes()["admin.dark.mode"]}>
                    <Switch size={"small"} />
                </Form.Item>
                <Form.Item name="admin_article_page_size" label={getRes()["admin_article_page_size"]}>
                    <Select
                        style={{ maxWidth: "120px" }}
                        options={[
                            {
                                value: 10,
                                label: "10 " + getItems_per_page(),
                            },
                            {
                                value: 20,
                                label: "20 " + getItems_per_page(),
                            },
                            {
                                value: 50,
                                label: "50 " + getItems_per_page(),
                            },
                            {
                                value: 100,
                                label: "100 " + getItems_per_page(),
                            },
                        ]}
                    />
                </Form.Item>
                <Form.Item label={getRes()["admin.color.primary"]}>
                    <div style={{ display: "flex", justifyContent: "flex-start", alignItems: "center" }}>
                        <ColorPicker
                            value={form["admin_color_primary"]}
                            onChange={(color) => {
                                setForm({ ...form, admin_color_primary: color.toHexString() });
                            }}
                            presets={[
                                {
                                    defaultOpen: true,
                                    label: "预设",
                                    colors: colorPickerBgColors,
                                },
                            ]}
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
                <Button disabled={offline} type="primary" htmlType="submit">
                    {getRes().submit}
                </Button>
            </Form>
        </>
    );
};

export default BlogForm;
