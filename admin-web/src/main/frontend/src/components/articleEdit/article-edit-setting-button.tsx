import { getRes } from "../../utils/constants";
import Col from "antd/es/grid/col";
import Row from "antd/es/grid/row";
import Card from "antd/es/card";
import ThumbnailUpload from "./thumbnail-upload";
import Form from "antd/es/form";
import Switch from "antd/es/switch";
import ArticleEditTag from "./article-edit-tag";
import BaseTextArea from "../../common/BaseTextArea";
import { Drawer, InputRef } from "antd";
import EnvUtils from "../../utils/env-utils";
import { SettingFilled, SettingOutlined } from "@ant-design/icons";
import { RefObject, useState } from "react";
import { ArticleChangeableValue, ArticleEntry } from "./index.types";
import Button from "antd/es/button";

const ArticleEditSettingButton = ({
    article,
    saving,
    tags,
    containerRef,
    digestRef,
    handleValuesChange,
}: {
    article: ArticleEntry;
    saving: () => boolean;
    tags: any;
    containerRef: RefObject<HTMLDivElement>;
    digestRef: RefObject<InputRef>;
    handleValuesChange: (cv: ArticleChangeableValue) => void;
}) => {
    const [settingsOpen, setSettingsOpen] = useState(false);

    return (
        <>
            <Button
                href={window.location.pathname + "#setting"}
                type={"default"}
                title={getRes()["admin.setting"]}
                style={{
                    border: 0,
                    display: "flex",
                    justifyContent: "center",
                    alignItems: "center",
                    width: 47,
                    minWidth: 47,
                    borderRadius: 8,
                    height: 47,
                    cursor: "pointer",
                    background: EnvUtils.isDarkMode() ? "#141414" : "white",
                    color: "rgb(102, 102, 102)",
                }}
                icon={
                    settingsOpen ? (
                        <SettingFilled style={{ fontSize: 24 }} />
                    ) : (
                        <SettingOutlined style={{ fontSize: 24 }} />
                    )
                }
                className={"editor-icon"}
                onClick={(e) => {
                    e.stopPropagation();
                    e.preventDefault();
                    setSettingsOpen((prevState) => {
                        return !prevState;
                    });
                }}
            ></Button>
            <Drawer
                title={getRes()["admin.setting"]}
                placement="right"
                closable={true}
                autoFocus={false}
                onClose={() => {
                    setSettingsOpen(false);
                }}
                styles={{
                    header: {
                        padding: 12,
                    },
                    body: {
                        padding: 12,
                        overflowX: "hidden",
                    },
                }}
                open={settingsOpen}
                //@ts-ignore
                getContainer={() => {
                    return containerRef.current;
                }}
            >
                <Col md={24} sm={24} xs={24} style={{ cursor: saving() ? "none" : "inherit", overflow: "hidden" }}>
                    <Row gutter={[8, 8]}>
                        <Col span={24}>
                            <Card
                                size="small"
                                title={
                                    <span style={{ textAlign: "start", display: "flex" }}>
                                        {getRes()["articleCover"]}
                                    </span>
                                }
                                style={{ textAlign: "center", marginTop: 6 }}
                            >
                                <ThumbnailUpload
                                    thumbnail={article.thumbnail}
                                    onChange={(e) => {
                                        handleValuesChange({ thumbnail: e });
                                    }}
                                />
                            </Card>
                        </Col>
                        <Col span={24}>
                            <Card size="small" title={getRes()["admin.setting"]}>
                                <Row>
                                    <Col xs={24} md={12}>
                                        <Form.Item
                                            style={{ marginBottom: 0 }}
                                            valuePropName="checked"
                                            label={getRes()["commentAble"]}
                                        >
                                            <Switch
                                                value={article.canComment}
                                                size="small"
                                                onChange={(checked) => {
                                                    handleValuesChange({ canComment: checked });
                                                }}
                                            />
                                        </Form.Item>
                                    </Col>
                                    <Col xs={24} md={12}>
                                        <Form.Item
                                            style={{ marginBottom: 0 }}
                                            valuePropName="checked"
                                            label={getRes()["private"]}
                                        >
                                            <Switch
                                                value={article.privacy}
                                                size="small"
                                                onChange={(checked) => {
                                                    handleValuesChange({ privacy: checked });
                                                }}
                                            />
                                        </Form.Item>
                                    </Col>
                                </Row>
                            </Card>
                        </Col>
                        <Col span={24}>
                            <Card size="small" title={getRes().tag}>
                                <ArticleEditTag
                                    onKeywordsChange={(text: string) => {
                                        handleValuesChange({ keywords: text });
                                    }}
                                    keywords={article!.keywords ? article.keywords : ""}
                                    allTags={tags.map((x: { text: any }) => x.text)}
                                />
                            </Card>
                        </Col>
                        <Col span={24}>
                            <Card size="small" title={getRes().digest} style={{ marginBottom: 36 }}>
                                <BaseTextArea
                                    ref={digestRef}
                                    variant={"borderless"}
                                    defaultValue={article.digest}
                                    placeholder={getRes().digestTips}
                                    rows={6}
                                    onChange={(text: string) => {
                                        handleValuesChange({ digest: text });
                                    }}
                                    style={{ padding: 0 }}
                                />
                            </Card>
                        </Col>
                    </Row>
                </Col>
            </Drawer>
        </>
    );
};
export default ArticleEditSettingButton;
