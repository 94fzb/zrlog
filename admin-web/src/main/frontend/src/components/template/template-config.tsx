import { useState } from "react";
import { App, Form, Input, Row } from "antd";
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

const TemplateConfig = ({ data }: { data: TemplateConfigState }) => {
    const dataMap = {};
    for (const [key, value] of Object.entries(data.config)) {
        //@ts-ignore
        dataMap[key] = value.value;
    }

    const [state, setState] = useState<TemplateConfigState>({
        config: data.config,
        dataMap: dataMap,
    });

    const { message } = App.useApp();

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
            message.error(`${info.file.name} file upload failed.`);
        }
    };

    const getInput = (key: string, value: ConfigParam) => {
        if (value.type === "file") {
            return (
                <>
                    <Dragger
                        style={{ width: "128px" }}
                        multiple={false}
                        onChange={(e) => onUploadChange(e, key)}
                        name="imgFile"
                        action="/api/admin/upload?dir=image"
                    >
                        <Image preview={false} width={128} src={state.dataMap[key]} />
                    </Dragger>
                </>
            );
        } else if (value.htmlElementType === "textarea") {
            return <TextArea rows={5} placeholder={value.placeholder} />;
        } else if (value.type === "hidden") {
            return <Input hidden={true} />;
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
        axios.post("/api/admin/template/config", state.dataMap).then(({ data }) => {
            if (data.error) {
                message.error(data.message);
            } else {
                message.success(data.message);
            }
        });
    };

    return (
        <>
            <Title className="page-header" level={3}>
                {getRes()["templateConfig"]}
            </Title>
            <Divider />
            <Row>
                <Col md={12} xs={24}>
                    <Form
                        onFinish={() => onFinish()}
                        initialValues={state.dataMap}
                        onValuesChange={(_k, v) => setValue(v)}
                        {...layout}
                    >
                        {getFormItems()}
                        <Divider />
                        <Button type="primary" htmlType="submit">
                            {getRes()["submit"]}
                        </Button>
                    </Form>
                </Col>
            </Row>
        </>
    );
};

export default TemplateConfig;
