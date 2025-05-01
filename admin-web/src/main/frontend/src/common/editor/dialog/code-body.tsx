import FormItem from "antd/es/form/FormItem";
import BaseTextArea from "../../BaseTextArea";
import Select from "antd/es/select";
import { Option } from "rc-select";
import { FunctionComponent, useEffect, useState } from "react";
import { getCodeLanguages } from "../highlight/hljs-helpers";

type CodeBodyProps = {
    onChange: (content: string) => void;
    getContainer?: () => HTMLElement;
};

type CodeBodyState = {
    language: string;
    code: string;
};

const CodeBody: FunctionComponent<CodeBodyProps> = ({ onChange, getContainer }) => {
    const [state, setState] = useState<CodeBodyState>({
        language: "",
        code: "",
    });

    const languages = getCodeLanguages();

    useEffect(() => {
        if (state.code.length === 0) {
            return;
        }
        onChange("```" + state.language + "\n" + "" + state.code + "\n" + "```\n");
    }, [state]);

    return (
        <>
            <FormItem label={"语言"}>
                <Select
                    style={{ maxWidth: 240 }}
                    onChange={(k) => {
                        setState((prevState) => {
                            return {
                                ...prevState,
                                language: k,
                            };
                        });
                    }}
                    getPopupContainer={getContainer}
                >
                    {Object.keys(languages).map((key) => (
                        <Option key={key} value={key}>
                            {languages[key][0]}
                        </Option>
                    ))}
                </Select>
            </FormItem>
            <FormItem>
                <BaseTextArea
                    placeholder={""}
                    onChange={(v) => {
                        setState((prevState) => {
                            return {
                                ...prevState,
                                code: v,
                            };
                        });
                    }}
                    rows={25}
                />
            </FormItem>
        </>
    );
};
export default CodeBody;
