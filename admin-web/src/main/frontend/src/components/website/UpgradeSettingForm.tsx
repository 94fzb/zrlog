import Row from "antd/es/grid/row";
import Col from "antd/es/grid/col";
import Button from "antd/es/button";
import { getRealRouteUrl, getRes } from "../../utils/constants";
import Form from "antd/es/form";
import Select from "antd/es/select";
import Switch from "antd/es/switch";
import Divider from "antd/es/divider";
import { App, message } from "antd";
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { Upgrade } from "./index";
import { useAxiosBaseInstance } from "../../base/AppBase";

const layout = {
    labelCol: { span: 8 },
    wrapperCol: { span: 16 },
};

type UpgradeFormState = {
    autoUpgradeVersion: number;
    upgradePreview: boolean;
};

const UpgradeSettingForm = ({ data, offline }: { data: Upgrade; offline: boolean }) => {
    const [checking, setChecking] = useState<boolean>(false);
    const { modal } = App.useApp();
    const [messageApi, contextHolder] = message.useMessage({ maxCount: 3 });

    const [form, setForm] = useState<UpgradeFormState>(data);

    const navigate = useNavigate();

    const axiosBaseInstance = useAxiosBaseInstance();

    const websiteFormFinish = (changedValues: any) => {
        axiosBaseInstance.post("/api/admin/website/upgrade", { ...form, ...changedValues }).then(({ data }) => {
            if (data.error) {
                messageApi.error(data.message).then();
                return;
            }
            messageApi.success(data.message).then(() => {
                //ignore
            });
        });
    };

    const checkNewVersion = async () => {
        if (checking) {
            return;
        }
        setChecking(true);
        try {
            const { data } = await axiosBaseInstance.get("/api/admin/upgrade");
            if (data.data.upgrade) {
                const title = `${getRes()["newVersion"]} - #${data.data.version.type}`;
                modal.info({
                    title: title,
                    content: (
                        <div
                            dangerouslySetInnerHTML={{
                                __html: data.data.version.changeLog,
                            }}
                        />
                    ),
                    closable: true,
                    okText: getRes()["doUpgrade"],
                    onOk: function () {
                        navigate(getRealRouteUrl("/upgrade"));
                    },
                });
            } else {
                if (data.error === 0) {
                    setChecking(false);
                    await messageApi.info(getRes()["notFoundNewVersion"]);
                }
            }
        } finally {
            setChecking(false);
        }
    };

    useEffect(() => {
        setForm(data);
    }, [data]);

    return (
        <div style={{ maxWidth: 600 }}>
            {contextHolder}
            <Row>
                <Col xs={24}>
                    <Button
                        type="dashed"
                        disabled={offline}
                        loading={checking}
                        onClick={checkNewVersion}
                        style={{ float: "right" }}
                    >
                        {getRes().checkUpgrade}
                    </Button>
                </Col>
            </Row>
            <Row>
                <Col xs={24}>
                    <Form
                        {...layout}
                        initialValues={form}
                        onValuesChange={(_k, v) => setForm({ ...form, ...v })}
                        onFinish={(k) => websiteFormFinish(k)}
                    >
                        <Form.Item name="autoUpgradeVersion" label={getRes()["admin.upgrade.autoCheckCycle"]}>
                            <Select style={{ maxWidth: "120px" }}>
                                <Select.Option key="86400" value={86400}>
                                    {getRes()["admin.upgrade.cycle.oneDay"]}
                                </Select.Option>
                                <Select.Option key="604800" value={604800}>
                                    {getRes()["admin.upgrade.cycle.oneWeek"]}
                                </Select.Option>
                                <Select.Option key="1296000" value={1296000}>
                                    {getRes()["admin.upgrade.cycle.halfMonth"]}
                                </Select.Option>
                                <Select.Option key="-1" value={-1}>
                                    {getRes()["admin.upgrade.cycle.never"]}
                                </Select.Option>
                            </Select>
                        </Form.Item>
                        <Form.Item
                            valuePropName="checked"
                            name="upgradePreview"
                            label={getRes()["admin.upgrade.canPreview"]}
                        >
                            <Switch size={"small"} />
                        </Form.Item>
                        <Divider />
                        <Button disabled={offline} type="primary" htmlType="submit">
                            {getRes().submit}
                        </Button>
                    </Form>
                </Col>
            </Row>
        </div>
    );
};

export default UpgradeSettingForm;
