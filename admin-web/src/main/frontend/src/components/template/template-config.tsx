import { useEffect, useState } from "react";
import { ColorPicker, Form, Input, message, Row } from "antd";
import Title from "antd/es/typography/Title";
import Divider from "antd/es/divider";
import Button from "antd/es/button";
import Image from "antd/es/image";
import Dragger from "antd/es/upload/Dragger";
import TextArea from "antd/es/input/TextArea";
import Col from "antd/es/grid/col";
import axios from "axios";
import { getRes } from "../../utils/constants";
import { UploadChangeParam } from "antd/es/upload";
import Switch from "antd/es/switch";

const layout = {
    labelCol: { span: 8 },
    wrapperCol: { span: 16 },
};

type TemplateConfigState = {
    dataMap: Record<string, any>;
    config: ConfigParam[];
};

export type ConfigParam = {
    label: string;
    type: string;
    htmlElementType: string;
    placeholder: string;
};

const convertToDataMap = (data: TemplateConfigState) => {
    const dataMap = {};
    for (const [key, value] of Object.entries(data.config)) {
        //@ts-ignore
        dataMap[key] = value.value;
    }
    return dataMap;
};

const TemplateConfig = ({ data, offline }: { data: TemplateConfigState; offline: boolean }) => {
    const dataMap = convertToDataMap(data);
    const [state, setState] = useState<TemplateConfigState>({
        config: data.config,
        dataMap: dataMap,
    });

    const [messageApi, contextHolder] = message.useMessage({ maxCount: 3 });

    const setValue = (changedValues: any) => {
        setState({
            ...state,
            dataMap: changedValues,
        });
    };

    const onUploadChange = (info: UploadChangeParam, key: string) => {
        const { status } = info.file;
        if (status === "done") {
            state.dataMap[key] = info.file.response.data.url;
            setState({
                ...state,
                dataMap: state.dataMap,
            });
        } else if (status === "error") {
            messageApi.error(`${info.file.name} file upload failed.`);
        }
    };

    const getInput = (key: string, value: ConfigParam) => {
        if (value.type === "file") {
            return (
                <>
                    <Dragger
                        style={{ width: "128px", height: "128px" }}
                        multiple={false}
                        onChange={(e) => onUploadChange(e, key)}
                        name="imgFile"
                        action="/api/admin/upload?dir=image"
                    >
                        <Image
                            style={{ borderRadius: 8 }}
                            preview={false}
                            height={128}
                            width={128}
                            src={state.dataMap[key]}
                        />
                    </Dragger>
                </>
            );
        } else if (value.htmlElementType === "switch") {
            return <Switch size={"small"} />;
        } else if (value.htmlElementType === "textarea" || value.htmlElementType === "large-textarea") {
            return (
                <TextArea rows={value.htmlElementType === "large-textarea" ? 20 : 5} placeholder={value.placeholder} />
            );
        } else if (value.type === "hidden") {
            return <Input hidden={true} />;
        } else if (value.htmlElementType === "colorPicker") {
            return (
                <div style={{ display: "flex", justifyContent: "flex-start", alignItems: "center" }}>
                    <ColorPicker
                        value={state.dataMap[key]}
                        onChange={(color) => {
                            state.dataMap[key] = color.toHexString();
                            setState({
                                ...state,
                                dataMap: state.dataMap,
                            });
                        }}
                    />
                    <span style={{ paddingLeft: 8 }}>{state.dataMap[key]}</span>
                </div>
            );
        }
        return <Input type={value.type} placeholder={value.placeholder} />;
    };

    const getFormItems = () => {
        const formInputs = [];
        for (const [key, value] of Object.entries(state.config)) {
            const input = (
                <Form.Item
                    label={value.label}
                    name={key}
                    key={key}
                    style={{ display: value.type === "hidden" ? "none" : "" }}
                >
                    {getInput(key, value)}
                </Form.Item>
            );
            formInputs.push(input);
        }
        return formInputs;
    };

    const onFinish = () => {
        axios.post("/api/admin/template/config", state.dataMap).then(async ({ data }) => {
            if (data.error) {
                await messageApi.error(data.message);
            } else if (data.error === 0) {
                await messageApi.success(data.message);
            }
        });
    };

    useEffect(() => {
        setState({
            config: data.config,
            dataMap: convertToDataMap(data),
        });
    }, [data]);

    return (
        <>
            {contextHolder}
            <Title className="page-header" level={3}>
                {getRes()["templateConfig"]}
            </Title>
            <Divider />
            <Row>
                <Col xs={24} style={{ maxWidth: 600 }}>
                    <Form
                        onFinish={() => onFinish()}
                        initialValues={state.dataMap}
                        onValuesChange={(_k, v) => setValue(v)}
                        {...layout}
                    >
                        {getFormItems()}
                        <Divider />
                        <Button disabled={offline} type="primary" htmlType="submit">
                            {getRes()["submit"]}
                        </Button>
                    </Form>
                </Col>
            </Row>
        </>
    );
};

export default TemplateConfig;
