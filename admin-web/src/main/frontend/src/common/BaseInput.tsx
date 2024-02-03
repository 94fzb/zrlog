import { Input, InputRef } from "antd";
import Form from "antd/es/form";
import { FunctionComponent, ReactElement, useRef, useState } from "react";

type BaseInputProps = {
    addonBefore?: ReactElement | string;
    placeholder?: string;
    defaultValue?: string;
    name?: string;
    onChange: (value: string) => Promise<void>;
    required?: boolean;
};
const BaseInput: FunctionComponent<BaseInputProps> = ({
    name,
    defaultValue,
    onChange,
    addonBefore,
    required,
    placeholder,
}) => {
    const [composing, setComposing] = useState<boolean>(false);
    const inputRef = useRef<InputRef>(null);
    const [changing, setChanging] = useState<boolean>(false);

    const loopSubmit = async () => {
        if (changing) {
            setTimeout(() => {
                loopSubmit();
            }, 1000);
        }
        try {
            setChanging(true);
            if (inputRef.current && inputRef.current.input) {
                const val = inputRef.current.input.value as unknown as never;
                await onChange(val);
            }
        } finally {
            setChanging(false);
        }
    };

    return (
        <Form.Item
            name={name}
            style={{ marginBottom: 8, width: "100%" }}
            validateTrigger={["onChange", "onBlur", "onSubmit"]}
            rules={[{ required: required, message: "" }]}
        >
            <Input
                addonBefore={addonBefore}
                ref={inputRef}
                defaultValue={defaultValue}
                onCompositionStart={() => {
                    setComposing(true);
                }}
                onCompositionUpdate={() => {
                    setComposing(true);
                }}
                onChange={async () => {
                    if (composing) {
                        return;
                    }
                    await loopSubmit();
                }}
                onCompositionEnd={() => {
                    setComposing(false);
                    setTimeout(async () => {
                        await loopSubmit();
                    }, 20);
                }}
                placeholder={placeholder}
            />
        </Form.Item>
    );
};
export default BaseInput;
