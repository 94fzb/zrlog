import { useEffect, useState } from "react";
import { ColorPicker, Form, Input, message, Row } from "antd";
import Divider from "antd/es/divider";
import Button from "antd/es/button";
import Image from "antd/es/image";
import TextArea from "antd/es/input/TextArea";
import Col from "antd/es/grid/col";
import { getRes } from "../../utils/constants";
import Switch from "antd/es/switch";
import { colorPickerBgColors } from "../../utils/helpers";
import { useAxiosBaseInstance } from "../../base/AppBase";
import BaseDragger, { DraggerUploadResponse } from "../../common/BaseDragger";
import BaseTitle from "../../base/BaseTitle";

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

    const onUploadChange = (data: DraggerUploadResponse, key: string) => {
        state.dataMap[key] = data.data.url;
        setState({
            ...state,
            dataMap: state.dataMap,
        });
    };

    const getInput = (key: string, value: ConfigParam) => {
        if (value.type === "file") {
            return (
                <>
                    <BaseDragger
                        style={{ width: "128px", height: "128px" }}
                        onSuccess={(e) => onUploadChange(e, key)}
                        name="imgFile"
                        onError={(e) => {
                            messageApi.error(e.message);
                        }}
                        action="/api/admin/upload?dir=image"
                    >
                        <Image
                            style={{ borderRadius: 8 }}
                            preview={false}
                            height={128}
                            width={128}
                            src={state.dataMap[key]}
                        />
                    </BaseDragger>
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
                        presets={[
                            {
                                defaultOpen: true,
                                label: "预设",
                                colors: colorPickerBgColors,
                            },
                        ]}
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

    const axiosInstance = useAxiosBaseInstance();
    const onFinish = () => {
        axiosInstance.post("/api/admin/template/config", state.dataMap).then(async ({ data }) => {
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
            <BaseTitle title={getRes()["templateConfig"]} />
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
