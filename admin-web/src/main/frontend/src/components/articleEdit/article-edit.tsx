import {useEffect, useRef, useState} from "react";
import {CameraOutlined, EyeOutlined, SaveOutlined, SendOutlined} from '@ant-design/icons';
import {Button, Input, Modal, Radio} from "antd";
import Form from "antd/es/form";
import Row from "antd/es/grid/row";
import Col from "antd/es/grid/col";
import Divider from "antd/es/divider";
import Title from "antd/es/typography/Title";
import Card from "antd/es/card";
import Dragger from "antd/es/upload/Dragger";
import Switch from "antd/es/switch";
import TextArea from "antd/es/input/TextArea";
import {message} from "antd/es";
import Image from "antd/es/image";
import jquery from 'jquery';
import MyEditorMdWrapper, {ChangedContent} from "./editor/my-editormd-wrapper";
import './article-edit.less'
import Constants, {getRes} from "../../utils/constants";
import screenfull from "screenfull";
import ArticleEditTag from "./article-edit-tag";
import axios from "axios";
import {UploadChangeParam} from "antd/es/upload";

const md5 = require('md5');

export type ArticleEntry = ChangedContent & {
    keywords: string,
    rubbish?: boolean,
    alias?: string,
    logId?: number,
    digest?: string,
    thumbnail?: string,
    version: number,
}

type ArticleEditState = {
    types: [],
    typeOptions: any[],
    tags: any[],
    globalLoading: boolean,
    fullScreen: boolean,
    editorInitSuccess: boolean,
}

type ArticleSavingState = {
    rubbishSaving: boolean,
    savedVersion: number,
    previewIng: boolean,
    releaseSaving: boolean,
}

