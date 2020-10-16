import React from "react";
import axios from "axios";

import {BaseResourceComponent} from "./base-resource-component";
import {Tabs} from "antd";
import Title from "antd/lib/typography/Title";
import Form from "antd/es/form";
import Input from "antd/es/input";
import Row from "antd/es/grid/row";
import Col from "antd/es/grid/col";
import Button from "antd/es/button";
import Divider from "antd/es/divider";
import TextArea from "antd/es/input/TextArea";
import {Spin} from "antd/es";
import Switch from "antd/es/switch";
import Select from "antd/es/select";

const {Option} = Select;

const {TabPane} = Tabs;

const activeKey = window.location.hash !== '' ? window.location.hash.substr(1) : "basic";

const layout = {
    labelCol: {span: 8},
    wrapperCol: {span: 16},
};

export class Website extends BaseResourceComponent {

    basicForm = React.createRef();
    otherForm = React.createRef();
    upgradeForm = React.createRef();
    blogForm = React.createRef();

    initState() {
        return {
            settingsLoading: true
        }
    }

    handleTabClick = key => {
        this.props.history.push(`/admin/website#${key}`)
        this.initForm();

    }

    getSecondTitle() {
        return this.state.res['admin.setting'];
    }

    componentDidMount() {
        super.componentDidMount();
        this.initForm();
    }

    initForm() {
        axios.get("/api/admin/website/settings").then(({data}) => {
            if (data.data.basic != null) {
                this.setBasicFormValue(data.data.basic);
            }
            if (data.data.other != null) {
                this.setOtherFormValue(data.data.other);
            }
            if (data.data.upgrade != null) {
                this.setUpgradeFormValue(data.data.upgrade);
            }
            if (data.data.blog != null) {
                this.setBlogFormValue(data.data.blog);
            }
            this.setState({
                settingsLoading: false
            })
        })
    }

    setBasicFormValue(changedValues) {
        if (this.basicForm.current !== null) {
            this.basicForm.current.setFieldsValue(changedValues);
            this.setState({
                basic: changedValues,
            });
        }
    }

    setOtherFormValue(changedValues) {
        if (this.otherForm.current !== null) {
            this.otherForm.current.setFieldsValue(changedValues);
            this.setState({
                other: changedValues,
            });
        }
    }

    setUpgradeFormValue(changedValues) {
        if (this.upgradeForm.current !== null) {
            this.upgradeForm.current.setFieldsValue(changedValues);
            this.setState({
                upgrade: changedValues,
            });
        }
    }

    setBlogFormValue(changedValues) {
        if (this.blogForm.current !== null) {
            this.blogForm.current.setFieldsValue(changedValues);
            this.setState({
                blog: changedValues,
            });
        }
    }

