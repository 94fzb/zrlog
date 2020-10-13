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

export class ArticleEdit extends BaseResourceComponent {

    articleFrom = React.createRef();

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
            this.setState({
                globalLoading: false
            });
            const query = new URLSearchParams(this.props.location.search);
            const id = query.get("id");
            if (id !== null && id !== '') {
                axios.get("/api/admin/article/detail?id=" + id).then(({data}) => {
                    this.editMdConfig.markdown = data.data.markdown
                    this.setValue(data.data)
                })
            }
        });
    };

    editMdConfig = {
        codeFold: true,
        appendMarkdown: "",
        markdown: '',
        path: "/vendors/markdown/lib/",
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
        /*theme: dark ? "dark" : "default",
        previewTheme: dark ? "dark" : "default",
        editorTheme: dark ? "pastel-on-dark" : "default",*/
    }

    setValue(changedValues) {
        this.setState(changedValues);
        console.info(changedValues);
        this.articleFrom.current.setFieldsValue(changedValues);
    }

    onFinish(allValues) {
        axios.post("/api/admin/article/create", JSON.stringify(allValues)).then(({data}) => {
            if (data.error) {
                message.error(data.message);
            } else {

            }
        })
    }

    getArticleRoute() {
        if(this.state === undefined || this.state.res === undefined || this.state.res['articleRoute'] === undefined) {
            return "";
        }
        return this.state.res['articleRoute'];
    }


    render() {
        return (
            <Spin spinning={this.state.resLoading}>
                <Title className='page-header' level={3}>{this.getSecondTitle()}</Title>
                <Divider/>
                <Form
                    ref={this.articleFrom}
                    onFinish={(values) => this.onFinish(values)}
                    onValuesChange={(k, v) => this.setValue(k, v)}>
                    <Row style={{paddingBottom: "15px"}}>
                        <Col offset={18} span={6}>
                            <Space size={5} style={{float: "right"}}>
                                <Button type="ghost">{this.state.res.preview}</Button>
                                <Button type='primary' enterButton
                                        htmlType='submit'>{this.state.res.saveAsDraft}</Button>
                            </Space>
                        </Col>
                    </Row>
                    <Row>
                        <Col span={10}>
                            <Form.Item
                                name="title"
                                rules={[{required: true, message: this.state.res.inputArticleTitle}]}>
                                <Input placeholder={this.state.res.inputArticleTitle}/>
                            </Form.Item>
                        </Col>
                        <Col span={5}>
                            <Form.Item name="alias">
                                <Input addonBefore={this.getArticleRoute() + "/"}
                                       placeholder={this.state.res.inputArticleAlias}/>
                            </Form.Item>
                        </Col>
                        <Col span={2}>

                        </Col>
                    </Row>
                    <Row>
                        <Col span={18} style={{zIndex: 10}}>
                            <Form.Item name='content'>
                                <Editor config={this.editMdConfig}>{this.state.markdown}</Editor>
                            </Form.Item>
                        </Col>
                        <Col span={6}>
                            <Row style={{paddingLeft: "12px"}}>
                                <Col span={24}>
                                    <Card size="small" className='x-card'>
                                        <Dragger>
                                            <CameraOutlined style={{fontSize: "28px"}}/>
                                        </Dragger>
                                    </Card>
                                </Col>
                                <Col span={24}>
                                    <Card size="small" className='x-card' title={this.state.res['admin.setting']}>
                                        <Form.Item valuePropName="checked" name='canComment' label={this.state.res['commentAble']}>
                                            <Switch size="small"/>
                                        </Form.Item>
                                        <Form.Item valuePropName="checked" name='privacy' label={this.state.res['private']}>
                                            <Switch size="small"/>
                                        </Form.Item>
                                    </Card>
                                </Col>
                                <Col span={24}>
                                    <Card size="small" className='x-card' title={this.state.res['admin.type.manage']}>
                                        <Form.Item name='typeId' rules={[{required: true}]}>
                                            <Radio.Group style={{width: "100%"}}>
                                                {this.state.typeOptions}
                                            </Radio.Group>
                                        </Form.Item>
                                    </Card>
                                </Col>
                                <Col span={24}>
                                    <Card size="small" className='x-card' title={this.state.res.tag}>
                                        <ArticleEditTag allTags={this.state.tags.map(x => x.text)} tags={[]}/>
                                    </Card>
                                </Col>

                                <Col span={24}>
                                    <Card size="small" className='x-card' title={this.state.res.digest}>
                                        <Form.Item name='digest'>
                                            <TextArea rows={3}/>
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