import Form from "antd/es/form";
import React, { CSSProperties, FunctionComponent, useEffect, useState } from "react";
import TextArea from "antd/es/input/TextArea";
import { Variant } from "antd/es/config-provider/context";

type BaseTextAreaProps = {
    placeholder?: string;
    value?: string;
    onChange: (value: string) => Promise<void>;
    required?: boolean;
    rows?: number;
    variant?: Variant;
    style?: CSSProperties;
};
const BaseTextArea: FunctionComponent<BaseTextAreaProps> = ({
    style,
    value,
    variant,
    rows,
    onChange,
    required,
    placeholder,
}) => {
    const [isComposing, setIsComposing] = useState<boolean>(false);
    const [inputValue, setInputValue] = useState<string>(value || "");
    // 更新 inputValue 以匹配外部传入的 value，处理受控组件的需求
    useEffect(() => {
        setInputValue(value || "");
    }, [value]);

    const handleInputChange = async (e: React.ChangeEvent<HTMLTextAreaElement>) => {
        setInputValue(e.target.value);
        if (!isComposing) {
            onChange(e.target.value).then(() => {
                //ignore
            });
        }
    };

    return (
        <Form.Item style={{ marginBottom: 8, width: "100%" }} rules={[{ required: required, message: "" }]}>
            <TextArea
                style={{ ...style }}
                variant={variant}
                value={inputValue}
                onChange={handleInputChange}
                onCompositionStart={() => setIsComposing(true)}
                onCompositionUpdate={() => setIsComposing(true)}
                onCompositionEnd={async () => {
                    setIsComposing(false);
                    await onChange(inputValue);
                }}
                rows={rows}
                placeholder={placeholder}
            />
        </Form.Item>
    );
};
export default BaseTextArea;
