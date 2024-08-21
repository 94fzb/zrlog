import EnvUtils from "../../../utils/env-utils";
import { getRes } from "../../../utils/constants";
import { FunctionComponent } from "react";

export type EditorStatisticsInfo = {
    contentWordsLength: number;
    contentLinesLength: number;
};

export type EditorStatisticsInfoProps = {
    data: EditorStatisticsInfo;
    fullScreen?: boolean;
};

export const toStatisticsByMarkdown = (markdownStr?: string): EditorStatisticsInfo => {
    return {
        contentWordsLength: markdownStr?.length ? markdownStr.length : 0,
        contentLinesLength: markdownStr?.length ? (markdownStr.length > 0 ? markdownStr.split("\n").length : 0) : 0,
    };
};

const EditorStatistics: FunctionComponent<EditorStatisticsInfoProps> = ({ data, fullScreen }) => {
    return (
        <div
            style={{
                position: fullScreen ? "fixed" : "absolute",
                borderTop: EnvUtils.isDarkMode() ? `1px solid rgba(253, 253, 253, 0.12)` : "1px solid #DDD",
                left: 0,
                width: "100%",
                height: 30,
                boxSizing: "content-box",
                display: "flex",
                alignItems: "center",
                fontSize: 14,
                bottom: 0,
                zIndex: 999,
                background: EnvUtils.isDarkMode() ? "#141414" : "white",
            }}
        >
            <span style={{ padding: 16, paddingLeft: 40 }}>
                {getRes()["editor.wordsCount"]}
                <span style={{ paddingRight: 4, paddingLeft: 4 }}>:</span>
                <b style={{ marginLeft: 18, width: 60 }}>{data.contentWordsLength}</b>
            </span>
            <span style={{ padding: 16, paddingLeft: 20 }}>
                {getRes()["editor.linesCount"]}
                <span style={{ paddingRight: 4, paddingLeft: 4 }}>:</span>
                <b style={{ marginLeft: 18, width: 60 }}>{data.contentLinesLength}</b>
            </span>
        </div>
    );
};

export default EditorStatistics;
