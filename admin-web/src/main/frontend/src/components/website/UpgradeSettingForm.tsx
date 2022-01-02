import Row from "antd/es/grid/row";
import Col from "antd/es/grid/col";
import Button from "antd/es/button";
import Constants, {getRes, resourceKey} from "../../utils/constants";
import Form from "antd/es/form";
import Select from "antd/es/select";
import Switch from "antd/es/switch";
import Divider from "antd/es/divider";
import {message, Modal} from "antd";
import axios from "axios";
import {useEffect, useState} from "react";


const layout = {
    labelCol: {span: 8},
    wrapperCol: {span: 16},
};


type UpgradeFormState = {
    autoUpgradeVersion: number,
    upgradePreview: boolean,
}

const UpgradeSettingForm = () => {

    const [checking, setChecking] = useState<boolean>(false)

    const [form, setForm] = useState<UpgradeFormState>({autoUpgradeVersion: -2, upgradePreview: false})

    const websiteFormFinish = (changedValues: any) => {
        axios.post("/api/admin/website/upgrade", changedValues).then(({data}) => {
            if (!data.error) {
                message.info(data.message).then(() => {
                    sessionStorage.removeItem(resourceKey);
                    window.location.reload();
                });
            }
        });
    }

    useEffect(() => {
        axios.get("/api/admin/website/settings").then(({data}) => {
            if (data.data.upgrade != null) {
                setForm(data.data.upgrade);
                console.info(data.data.upgrade);
            }
        })
    }, [])

    if (form === undefined || form.autoUpgradeVersion === -2) {
        return <></>
    }

    const checkNewVersion = async () => {
        if (checking) {
            return;
        }
        setChecking(true);
        try {
            await axios.get("/api/admin/upgrade/checkNewVersion").then(async ({data}) => {
                if (data.data.upgrade) {
                    const title = "V" + data.data.version.version + "-" + data.data.version.buildId + " (" + data.data.version.type + ")";
                    Modal.info({
                        title: title,
                        content: <div dangerouslySetInnerHTML={{__html: data.data.version.changeLog}}/>,
                        closable: true,
                        okText: "去更新",
                        onOk: function () {
                            Constants.getHistory().push("./upgrade");
                        }
                    });
                } else {
                    message.info(getRes()['notFoundNewVersion'])
                }
            });
        } finally {
            setChecking(false);
        }
    }

    return (<>
        <Row>
            <Col md={12} xs={24}>
                <Button type='dashed' loading={checking} onClick={checkNewVersion}
                        style={{float: "right"}}>{getRes().checkUpgrade}</Button>
            </Col>
        </Row>
        <Row>
            <Col md={12} xs={24}>
                <Form {...layout}
                      initialValues={form}
                      onValuesChange={(_k, v) => setForm(v)}
                      onFinish={k => websiteFormFinish(k)}>
                    <Form.Item name='autoUpgradeVersion'
                               label={getRes()['admin.upgrade.autoCheckCycle']}>
                        <Select style={{maxWidth: "100px"}}>
                            <Select.Option key='86400' value={86400}>
                                {getRes()['admin.upgrade.cycle.oneDay']}
                            </Select.Option>
                            <Select.Option key='604800' value={604800}>
                                {getRes()['admin.upgrade.cycle.oneWeek']}
                            </Select.Option>
                            <Select.Option key='1296000' value={1296000}>
                                {getRes()['admin.upgrade.cycle.halfMonth']}
                            </Select.Option>
                            <Select.Option key='-1' value={-1}>
                                {getRes()['admin.upgrade.cycle.never']}
                            </Select.Option>
                        </Select>
                    </Form.Item>
                    <Form.Item valuePropName="checked" name='upgradePreview'
                               label={getRes()['admin.upgrade.canPreview']}>
                        <Switch size={"small"}/>
                    </Form.Item>
                    <Divider/>
                    <Button type='primary' htmlType='submit'>{getRes().submit}</Button>
                </Form>
            </Col>
        </Row>
    </>)
}

export default UpgradeSettingForm;