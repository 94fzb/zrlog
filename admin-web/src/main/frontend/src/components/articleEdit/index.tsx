import { FunctionComponent, useEffect, useRef, useState } from "react";
import { EyeOutlined, FullscreenExitOutlined, FullscreenOutlined, SaveOutlined, SendOutlined } from "@ant-design/icons";
import { App, Button, message, Space } from "antd";
import Row from "antd/es/grid/row";
import Col from "antd/es/grid/col";
import Divider from "antd/es/divider";
import Title from "antd/es/typography/Title";
import Card from "antd/es/card";
import MyEditorMdWrapper, { ChangedContent } from "./editor/my-editormd-wrapper";
import { createUri, getRes, updateUri } from "../../utils/constants";
import screenfull from "screenfull";
import styled from "styled-components";
import TimeAgo from "../../common/TimeAgo";
import Select from "antd/es/select";
import BaseInput from "../../common/BaseInput";
import { addToCache, deleteCacheDataByKey, getCacheByKey, getPageFullState, savePageFullState } from "../../cache";
import { getFullPath } from "../../utils/helpers";
import { useLocation } from "react-router";
import EnvUtils, { isPWA } from "../../utils/env-utils";
import EditorStatistics, { toStatisticsByMarkdown } from "./editor/editor-statistics-info";
import { commonAxiosErrorHandle, createAxiosBaseInstance } from "../../AppBase";
import ArticleEditSettingButton from "./article-edit-setting-button";

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

    @media screen and (max-width: 560px) {
        #action-bar {
            flex-flow: column;
            gap: 8px;
            width: 100%;
        }
    }

    @media screen and (max-width: 767px) {
        .item {
            flex: auto;
        }
    }
