import { useEffect, useState } from "react";
import { EyeOutlined, SaveOutlined, SendOutlined } from "@ant-design/icons";
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
    editorInitSuccess: boolean;
    article: ArticleEntry;
    saving: ArticleSavingState;
};

type ArticleSavingState = {
    rubbishSaving: boolean;
    previewIng: boolean;
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
        position: fixed;
        z-index: 100000;
        top: 3px;
        right: 8px;
        min-width: 120px;
    }

    .saveToRubbish-btn-full-screen {
        position: fixed;
        z-index: 100000;
        top: 3px;
        right: 140px;
        min-width: 120px;
    }

    .saveToRubbish-btn-full-screen > button {
        display: flex;
        justify-content: flex-start;
        align-items: center;
    }

    .save-text-full-screen {
        position: fixed;
        z-index: 100000;
        top: 3px;
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

export type ArticleEditProps = {
    tags: any[];
    types: any[];
    article: ArticleEntry;
};

const dataToState = (data: ArticleEditProps) => {
    const a = {
        typeOptions: data.types
            ? data.types.map((x) => {
                  return { value: x.id, label: x.typeName };
              })
            : [],
        editorInitSuccess: false,
        fullScreen: false,
        tags: data.tags ? data.tags : [],
        rubbish: data.article && data.article.rubbish ? data.article.rubbish : false,
        article: data.article
            ? data.article
            : {
                  typeId: -1,
                  version: -1,
                  title: "",
                  keywords: "",
              },
        saving: {
            previewIng: false,
            releaseSaving: false,
            rubbishSaving: false,
        },
    };
    return a;
};

const Index = ({ data }: { data: ArticleEditProps }) => {
    const defaultState = dataToState(data);

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
                    },
                };
            });
        }
        exitTips(getRes()["articleEditExitWithOutSaveSuccess"]);
        let newArticle = {
            ...article,
            version: state.article.version,
            rubbish: !release,
        };
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
            exitNotTips();
            if (release) {
                message.success(getRes()["releaseSuccess"]);
            } else {
                if (!autoSave) {
                    message.info(getRes()["saveSuccess"]);
                }
            }
            if (preview) {
                window.open(document.baseURI + state.article!.alias, "_blank");
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
        } finally {
            if (release) {
                //@ts-ignore
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
                        },
                    };
                });
            } else {
                //@ts-ignore
                setState((prevState) => {
                    return {
                        ...prevState,
                        rubbish: true,
                        article: newArticle,
                        saving: {
                            ...prevState.saving,
                            rubbishSaving: false,
                            previewIng: false,
                        },
                    };
                });
            }
        }
    };

    const getArticleRoute = () => {
        if (state === undefined || getRes() === undefined || getRes()["articleRoute"] === undefined) {
            return "";
        }
        return getRes()["articleRoute"];
    };

    const onfullscreen = (editor: any) => {
        if (screenfull.isEnabled) {
            screenfull.request().then(() => {
                setState((prevState) => {
                    return {
                        ...prevState,
                        fullScreen: true,
                    };
                });
            });
            screenfull.on("change", () => {
                //@ts-ignore
                if (!screenfull.isFullscreen) {
                    editor.fullscreenExit();
                }
            });
        } else {
            setState((prevState) => {
                return {
                    ...prevState,
                    fullScreen: true,
                };
            });
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
        setState(dataToState(data));
    }, [data]);

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
        if (!state.rubbish) {
            return <Col xxl={3} md={3} sm={4} style={{ padding: 0 }} />;
        }
        let tips;
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

    return (
        <StyledArticleEdit>
            <Row gutter={[8, 8]} style={{ paddingTop: 20 }}>
                <Col md={12} xxl={15} sm={6} span={24}>
                    <Title className="page-header" style={{ marginTop: 0, marginBottom: 0 }} level={3}>
                        {getRes()["admin.log.edit"]}
                    </Title>
                </Col>
                {getRubbishText()}
                <Col xxl={2} md={3} sm={4} className={state.fullScreen ? "saveToRubbish-btn-full-screen" : ""}>
                    <Button
                        type={state.fullScreen ? "default" : "dashed"}
                        style={{ width: "100%" }}
                        onClick={() => onSubmit(state.article, false, false, false)}
                    >
                        <SaveOutlined hidden={state.saving.rubbishSaving} />
                        {state.saving.rubbishSaving ? getRes().saving : getRes().saveAsDraft}
                    </Button>
                </Col>
                <Col xxl={2} md={3} sm={4}>
                    <Button
                        type="dashed"
                        loading={state.saving.rubbishSaving && state.saving.previewIng}
                        style={{ width: "100%" }}
                        onClick={() => onSubmit(state.article, !state.rubbish, true, false)}
                    >
                        <EyeOutlined />
                        {getRes().preview}
                    </Button>
                </Col>
                <Col xxl={2} md={3} sm={4} className={state.fullScreen ? "save-btn-full-screen" : ""}>
                    <Button
                        type="primary"
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
            </Row>
            <Divider />
            <Row gutter={[8, 8]} style={{ paddingBottom: 8 }}>
                <Col md={13} xs={24}>
                    <Space.Compact style={{ display: "flex" }}>
                        <Select
                            style={{ minWidth: 156 }}
                            status={formValidState.typeError ? "error" : ""}
                            value={state.article.typeId}
                            showSearch={true}
                            optionFilterProp="children"
                            filterOption={(input, option) => (option?.label ?? "").includes(input)}
                            filterSort={(optionA, optionB) =>
                                (optionA?.label ?? "").toLowerCase().localeCompare((optionB?.label ?? "").toLowerCase())
                            }
                            onChange={async (value) => {
                                await handleValuesChange({ typeId: value });
                            }}
                            options={state.typeOptions}
                            placeholder={"请选择" + getRes()["admin.type.manage"]}
                        />
                        <BaseInput
                            status={formValidState.titleError ? "error" : ""}
                            placeholder={getRes().inputArticleTitle}
                            value={state.article.title ? state.article.title : undefined}
                            onChange={async (e) => {
                                await handleValuesChange({ title: e });
                            }}
                        />
                    </Space.Compact>
                </Col>
                <Col md={5} xs={24}>
                    <BaseInput
                        value={state.article.alias}
                        onChange={async (e) => {
                            await handleValuesChange({ alias: e });
                        }}
                        addonBefore={getArticleRoute() + "/"}
                        placeholder={getRes().inputArticleAlias}
                    />
                </Col>
            </Row>
            <Row gutter={[8, 8]}>
                <Col md={18} sm={24} xs={24} style={{ zIndex: 10 }}>
                    <MyEditorMdWrapper
                        key={data.article.version + "" + data.article.logId}
                        onfullscreen={onfullscreen}
                        onfullscreenExit={onfullscreenExit}
                        markdown={data.article.markdown}
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
                <Col md={6} sm={24} xs={24} style={{ cursor: isSaving() ? "none" : "inherit" }}>
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
                                    value={state.article.digest}
                                    placeholder={getRes().digestTips}
                                    rows={3}
                                    onChange={async (text: string) => {
                                        await handleValuesChange({ digest: text });
                                    }}
                                />
                            </Card>
                        </Col>
                    </Row>
                </Col>
            </Row>
        </StyledArticleEdit>
    );
};

export default Index;
