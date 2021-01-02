import React from "react";
import {CameraOutlined, EyeOutlined} from '@ant-design/icons';
import {BaseResourceComponent} from "./base-resource-component";
import {Button, Input, Modal, Radio} from "antd";
import Form from "antd/es/form";
import Spin from "antd/es/spin";
import Row from "antd/es/grid/row";
import Col from "antd/es/grid/col";
import Divider from "antd/es/divider";
import Title from "antd/es/typography/Title";
import Card from "antd/es/card";
import Dragger from "antd/es/upload/Dragger";
import Switch from "antd/es/switch";
import TextArea from "antd/es/input/TextArea";
import {ArticleEditTag} from "./article-edit-tag";
import {message} from "antd/es";
import Image from "antd/es/image";

import {SaveOutlined, SendOutlined} from '@ant-design/icons';
import jquery from 'jquery';
import MyEditorMdWrapper from "./editor/my-editormd-wrapper";

const md5 = require('md5');
const axios = require('axios');

class ArticleEdit extends BaseResourceComponent {

    articleFrom = React.createRef();

    initState() {
        return {
            types: [],
            tags: [],
            globalLoading: true,
            article: {
                keywords: ""
            },
            mdEditorScriptLoaded: false
        }
    }

    rubbish = async (preview) => {
        await this.onFinish(this.state.article, false, preview);
    }

    keydownHandler = async (e) => {
        let charCode = String.fromCharCode(e.code).toLowerCase();
        //console.info(e);
        if (charCode === 'Keys' && e.ctrlKey) {
            await this.preview(false);
            e.preventDefault();
        }
    }

    componentDidMount() {
        super.componentDidMount();
        this.getAxios().get("/api/admin/article/global").then(({data}) => {
            const options = [];
            data.data.types.forEach(x => {
                options.push(<Radio style={{display: "block"}} key={x.id} value={x.id}>{x.typeName}</Radio>);
            })
            let pageState = {
                types: data.data.types,
                tags: data.data.tags,
                typeOptions: options
            }
            const query = new URLSearchParams(this.props.location.search);
            const id = query.get("id");
            if (id !== null && id !== '') {
                axios.get("/api/admin/article/detail?id=" + id).then(({data}) => {
                    pageState.globalLoading = false;
                    pageState.article = data.data;
                    this.initValue(pageState);
                })
            } else {
                pageState.globalLoading = false;
                this.setState(pageState);
            }
        });
        document.addEventListener('keydown', this.keydownHandler);
    };

    componentWillUnmount() {
        document.removeEventListener('keydown', this.keydownHandler);
    }

    getSecondTitle() {
        return this.state.res['admin.log.edit'] + (this.state.article.rubbish ? '-当前为草稿' : '');
    }


    setValue = (changedValues) => {
        let currentArticle = {...this.state.article, ...changedValues};
        if (this.articleFrom.current) {
            this.articleFrom.current.setFieldsValue(currentArticle);
        }
        this.setState({
            article: currentArticle
        });
    }

    initValue = (pageState) => {
        this.articleFrom.current.setFieldsValue(pageState.article);
        this.setState(pageState);
    }

    onFinish = async (allValues, release, preview, autoSave) => {
        if (allValues.title === undefined || allValues.title === '') {
            return;
        }
        if (allValues.typeId === undefined || allValues.typeId === '') {
            return;
        }
        allValues.rubbish = !release;
        allValues.keywords = jquery("#keywords").val();
        let uri;
        if (allValues.logId !== undefined) {
            uri = '/api/admin/article/update'
        } else {
            uri = '/api/admin/article/create'
        }
        const currentVersion = md5(JSON.stringify(allValues));
        //自动保存模式下，没有变化
        if (autoSave && currentVersion === this.state.savedVersion) {
            return;
        }
        if (!release) {
            this.setState({
                rubbishSaving: true
            })
        }
        await axios.post(uri, allValues).then(({data}) => {
            if (data.error) {
                message.error(data.message);
                return;
            }
            if (release) {
                message.info(this.state.res['releaseSuccess']);
            } else {
                if (!autoSave) {
                    message.info(this.state.res['saveAsDraft']);
                }
                if (preview) {
                    window.open("/post/" + allValues.logId, '_blank');
                }
            }
            this.setState({
                savedVersion: currentVersion
            });
            this.setValue({
                logId: data.data.id,
                digest: data.data.digest,
                alias: data.data.alias,
                rubbish: allValues.rubbish
            });
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
        }).finally(() => {
            if (!release) {
                this.setState({
                    rubbishSaving: false
                })
            }
        });
    }

    getArticleRoute() {
        if (this.state === undefined || this.state.res === undefined || this.state.res['articleRoute'] === undefined) {
            return "";
        }
        return this.state.res['articleRoute'];
    }

    gup(name, url) {
        // eslint-disable-next-line
        const results = new RegExp('[\?&]' + name + '=([^&#]*)').exec(url);
        if (!results) {
            return undefined;
        }
        return results[1] || undefined;
    }

    setThumbnailHeight(url) {
        let h = this.gup("h", url);
        if (h) {
            let originW = jquery("#thumbnail").width();
            const w = this.gup("w", url);
            jquery("#thumbnail").width(originW / w * h);
        }
    }

    onUploadChange = async (info) => {
        const {status} = info.file;
        if (status === 'done') {
            //message.success(`${info.file.name} file uploaded successfully.`);
            let infos = {thumbnail: info.file.response.data.url}
            this.setValue(infos);
            this.setThumbnailHeight(info.file.response.data.url);
            await this.autoSaveToRubbish(infos, 0)
        } else if (status === 'error') {
            message.error(`${info.file.name} file upload failed.`);
        }
    };

