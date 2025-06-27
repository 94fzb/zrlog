import FormItem from "antd/es/form/FormItem";
import BaseTextArea from "../../../common/BaseTextArea";
import Select from "antd/es/select";
import {Option} from "rc-select";
import {FunctionComponent, useEffect, useState} from "react";

const codeLanguages: Record<string, string[]> = {
    asp: ["ASP", "vbscript"],
    actionscript: ["ActionScript(3.0)/Flash/Flex", "clike"],
    bash: ["Bash/Bat", "shell"],
    css: ["CSS", "css"],
    c: ["C", "clike"],
    cpp: ["C++", "clike"],
    csharp: ["C#", "clike"],
    coffeescript: ["CoffeeScript", "coffeescript"],
    d: ["D", "d"],
    dart: ["Dart", "dart"],
    delphi: ["Delphi/Pascal", "pascal"],
    erlang: ["Erlang", "erlang"],
    go: ["Golang", "go"],
    groovy: ["Groovy", "groovy"],
    html: ["HTML", "text/html"],
    java: ["Java", "clike"],
    json: ["JSON", "text/json"],
    javascript: ["Javascript", "javascript"],
    lua: ["Lua", "lua"],
    less: ["LESS", "css"],
    markdown: ["Markdown", "gfm"],
    "objective-c": ["Objective-C", "clike"],
    php: ["PHP", "php"],
    perl: ["Perl", "perl"],
    python: ["Python", "python"],
    r: ["R", "r"],
    rst: ["reStructedText", "rst"],
    ruby: ["Ruby", "ruby"],
    sql: ["SQL", "sql"],
    sass: ["SASS/SCSS", "sass"],
    shell: ["Shell", "shell"],
    scala: ["Scala", "clike"],
    swift: ["Swift", "clike"],
    vb: ["VB/VBScript", "vb"],
    xml: ["XML", "text/xml"],
    yaml: ["YAML", "yaml"],
    "": ["其他", ""]
};

type CodeBodyProps = {
    onChange: (content: string) => void;
}

type CodeBodyState = {
    language: string;
    code: string;
}

const CodeBody: FunctionComponent<CodeBodyProps> = ({onChange}) => {

    const [state, setState] = useState<CodeBodyState>({
        language: "",
        code: "",
    })

    useEffect(() => {
        onChange("```" + state.language + "\n" +
            "" + state.code + "\n" +
            "```\n")
    }, [state]);

    return <>
        <FormItem label={"语言"}>
            <Select style={{maxWidth: 240}} onChange={(k) => {
                setState((prevState) => {
                    return {
                        ...prevState,
                        language: k
                    }
                })
            }}>
                {Object.keys(codeLanguages).map(key => <Option key={key}
                                                               value={key}>{(codeLanguages[key])[0]}</Option>)}
            </Select>
        </FormItem>
        <FormItem>
            <BaseTextArea placeholder={""} onChange={(v) => {
                setState((prevState) => {
                    return {
                        ...prevState,
                        code: v
                    }
                })
            }} rows={25}/>
        </FormItem>
    </>

}
export default CodeBody;