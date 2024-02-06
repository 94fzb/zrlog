import Form from "antd/es/form";
import { FunctionComponent, useRef, useState } from "react";
import TextArea, { TextAreaRef } from "antd/es/input/TextArea";

type BaseTextAreaProps = {
    placeholder?: string;
    value?: string;
    onChange: (value: string) => Promise<void>;
    required?: boolean;
    rows?: number;
};
const BaseInput: FunctionComponent<BaseTextAreaProps> = ({ value, rows, onChange, required, placeholder }) => {
    const [composing, setComposing] = useState<boolean>(false);
    const inputRef = useRef<TextAreaRef>(null);

    return (
        <Form.Item style={{ marginBottom: 8, width: "100%" }} rules={[{ required: required, message: "" }]}>
            <TextArea
                ref={inputRef}
                value={value}
                onCompositionStart={() => {
                    setComposing(true);
                }}
                onCompositionUpdate={() => {
                    setComposing(true);
                }}
                rows={rows}
                onChange={async (e) => {
                    if (composing) {
                        return;
                    }
                    setTimeout(async () => {
                        await onChange(e.target.value);
                    }, 2000);
                }}
                onCompositionEnd={() => {
                    setComposing(false);
                    setTimeout(async () => {
                        if (
                            inputRef.current &&
                            inputRef.current.resizableTextArea &&
                            inputRef.current.resizableTextArea.textArea
                        ) {
                            const val = inputRef.current.resizableTextArea.textArea.value as unknown as never;
                            await onChange(val);
                        }
                    });
                }}
                placeholder={placeholder}
            />
        </Form.Item>
    );
};
export default BaseInput;
