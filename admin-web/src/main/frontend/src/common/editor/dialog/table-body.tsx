import { InputNumber, Radio } from "antd";
import { useEffect, useState } from "react";
import FormItem from "antd/es/form/FormItem";
import { AlignCenterOutlined, AlignLeftOutlined, AlignRightOutlined } from "@ant-design/icons";

type TableState = {
    rows: number;
    cols: number;
    align: "left" | "center" | "right" | "_default";
};

const TableBody = ({ onChange }: { onChange: (value: string) => void }) => {
    const [state, setState] = useState<TableState>({ rows: 0, cols: 0, align: "_default" });

    const change = () => {
        const align = state.align;
        const hrLine = "------------";

        const alignSign = {
            _default: hrLine,
            left: ":" + hrLine,
            center: ":" + hrLine + ":",
            right: hrLine + ":",
        };

        let table = "\n\n";
        if (state.rows > 1 && state.cols > 0) {
            let r = 0;
            const len = state.rows;
            for (; r < len; r++) {
                const row = [];
                const head = [];

                let c = 0;
                const len2 = state.cols;
                for (; c < len2; c++) {
                    if (r === 1) {
                        head.push(alignSign[align]);
                    }

                    row.push(" ");
                }

                if (r === 1) {
                    table += "| " + head.join(" | ") + " |" + "\n";
                }
                table += "| " + row.join(state.cols === 1 ? "" : " | ") + " |" + "\n";
            }
        }
        onChange(table);
    };

    useEffect(() => {
        if (state.rows < 1 || state.cols < 1) {
            return;
        }
        change();
    }, [state]);

    return (
        <div style={{ paddingTop: 12 }}>
            <div style={{ display: "flex", gap: 16 }}>
                <FormItem label={"行"}>
                    <InputNumber
                        min={2}
                        onChange={(e) => {
                            setState((prevState) => {
                                return {
                                    ...prevState,
                                    rows: e as number,
                                };
                            });
                        }}
                    />
                </FormItem>
                <FormItem label={"列"}>
                    <InputNumber
                        min={1}
                        title={"列"}
                        onChange={(e) => {
                            setState((prevState) => {
                                return {
                                    ...prevState,
                                    cols: e as number,
                                };
                            });
                        }}
                    />
                </FormItem>
            </div>
            <div>
                <FormItem label={"对齐"}>
                    <Radio.Group
                        value={state.align}
                        onChange={(e) => {
                            setState((prevState) => {
                                return {
                                    ...prevState,
                                    align: e.target.value,
                                };
                            });
                        }}
                    >
                        <Radio value={"left"}>
                            <AlignLeftOutlined />
                            <span style={{ paddingLeft: 4 }}>左</span>
                        </Radio>
                        <Radio value={"center"}>
                            <AlignCenterOutlined />
                            <span style={{ paddingLeft: 4 }}>中</span>
                        </Radio>
                        <Radio value={"right"}>
                            <AlignRightOutlined />
                            <span style={{ paddingLeft: 4 }}>右</span>
                        </Radio>
                    </Radio.Group>
                </FormItem>
            </div>
        </div>
    );
};
export default TableBody;