`;

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

export type ArticleChangeableValue =
    | ArticleEntry
    | PrivacyChanged
    | CanCommentChanged
    | AliasChanged
    | TypeChanged
    | TitleChanged
    | KeywordsChanged
    | ChangedContent
    | ThumbnailChanged
    | DigestChanged;

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

let editorInstance: { width: (arg0: string) => void };

const Index: FunctionComponent<ArticleEditProps> = ({ offline, data, onExitFullScreen, onFullScreen }) => {
    const location = useLocation();
    const editCardRef = useRef<HTMLDivElement>(null);

    const defaultState = dataToState(data, getPageFullState(getFullPath(location)), offline);
    const [state, setState] = useState<ArticleEditState>(defaultState);

    const [content, setContent] = useState<ChangedContent | undefined>(undefined);

    const [messageApi, messageContextHolder] = message.useMessage({
        maxCount: 3,
        getContainer: () => editCardRef.current as HTMLElement,
    });
    const { modal } = App.useApp();
    const onSubmit = async (article: ArticleEntry, release: boolean, preview: boolean, autoSave: boolean) => {
        if (isTitleError(article)) {
            messageApi.error({ content: "文章标题不能为空" });
            return;
        }
        if (isTypeError(article)) {
            messageApi.error("文章分类不能为空");
            return;
        }
        let uri;
        const create = article!.logId === undefined;
        if (create) {
            uri = createUri;
        } else {
            uri = updateUri;
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
            let responseData;
            try {
                const { data } = await createAxiosBaseInstance().post(uri, newArticle);
                responseData = data;
                if (data.error) {
                    modal.error({
                        title: "保存失败",
                        content: data.message,
                        okText: "确认",
                        getContainer: () => editCardRef.current as HTMLElement,
                    });
                    return;
                }
            } catch (e) {
                return commonAxiosErrorHandle(e, modal, messageApi, editCardRef.current as HTMLElement);
            }
            const data = responseData;
            if (data.error === 0) {
                exitNotTips();
                if (!autoSave) {
                    messageApi.info(data.message);
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
        if (editorInstance) {
            editorInstance.width("100%");
        }
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
                screenfull
                    .request(editCardRef.current as Element)
                    .then(() => {
                        doFullState();
                    })
                    .catch((e) => {
                        console.error(e);
                        doFullState();
                    });
                screenfull.on("change", () => {
                    if (screenfull.isEnabled && !screenfull.isFullscreen) {
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
        if (editorInstance) {
            editorInstance.width("100%");
        }
        onExitFullScreen();
        if (screenfull.isEnabled) {
            screenfull.exit().catch((e) => {
                console.error(e);
            });
        }
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
                .catch(async (e) => {
                    await messageApi.error(e);
                });
        }
    }, [data, offline]);

    useEffect(() => {
        if (state.fullScreen && isPWA()) {
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

    const isTitleError = (changedArticle: ArticleEntry) => {
        return changedArticle.title === undefined || changedArticle.title === null || changedArticle.title === "";
    };

    const isTypeError = (changedArticle: ArticleEntry) => {
        return changedArticle.typeId === undefined || changedArticle.typeId === null || changedArticle.typeId < 0;
    };

    const validForm = (changedArticle: ArticleEntry): boolean => {
        const titleError = isTitleError(changedArticle);
        const typeError = isTypeError(changedArticle);
        return !(titleError || typeError);
    };

    const handleValuesChange = async (cv: ArticleChangeableValue) => {
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
            <Button
                type={"default"}
                className={state.fullScreen ? "saveToRubbish-btn-full-screen" : "item"}
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
                    backgroundColor: state.fullScreen ? (EnvUtils.isDarkMode() ? "rgb(20 20 20)" : "white") : "inherit",
                }}
            >
                {tips}
            </Button>
        );
    };

    const getActionBar = () => {
        return (
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
                    className={state.fullScreen ? "saveToRubbish-btn-full-screen" : "item"}
                >
                    {getRubbishText()}
                </Col>
                <Col xxl={6} md={9} sm={24} className={state.fullScreen ? "saveToRubbish-btn-full-screen" : "item"}>
                    <Button
                        type={state.fullScreen ? "default" : "dashed"}
                        style={{ width: "100%", maxWidth: 256 }}
                        disabled={state.offline || (state.saving.rubbishSaving && !state.saving.autoSaving)}
                        onClick={async () => await onSubmit(state.article, false, false, false)}
                    >
                        <SaveOutlined hidden={state.saving.rubbishSaving} />
                        {state.saving.rubbishSaving ? getRes().saving : getRes().saveAsDraft}
                    </Button>
                </Col>
                <Col xxl={6} md={9} sm={24} className={"item"} style={{ display: state.fullScreen ? "none" : "flex" }}>
                    <Button
                        type="dashed"
                        disabled={state.offline || (state.saving.previewIng && !state.saving.autoSaving)}
                        style={{ width: "100%", maxWidth: 256 }}
                        onClick={async () => await onSubmit(state.article, !state.rubbish, true, false)}
                    >
                        <EyeOutlined />
                        {getRes().preview}
                    </Button>
                </Col>
                <Col xxl={6} md={9} sm={24} className={state.fullScreen ? "save-btn-full-screen" : "item"}>
                    <Button
                        type="primary"
                        disabled={state.offline}
                        loading={state.saving.releaseSaving}
                        style={{ width: "100%", maxWidth: 256 }}
                        onClick={async () => {
                            await onSubmit(state.article, true, false, false);
                        }}
                    >
                        <SendOutlined />
                        {state.article.privacy === true ? getRes()["save"] : getRes().release}
                    </Button>
                </Col>
            </Col>
        );
    };

    const editorHeight = state.fullScreen ? window.innerHeight - 47 : `calc(100vh - 200px)`;

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
                {!state.fullScreen && getActionBar()}
            </Row>
            {!state.fullScreen && <Divider style={{ marginTop: 16, marginBottom: 16 }} />}
            {messageContextHolder}
            <Card
                title={""}
                ref={editCardRef}
                style={{
                    borderRadius: state.fullScreen ? 0 : 8,
                }}
                styles={{
                    body: {
                        padding: 0,
                        overflow: "hidden",
                    },
                }}
            >
                <Row
                    gutter={[8, 8]}
                    style={{
                        position: "relative",
                        borderBottom: EnvUtils.isDarkMode() ? "1px solid rgba(253, 253, 253, 0.12)" : "1px solid #DDD",
                    }}
                >
                    <Col md={12} xs={24}>
                        <BaseInput
                            maxLength={100}
                            variant={"borderless"}
                            size={"large"}
                            placeholder={getRes().inputArticleTitle}
                            value={state.article.title ? state.article.title : undefined}
                            onChange={async (e) => {
                                await handleValuesChange({ title: e });
                            }}
                            style={{ fontSize: 22, fontWeight: 500, textOverflow: "ellipsis" }}
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
                                    zIndex: 20,
                                }}
                                size={"large"}
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
                                style={{ fontSize: 16, paddingLeft: 0, textOverflow: "ellipsis" }}
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
                        <ArticleEditSettingButton
                            article={state.article}
                            saving={() => isSaving()}
                            tags={state.tags}
                            containerRef={editCardRef}
                            handleValuesChange={handleValuesChange}
                        />
                        <Button
                            type={"default"}
                            icon={
                                state.fullScreen ? (
                                    <FullscreenExitOutlined style={{ fontSize: 24 }} />
                                ) : (
                                    <FullscreenOutlined style={{ fontSize: 24 }} />
                                )
                            }
                            href={
                                state.fullScreen
                                    ? window.location.pathname + "#exitFullScreen"
                                    : window.location.pathname + "#enterFullScreen"
                            }
                            style={{
                                border: 0,
                                display: "flex",
                                justifyContent: "center",
                                alignItems: "center",
                                width: 46,
                                minWidth: 46,
                                borderRadius: 8,
                                height: 46,
                                fontSize: 24,
                                cursor: "pointer",
                                color: "rgb(102, 102, 102)",
                                background: EnvUtils.isDarkMode() ? "#141414" : "white",
                            }}
                            onClick={(e) => {
                                e.stopPropagation();
                                e.preventDefault();
                                toggleFullScreen();
                            }}
                        ></Button>
                    </Col>
                </Row>
                <Row gutter={[state.fullScreen ? 0 : 8, state.fullScreen ? 0 : 8]}>
                    <Col
                        md={24}
                        sm={24}
                        xs={24}
                        style={{
                            paddingRight: 0,
                        }}
                    >
                        <MyEditorMdWrapper
                            height={editorHeight}
                            loadSuccess={(editor) => {
                                editorInstance = editor;
                            }}
                            key={
                                data.article.logId +
                                "_" +
                                state.fullScreen +
                                "_" +
                                state.editorVersion +
                                "_offline:" +
                                state.offline
                            }
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
                        <EditorStatistics
                            data={toStatisticsByMarkdown(state.article.markdown)}
                            fullScreen={state.fullScreen}
                        />
                    </Col>
                </Row>
            </Card>
        </StyledArticleEdit>
    );
};

export default Index;