const ArticleEdit = () => {


    const [state, setState] = useState<ArticleEditState>({
        typeOptions: [],
        editorInitSuccess: false,
        fullScreen: false, globalLoading: true,
        tags: [],
        types: []
    })

    const [articleState, setArticleState] = useState<ArticleEntry>({
        keywords: "",
        version: -1,
    });

    const [savingState, setSavingState] = useState<ArticleSavingState>({
        previewIng: false,
        releaseSaving: false,
        rubbishSaving: false,
        savedVersion: 0,
    })

    const articleForm = useRef(null);

    const rubbish = async (preview: boolean) => {
        await onSubmit(articleState, false, false, preview);
    }

    useEffect(() => {
        axios.get("/api/admin/article/global").then(({data}) => {
            const options: any[] = [];
            data.data.types.forEach((x: any) => {
                options.push(<Radio style={{display: "block"}} key={x.id} value={x.id}>{x.typeName}</Radio>);
            })
            const nDate = data;
            const query = new URLSearchParams(window.location.search);
            const id = query.get("id");
            if (id !== null && id !== '') {
                axios.get("/api/admin/article/detail?id=" + id).then(({data}) => {
                    if (data.error) {
                        message.error(data.message);
                        return;
                    }
                    setState({
                        ...state,
                        globalLoading: false,
                        typeOptions: options,
                        types: nDate.data.types,
                        tags: nDate.data.tags,
                    })
                    setArticleState(data.data);
                })
            } else {
                setState({
                    ...state,
                    typeOptions: options,
                    types: nDate.data.types,
                    tags: nDate.data.tags,
                    globalLoading: false
                });
                setArticleState({keywords: "", version: 0});
            }
        });
    }, [])

    const getCurrentSignVersion = (allValues: ArticleEntry | undefined) => {
        const signObj = JSON.parse(JSON.stringify(allValues));
        //version会改变随着保存，所以不参与变更检查
        delete signObj.version;
        return md5(JSON.stringify(signObj));
    }

    const onSubmit = async (allValues: ArticleEntry, release: boolean, preview: boolean, autoSave: boolean): Promise<ArticleEntry> => {
        // @ts-ignore
        const errors = await articleForm.current.validateFields();
        if (errors.length > 0) {
            return Promise.resolve(allValues);
        }
        allValues.rubbish = !release;
        allValues.keywords = jquery("#keywords").val() as unknown as string;
        let uri;
        const create = allValues!.logId === undefined;
        if (create) {
            uri = '/api/admin/article/create';
        } else {
            uri = '/api/admin/article/update';
        }
        const currentVersion = getCurrentSignVersion(allValues);
        //自动保存模式下，没有变化
        if (autoSave && currentVersion === savingState.savedVersion) {
            return Promise.resolve(allValues);
        }
        if (release) {
            setSavingState({
                ...savingState,
                releaseSaving: true
            })
        } else {
            setSavingState({
                ...savingState,
                rubbishSaving: true,
                previewIng: preview,
            })
        }
        exitTips(getRes()['articleEditExitWithOutSaveSuccess']);
        try {
            await axios.post(uri, allValues).then(({data}) => {
                if (data.error) {
                    Modal.error({
                        title: '保存失败',
                        content: data.message,
                        okText: '确认'
                    });
                    return;
                }
                exitNotTips();
                if (release) {
                    message.info(getRes()['releaseSuccess']);
                } else {
                    if (!autoSave) {
                        message.info(getRes()['saveSuccess']);
                    }
                    if (preview) {
                        window.open(document.baseURI + "post/" + allValues!.logId, '_blank');
                    }
                }
                const respData = data.data;
                //当前文本已经存在了，就不用服务器端的覆盖了
                if (allValues!.alias && allValues!.alias !== '') {
                    delete respData.alias;
                }
                if (allValues!.digest && allValues!.digest !== '') {
                    delete respData.digest;
                }
                allValues = {...allValues, ...respData};
                console.info(allValues.version + "aaaa");
                setSavingState({
                    ...savingState,
                    savedVersion: currentVersion
                })
                if (create) {
                    const url = new URL(window.location.href);
                    url.searchParams.set('id', data.data.logId);
                    window.history.replaceState(null, "", url.toString());
                }
            }).catch((e) => {
                let msg;
                if (e.error) {
                    msg = e.message;
                } else {
                    msg = e.toString();
                }
                Modal.error({
                    title: "保存失败",
                    content: msg,
                    okText: '确认'
                });
            })
        } finally {
            if (release) {
                setSavingState({
                    ...savingState,
                    releaseSaving: false
                })
            } else {
                setSavingState({
                    ...savingState,
                    rubbishSaving: false,
                    previewIng: preview,
                })
            }
        }
        return Promise.resolve(allValues);
    }

    const getArticleRoute = () => {
        if (state === undefined || getRes() === undefined || getRes()['articleRoute'] === undefined) {
            return "";
        }
        return getRes()['articleRoute'];
    }

    const gup = (name: string, url: string) => {
        // eslint-disable-next-line
        const results = new RegExp('[\?&]' + name + '=([^&#]*)').exec(url);
        if (!results) {
            return undefined;
        }
        return results[1] || undefined;
    }

    const setThumbnailHeight = (url: string) => {
        const height = Number.parseInt(gup("h", url) + "");
        if (height) {
            const originW = Number.parseInt(jquery("#thumbnail").width() + "");
            const w = Number.parseInt(gup("w", url) + "");
            jquery("#thumbnail").width(w / originW * height);
        }
    }

    const onUploadChange = async (info: UploadChangeParam) => {
        const {status} = info.file;
        if (status === 'done') {
            //message.success(`${info.file.name} file uploaded successfully.`);
            setThumbnailHeight(info.file.response.data.url);
            await autoSaveToRubbish({thumbnail: info.file.response.data.url}, 0)
        } else if (status === 'error') {
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
            screenfull.on('change', () => {
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
    }

    const onfullscreenExit = () => {
        setState({
            ...state,
            fullScreen: false,
        });
        //@ts-ignore
        screenfull.exit();
    }

    const exitTips = (tips: string) => {
        window.onbeforeunload = function () {
            return tips;
        };
    }

    const exitNotTips = () => {
        window.onbeforeunload = null;
    }

    const save = async (article: ArticleEntry) => {
        //如果正在保存，尝试1s后再检查下
        // @ts-ignore
        if (savingState.rubbishSaving || savingState.releaseSaving) {
            setTimeout(async () => {
                if (article.version < savingState.savedVersion) {
                    return;
                }
                await save(article)
            }, 1000);
            return;
        }
        const savedResult = await onSubmit(article, false, false, true);
        setArticleState(savedResult);
    };

    const autoSaveToRubbish = (changedValues: any, delayMs: number) => {
        const newV = JSON.parse(JSON.stringify(articleState));
        setTimeout(async () => {
            await save({...newV, ...changedValues});
        }, delayMs);
    }

    if (state.globalLoading || articleState.version < 0) {
        return <></>
    }

    const editorLoadSuccess = () => {
        //setState({...state, editorInitSuccess: true})
    }

    return (
        <>
            <Title className='page-header'
                   level={3}>{getRes()['admin.log.edit'] + (articleState!.rubbish ? '-当前为草稿' : '')}</Title>
            <Divider/>
            <Form
                ref={articleForm}
                onValuesChange={(cv) => autoSaveToRubbish(cv, 0)}
                initialValues={articleState}
                onFinish={() => onSubmit(articleState, true, false, false)}>
                <Form.Item name='logId' style={{display: "none"}}>
                    <Input hidden={true}/>
                </Form.Item>
                <Form.Item name='version' style={{display: "none"}}>
                    <Input hidden={true}/>
                </Form.Item>
                <Form.Item name='thumbnail' style={{display: "none"}}>
                    <Input hidden={true}/>
                </Form.Item>
                <Form.Item name='rubbish' style={{display: "none"}}>
                    <Input hidden={true}/>
                </Form.Item>
                <Form.Item name='markdown' style={{display: "none"}}>
                    <Input hidden={true}/>
                </Form.Item>
                <Form.Item name='content' style={{display: "none"}}>
                    <Input hidden={true}/>
                </Form.Item>
                <Row gutter={[8, 8]} style={{paddingBottom: "5px"}}>
                    <Col md={14} xxl={18} sm={6} span={0}/>
                    <Col xxl={2} md={4} sm={6}
                         className={state.fullScreen ? 'saveToRubbish-btn-full-screen' : ''}>
                        <Button
                            type={state.fullScreen ? 'default' : "ghost"}
                            block={true} loading={savingState.rubbishSaving}
                            onClick={() => rubbish(false)}>
                            <SaveOutlined hidden={savingState.rubbishSaving}/>
                            {savingState.rubbishSaving ? getRes().saving : getRes().saveAsDraft}
                        </Button>
                    </Col>
                    <Col xxl={2} md={3} sm={6}>
                        <Button type="ghost" loading={savingState.rubbishSaving && savingState.previewIng}
                                block={true} onClick={() => rubbish(true)}>
                            <EyeOutlined/>
                            {getRes().preview}
                        </Button>
                    </Col>
                    <Col xxl={2} md={3} sm={6} className={state.fullScreen ? 'save-btn-full-screen' : ''}>
                        <Button id='save'
                                type='primary'
                                loading={savingState.releaseSaving}
                                block={true}
                                htmlType='submit'>
                            <SendOutlined/>
                            {getRes().release}</Button>
                    </Col>
                </Row>
                <Row gutter={8}>
                    <Col md={8} xs={24}>
                        <Form.Item
                            style={{marginBottom: 8}}
                            name="title"
                            rules={[{required: true, message: getRes().inputArticleTitle}]}>
                            <Input placeholder={getRes().inputArticleTitle}/>
                        </Form.Item>
                    </Col>
                    <Col md={5} xs={24}>
                        <Form.Item
                            style={{marginBottom: 8}}
                            name="alias">
                            <Input addonBefore={getArticleRoute() + "/"}
                                   placeholder={getRes().inputArticleAlias}/>
                        </Form.Item>
                    </Col>
                    <Col md={5} xs={0}>

                    </Col>
                </Row>
                <Row gutter={[8, 8]}>
                    <Col md={18} xs={24} style={{zIndex: 10}}>
                        <MyEditorMdWrapper onfullscreen={onfullscreen} onfullscreenExit={onfullscreenExit}
                                           markdown={articleState!.markdown}
                                           loadSuccess={editorLoadSuccess}
                                           autoSaveToRubbish={autoSaveToRubbish}/>
                    </Col>
                    <Col md={6} xs={24}>
                        <Row gutter={[8, 8]}>
                            <Col span={24}>
                                <Card size="small" style={{textAlign: 'center'}}>
                                    <Dragger
                                        accept={"image/*"}
                                        action={"/api/admin/upload/thumbnail?dir=thumbnail"}
                                        name='imgFile'
                                        onChange={(e) => onUploadChange(e)}>
                                        {(articleState!.thumbnail === undefined ||
                                            articleState!.thumbnail === null ||
                                            articleState!.thumbnail === '') && (
                                            <>
                                                <p className="ant-upload-drag-icon" style={{height: '88px'}}>
                                                    <CameraOutlined style={{fontSize: "28px", paddingTop: '40px'}}/>
                                                </p>
                                                <p className="ant-upload-text">拖拽或点击，上传文章封面</p>
                                            </>

                                        )}
                                        {articleState!.thumbnail !== '' && (
                                            <Image fallback={Constants.getFillBackImg()} preview={false}
                                                   id='thumbnail'
                                                   src={articleState!.thumbnail}/>
                                        )}
                                    </Dragger>
                                </Card>
                            </Col>
                            <Col span={24}>
                                <Card size="small" title={getRes()['admin.setting']}>
                                    <Row>
                                        <Col xs={24} md={12}>
                                            <Form.Item
                                                style={{marginBottom: 0}}
                                                valuePropName="checked" name='canComment'
                                                label={getRes()['commentAble']}>
                                                <Switch size="small"/>
                                            </Form.Item>
                                        </Col>
                                        <Col xs={24} md={12}>
                                            <Form.Item
                                                style={{marginBottom: 0}}
                                                valuePropName="checked" name='privacy'
                                                label={getRes()['private']}>
                                                <Switch size="small"/>
                                            </Form.Item>
                                        </Col>
                                    </Row>
                                </Card>
                            </Col>
                            <Col span={24}>
                                <Card size="small"
                                      title={getRes()['admin.type.manage']}>
                                    <Form.Item label=''
                                               style={{marginBottom: 0}}
                                               name='typeId' rules={[{
                                        required: true,
                                        message: "请选择" + getRes()['admin.type.manage']
                                    }]}>
                                        <Radio.Group style={{width: "100%"}}>
                                            {state.typeOptions}
                                        </Radio.Group>
                                    </Form.Item>
                                </Card>
                            </Col>
                            <Col span={24}>
                                <Card size="small" title={getRes().tag}>
                                    <ArticleEditTag keywords={articleState!.keywords}
                                                    allTags={state.tags.map(x => x.text)}/>
                                </Card>
                            </Col>

                            <Col span={24}>
                                <Card size="small" title={getRes().digest}>
                                    <Form.Item
                                        style={{marginBottom: 0}}
                                        name='digest'>
                                        <TextArea placeholder={getRes().digestTips} rows={3}/>
                                    </Form.Item>
                                </Card>
                            </Col>
                        </Row>
                    </Col>
                </Row>
            </Form>
        </>
    )
}

export default ArticleEdit;
