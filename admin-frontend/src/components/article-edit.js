import React from "react";
import {CameraOutlined} from '@ant-design/icons';
import {BaseResourceComponent} from "./base-resource-component";
import {Button, Input, Radio} from "antd";
import Form from "antd/es/form";
import Spin from "antd/es/spin";
import Row from "antd/es/grid/row";
import Col from "antd/es/grid/col";
import Divider from "antd/es/divider";
import Space from "antd/es/space";
import Title from "antd/es/typography/Title";
import Card from "antd/es/card";
import Dragger from "antd/es/upload/Dragger";
import Switch from "antd/es/switch";
import TextArea from "antd/es/input/TextArea";
import * as axios from "axios";
import {ArticleEditTag} from "./article-edit-tag";
import {message} from "antd/es";
import Image from "antd/es/image";

import {SaveOutlined, SendOutlined} from '@ant-design/icons';
import {MyEditorMdWrapper} from "./editor/my-editormd";
import jquery from 'jquery';


class ArticleEdit extends BaseResourceComponent {

    articleFrom = React.createRef();

    getSecondTitle() {
        return this.state.res['admin.log.edit'];
    }

    initState() {
        return {
            types: [],
            tags: [],
            globalLoading: true,
            article: {
                keywords: ""
            },
        }
    }

