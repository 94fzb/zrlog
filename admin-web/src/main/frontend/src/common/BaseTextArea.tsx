import Form from "antd/es/form";
import React, { CSSProperties, FunctionComponent, Ref, useState } from "react";
import TextArea from "antd/es/input/TextArea";
import { Variant } from "antd/es/config-provider/context";
import { InputRef } from "antd";

type BaseTextAreaProps = {
    placeholder?: string;
    defaultValue?: string;
    onChange: (value: string) => void;
    required?: boolean;
    ref?: Ref<InputRef>;
    rows?: number;
    variant?: Variant;
    style?: CSSProperties;
};
const BaseTextArea: FunctionComponent<BaseTextAreaProps> = ({
    style,
    defaultValue,
    variant,
    ref,
    rows,
    onChange,
    required,
    placeholder,
}) => {
    const [isComposing, setIsComposing] = useState<boolean>(false);
    const [inputValue, setInputValue] = useState<string>(defaultValue || "");

    const handleInputChange = async (e: React.ChangeEvent<HTMLTextAreaElement>) => {
        setInputValue(e.target.value);
        if (!isComposing) {
            onChange(e.target.value);
        }
    };

    return (
        <Form.Item style={{ marginBottom: 8, width: "100%" }} rules={[{ required: required, message: "" }]}>
            <TextArea
                ref={ref}
                style={{ ...style }}
                variant={variant}
                defaultValue={inputValue}
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
