import { FunctionComponent, useEffect, useState } from "react";
import { EyeOutlined, FullscreenExitOutlined, FullscreenOutlined, SaveOutlined, SendOutlined } from "@ant-design/icons";
import { App, Button, Space } from "antd";
import Row from "antd/es/grid/row";
import Col from "antd/es/grid/col";
import Divider from "antd/es/divider";
import Title from "antd/es/typography/Title";
import Card from "antd/es/card";
import Switch from "antd/es/switch";
import MyEditorMdWrapper, { ChangedContent } from "./editor/my-editormd-wrapper";
import { getRes } from "../../utils/constants";
import screenfull from "screenfull";
import ArticleEditTag from "./article-edit-tag";
import axios from "axios";
import styled from "styled-components";
import TimeAgo from "../../common/TimeAgo";
import Select from "antd/es/select";
import ThumbnailUpload from "./thumbnail-upload";
import BaseInput from "../../common/BaseInput";
import BaseTextArea from "../../common/BaseTextArea";
import Form from "antd/es/form";
import { addToCache, deleteCacheDataByKey, getCacheByKey, getPageFullState, savePageFullState } from "../../cache";
import { getFullPath } from "../../utils/helpers";
import { useLocation } from "react-router";
import { isPWA } from "../../utils/env-utils";

export type ArticleEntry = ChangedContent &
    ThumbnailChanged &
    TitleChanged &
    AliasChanged &
    DigestChanged &
    KeywordsChanged &
    PrivacyChanged &
    CanCommentChanged &
    TypeChanged & {
        rubbish?: boolean;
        logId?: number;
        lastUpdateDate?: number;
        version: number;
    };

type ArticleEditState = {
    typeOptions: any[];
    tags: any[];
    rubbish: boolean;
    fullScreen: boolean;
    editorVersion: number;
    editorInitSuccess: boolean;
    article: ArticleEntry;
    saving: ArticleSavingState;
    offline: boolean;
};

type ArticleSavingState = {
    rubbishSaving: boolean;
    previewIng: boolean;
    autoSaving: boolean;
    releaseSaving: boolean;
};

export type ThumbnailChanged = {
    thumbnail?: string;
};

export type TitleChanged = {
    title: string;
};

export type AliasChanged = {
    alias?: string;
};

export type DigestChanged = {
    digest?: string;
};

export type KeywordsChanged = {
    keywords?: string;
};

export type TypeChanged = {
    typeId?: number;
};

export type CanCommentChanged = {
    canComment?: boolean;
};
export type PrivacyChanged = {
    privacy?: boolean;
};

const StyledArticleEdit = styled("div")`
    .ant-btn {
        -webkit-transition: none;
        box-shadow: none;
    }

    .save-btn-full-screen {
        min-width: 120px;
    }

    .saveToRubbish-btn-full-screen {
        min-width: 120px;
    }

    .saveToRubbish-btn-full-screen > button {
        display: flex;
        justify-content: flex-start;
        align-items: center;
    }

    .save-text-full-screen {
        right: 300px;
    }

    .anticon {
        color: inherit !important;
    }
`;
type FormValidState = {
    titleError: boolean;
    typeError: boolean;
};

type ArticleEditInfo = {
    tags: any[];
    types: any[];
    article: ArticleEntry;
};

export type ArticleEditProps = {
    data: ArticleEditInfo;
    onExitFullScreen: () => void;
    onFullScreen: () => void;
    offline: boolean;
};

const buildCacheKey = (newArticle: ArticleEntry) => {
    return "local-article-info-" + (newArticle && newArticle.logId && newArticle.logId > 0 ? newArticle.logId : -1);
};

const dataToState = (data: ArticleEditInfo, fullScreen: boolean, offline: boolean): ArticleEditState => {
    const article: ArticleEntry =
        data.article.logId && data.article.logId > 0
            ? data.article
            : {
                  version: -1,
                  title: "",
                  keywords: "",
              };
    const cachedArticle = getCacheByKey(buildCacheKey(article)) as ArticleEntry;
    const realArticle = {
        ...article,
        ...cachedArticle,
        //use input version
        version: data.article.version,
    };
    //console.info("debug => " + JSON.stringify(realArticle));
    return {
        offline: offline,
        typeOptions: data.types
            ? data.types.map((x) => {
                  return { value: x.id, label: x.typeName };
              })
            : [],
        editorInitSuccess: false,
        editorVersion: realArticle.version,
        fullScreen: fullScreen,
        tags: data.tags ? data.tags : [],
        rubbish: data.article && data.article.rubbish ? data.article.rubbish : false,
        article: realArticle,
        saving: {
            previewIng: false,
            releaseSaving: false,
            rubbishSaving: false,
            autoSaving: false,
        },
    };
};