    componentDidMount() {
        super.componentDidMount();
        axios.get("/api/admin/article/global").then(({data}) => {
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
                console.info(pageState);
                pageState.globalLoading = false;
                this.setState(pageState);
            }
        });
    };

    setValue = (changedValues) => {
        this.articleFrom.current.setFieldsValue(changedValues);
        this.setState({
            article: changedValues
        });
    }

    initValue = (pageState) => {
        this.articleFrom.current.setFieldsValue(pageState.article);
        this.setState(pageState);
    }

    onFinish = async (allValues) => {
        console.info(allValues);
        allValues.content = jquery("#content").text();
        allValues.markdown = jquery("#markdown").text();
        allValues.keywords = jquery("#keywords").val();
        allValues.thumbnail = this.state.article.thumbnail;
        let uri;
        if (allValues.logId !== undefined) {
            uri = '/api/admin/article/update'
        } else {
            uri = '/api/admin/article/create'
        }
        await axios.post(uri, JSON.stringify(allValues)).then(({data}) => {
            if (data.error) {
                message.error(data.message);
                return
            }
            allValues.logId = data.data.id;
            allValues.digest = data.data.digest
            allValues.alias = data.data.alias;
            if (allValues.rubbish) {
                message.info(this.state.res['saveAsDraft']);
                window.open("/post/" + allValues.logId, '_blank');
                allValues.rubbish = false;
            } else {
                message.info(this.state.res['releaseSuccess']);
            }
            this.setValue(allValues);
        })
    }

    getArticleRoute() {
        if (this.state === undefined || this.state.res === undefined || this.state.res['articleRoute'] === undefined) {
            return "";
        }
        return this.state.res['articleRoute'];
    }

    preview = async () => {
        /*this.setState({
            article: {
                rubbish: true
            }
        }, () => {
            this.articleFrom.current.submit();
        })*/
    }

    gup(name, url) {
        if (!url) url = window.location.href;
        name = name.replace(/[\[]/, "\\\[").replace(/[\]]/, "\\\]");
        const regexS = "[\\?&]" + name + "=([^&#]*)";
        const regex = new RegExp(regexS);
        const results = regex.exec(url);
        return results === null ? null : results[1];
    }

    getThumbnailHeight(url) {
        let originW = jquery("#thumbnail").width();
        const w = this.gup("w", url);
        let h = this.gup("h", url);
        if (h) {
            return originW / w * h;
        } else {
            return 128;
        }
    }

    onUploadChange(info) {
        const {status} = info.file;
        if (status === 'done') {
            message.success(`${info.file.response.data.url} file uploaded successfully.`);
            this.setValue({
                thumbnail: info.file.response.data.url
            });
        } else if (status === 'error') {
            message.error(`${info.file.name} file upload failed.`);
        }
    };


    render() {

        return (
            <div>
                <Spin delay={this.getSpinDelayTime()} spinning={this.state.resLoading && this.state.globalLoading}>
                    <Title className='page-header' level={3}>{this.getSecondTitle()}</Title>
                    <Divider/>
                    <Form
                        ref={this.articleFrom}
                        onFinish={(values) => this.onFinish(values)}
                        onValuesChange={(k, v) => this.setValue(k, v)}>
                        <Form.Item name='logId' style={{display: "none"}}>
                            <Input hidden={true}/>
                        </Form.Item>
                        <Row style={{paddingBottom: "15px"}}>
                            <Col span={24}>
                                <div style={{float: "right"}}>
                                    <Space size={5}>
                                        <Button type="ghost" onClick={this.preview}>
                                            <SaveOutlined/>
                                            {this.state.res.preview}
                                        </Button>
                                        <Button type='primary' enterButton
                                                htmlType='submit'>
                                            <SendOutlined/>
                                            {this.state.res.release}</Button>
                                    </Space>
                                </div>
                            </Col>
                        </Row>
                        <Row gutter={8}>
                            <Col md={8} xs={24}>
                                <Form.Item
                                    name="title"
                                    rules={[{required: true, message: this.state.res.inputArticleTitle}]}>
                                    <Input placeholder={this.state.res.inputArticleTitle}/>
                                </Form.Item>
                            </Col>
                            <Col md={5} xs={24}>
                                <Form.Item name="alias">
                                    <Input addonBefore={this.getArticleRoute() + "/"}
                                           placeholder={this.state.res.inputArticleAlias}/>
                                </Form.Item>
                            </Col>
                            <Col md={5} xs={0}>

                            </Col>
                        </Row>
                        <Row gutter={8}>
                            <Col md={18} xs={24} style={{zIndex: 10}}>
                                <Form.Item name='markdown'>
                                    <div id='markdown' dangerouslySetInnerHTML={{__html: this.state.article.markdown}}
                                         style={{display: "none"}}/>
                                    <div id='content' style={{display: "none"}}/>
                                    {!this.state.globalLoading &&
                                    <MyEditorMdWrapper editorPlaceholder={this.state.res['editorPlaceholder']} markdown={this.state.article.markdown}/>}
                                </Form.Item>
                            </Col>
                            <Col md={6} xs={24}>
                                <Row gutter={[8, 8]}>
                                    <Col span={24}>
                                        <Card size="small">
                                            <Dragger
                                                action={"/api/admin/upload/thumbnail?dir=thumbnail"}
                                                name='imgFile'
                                                onChange={(e) => this.onUploadChange(e)}>
                                                <div id="thumbnail">
                                                    {(this.state.article.thumbnail === undefined ||
                                                        this.state.article.thumbnail === null ||
                                                        this.state.article.thumbnail === '') && (
                                                        <CameraOutlined style={{fontSize: "28px"}}/>
                                                    )}
                                                    {this.state.article.thumbnail !== '' && (
                                                        <Image
                                                            height={this.getThumbnailHeight(this.state.article.thumbnail)}
                                                            src={this.state.article.thumbnail}/>
                                                    )}
                                                </div>
                                            </Dragger>
                                        </Card>
                                    </Col>
                                    <Col span={24}>
                                        <Card size="small" title={this.state.res['admin.setting']}>
                                            <Form.Item valuePropName="checked" name='canComment'
                                                       label={this.state.res['commentAble']}>
                                                <Switch size="small"/>
                                            </Form.Item>
                                            <Form.Item valuePropName="checked" name='privacy'
                                                       label={this.state.res['private']}>
                                                <Switch size="small"/>
                                            </Form.Item>
                                        </Card>
                                    </Col>
                                    <Col span={24}>
                                        <Card size="small"
                                              title={this.state.res['admin.type.manage']}>
                                            <Form.Item label='' name='typeId' rules={[{required: true}]}>
                                                <Radio.Group style={{width: "100%"}}>
                                                    {this.state.typeOptions}
                                                </Radio.Group>
                                            </Form.Item>
                                        </Card>
                                    </Col>
                                    <Col span={24}>
                                        <Card size="small" title={this.state.res.tag}>
                                            <ArticleEditTag keywords={this.state.article.keywords}
                                                            allTags={this.state.tags.map(x => x.text)} tags={[]}/>
                                        </Card>
                                    </Col>

                                    <Col span={24}>
                                        <Card size="small" title={this.state.res.digest}>
                                            <Form.Item name='digest'>
                                                <TextArea placeholder={this.state.res.digestTips} rows={3}/>
                                            </Form.Item>
                                        </Card>
                                    </Col>
                                </Row>
                            </Col>
                        </Row>
                    </Form>
                </Spin>
            </div>
        )
    }
}

export default ArticleEdit;