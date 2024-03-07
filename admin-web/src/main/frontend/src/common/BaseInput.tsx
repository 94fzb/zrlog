import { Input, InputRef } from "antd";
import { FunctionComponent, ReactElement, useRef, useState } from "react";
import { InputStatus } from "antd/es/_util/statusUtils";

type BaseInputProps = {
    addonBefore?: ReactElement | string;
    placeholder?: string;
    value?: string;
    name?: string;
    status?: InputStatus;
    onChange: (value: string) => Promise<void>;
    required?: boolean;
};
const BaseInput: FunctionComponent<BaseInputProps> = ({ value, onChange, addonBefore, status, placeholder }) => {
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
        <Input
            status={status}
            addonBefore={addonBefore}
            ref={inputRef}
            value={value}
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
    );
};
export default BaseInput;