    render() {
        return (
            <Spin spinning={this.state.resLoading && this.state.settingsLoading}>
                <Title className='page-header' level={3}>{this.getSecondTitle()}</Title>
                <Divider/>
                <Tabs defaultActiveKey={activeKey} onChange={e => this.handleTabClick(e)}>
                    <TabPane tab="基本信息" key="basic">
                        <Row>
                            <Col md={12} xs={24}>
                                <Title level={4}>认真输入，有助于网站被收录</Title>
                                <Divider/>
                                <Form ref={this.basicForm} {...layout}
                                      onValuesChange={(k, v) => this.setBasicFormValue(k, v)}
                                >
                                    <Form.Item name='title' label='网站标题'>
                                        <Input placeholder='请输入网站标题'/>
                                    </Form.Item>
                                    <Form.Item name='second_title' label='网站副标题'>
                                        <Input placeholder='请输入网站副标题'/>
                                    </Form.Item>
                                    <Form.Item name='keywords' label='网站关键词'>
                                        <Input placeholder='请输入网站关键词'/>
                                    </Form.Item>
                                    <Form.Item name='description' label='网站描述'>
                                        <TextArea rows={5}/>
                                    </Form.Item>
                                    <Divider/>
                                    <Button type='primary'>{this.state.res.submit}</Button>
                                </Form>
                            </Col>
                        </Row>
                    </TabPane>
                    <TabPane tab="博客设置" key="blog">
                        <Row>
                            <Col md={12} xs={24}>
                                <Form {...layout} ref={this.blogForm}
                                      onValuesChange={(k, v) => this.setBlogFormValue(k, v)}>
                                    <Form.Item name='session_timeout' label='会话过期时间（分钟）'>
                                        <Input type={"number"} placeholder=''/>
                                    </Form.Item>
                                    <Form.Item valuePropName="checked" name='generator_html_status' label='静态化文章页'>
                                        <Switch/>
                                    </Form.Item>
                                    <Form.Item valuePropName="checked" name='disable_comment_status' label='关闭评论'>
                                        <Switch/>
                                    </Form.Item>
                                    <Form.Item valuePropName="checked" name='article_thumbnail_status' label='文章封面'>
                                        <Switch/>
                                    </Form.Item>
                                    <Form.Item name='language' label='语言'>
                                        <Select>
                                            <Option value='zh_CN'>{this.state.res['languageChinese']}</Option>
                                            <Option value='en_US'>{this.state.res['languageEnglish']}</Option>
                                        </Select>
                                    </Form.Item>
                                    <Form.Item name='article_route' label='文章路由'>
                                        <Select>
                                            <Option value=''>默认</Option>
                                            <Option value='post'>post</Option>
                                        </Select>
                                    </Form.Item>
                                    <Divider/>
                                    <Button type='primary'>{this.state.res.submit}</Button>
                                </Form>
                            </Col>
                        </Row>
                    </TabPane>
                    <TabPane tab="主题设置" key="template">

                    </TabPane>
                    <TabPane tab="其他设置" key="other">
                        <Row>
                            <Col md={12} xs={24}>
                                <Title level={4}>ICP，网站统计等信息</Title>
                                <Divider/>
                                <Form ref={this.otherForm} {...layout}
                                      onValuesChange={(k, v) => this.setBlogFormValue(k, v)}>
                                    <Form.Item name='icp' label='ICP备案信息'>
                                        <TextArea/>
                                    </Form.Item>
                                    <Form.Item name='webCm' label='网站统计'>
                                        <TextArea rows={7}/>
                                    </Form.Item>
                                    <Divider/>
                                    <Button type='primary'>{this.state.res.submit}</Button>
                                </Form>
                            </Col>
                        </Row>
                    </TabPane>
                    <TabPane tab={this.state.res['admin.upgrade.manage']} key="upgrade">
                        <Row>
                            <Col md={12} xs={24}>
                                <Form ref={this.upgradeForm} {...layout}
                                      onValuesChange={(k, v) => this.setUpgradeFormValue(k, v)}>
                                    <Form.Item name='autoUpgradeVersion'
                                               label={this.state.res['admin.upgrade.autoCheckCycle']}>
                                        <Select style={{width: 200}}>
                                            <Select.Option key='86400' value={86400}>
                                                {this.state.res['admin.upgrade.cycle.oneDay']}
                                            </Select.Option>
                                            <Select.Option key='604800' value={604800}>
                                                {this.state.res['admin.upgrade.cycle.oneWeek']}
                                            </Select.Option>
                                            <Select.Option key='1296000' value={1296000}>
                                                {this.state.res['admin.upgrade.cycle.halfMonth']}
                                            </Select.Option>
                                            <Select.Option key='-1' value={-1}>
                                                {this.state.res['admin.upgrade.cycle.never']}
                                            </Select.Option>
                                        </Select>
                                    </Form.Item>
                                    <Form.Item valuePropName="checked" name='upgradePreview' label={this.state.res['admin.upgrade.canPreview']}>
                                        <Switch/>
                                    </Form.Item>
                                    <Divider/>
                                    <Button type='primary'>{this.state.res.submit}</Button>
                                </Form>
                            </Col>
                        </Row>
                    </TabPane>
                </Tabs>
            </Spin>
        )
    }
}