import { FunctionComponent, useEffect, useRef, useState } from "react";
import { App, InputRef, message, Space } from "antd";
import Row from "antd/es/grid/row";
import Col from "antd/es/grid/col";
import Divider from "antd/es/divider";
import Title from "antd/es/typography/Title";
import Card from "antd/es/card";
import MyEditorMdWrapper from "./editor/my-editormd-wrapper";
import { createUri, getRes, updateUri } from "../../utils/constants";
import styled from "styled-components";
import Select from "antd/es/select";
import BaseInput from "../../common/BaseInput";
import { getPageFullState } from "../../cache";
import { getFullPath } from "../../utils/helpers";
import { useLocation } from "react-router";
import EnvUtils from "../../utils/env-utils";
import EditorStatistics, { toStatisticsByMarkdown } from "./editor/editor-statistics-info";
import { commonAxiosErrorHandle, createAxiosBaseInstance } from "../../AppBase";
import ArticleEditSettingButton from "./article-edit-setting-button";
import { ArticleEntry, ArticleChangeableValue, ArticleEditProps, ArticleEditState } from "./index.types";
import ArticleEditActionBar from "./article-edit-action-bar";
import { articleDataToState, articleSaveToCache, deleteArticleCache } from "../../utils/article-cache";
import ArticleEditFullscreenButton from "./article-edit-fullscreen-button";

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

let editorInstance: { width: (arg0: string) => void };

