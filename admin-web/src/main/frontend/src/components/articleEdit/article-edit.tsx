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
import "./article-edit.less";
import Constants, { getRes } from "../../utils/constants";
import screenfull from "screenfull";
import ArticleEditTag from "./article-edit-tag";
import axios from "axios";
import { UploadChangeParam } from "antd/es/upload";

export type ArticleEntry = ChangedContent & {
    keywords: string;
    rubbish?: boolean;
    alias?: string;
    logId?: number;
    digest?: string;
    thumbnail?: string;
    version: number;
};

type ArticleEditState = {
    types: [];
    typeOptions: any[];
    tags: any[];
    globalLoading: boolean;
    fullScreen: boolean;
    editorInitSuccess: boolean;
};

type ArticleSavingState = {
    rubbishSaving: boolean;
    previewIng: boolean;
    releaseSaving: boolean;
};
const ArticleEdit = () => {
    const [state, setState] = useState<ArticleEditState>({
        typeOptions: [],
        editorInitSuccess: false,
        fullScreen: false,
        globalLoading: true,
        tags: [],
        types: [],
    });

    const [articleState, setArticleState] = useState<ArticleEntry>({
        keywords: "",
        version: -1,
    });

    const [savingState, setSavingState] = useState<ArticleSavingState>({
        previewIng: false,
        releaseSaving: false,
        rubbishSaving: false,
    });

    const articleForm = useRef<FormInstance>(null);

    const rubbish = (preview: boolean) => {
        onSubmit(articleState, false, false, preview);
    };

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
                        message.error(data.message);
                        return;
                    }
                    setArticleState(data.data);
                    setState({
                        ...state,
                        globalLoading: false,
                        typeOptions: options,
                        types: nDate.data.types,
                        tags: nDate.data.tags,
                    });
                    (document.getElementById("version") as HTMLInputElement).value = data.data.version;
                });
            } else {
                setState({
                    ...state,
                    typeOptions: options,
                    types: nDate.data.types,
                    tags: nDate.data.tags,
                    globalLoading: false,
                });
                setArticleState({ keywords: "", version: 0 });
            }
        });
    }, []);

    const getVersion = () => {
        return Number((document.getElementById("version") as HTMLInputElement).value);
    };

    const onSubmit = (allValues: ArticleEntry, release: boolean, preview: boolean, autoSave: boolean) => {
        allValues.rubbish = !release;
        allValues.version = getVersion();
        allValues.keywords = jquery("#keywords").val() as unknown as string;
        let uri;
        const create = allValues!.logId === undefined;
        if (create) {
            uri = "/api/admin/article/create";
        } else {
            uri = "/api/admin/article/update";
        }
        if (release) {
            setSavingState({
                ...savingState,
                releaseSaving: true,
            });
        } else {
            setSavingState({
                ...savingState,
                rubbishSaving: true,
                previewIng: preview,
            });
        }
        exitTips(getRes()["articleEditExitWithOutSaveSuccess"]);
        try {
            axios
                .post(uri, allValues)
                .then(({ data }) => {
                    if (data.error) {
                        if (data.error === 9094) {
                            console.warn("Save error ", data);
                            return;
                        }
                        Modal.error({
                            title: "保存失败",
                            content: data.message,
                            okText: "确认",
                        });
                        return;
                    }
                    exitNotTips();
                    if (release) {
                        message.info(getRes()["releaseSuccess"]);
                    } else {
                        if (!autoSave) {
                            message.info(getRes()["saveSuccess"]);
                        }
                        if (preview) {
                            window.open(document.baseURI + "post/" + allValues!.logId, "_blank");
                        }
                    }
                    const respData = data.data;
                    (document.getElementById("version") as HTMLInputElement).value = respData.version;
                    allValues = { ...allValues, ...respData };
                    setArticleState(allValues);
                    if (create) {
                        const url = new URL(window.location.href);
                        url.searchParams.set("id", respData.logId);
                        window.history.replaceState(null, "", url.toString());
                        //@ts-ignore
                        articleForm.current.setFieldsValue({
                            alias: respData.alias,
                            digest: respData.digest,
                            logId: respData.logId,
                        });
                    }
                })
                .catch((e) => {
                    Modal.error({
                        title: "保存失败",
                        content: e.toString(),
                        okText: "确认",
                    });
                });
        } finally {
            if (release) {
                setSavingState({
                    ...savingState,
                    releaseSaving: false,
                });
            } else {
                setSavingState({
                    ...savingState,
                    rubbishSaving: false,
                    previewIng: preview,
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
            await save({ ...articleState, thumbnail: info.file.response.data.url });
        } else if (status === "error") {
            message.error(`${info.file.name} file upload failed.`);
        }
    };

    const onfullscreen = (editor: any) => {
        if (screenfull.isEnabled) {
            screenfull.request().then(() => {
                setState({
                    ...state,
                    fullScreen: true,
                });
            });
            screenfull.on("change", () => {
                //@ts-ignore
                if (!screenfull.isFullscreen) {
                    editor.fullscreenExit();
                }
            });
        } else {
            setState({
                ...state,
                fullScreen: true,
            });
        }
    };

    const onfullscreenExit = () => {
        setState({
            ...state,
            fullScreen: false,
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

    const save = async (article: ArticleEntry | ChangedContent) => {
        if (articleForm.current === undefined || articleForm.current === null) {
            return;
        }
        try {
            await articleForm.current.validateFields();
        } catch (e) {
            // @ts-ignore
            if (e.errorFields.length > 0) {
                console.error(e);
                return;
            }
        }
        const newArticle: ArticleEntry = { ...articleForm.current.getFieldsValue(), ...article };
        //如果正在保存，尝试1s后再检查下
        if (savingState.rubbishSaving || savingState.releaseSaving || savingState.previewIng) {
            setTimeout(() => {
                save(newArticle);
            }, 1000);
            return;
        }
        if (newArticle.version < getVersion()) {
            return;
        }
        await onSubmit(newArticle, false, false, true);
    };

    if (state.globalLoading || articleState.version < 0) {
        return <></>;
    }

    const editorLoadSuccess = () => {
        //setState({ ...state, editorInitSuccess: true });
    };

    return (
        <>
            <input hidden={true} id={"version"} />
            <Title className="page-header" level={3}>
                {getRes()["admin.log.edit"] + (articleState!.rubbish ? "-当前为草稿" : "")}
            </Title>
            <Divider />
            <Form
                ref={articleForm}
                onValuesChange={(_key, cv) => save(cv)}
                initialValues={articleState}
                onFinish={() => onSubmit(articleState, true, false, false)}
            >
                <Form.Item name="logId" style={{ display: "none" }}>
                    <Input hidden={true} />
                </Form.Item>

                <Row gutter={[8, 8]} style={{ paddingBottom: "5px" }}>
                    <Col md={14} xxl={18} sm={6} span={0} />
                    <Col xxl={2} md={4} sm={6} className={state.fullScreen ? "saveToRubbish-btn-full-screen" : ""}>
                        <Button
                            type={state.fullScreen ? "default" : "ghost"}
                            style={{ width: "100%" }}
                            loading={savingState.rubbishSaving}
                            onClick={() => rubbish(false)}
                        >
                            <SaveOutlined hidden={savingState.rubbishSaving} />
                            {savingState.rubbishSaving ? getRes().saving : getRes().saveAsDraft}
                        </Button>
                    </Col>
                    <Col xxl={2} md={3} sm={6}>
                        <Button
                            type="ghost"
                            loading={savingState.rubbishSaving && savingState.previewIng}
                            style={{ width: "100%" }}
                            onClick={() => rubbish(true)}
                        >
                            <EyeOutlined />
                            {getRes().preview}
                        </Button>
                    </Col>
                    <Col xxl={2} md={3} sm={6} className={state.fullScreen ? "save-btn-full-screen" : ""}>
                        <Button
                            id="save"
                            type="primary"
                            loading={savingState.releaseSaving}
                            style={{ width: "100%" }}
                            htmlType="submit"
                        >
                            <SendOutlined />
                            {getRes().release}
                        </Button>
                    </Col>
                </Row>
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
                            markdown={articleState.markdown}
                            loadSuccess={editorLoadSuccess}
                            onChange={(v) => save(v)}
                        />
                    </Col>
                    <Col md={6} xs={24}>
                        <Row gutter={[8, 8]}>
                            <Col span={24}>
                                <Card size="small" style={{ textAlign: "center" }}>
                                    <Dragger
                                        accept={"image/*"}
                                        action={"/api/admin/upload/thumbnail?dir=thumbnail"}
                                        name="imgFile"
                                        onChange={(e) => onUploadChange(e)}
                                    >
                                        {(articleState!.thumbnail === undefined ||
                                            articleState!.thumbnail === null ||
                                            articleState!.thumbnail === "") && (
                                            <>
                                                <p className="ant-upload-drag-icon" style={{ height: "88px" }}>
                                                    <CameraOutlined style={{ fontSize: "28px", paddingTop: "40px" }} />
                                                </p>
                                                <p className="ant-upload-text">拖拽或点击，上传文章封面</p>
                                            </>
                                        )}
                                        {articleState!.thumbnail !== "" && (
                                            <Image
                                                fallback={Constants.getFillBackImg()}
                                                preview={false}
                                                id="thumbnail"
                                                src={articleState!.thumbnail}
                                            />
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
                                        keywords={articleState!.keywords}
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
        </>
    );
};

export default ArticleEdit;
