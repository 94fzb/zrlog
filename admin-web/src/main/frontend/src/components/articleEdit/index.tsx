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
import { useLocation } from "react-router";
import EnvUtils from "../../utils/env-utils";
import EditorStatistics, { toStatisticsByMarkdown } from "./editor/editor-statistics-info";
import { commonAxiosErrorHandle, createAxiosBaseInstance } from "../../AppBase";
import ArticleEditSettingButton from "./article-edit-setting-button";
import { ArticleChangeableValue, ArticleEditProps, ArticleEditState, ArticleEntry } from "./index.types";
import ArticleEditActionBar from "./article-edit-action-bar";
import { articleDataToState, articleSaveToCache, deleteArticleCacheWithPageCache } from "../../utils/article-cache";
import ArticleEditFullscreenButton from "./article-edit-fullscreen-button";
import { auditTime, concatMap, Subject, tap } from "rxjs";
import { Subscription } from "rxjs/internal/Subscription";

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

const Index: FunctionComponent<ArticleEditProps> = ({
    offline,
    data,
    onExitFullScreen,
    onFullScreen,
    fullScreen,
    deleteStateCacheOnDestroy,
}) => {
    const location = useLocation();
    const editCardRef = useRef<HTMLDivElement>(null);

    const defaultState = articleDataToState(data, offline);
    const [state, setState] = useState<ArticleEditState>(defaultState);

    const aliasRef = useRef<InputRef>(null);
    const digestRef = useRef<InputRef>(null);
    const versionRef = useRef<number>(defaultState.article.version);
    const subjectRef = useRef<Subject<ArticleEntry> | null>(null);
    const subRef = useRef<Subscription | null>(null);
    const deletePageStateRef = useRef<boolean>(false);
    let pendingMessages = 0;

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
            messageApi.error({ content: getRes()["article_require_title"] });
            return;
        }
        if (isTypeError(article)) {
            messageApi.error(getRes()["article_require_type"]);
            return;
        }
        //非自动保存的情况下，需要清空当前缓存队列
        if (!autoSave) {
            setSubject();
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
        enableExitTips();
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
                if (pendingMessages === 0) {
                    disableExitTips();
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
                deleteArticleCacheWithPageCache(newArticle, location);
            }
        } finally {
            // 根据 release 的值调用对应的状态更新回调函数
            release ? updateReleaseState(newArticle, create) : updateRubbishState(newArticle, create);
            deletePageStateRef.current = true;
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

    const enableExitTips = () => {
        window.onbeforeunload = function () {
            return getRes()["articleEditExitWithOutSaveSuccess"];
        };
    };

    const disableExitTips = () => {
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
        const newState = articleDataToState(data, offline);
        //如果网络恢复，自动保存一次
        if (lastOffline && !offline) {
            handleValuesChange(newState.article);
        } else {
            //仅设置状态，同时覆盖版本信息
            versionRef.current = newState.article.version;
            setState(newState);
        }
    }, [data, offline]);

    const setSubject = () => {
        if (subRef.current) {
            subRef.current.unsubscribe();
        }
        subjectRef.current = new Subject();
        // 订阅 Subject，只在 2 秒内没有新事件时更新状态
        const subscription = subjectRef.current
            .pipe(
                tap(() => {
                    enableExitTips();
                }),
                auditTime(2000),
                tap(() => {
                    pendingMessages += 1; // 有新消息进入，标记为“待处理”
                }),
                concatMap(async (article) => {
                    // 确保顺序执行
                    pendingMessages -= 1;
                    //console.log("Submitting:", nextValue);
                    if (state.article.logId && state.article.logId > 0) {
                        article.logId = state.article.logId;
                    }
                    await onSubmit(article, false, false, true);
                })
            )
            .subscribe();
        if (subRef.current) {
            subRef.current = subscription;
        }
    };

    useEffect(() => {
        // 初始化 Subject，仅在组件挂载时创建一次
        setSubject();
        // 在组件卸载时清理订阅
        return () => {
            if (subRef.current) {
                subRef.current.unsubscribe();
            }
            if (deletePageStateRef.current) {
                deleteStateCacheOnDestroy();
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

    const handleValuesChange = (cv: ArticleChangeableValue) => {
        setState((prev) => {
            const newArticle = { ...prev.article, ...cv };
            const sub = subjectRef.current;
            const ok = validForm(newArticle);
            if (ok && sub) {
                sub.next(newArticle);
            }
            return { ...prev, article: newArticle };
        });
    };

    const editorHeight = fullScreen ? window.innerHeight - 47 : `calc(100vh - 200px)`;

    return (
        <StyledArticleEdit>
            <Row gutter={[8, 8]} style={{ paddingTop: fullScreen ? 0 : 20 }}>
                <Col md={12} xxl={15} sm={6} span={24}>
                    <Title
                        className="page-header"
                        style={{ marginTop: 0, marginBottom: 0 }}
                        level={3}
                        hidden={fullScreen}
                    >
                        {getRes()["admin.log.edit"]}
                    </Title>
                </Col>
                {!fullScreen && <ArticleEditActionBar fullScreen={fullScreen} data={state} onSubmit={onSubmit} />}
            </Row>
            {!fullScreen && <Divider style={{ marginTop: 16, marginBottom: 16 }} />}
            {messageContextHolder}
            <Card
                title={""}
                ref={editCardRef}
                style={{
                    borderRadius: fullScreen ? 0 : 8,
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
                            key={data.article.version}
                            placeholder={getRes().inputArticleTitle}
                            defaultValue={state.article.title ? state.article.title : undefined}
                            onChange={(e) => {
                                handleValuesChange({ title: e });
                            }}
                            style={{ fontSize: 22, fontWeight: 500, textOverflow: "ellipsis" }}
                        />
                    </Col>
                    <Col md={6} xs={24} style={{ display: "flex", alignItems: "center" }}>
                        <Space.Compact style={{ display: "flex" }} hidden={fullScreen}>
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
                                onChange={(value) => {
                                    handleValuesChange({ typeId: value });
                                }}
                                options={state.typeOptions}
                                placeholder={getRes()["pleaseChoose"] + getRes()["admin.type.manage"]}
                            />
                            <BaseInput
                                ref={aliasRef}
                                defaultValue={state.article.alias}
                                onChange={(e) => {
                                    handleValuesChange({ alias: e });
                                }}
                                key={data.article.version}
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
                        {fullScreen && (
                            <ArticleEditActionBar fullScreen={fullScreen} data={state} onSubmit={onSubmit} />
                        )}
                        <ArticleEditSettingButton
                            digestRef={digestRef}
                            article={state.article}
                            saving={() => isSaving()}
                            tags={state.tags}
                            containerRef={editCardRef}
                            handleValuesChange={handleValuesChange}
                        />
                        <ArticleEditFullscreenButton
                            fullScreen={fullScreen}
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
                <Row gutter={[fullScreen ? 0 : 8, fullScreen ? 0 : 8]}>
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
                                fullScreen +
                                "_" +
                                state.editorVersion +
                                "_offline:" +
                                state.offline
                            }
                            markdown={state.article.markdown}
                            onChange={(v) => {
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
                                handleValuesChange(v);
                            }}
                        />
                        <EditorStatistics
                            data={toStatisticsByMarkdown(state.article.markdown)}
                            fullScreen={fullScreen}
                        />
                    </Col>
                </Row>
            </Card>
        </StyledArticleEdit>
    );
};

export default Index;