const Index: FunctionComponent<ArticleEditProps> = ({ offline, data, onExitFullScreen, onFullScreen }) => {
    const location = useLocation();
    const editCardRef = useRef<HTMLDivElement>(null);

    const defaultState = articleDataToState(data, getPageFullState(getFullPath(location)), offline);
    const [state, setState] = useState<ArticleEditState>(defaultState);

    const aliasRef = useRef<InputRef>(null);
    const digestRef = useRef<InputRef>(null);
    const versionRef = useRef<number>(defaultState.article.version);
    const changeQueue = useRef<ArticleEntry[]>([]);
    const isProcessing = useRef(false); // 使用 ref 来持久化处理状态
    const savingTimer = useRef<NodeJS.Timeout | null>(null);

    const [messageApi, messageContextHolder] = message.useMessage({
        maxCount: 3,
        getContainer: () => editCardRef.current as HTMLElement,
    });
    const { modal } = App.useApp();

    const updateRubbishState = (newArticle: ArticleEntry, create: boolean) => {
        setState((prevState) => ({
            ...prevState,
            rubbish: true,
            article: doMergeArticle(prevState.article, newArticle, create),
            saving: {
                ...prevState.saving,
                rubbishSaving: false,
                previewIng: false,
                autoSaving: false,
            },
        }));
    };

    const updateReleaseState = (newArticle: ArticleEntry, create: boolean) => {
        setState((prevState) => ({
            ...prevState,
            rubbish: false,
            article: doMergeArticle(prevState.article, newArticle, create),
            saving: {
                ...prevState.saving,
                releaseSaving: false,
                rubbishSaving: false,
                previewIng: false,
                autoSaving: false,
            },
        }));
    };

    const onSubmit = async (article: ArticleEntry, release: boolean, preview: boolean, autoSave: boolean) => {
        if (isTitleError(article)) {
            messageApi.error({ content: "文章标题不能为空" });
            return;
        }
        if (isTypeError(article)) {
            messageApi.error("文章分类不能为空");
            return;
        }
        //非自动保存的情况下，需要清空当前缓存队列
        if (!autoSave) {
            changeQueue.current = [];
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
            version: versionRef.current,
            rubbish: !release,
        };
        if (offline) {
            articleSaveToCache(newArticle);
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
                versionRef.current = data.data.version;
            } catch (e) {
                return commonAxiosErrorHandle(e, modal, messageApi, editCardRef.current as HTMLElement);
            }
            const data = responseData;
            if (data.error === 0) {
                //没有堆积的消息了，才能触发移除强制离开页面的提示
                if (changeQueue.current.length == 0) {
                    exitNotTips();
                }
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
                deleteArticleCache(newArticle);
            }
        } finally {
            // 根据 release 的值调用对应的状态更新回调函数
            release ? updateReleaseState(newArticle, create) : updateRubbishState(newArticle, create);
        }
    };

    const doMergeArticle = (
        stateArticle: ArticleEntry,
        updateResponseArticle: ArticleEntry,
        create: boolean
    ): ArticleEntry => {
        const mergeArticle = {
            ...updateResponseArticle,
            logId: updateResponseArticle.logId,
            lastUpdateDate: updateResponseArticle.lastUpdateDate,
            version: updateResponseArticle.version,
            thumbnail:
                stateArticle.thumbnail && stateArticle.thumbnail.trim().length > 0
                    ? stateArticle.thumbnail
                    : updateResponseArticle.thumbnail,
        };
        //处理文章别名
        if (aliasRef.current && aliasRef.current.input) {
            if (aliasRef.current.input.value.trim().length === 0 && create) {
                mergeArticle.alias = updateResponseArticle.alias;
                aliasRef.current.input.value = updateResponseArticle.alias as string;
            } else {
                mergeArticle.alias = aliasRef.current.input.value;
            }
        }
        //处理摘要
        if (digestRef.current && digestRef.current.input) {
            if (digestRef.current.input.value.trim().length === 0 && create) {
                mergeArticle.digest = updateResponseArticle.digest;
                digestRef.current.input.value = updateResponseArticle.digest as string;
            } else {
                mergeArticle.digest = digestRef.current.input.value;
            }
        }
        return mergeArticle;
    };

    const exitTips = (tips: string) => {
        window.onbeforeunload = function () {
            return tips;
        };
    };

    const exitNotTips = () => {
        window.onbeforeunload = null;
    };

    const isSaving = () => {
        return state.saving.rubbishSaving || state.saving.releaseSaving || state.saving.previewIng;
    };

    useEffect(() => {
        const lastOffline = state.offline;
        //离线，保存到本地编辑
        if (!lastOffline && offline) {
            articleSaveToCache(state.article);
            setState((prevState) => {
                return {
                    ...prevState,
                    offline: true,
                };
            });
            return;
        }
        const newSate = articleDataToState(data, getPageFullState(getFullPath(location)), offline);
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
        autoSaveChange();
        return () => {
            if (savingTimer.current) {
                clearTimeout(savingTimer.current);
            }
        };
    }, []);

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

    const autoSaveChange = () => {
        savingTimer.current = setTimeout(() => {
            processQueue();
        }, 20);
    };

    const processQueue = async () => {
        try {
            // 如果当前正在处理请求，直接返回
            if (isProcessing.current || changeQueue.current.length === 0) {
                return;
            }

            // 标记为正在处理
            isProcessing.current = true;

            while (changeQueue.current.length > 0) {
                // 获取队列的第一个值并进行处理
                const article = changeQueue.current.shift();
                if (article) {
                    const ok = validForm(article);
                    if (!ok) {
                        continue;
                    }
                    //console.log("Submitting:", nextValue);
                    if (state.article.logId && state.article.logId > 0) {
                        article.logId = state.article.logId;
                    }
                    await onSubmit(article, false, false, true);
                }
            }
            // 重置处理状态
            isProcessing.current = false;
        } finally {
            autoSaveChange();
        }
    };

    const handleValuesChange = async (cv: ArticleChangeableValue) => {
        setState((prev) => {
            const n = { ...prev.article, ...cv };
            changeQueue.current.push(n);
            return { ...prev, article: n };
        });
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
                {!state.fullScreen && <ArticleEditActionBar data={state} onSubmit={onSubmit} />}
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
                            defaultValue={state.article.title ? state.article.title : undefined}
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
                                ref={aliasRef}
                                defaultValue={state.article.alias}
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
                        {state.fullScreen && <ArticleEditActionBar data={state} onSubmit={onSubmit} />}
                        <ArticleEditSettingButton
                            digestRef={digestRef}
                            article={state.article}
                            saving={() => isSaving()}
                            tags={state.tags}
                            containerRef={editCardRef}
                            handleValuesChange={handleValuesChange}
                        />
                        <ArticleEditFullscreenButton
                            fullScreenElement={editCardRef.current as HTMLDivElement}
                            editorInstance={editorInstance}
                            onExitFullScreen={onExitFullScreen}
                            onFullScreen={onFullScreen}
                            onChange={(full) => {
                                setState((prevState) => {
                                    return {
                                        ...prevState,
                                        fullScreen: full,
                                    };
                                });
                            }}
                        />
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
                                await handleValuesChange(v);
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