    save = async () => {
        //如果正在保存，尝试1s后再检查下
        if (this.state.rubbishSaving) {
            setTimeout(this.save, 1000);
            return;
        }
        await this.onFinish(this.state.article, false, false, true);
    };

    autoSaveToRubbish = (changedValues, delayMs) => {
        //还在加载不管
        if (this.state.globalLoading) {
            return;
        }
        this.setValue(changedValues);
        setTimeout(this.save, delayMs);
    }

    render() {
        return (
            <Spin delay={this.getSpinDelayTime()}
                  spinning={this.state.resLoading && this.state.globalLoading}>
                <Title className='page-header' level={3}>{this.getSecondTitle()}</Title>
                <Divider/>
                <Form
                    onValuesChange={(v) => this.autoSaveToRubbish(v, 0)}
                    ref={this.articleFrom}
                    onFinish={(values) => this.onFinish(values, true, false)}>
                    <Form.Item name='logId' style={{display: "none"}}>
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
                        <Col xxl={2} md={4} sm={6}>
                            <Button type="ghost" block={true} loading={this.state.rubbishSaving}
                                    onClick={() => this.rubbish(false)}>
                                <SaveOutlined/>
                                {this.state.res.saveAsDraft}
                            </Button>
                        </Col>
                        <Col xxl={2} md={3} sm={6}>
                            <Button type="ghost" block={true} onClick={() => this.rubbish(true)}>
                                <EyeOutlined/>
                                {this.state.res.preview}
                            </Button>

                        </Col>
                        <Col xxl={2} md={3} sm={6}>
                            <Button type='primary' block={true} enterbutton='true'
                                    htmlType='submit'>
                                <SendOutlined/>
                                {this.state.res.release}</Button>
                        </Col>
                    </Row>
                    <Row gutter={[8, 8]}>
                        <Col md={8} xs={24}>
                            <Form.Item
                                style={{marginBottom: 0}}
                                name="title"
                                rules={[{required: true, message: this.state.res.inputArticleTitle}]}>
                                <Input placeholder={this.state.res.inputArticleTitle}/>
                            </Form.Item>
                        </Col>
                        <Col md={5} xs={24}>
                            <Form.Item
                                style={{marginBottom: 0}}
                                name="alias">
                                <Input addonBefore={this.getArticleRoute() + "/"}
                                       placeholder={this.state.res.inputArticleAlias}/>
                            </Form.Item>
                        </Col>
                        <Col md={5} xs={0}>

                        </Col>
                    </Row>
                    <Row gutter={8}>
                        <Col md={18} xs={24} style={{zIndex: 10}}>
                            {!this.state.globalLoading &&
                            <MyEditorMdWrapper superThis={this}/>
                            }
                        </Col>
                        <Col md={6} xs={24}>
                            <Row gutter={[8, 8]}>
                                <Col span={24}>
                                    <Card size="small">
                                        <Dragger
                                            action={"/api/admin/upload/thumbnail?dir=thumbnail"}
                                            name='imgFile'
                                            onChange={(e) => this.onUploadChange(e)}>
                                            {(this.state.article.thumbnail === undefined ||
                                                this.state.article.thumbnail === null ||
                                                this.state.article.thumbnail === '') && (
                                                <div style={{height: '108px'}}>
                                                    <CameraOutlined style={{fontSize: "28px", paddingTop: '50px'}}/>
                                                </div>
                                            )}
                                            {this.state.article.thumbnail !== '' && (
                                                <Image id='thumbnail'
                                                       src={this.state.article.thumbnail}/>
                                            )}
                                        </Dragger>
                                    </Card>
                                </Col>
                                <Col span={24}>
                                    <Card size="small" title={this.state.res['admin.setting']}>
                                        <Row>
                                            <Col xs={24} md={12}>
                                                <Form.Item
                                                    style={{marginBottom: 0}}
                                                    valuePropName="checked" name='canComment'
                                                    label={this.state.res['commentAble']}>
                                                    <Switch size="small"/>
                                                </Form.Item>
                                            </Col>
                                            <Col xs={24} md={12}>
                                                <Form.Item
                                                    style={{marginBottom: 0}}
                                                    valuePropName="checked" name='privacy'
                                                    label={this.state.res['private']}>
                                                    <Switch size="small"/>
                                                </Form.Item>
                                            </Col>
                                        </Row>
                                    </Card>
                                </Col>
                                <Col span={24}>
                                    <Card size="small"
                                          title={this.state.res['admin.type.manage']}>
                                        <Form.Item label=''
                                                   style={{marginBottom: 0}}
                                                   name='typeId' rules={[{required: true}]}>
                                            <Radio.Group style={{width: "100%"}}>
                                                {this.state.typeOptions}
                                            </Radio.Group>
                                        </Form.Item>
                                    </Card>
                                </Col>
                                <Col span={24}>
                                    <Card size="small" title={this.state.res.tag}>
                                        <ArticleEditTag superThis={this}
                                                        keywords={this.state.article.keywords}
                                                        allTags={this.state.tags.map(x => x.text)} tags={[]}/>
                                    </Card>
                                </Col>

                                <Col span={24}>
                                    <Card size="small" title={this.state.res.digest}>
                                        <Form.Item
                                            style={{marginBottom: 0}}
                                            name='digest'>
                                            <TextArea placeholder={this.state.res.digestTips} rows={3}/>
                                        </Form.Item>
                                    </Card>
                                </Col>
                            </Row>
                        </Col>
                    </Row>
                </Form>
            </Spin>
        )
    }
}

export default ArticleEdit;