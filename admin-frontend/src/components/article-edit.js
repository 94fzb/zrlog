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
import Editor from "wrap-md-editor";
import Title from "antd/es/typography/Title";
import Card from "antd/es/card";
import Dragger from "antd/es/upload/Dragger";
import Switch from "antd/es/switch";
import TextArea from "antd/es/input/TextArea";
import * as axios from "axios";
import {ArticleEditTag} from "./article-edit-tag";
import {message} from "antd/es";
import Image from "antd/es/image";

export class ArticleEdit extends BaseResourceComponent {

    articleFrom = React.createRef();

    // eslint-disable-next-line no-undef
    jquery = $;

    getSecondTitle() {
        return this.state.res['admin.log.edit'];
    }

    initState() {
        return {
            types: [],
            tags: [],
            globalLoading: true,
            article: {}
        }
    }

    componentDidMount() {
        super.componentDidMount();
        axios.get("/api/admin/article/global").then(({data}) => {
            this.setState({
                types: data.data.types,
                tags: data.data.tags,
            });
            const options = [];
            data.data.types.forEach(x => {
                options.push(<Radio style={{display: "block"}} key={x.id} value={x.id}>{x.typeName}</Radio>);
            })
            this.setState({
                typeOptions: options
            })

            const query = new URLSearchParams(this.props.location.search);
            const id = query.get("id");
            if (id !== null && id !== '') {
                axios.get("/api/admin/article/detail?id=" + id).then(({data}) => {
                    data.data.globalLoading = false;
                    this.setValue(data.data);
                })
            } else {
                this.setState({
                    globalLoading: false
                });
            }
        });
    };


    getEditorConfig() {
        return {
            codeFold: true,
            appendMarkdown: "",
            markdown: '',
            path: "/admin/vendors/markdown/lib/",
            searchReplace: true,
            htmlDecode: "pre",
            emoji: true,
            taskList: true,
            tocm: false,         // Using [TOCM]
            tex: true,                   // 开启科学公式TeX语言支持，默认关闭
            flowChart: true,             // 开启流程图支持，默认关闭
            sequenceDiagram: true,       // 开启时序/序列图支持，默认关闭,
            dialogMaskOpacity: 0,    // 设置透明遮罩层的透明度，全局通用，默认值为0.1
            dialogMaskBgColor: "#000", // 设置透明遮罩层的背景颜色，全局通用，默认为#fff
            imageUpload: true,
            imageFormats: [],
            imageUploadURL: "/api/admin/upload",
            width: "100%",
            height: "1240px",
            onload: function (e, editor) {
                // eslint-disable-next-line no-undef
                const jquery = $;
                e.setValue(jquery("#markdown").text());
                jquery("#fileDialog").on("click", function () {
                    editor.executePlugin("fileDialog", "file-dialog/file-dialog");
                });
                jquery("#videoDialog").on("click", function () {
                    editor.executePlugin("videoDialog", "video-dialog/video-dialog");
                });
                jquery("#copPreviewHtmlToClipboard").on("click", function () {
                    function copyToClipboard(html) {
                        const temp = jquery("<input>");
                        jquery("body").append(temp);
                        temp.val(html).select();
                        document.execCommand("copy");
                        temp.remove();
                    }

                    copyToClipboard('<div class="markdown-body" style="padding:0">' + editor.getPreviewedHTML() + "</div>");
                    //const e = {"message": lang.copPreviewHtmlToClipboardSuccess, "error": 0};
                    //notify(e, "info");
                });
            },
            onChange: function (e) {
                console.info(e);
            }
            /*        theme: true ? "dark" : "default",
                    previewTheme: true ? "dark" : "default",
                    editorTheme: true ? "pastel-on-dark" : "default",*/
        }
    }

    setValue(changedValues) {
        /*      console.info(changedValues);
              const {file} = changedValues.thumbnail;
              if (file !== undefined) {
                  changedValues.thumbnail = this.state.thumbnail;
              }*/
        this.setState(changedValues);
        this.articleFrom.current.setFieldsValue(changedValues);
    }

    onFinish(allValues) {
        allValues.content = this.jquery("#content").text();
        allValues.markdown = this.jquery("#markdown").text();
        allValues.keywords = this.jquery("#keywords").val();
        allValues.thumbnail = this.state.thumbnail;
        let uri;
        if (allValues.logId !== undefined) {
            uri = '/api/admin/article/update'
        } else {
            uri = '/api/admin/article/create'
        }
        axios.post(uri, JSON.stringify(allValues)).then(({data}) => {
            if (data.error) {
                message.error(data.message);
            } else {
                message.info(this.state.res['releaseSuccess']);
            }
            this.setValue({
                "logId": data.data.id,
                "digest": data.data.digest,
                "alias": data.data.alias
            })
        })
    }

    getArticleRoute() {
        if (this.state === undefined || this.state.res === undefined || this.state.res['articleRoute'] === undefined) {
            return "";
        }
        return this.state.res['articleRoute'];
    }

    preview() {
        this.onFinish();
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
        let originW = this.jquery("#thumbnail").width();
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
            message.success(`${info.file.response.url} file uploaded successfully.`);
            this.setValue({
                thumbnail: info.file.response.data.url
            });
        } else if (status === 'error') {
            message.error(`${info.file.name} file upload failed.`);
        }
    };


    render() {

        return (
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
                                    {/*<Button type="ghost">{this.state.res.preview}</Button>*/}
                                    <Button type='primary' enterButton
                                            htmlType='submit'>{this.state.res.release}</Button>
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
                                <div id='markdown' dangerouslySetInnerHTML={{__html: this.state.markdown}}
                                     style={{display: "none"}}/>
                                <div id='content' style={{display: "none"}}/>
                                <Editor config={this.getEditorConfig()}/>
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
                                                {(this.state.thumbnail === undefined ||
                                                    this.state.thumbnail === null ||
                                                    this.state.thumbnail === '') && (
                                                    <CameraOutlined style={{fontSize: "28px"}}/>
                                                )}
                                                {this.state.thumbnail !== '' && (
                                                    <Image
                                                        height={this.getThumbnailHeight(this.state.thumbnail)}
                                                        src={this.state.thumbnail}/>
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
                                        <ArticleEditTag keywords={this.state.keywords}
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
        )
    }
}