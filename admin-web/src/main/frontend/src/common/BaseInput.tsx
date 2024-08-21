import { Input } from "antd";
import React, { FunctionComponent, ReactElement, useEffect, useState } from "react";
import { InputStatus } from "antd/es/_util/statusUtils";
import { Variant } from "antd/es/config-provider/context";
import { SizeType } from "antd/es/config-provider/SizeContext";

type BaseInputProps = {
    addonBefore?: ReactElement | string;
    placeholder?: string;
    value?: string;
    name?: string;
    status?: InputStatus;
    onChange: (value: string) => Promise<void>;
    required?: boolean;
    hidden?: boolean;
    maxLength?: number;
    variant?: Variant;
    size?: SizeType;
    style?: React.CSSProperties;
};
const BaseInput: FunctionComponent<BaseInputProps> = ({
    hidden,
    value,
    onChange,
    addonBefore,
    status,
    placeholder,
    style,
    variant,
    size,
    maxLength,
}) => {
    const [inputValue, setInputValue] = useState<string>(value || "");
    const [isComposing, setIsComposing] = useState<boolean>(false);

    // 更新 inputValue 以匹配外部传入的 value，处理受控组件的需求
    useEffect(() => {
        setInputValue(value || "");
    }, [value]);

    const handleInputChange = async (e: React.ChangeEvent<HTMLInputElement>) => {
        setInputValue(e.target.value);
        if (!isComposing) {
            onChange(e.target.value).then(() => {
                //ignore
            });
        }
    };

    return (
        <Input
            style={{
                ...style,
                display: hidden ? "none" : "flex",
            }}
            variant={variant}
            status={status}
            maxLength={maxLength}
            addonBefore={addonBefore}
            value={inputValue}
            size={size}
            onChange={handleInputChange}
            onCompositionStart={() => setIsComposing(true)}
            onCompositionUpdate={() => setIsComposing(true)}
            onCompositionEnd={async () => {
                setIsComposing(false);
                await onChange(inputValue);
            }}
            placeholder={placeholder}
        />
    );
};
export default BaseInput;
