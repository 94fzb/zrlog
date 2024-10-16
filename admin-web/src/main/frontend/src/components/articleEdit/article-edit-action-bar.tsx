import Col from "antd/es/grid/col";
import { Button } from "antd";
import { EyeOutlined, SaveOutlined, SendOutlined } from "@ant-design/icons";
import { getRes } from "../../utils/constants";
import TimeAgo from "../../common/TimeAgo";
import EnvUtils from "../../utils/env-utils";
import { ArticleEditState } from "./index";
import { FunctionComponent, useEffect, useRef } from "react";
import { ArticleEntry } from "./index.types";

type ArticleEditActionBarProps = {
    data: ArticleEditState;
    onSubmit: (article: ArticleEntry, release: boolean, preview: boolean, autoSave: boolean) => Promise<void>;
};

const ArticleEditActionBar: FunctionComponent<ArticleEditActionBarProps> = ({ data, onSubmit }) => {
    const enterBtnRef = useRef<HTMLAnchorElement | HTMLButtonElement>(null);

    const getRubbishText = () => {
        let tips;
        if (data.offline) {
            tips = getRes()["admin.offline.article-editing"];
        } else {
            if (!data.rubbish) {
                return <Col xxl={3} md={3} sm={4} style={{ padding: 0 }} />;
            }

            if (data.article.lastUpdateDate && data.article.lastUpdateDate > 0) {
                tips = (
                    <>
                        <TimeAgo timestamp={data.article.lastUpdateDate} />
                        更新
                    </>
                );
            } else {
                tips = "当前为草稿";
            }
        }
        return (
            <Button
                type={"default"}
                className={data.fullScreen ? "saveToRubbish-btn-full-screen" : "item"}
                style={{
                    border: 0,
                    width: "100%",
                    maxWidth: 256,
                    display: "flex",
                    justifyContent: "center",
                    alignItems: "center",
                    textAlign: "center",
                    height: "32px",
                    paddingRight: "8px",
                    paddingLeft: "8px",
                    backgroundColor: data.fullScreen ? (EnvUtils.isDarkMode() ? "rgb(20 20 20)" : "white") : "inherit",
                }}
            >
                {tips}
            </Button>
        );
    };

    useEffect(() => {
        const handleKeyPress = (event: KeyboardEvent) => {
            // 检查是否是 macOS 系统
            const isMac = /Mac|iPhone|iPad|iPod/i.test(navigator.userAgent);

            // 检查按键组合
            if (
                (isMac && event.metaKey && event.key === "Enter") ||
                (!isMac && event.ctrlKey && event.key === "Enter")
            ) {
                // 处理 Ctrl + Enter 或 Cmd + Enter 的逻辑
                //console.log('Ctrl + Enter 或 Cmd + Enter 按下');
                if (enterBtnRef.current) {
                    enterBtnRef.current.click();
                }
                //onSubmit(data.article, true, false, false);
            }
        };

        // 绑定键盘事件
        window.addEventListener("keydown", handleKeyPress);

        // 在组件卸载时移除事件监听
        return () => {
            window.removeEventListener("keydown", handleKeyPress);
        };
    }, []);

    return (
        <>
            <Col
                id={"action-bar"}
                xxl={9}
                md={12}
                sm={24}
                style={{ display: "flex", justifyContent: "end", padding: 0 }}
            >
                <Col
                    id={"item"}
                    xxl={6}
                    md={9}
                    sm={24}
                    className={data.fullScreen ? "saveToRubbish-btn-full-screen" : "item"}
                >
                    {getRubbishText()}
                </Col>
                <Col xxl={6} md={9} sm={24} className={data.fullScreen ? "saveToRubbish-btn-full-screen" : "item"}>
                    <Button
                        type={data.fullScreen ? "default" : "dashed"}
                        style={{ width: "100%", maxWidth: 256 }}
                        disabled={data.offline || (data.saving.rubbishSaving && !data.saving.autoSaving)}
                        onClick={async () => await onSubmit(data.article, false, false, false)}
                    >
                        <SaveOutlined hidden={data.saving.rubbishSaving} />
                        {data.saving.rubbishSaving ? getRes().saving : getRes().saveAsDraft}
                    </Button>
                </Col>
                <Col xxl={6} md={9} sm={24} className={"item"} style={{ display: data.fullScreen ? "none" : "flex" }}>
                    <Button
                        type="dashed"
                        disabled={data.offline || (data.saving.previewIng && !data.saving.autoSaving)}
                        style={{ width: "100%", maxWidth: 256 }}
                        onClick={async () => await onSubmit(data.article, !data.rubbish, true, false)}
                    >
                        <EyeOutlined />
                        {getRes().preview}
                    </Button>
                </Col>
                <Col xxl={6} md={9} sm={24} className={data.fullScreen ? "save-btn-full-screen" : "item"}>
                    <Button
                        ref={enterBtnRef}
                        type="primary"
                        disabled={data.offline}
                        loading={data.saving.releaseSaving}
                        style={{ width: "100%", maxWidth: 256 }}
                        onClick={async () => {
                            await onSubmit(data.article, true, false, false);
                        }}
                    >
                        <SendOutlined />
                        {data.article.privacy === true ? getRes()["save"] : getRes().release}
                    </Button>
                </Col>
            </Col>
        </>
    );
};
export default ArticleEditActionBar;
