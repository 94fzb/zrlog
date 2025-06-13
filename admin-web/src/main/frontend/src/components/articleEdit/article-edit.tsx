import { useEffect, useRef, useState } from "react";
import { CameraOutlined, EyeOutlined, SaveOutlined, SendOutlined } from "@ant-design/icons";
import { Button, FormInstance, Input, Modal, Radio } from "antd";
import Form from "antd/es/form";
import Row from "antd/es/grid/row";
import Col from "antd/es/grid/col";
import Divider from "antd/es/divider";
import Title from "antd/es/typography/Title";
import Card from "antd/es/card";
import Dragger from "antd/es/upload/Dragger";
import Switch from "antd/es/switch";
import TextArea from "antd/es/input/TextArea";
import { message } from "antd/es";
import Image from "antd/es/image";
import jquery from "jquery";
import MyEditorMdWrapper, { ChangedContent } from "./editor/my-editormd-wrapper";
import { getRes } from "../../utils/constants";
import screenfull from "screenfull";
import ArticleEditTag from "./article-edit-tag";
import axios from "axios";
import { UploadChangeParam } from "antd/es/upload";
import styled from "styled-components";
import { apiBasePath } from "../../index";

export type ArticleEntry = ChangedContent &
    ThumbnailChanged & {
        keywords: string;
        alias?: string;
        logId?: number;
        digest?: string;
        version: number;
        title?: string;
        typeId?: number;
    };

type ArticleEditState = {
    types: [];
    typeOptions: any[];
    tags: any[];
    rubbish: boolean;
    globalLoading: boolean;
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
        width: 120px;
    }

    .saveToRubbish-btn-full-screen {
        position: fixed;
        z-index: 100000;
        top: 3px;
        right: 140px;
        width: 160px;
    }

    .anticon {
        color: inherit !important;
    }
