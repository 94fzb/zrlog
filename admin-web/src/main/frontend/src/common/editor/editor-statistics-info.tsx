import EnvUtils from "../../utils/env-utils";
import { getRes } from "../../utils/constants";
import { FunctionComponent } from "react";
import { getBorder } from "./editor-helpers";

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
                borderTop: getBorder(),
                left: 0,
                width: "100%",
                height: 30,
                boxSizing: "content-box",
                display: "flex",
                alignItems: "center",
                fontSize: 14,
                bottom: 0,
                userSelect: "none",
                background: EnvUtils.isDarkMode() ? "#141414" : "white",
            }}
        >
            <span style={{ padding: 16, paddingLeft: 40, whiteSpace: "nowrap" }}>
                {getRes()["editor.wordsCount"]}
                <span style={{ paddingRight: 4, paddingLeft: 4 }}>:</span>
                <b style={{ marginLeft: 18, width: 60 }}>{data.contentWordsLength}</b>
            </span>
            <span style={{ padding: 16, whiteSpace: "nowrap" }}>
                {getRes()["editor.linesCount"]}
                <span style={{ paddingRight: 4, paddingLeft: 4 }}>:</span>
                <b style={{ marginLeft: 18, width: 60 }}>{data.contentLinesLength}</b>
            </span>
        </div>
    );
};

export default EditorStatistics;
