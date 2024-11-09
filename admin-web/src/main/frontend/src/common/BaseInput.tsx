import { Input, InputRef } from "antd";
import React, { forwardRef, ReactElement, RefObject, useState } from "react";
import { InputStatus } from "antd/es/_util/statusUtils";
import { Variant } from "antd/es/config-provider/context";
import { SizeType } from "antd/es/config-provider/SizeContext";

type BaseInputProps = {
    addonBefore?: ReactElement | string;
    placeholder?: string;
    defaultValue?: string;
    name?: string;
    status?: InputStatus;
    onChange: (value: string) => void;
    required?: boolean;
    hidden?: boolean;
    maxLength?: number;
    variant?: Variant;
    size?: SizeType;
    ref?: RefObject<InputRef>;
    style?: React.CSSProperties;
};
const BaseInput = forwardRef<InputRef, BaseInputProps>(
    ({ hidden, onChange, addonBefore, status, defaultValue, placeholder, style, variant, size, maxLength }, ref) => {
        const [inputValue, setInputValue] = useState<string>(defaultValue || "");
        const [isComposing, setIsComposing] = useState<boolean>(false);

        const handleInputChange = async (e: React.ChangeEvent<HTMLInputElement>) => {
            setInputValue(e.target.value);
            if (!isComposing) {
                onChange(e.target.value);
            }
        };

        return (
            <Input
                style={{
                    ...style,
                    display: hidden ? "none" : "flex",
                }}
                defaultValue={defaultValue}
                ref={ref}
                variant={variant}
                status={status}
                maxLength={maxLength}
                addonBefore={addonBefore}
                size={size}
                onChange={handleInputChange}
                onCompositionStart={() => setIsComposing(true)}
                onCompositionUpdate={() => setIsComposing(true)}
                onCompositionEnd={async () => {
                    setIsComposing(false);
                    onChange(inputValue);
                }}
                placeholder={placeholder}
            />
        );
    }
);
export default BaseInput;