`;
let articleVersion = -1;
const ArticleEdit = () => {
    const [state, setState] = useState<ArticleEditState>({
        typeOptions: [],
        editorInitSuccess: false,
        fullScreen: false,
        globalLoading: true,
        tags: [],
        types: [],
        rubbish: false,
        article: {
            keywords: "",
            version: -1,
        },
        saving: {
            previewIng: false,
            releaseSaving: false,
            rubbishSaving: false,
        },
    });

    const [messageApi, contextHolder] = message.useMessage();

    const articleForm = useRef<FormInstance>(null);

    useEffect(() => {
        axios.get("/api/admin/article/global").then(({ data }) => {
            const options: any[] = [];
            data.data.types.forEach((x: any) => {
                options.push(
                    <Radio style={{ display: "block" }} key={x.id} value={x.id}>
                        {x.typeName}
                    </Radio>
                );
            });
            const nDate = data;
            const query = new URLSearchParams(window.location.search);
            const id = query.get("id");
            if (id !== null && id !== "") {
                axios.get("/api/admin/article/detail?id=" + id).then(({ data }) => {
                    if (data.error) {
                        messageApi.error(data.message);
                        return;
                    }
                    setState((prevState) => {
                        return {
                            ...prevState,
                            globalLoading: false,
                            typeOptions: options,
                            rubbish: data.data.rubbish,
                            types: nDate.data.types,
                            tags: nDate.data.tags,
                            article: data.data,
                        };
                    });
                    articleVersion = data.data.version;
                });
            } else {
                setState((prevState) => {
                    return {
                        ...prevState,
                        typeOptions: options,
                        types: nDate.data.types,
                        tags: nDate.data.tags,
                        globalLoading: false,
                        article: { keywords: "", version: 0, content: "", markdown: "" },
                    };
                });
                articleVersion = 0;
            }
        });
    }, []);

    const getVersion = () => {
        return articleVersion;
    };

    const onSubmit = async (article: ArticleEntry, release: boolean, preview: boolean, autoSave: boolean) => {
        if (!(await validForm())) {
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
            version: getVersion(),
            rubbish: !release,
        };
        try {
            const { data } = await axios.post(uri, newArticle);
            if (data.error) {
                Modal.error({
                    title: "保存失败",
                    content: data.message,
                    okText: "确认",
                });
                return;
            }
            exitNotTips();
            if (release) {
                messageApi.info(getRes()["releaseSuccess"]);
            } else {
                if (!autoSave) {
                    messageApi.info(getRes()["saveSuccess"]);
                }
                if (preview) {
                    window.open(document.baseURI + "post/" + state.article!.logId, "_blank");
                }
            }
            const respData = data.data;
            articleVersion = respData.version;
            if (create) {
                const url = new URL(window.location.href);
                url.searchParams.set("id", respData.logId);
                window.history.replaceState(null, "", url.toString());
                newArticle = { ...newArticle, ...respData };
            } else {
                newArticle = { ...newArticle, version: respData.version };
            }
        } catch (e: any) {
            Modal.error({
                title: "保存失败",
                content: e.toString(),
                okText: "确认",
            });
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
                            previewIng: preview,
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

    const gup = (name: string, url: string) => {
        // eslint-disable-next-line
        const results = new RegExp("[?&]" + name + "=([^&#]*)").exec(url);
        if (!results) {
            return undefined;
        }
        return results[1] || undefined;
    };

    const setThumbnailHeight = (url: string) => {
        const height = Number.parseInt(gup("h", url) + "");
        if (height) {
            const originW = Number.parseInt(jquery("#thumbnail").width() + "");
            const w = Number.parseInt(gup("w", url) + "");
            jquery("#thumbnail").width((w / originW) * height);
        }
    };

    const onUploadChange = async (info: UploadChangeParam) => {
        const { status } = info.file;
        if (status === "done") {
            setThumbnailHeight(info.file.response.data.url);
            await handleValuesChange({ thumbnail: info.file.response.data.url });
        } else if (status === "error") {
            messageApi.error(`${info.file.name} file upload failed.`);
        }
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

    const save = async (article: ArticleEntry) => {
        if (articleForm.current === undefined || articleForm.current === null) {
            return;
        }
        console.info(article);
        //如果正在保存，尝试10ms后再检查下
        if (state.saving.rubbishSaving || state.saving.releaseSaving || state.saving.previewIng || saving) {
            setTimeout(() => {
                save(article);
            }, 10);
            return;
        }
        try {
            saving = true;
            await onSubmit(article, false, false, true);
        } finally {
            saving = false;
        }
    };

    if (state.globalLoading || state.article.version < 0) {
        return <></>;
    }

    const validForm = async (): Promise<boolean> => {
        if (articleForm.current === undefined || articleForm.current === null) {
            return false;
        }
        try {
            await articleForm.current.validateFields();
            return true;
        } catch (e) {
            // @ts-ignore
            if (e.errorFields.length > 0) {
                console.error(e);
                return false;
            }
        }
        return true;
    };

    const handleValuesChange = async (cv: ArticleEntry | ChangedContent | ThumbnailChanged) => {
        setState((prevState) => {
            const newArticle: ArticleEntry = {
                ...prevState.article,
                ...cv,
                keywords: jquery("#keywords").val() as unknown as string,
            };
            const compareArticle = prevState.article;
            if (compareArticle.keywords === null) {
                compareArticle.keywords = "";
            }
            //内容没有变化，不提交
            //console.info(newArticle);
            //console.info(compareArticle)

            if (JSON.stringify(newArticle) === JSON.stringify(compareArticle)) {
                return {
                    ...prevState,
                    article: newArticle,
                };
            }
            validForm().then(async (r) => {
                if (!r) {
                    return;
                }
                await save(newArticle);
            });
            return {
                ...prevState,
                article: newArticle,
            };
        });
    };

    return (
        <StyledArticleEdit>
            {contextHolder}
            <Form
                ref={articleForm}
                onValuesChange={(_key, cv) => handleValuesChange(cv)}
                initialValues={state.article}
                onFinish={() => onSubmit(state.article, true, false, false)}
            >
                <Form.Item name="logId" style={{ display: "none" }}>
                    <Input hidden={true} />
                </Form.Item>

                <Row gutter={[8, 8]} style={{ paddingTop: 20 }}>
                    <Col md={14} xxl={18} sm={6} span={24}>
                        <Title className="page-header" style={{ marginTop: 0, marginBottom: 0 }} level={3}>
                            {getRes()["admin.log.edit"] + (state.rubbish ? "-当前为草稿" : "")}
                        </Title>
                    </Col>
                    <Col xxl={2} md={4} sm={6} className={state.fullScreen ? "saveToRubbish-btn-full-screen" : ""}>
                        <Button
                            type={state.fullScreen ? "default" : "dashed"}
                            style={{ width: "100%" }}
                            loading={state.saving.rubbishSaving}
                            onClick={() => onSubmit(state.article, false, false, false)}
                        >
                            <SaveOutlined hidden={state.saving.rubbishSaving} />
                            {state.saving.rubbishSaving ? getRes().saving : getRes().saveAsDraft}
                        </Button>
                    </Col>
                    <Col xxl={2} md={3} sm={6}>
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
                    <Col xxl={2} md={3} sm={6} className={state.fullScreen ? "save-btn-full-screen" : ""}>
                        <Button
                            id="save"
                            type="primary"
                            loading={state.saving.releaseSaving}
                            style={{ width: "100%" }}
                            htmlType="submit"
                        >
                            <SendOutlined />
                            {getRes().release}
                        </Button>
                    </Col>
                </Row>
                <Divider />
                <Row gutter={8}>
                    <Col md={8} xs={24}>
                        <Form.Item
                            style={{ marginBottom: 8 }}
                            name="title"
                            rules={[{ required: true, message: getRes().inputArticleTitle }]}
                        >
                            <Input placeholder={getRes().inputArticleTitle} />
                        </Form.Item>
                    </Col>
                    <Col md={5} xs={24}>
                        <Form.Item style={{ marginBottom: 8 }} name="alias">
                            <Input addonBefore={getArticleRoute() + "/"} placeholder={getRes().inputArticleAlias} />
                        </Form.Item>
                    </Col>
                    <Col md={5} xs={0} />
                </Row>
                <Row gutter={[8, 8]}>
                    <Col md={18} xs={24} style={{ zIndex: 10 }}>
                        <MyEditorMdWrapper
                            onfullscreen={onfullscreen}
                            onfullscreenExit={onfullscreenExit}
                            markdown={state.article.markdown}
                            onChange={(v) => handleValuesChange(v)}
                        />
                    </Col>
                    <Col md={6} xs={24}>
                        <Row gutter={[8, 8]}>
                            <Col span={24}>
                                <Card size="small" style={{ textAlign: "center" }}>
                                    <Dragger
                                        accept={"image/*"}
                                        action={apiBasePath + "upload/thumbnail?dir=thumbnail"}
                                        name="imgFile"
                                        onChange={(e) => onUploadChange(e)}
                                    >
                                        {(state.article!.thumbnail === undefined ||
                                            state.article!.thumbnail === null ||
                                            state.article!.thumbnail === "") && (
                                            <>
                                                <p className="ant-upload-drag-icon" style={{ height: "88px" }}>
                                                    <CameraOutlined style={{ fontSize: "28px", paddingTop: "40px" }} />
                                                </p>
                                                <p className="ant-upload-text">拖拽或点击，上传文章封面</p>
                                            </>
                                        )}
                                        {state.article!.thumbnail !== "" && (
                                            <Image preview={false} id="thumbnail" src={state.article!.thumbnail} />
                                        )}
                                    </Dragger>
                                </Card>
                            </Col>
                            <Col span={24}>
                                <Card size="small" title={getRes()["admin.setting"]}>
                                    <Row>
                                        <Col xs={24} md={12}>
                                            <Form.Item
                                                style={{ marginBottom: 0 }}
                                                valuePropName="checked"
                                                name="canComment"
                                                label={getRes()["commentAble"]}
                                            >
                                                <Switch size="small" />
                                            </Form.Item>
                                        </Col>
                                        <Col xs={24} md={12}>
                                            <Form.Item
                                                style={{ marginBottom: 0 }}
                                                valuePropName="checked"
                                                name="privacy"
                                                label={getRes()["private"]}
                                            >
                                                <Switch size="small" />
                                            </Form.Item>
                                        </Col>
                                    </Row>
                                </Card>
                            </Col>
                            <Col span={24}>
                                <Card size="small" title={getRes()["admin.type.manage"]}>
                                    <Form.Item
                                        label=""
                                        style={{ marginBottom: 0 }}
                                        name="typeId"
                                        rules={[
                                            {
                                                required: true,
                                                message: "请选择" + getRes()["admin.type.manage"],
                                            },
                                        ]}
                                    >
                                        <Radio.Group style={{ width: "100%" }}>{state.typeOptions}</Radio.Group>
                                    </Form.Item>
                                </Card>
                            </Col>
                            <Col span={24}>
                                <Card size="small" title={getRes().tag}>
                                    <ArticleEditTag
                                        keywords={state.article!.keywords}
                                        allTags={state.tags.map((x) => x.text)}
                                    />
                                </Card>
                            </Col>
                            <Col span={24}>
                                <Card size="small" title={getRes().digest}>
                                    <Form.Item style={{ marginBottom: 0 }} name="digest">
                                        <TextArea placeholder={getRes().digestTips} rows={3} />
                                    </Form.Item>
                                </Card>
                            </Col>
                        </Row>
                    </Col>
                </Row>
            </Form>
        </StyledArticleEdit>
    );
};

export default ArticleEdit;