const saveToCache = (article: ArticleEntry) => {
    addToCache(buildCacheKey(article), article);
};

const Index: FunctionComponent<ArticleEditProps> = ({ offline, data, onExitFullScreen, onFullScreen }) => {
    const location = useLocation();
    const defaultState = dataToState(data, getPageFullState(getFullPath(location)), offline);
    const articleEditPanel = "article-edit-panel";
    const [state, setState] = useState<ArticleEditState>(defaultState);

    const [content, setContent] = useState<ChangedContent | undefined>(undefined);
    const [formValidState, setFormValidState] = useState<FormValidState>({ titleError: false, typeError: false });

    const { message, modal } = App.useApp();

    const onSubmit = async (article: ArticleEntry, release: boolean, preview: boolean, autoSave: boolean) => {
        if (!validForm(article)) {
            return;
        }
        let uri;
        const create = article!.logId === undefined;
        if (create) {
            uri = "/api/admin/article/create";
        } else {
            uri = "/api/admin/article/update";
        }
        if (release) {
            setState((prevState) => {
                return {
                    ...prevState,
                    saving: {
                        ...prevState.saving,
                        releaseSaving: true,
                        autoSaving: autoSave,
                    },
                };
            });
        } else {
            setState((prevState) => {
                return {
                    ...prevState,
                    saving: {
                        ...prevState.saving,
                        rubbishSaving: true,
                        previewIng: preview,
                        autoSaving: autoSave,
                    },
                };
            });
        }
        let newArticle = {
            ...article,
            version: state.article.version,
            rubbish: !release,
        };
        const ck = buildCacheKey(newArticle);
        if (offline) {
            saveToCache(newArticle);
            setState((prevState) => {
                return {
                    ...prevState,
                    article: newArticle,
                    saving: {
                        ...prevState.saving,
                        releaseSaving: false,
                        rubbishSaving: false,
                        previewIng: false,
                        autoSaving: false,
                    },
                };
            });
            return;
        }
        exitTips(getRes()["articleEditExitWithOutSaveSuccess"]);
        try {
            const { data } = await axios.post(uri, newArticle);
            if (data.error) {
                modal.error({
                    title: "保存失败",
                    content: data.message,
                    okText: "确认",
                });
                return;
            }
            if (data.error === 0) {
                exitNotTips();
                if (release) {
                    message.success(getRes()["releaseSuccess"]);
                } else {
                    if (!autoSave) {
                        message.info(getRes()["saveSuccess"]);
                    }
                }
                if (preview) {
                    window.open(data.data["previewUrl"], "_blank");
                }
                const respData = data.data;
                if (create) {
                    const url = new URL(window.location.href);
                    url.searchParams.set("id", respData.logId);
                    window.history.replaceState(null, "", url.toString());
                    newArticle = { ...newArticle, ...respData };
                } else {
                    newArticle = {
                        ...newArticle,
                        thumbnail: respData.thumbnail,
                        lastUpdateDate: respData.lastUpdateDate,
                        version: respData.version,
                    };
                }
                deleteCacheDataByKey(ck);
            }
        } finally {
            if (release) {
                setState((prevState) => {
                    return {
                        ...prevState,
                        rubbish: false,
                        article: newArticle,
                        saving: {
                            ...prevState.saving,
                            releaseSaving: false,
                            rubbishSaving: false,
                            previewIng: false,
                            autoSaving: false,
                        },
                    };
                });
            } else {
                setState((prevState) => {
                    return {
                        ...prevState,
                        rubbish: true,
                        article: newArticle,
                        saving: {
                            ...prevState.saving,
                            rubbishSaving: false,
                            previewIng: false,
                            autoSaving: false,
                        },
                    };
                });
            }
        }
    };

    const doFullState = () => {
        setState((prevState) => {
            return {
                ...prevState,
                fullScreen: true,
            };
        });
    };

    const toggleFullScreen = () => {
        if (state.fullScreen) {
            onfullscreenExit();
        } else {
            onfullscreen();
        }
    };

    const onfullscreen = () => {
        try {
            if (screenfull.isEnabled) {
                screenfull.request(document.getElementById(articleEditPanel) as Element).then(() => {
                    doFullState();
                });
                screenfull.on("change", () => {
                    //@ts-ignore
                    if (!screenfull.isFullscreen) {
                        onfullscreenExit();
                    }
                });
            } else {
                doFullState();
            }
        } catch (e) {
            console.error(e);
            doFullState();
        } finally {
            onFullScreen();
        }
    };

    const onfullscreenExit = () => {
        setState((prevState) => {
            return {
                ...prevState,
                fullScreen: false,
            };
        });
        //@ts-ignore
        screenfull.exit();
        onExitFullScreen();
    };

    const exitTips = (tips: string) => {
        window.onbeforeunload = function () {
            return tips;
        };
    };

    const exitNotTips = () => {
        window.onbeforeunload = null;
    };

    let saving = false;

    const isSaving = () => {
        return state.saving.rubbishSaving || state.saving.releaseSaving || state.saving.previewIng || saving;
    };

    const save = async (article: ArticleEntry) => {
        //如果正在保存，尝试10ms后再检查下
        if (isSaving()) {
            setTimeout(() => {
                save(article);
            }, 10);
            return;
        }
        try {
            //copy log id
            if (state.article.logId && state.article.logId > 0) {
                article.logId = state.article.logId;
            }
            saving = true;
            await onSubmit(article, false, false, true);
        } finally {
            saving = false;
        }
    };

    useEffect(() => {
        if (content) {
            handleValuesChange(content).then();
        }
    }, [content]);

    useEffect(() => {
        const lastOffline = state.offline;
        //离线，保存到本地编辑
        if (!lastOffline && offline) {
            saveToCache(state.article);
            setState((prevState) => {
                return {
                    ...prevState,
                    offline: true,
                };
            });
            return;
        }
        const newSate = dataToState(data, getPageFullState(getFullPath(location)), offline);
        setState(newSate);
        //如果网络恢复，自动保存一次
        if (lastOffline && !offline) {
            handleValuesChange(newSate.article)
                .then(() => {
                    //ignore
                })
                .catch((e) => {
                    message.error(e);
                });
        }
    }, [data, offline]);

    useEffect(() => {
        if (state.fullScreen) {
            onfullscreen();
        }
        return () => {
            exitNotTips();
        };
    }, []);

    useEffect(() => {
        if (isPWA()) {
            savePageFullState(getFullPath(location), state.fullScreen);
        }
    }, [state.fullScreen]);

    const validForm = (changedArticle: ArticleEntry): boolean => {
        const titleError =
            changedArticle.title === undefined || changedArticle.title === null || changedArticle.title === "";
        const typeError =
            changedArticle.typeId === undefined || changedArticle.typeId === null || changedArticle.typeId < 0;
        setFormValidState({ titleError: titleError, typeError: typeError });
        return !(titleError || typeError);
    };

    const handleValuesChange = async (
        cv:
            | ArticleEntry
            | PrivacyChanged
            | CanCommentChanged
            | AliasChanged
            | TypeChanged
            | TitleChanged
            | KeywordsChanged
            | ChangedContent
            | ThumbnailChanged
            | DigestChanged
    ) => {
        const newArticle: ArticleEntry = {
            ...state.article,
            ...cv,
        };
        //console.info(cv);
        const ok = validForm(newArticle);
        if (!ok) {
            setState((prev) => {
                return { ...prev, article: newArticle };
            });
            return;
        }
        await save(newArticle);
    };

    const getRubbishText = () => {
        let tips;
        if (state.offline) {
            tips = getRes()["admin.offline.article-editing"];
        } else {
            if (!state.rubbish) {
                return <Col xxl={3} md={3} sm={4} style={{ padding: 0 }} />;
            }

            if (state.article.lastUpdateDate && state.article.lastUpdateDate > 0) {
                tips = (
                    <>
                        <TimeAgo timestamp={state.article.lastUpdateDate} />
                        更新
                    </>
                );
            } else {
                tips = "当前为草稿";
            }
        }
        return (
            <Col xxl={3} md={3} sm={4} className={state.fullScreen ? "save-text-full-screen" : ""}>
                <div
                    style={{
                        width: "100%",
                        display: "flex",
                        justifyContent: "center",
                        alignItems: "center",
                        textAlign: "center",
                        height: "32px",
                        paddingRight: "8px",
                        paddingLeft: "8px",
                    }}
                >
                    {tips}
                </div>
            </Col>
        );
    };

    const getActionBar = () => {
        return (
            <>
                <Col xxl={2} md={3} sm={4} className={state.fullScreen ? "saveToRubbish-btn-full-screen" : ""}>
                    <Button
                        type={state.fullScreen ? "default" : "dashed"}
                        style={{ width: "100%" }}
                        disabled={state.offline || (state.saving.rubbishSaving && !state.saving.autoSaving)}
                        onClick={async () => await onSubmit(state.article, false, false, false)}
                    >
                        <SaveOutlined hidden={state.saving.rubbishSaving} />
                        {state.saving.rubbishSaving ? getRes().saving : getRes().saveAsDraft}
                    </Button>
                </Col>
                {!state.fullScreen && (
                    <Col xxl={2} md={3} sm={4}>
                        <Button
                            type="dashed"
                            disabled={state.offline || (state.saving.previewIng && !state.saving.autoSaving)}
                            style={{ width: "100%" }}
                            onClick={async () => await onSubmit(state.article, !state.rubbish, true, false)}
                        >
                            <EyeOutlined />
                            {getRes().preview}
                        </Button>
                    </Col>
                )}
                <Col xxl={2} md={3} sm={4} className={state.fullScreen ? "save-btn-full-screen" : ""}>
                    <Button
                        type="primary"
                        disabled={state.offline}
                        loading={state.saving.releaseSaving}
                        style={{ width: "100%" }}
                        onClick={async () => {
                            await onSubmit(state.article, true, false, false);
                        }}
                    >
                        <SendOutlined />
                        {getRes().release}
                    </Button>
                </Col>
            </>
        );
    };

    return (
        <StyledArticleEdit>
            <Row gutter={[8, 8]} style={{ paddingTop: state.fullScreen ? 0 : 20 }}>
                <Col md={12} xxl={15} sm={6} span={24}>
                    <Title
                        className="page-header"
                        style={{ marginTop: 0, marginBottom: 0 }}
                        level={3}
                        hidden={state.fullScreen}
                    >
                        {getRes()["admin.log.edit"]}
                    </Title>
                </Col>
                {getRubbishText()}
                {!state.fullScreen && getActionBar()}
            </Row>
            {!state.fullScreen && <Divider style={{ marginTop: 16, marginBottom: 8 }} />}
            <Card
                title={""}
                id={articleEditPanel}
                styles={{
                    body: {
                        padding: 0,
                        overflow: "hidden",
                    },
                }}
            >
                <Row gutter={[8, 8]} style={{ position: "relative", borderBottom: "1px solid #DDD" }}>
                    <Col md={12} xs={24}>
                        <BaseInput
                            maxLength={96}
                            variant={"borderless"}
                            size={"large"}
                            status={formValidState.titleError ? "error" : ""}
                            placeholder={getRes().inputArticleTitle}
                            value={state.article.title ? state.article.title : undefined}
                            onChange={async (e) => {
                                await handleValuesChange({ title: e });
                            }}
                            style={{ fontSize: 22, fontWeight: 500 }}
                        />
                    </Col>
                    <Col md={6} xs={24} style={{ display: "flex", alignItems: "center" }}>
                        <Space.Compact style={{ display: "flex" }} hidden={state.fullScreen}>
                            <Select
                                getPopupContainer={(triggerNode) => triggerNode.parentElement}
                                variant={"borderless"}
                                style={{
                                    minWidth: 156,
                                    paddingLeft: 0,
                                    display: "flex",
                                }}
                                size={"large"}
                                status={formValidState.typeError ? "error" : ""}
                                value={state.article.typeId}
                                showSearch={true}
                                optionFilterProp="children"
                                filterOption={(input, option) => (option?.label ?? "").includes(input)}
                                filterSort={(optionA, optionB) =>
                                    (optionA?.label ?? "")
                                        .toLowerCase()
                                        .localeCompare((optionB?.label ?? "").toLowerCase())
                                }
                                onChange={async (value) => {
                                    await handleValuesChange({ typeId: value });
                                }}
                                options={state.typeOptions}
                                placeholder={getRes()["pleaseChoose"] + getRes()["admin.type.manage"]}
                            />
                            <BaseInput
                                value={state.article.alias}
                                onChange={async (e) => {
                                    await handleValuesChange({ alias: e });
                                }}
                                maxLength={256}
                                size={"large"}
                                variant={"borderless"}
                                placeholder={getRes().inputArticleAlias}
                                style={{ fontSize: 16, paddingLeft: 0 }}
                            />
                        </Space.Compact>
                    </Col>
                    <Col
                        md={6}
                        style={{
                            display: "flex",
                            alignItems: "center",
                            justifyContent: "flex-end",
                            position: "absolute",
                            right: 0,
                            top: 0,
                        }}
                    >
                        {state.fullScreen && getActionBar()}
                        <div
                            style={{
                                display: "flex",
                                justifyContent: "center",
                                alignItems: "center",
                                width: 48,
                                minWidth: 48,
                                height: 48,
                                fontSize: 24,
                                cursor: "pointer",
                                color: "rgb(102, 102, 102)",
                            }}
                            onClick={() => toggleFullScreen()}
                        >
                            {state.fullScreen ? <FullscreenExitOutlined /> : <FullscreenOutlined />}
                        </div>
                    </Col>
                </Row>
                <Row gutter={[state.fullScreen ? 0 : 8, state.fullScreen ? 0 : 8]}>
                    <Col
                        md={state.fullScreen ? 24 : 18}
                        sm={24}
                        xs={24}
                        style={{ minHeight: state.fullScreen ? 0 : 1 }}
                    >
                        <MyEditorMdWrapper
                            key={data.article.logId + "_" + state.editorVersion + "_offline:" + state.offline}
                            markdown={state.article.markdown}
                            onChange={async (v) => {
                                if (
                                    v.markdown === "" &&
                                    (state.article.markdown === "" ||
                                        state.article.markdown === undefined ||
                                        state.article.markdown === null)
                                ) {
                                    return;
                                }
                                //不检查 content，避免因为 markdown 渲染库升级，载入文章时自动更新为草稿
                                if (v.markdown === state.article.markdown) {
                                    return;
                                }
                                setContent(v);
                            }}
                        />
                    </Col>
                    <Col
                        md={state.fullScreen ? 0 : 6}
                        sm={state.fullScreen ? 0 : 24}
                        xs={state.fullScreen ? 0 : 24}
                        style={{ cursor: isSaving() ? "none" : "inherit" }}
                    >
                        <Row gutter={[8, 8]}>
                            <Col span={24}>
                                <Card size="small" style={{ textAlign: "center" }}>
                                    <ThumbnailUpload
                                        thumbnail={state.article.thumbnail}
                                        onChange={async (e) => {
                                            await handleValuesChange({ thumbnail: e });
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
                                                    value={state.article.canComment}
                                                    size="small"
                                                    onChange={async (checked) => {
                                                        await handleValuesChange({ canComment: checked });
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
                                                    value={state.article.privacy}
                                                    size="small"
                                                    onChange={async (checked) => {
                                                        await handleValuesChange({ privacy: checked });
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
                                        onKeywordsChange={async (text: string) => {
                                            await handleValuesChange({ keywords: text });
                                        }}
                                        keywords={state.article!.keywords ? state.article.keywords : ""}
                                        allTags={state.tags.map((x) => x.text)}
                                    />
                                </Card>
                            </Col>
                            <Col span={24}>
                                <Card size="small" title={getRes().digest}>
                                    <BaseTextArea
                                        variant={"borderless"}
                                        value={state.article.digest}
                                        placeholder={getRes().digestTips}
                                        rows={3}
                                        onChange={async (text: string) => {
                                            await handleValuesChange({ digest: text });
                                        }}
                                        style={{ padding: 0 }}
                                    />
                                </Card>
                            </Col>
                        </Row>
                    </Col>
                </Row>
            </Card>
        </StyledArticleEdit>
    );
};

export default Index;
