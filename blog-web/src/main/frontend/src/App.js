import React from 'react';
import './App.css';
import {Alert, Button, Card, Divider, Form, Input, Layout, message, Spin, Steps, Typography} from 'antd';

import axios from "axios";
import Text from "antd/es/typography/Text";
import * as querystring from "querystring";

const FormItem = Form.Item;
const {Title} = Typography;

const {Content, Footer} = Layout;

const {Step} = Steps;

const formItemLayout = {
    labelCol: {
        xs: {span: 24},
        sm: {span: 4},
    },
    wrapperCol: {
        xs: {span: 24},
        sm: {span: 8},
    },
};


class App extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            current: -1,
            loading: true,
            installDone: false,
            steps: [
                {
                    title: '-',
                },
                {
                    title: '-',
                },
                {
                    title: '-',
                },
            ],
            dataBaseInfo: {},
            weblogInfo: {},
            res: {
                installWizard: '',
                utfTips: ''
            }
        };
        axios.defaults.baseURL = document.baseURI
    }

    formDataBaseInfoRef = React.createRef();
    formWeblogInfoRef = React.createRef();

    setDatabaseValue(changedValues, allValues) {
        this.formDataBaseInfoRef.current.setFieldsValue(changedValues);
        this.setState({
            dataBaseInfo: allValues,
        });
    }

    setWeblogValue(changedValues, allValues) {
        this.formWeblogInfoRef.current.setFieldsValue(changedValues);
        this.setState({
            weblogInfo: allValues,
        });
    }

    fetchRes(installed) {
        axios.get('/api/public/installResource').then(({data}) => {
            data.data.copyrightTips = data.data.copyright + ' ZrLog'
            this.setState({
                res: data.data,
                current: 0,
                steps: [{
                    title: data.data['installDatabaseInfo']
                }, {
                    title: data.data['installWebSiteInfo']
                }, {
                    title: data.data['installComplete']
                }],
                loading: false,
                installDone: installed
            });
            document.title = this.state.res.installWizard;
        })
    }

    componentDidMount() {
        axios.get('/api/public/installed').then(({data}) => {
            this.fetchRes(data.data.installed)
        })

    }

    next() {
        if (this.state.current === 0) {
            axios.get("/api/install/testDbConn?" + querystring.stringify(this.state.dataBaseInfo)).then(({data}) => {
                if (!data.error) {
                    const current = this.state.current + 1;
                    this.setState({current});
                } else {
                    message.error(data.message);
                }
            })
        } else if (this.state.current === 1) {
            axios.post("/api/install/startInstall", querystring.stringify({
                ...this.state.dataBaseInfo,
                ...this.state.weblogInfo
            }), {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                }
            }).then(({data}) => {
                if (!data.error) {
                    const current = this.state.current + 1;
                    this.setState({current});
                } else {
                    message.error(data.message);
                }
            })
        } else {

        }

    }

    prev() {
        const current = this.state.current - 1;
        this.setState({current});
    }

    render() {
        const {current} = this.state;
        return (
            <Spin spinning={this.state.loading} style={{height: "100vh"}}>
                <Layout hidden={this.state.loading}>
                    <Content>
                        <Card title={this.state.res.installWizard} className='container'>
                            {this.state.installDone && (
                                <Title level={3} type={"danger"}
                                       style={{textAlign: 'center'}}>{this.state.res.installed}</Title>
                            )}
                            {!this.state.installDone && (
                                <div>
                                    {/*utf tips for some window env*/}
                                    <div hidden={this.state.res['utfTips'] === ''}>
                                        <Alert type='error'
                                               message={<div
                                                   dangerouslySetInnerHTML={{__html: this.state.res['utfTips']}}/>}
                                               showIcon>
                                        </Alert>
                                        <Divider/>
                                    </div>
                                    <Steps current={current}>
                                        {this.state.steps.map(item => (
                                            <Step key={item.title} title={item.title}/>
                                        ))}
                                    </Steps>
                                    <div className="steps-content" style={{marginTop: '20px'}}>
                                        {current === 0 && (
                                            <Form ref={this.formDataBaseInfoRef} {...formItemLayout}
                                                  onValuesChange={(k, v) => this.setDatabaseValue(k, v)}>
                                                <div>
                                                    <Title level={3}
                                                           type="danger">{this.state.res.installPrompt}</Title>
                                                    <ul>
                                                        <li><Text type="danger">{this.state.res.installWarn1}</Text>
                                                        </li>
                                                        <li><Text type="danger">{this.state.res.installWarn2}</Text>
                                                        </li>
                                                    </ul>
                                                </div>
                                                <Title level={4}>{this.state.res.installInputDbInfo}</Title>
                                                <FormItem name='dbHost' label={this.state.res.installDbHost}
                                                          rules={[{required: true}]}>
                                                    <Input placeholder='127.0.0.1'/>
                                                </FormItem>
                                                <FormItem name='dbName' label={this.state.res.installDbName}
                                                          rules={[{required: true}]}>
                                                    <Input placeholder='ZrLog'/>
                                                </FormItem>
                                                <FormItem name='dbUserName' label={this.state.res.installDbUserName}
                                                          rules={[{required: true}]}>
                                                    <Input placeholder='root'/>
                                                </FormItem>
                                                <FormItem name='dbPassword' label={this.state.res.installDbPassword}>
                                                    <Input type='password'/>
                                                </FormItem>
                                                <FormItem name='dbPort' label={this.state.res.installDbPort}
                                                          rules={[{required: true}]}>
                                                    <Input type='number' placeholder='3306'/>
                                                </FormItem>
                                            </Form>
                                        )}
                                        {current === 1 && (
                                            <Form ref={this.formWeblogInfoRef} {...formItemLayout}
                                                  onValuesChange={(k, v) => this.setWeblogValue(k, v)}>
                                                <Title level={3}>{this.state.res.installInputWebSiteInfo}</Title>
                                                <FormItem name='username' label={this.state.res.installAdmin}
                                                          rules={[{required: true}]}>
                                                    <Input placeholder='admin'/>
                                                </FormItem>
                                                <FormItem name='password' label={this.state.res.installAdminPassword}
                                                          rules={[{required: true}]}>
                                                    <Input type='password'/>
                                                </FormItem>
                                                <FormItem name='email' label={this.state.res.installAdminEmail}>
                                                    <Input type='email'/>
                                                </FormItem>
                                                <FormItem name='title' label={this.state.res.installWebSiteTitle}
                                                          rules={[{required: true}]}>
                                                    <Input placeholder={this.state.res.installWebSiteTitleTip}/>
                                                </FormItem>
                                                <FormItem name='second_title'
                                                          label={this.state.res.installWebSiteSecond}>
                                                    <Input/>
                                                </FormItem>
                                            </Form>
                                        )}
                                        {current === 2 && (
                                            <div style={{textAlign: 'center'}}>
                                                <Title level={3} type='success'>{this.state.res.installSuccess}</Title>
                                                <a href={document.baseURI}>{this.state.res.installSuccessView}</a>
                                            </div>
                                        )}
                                    </div>
                                    <div className="steps-action" style={{paddingTop: '20px'}}>
                                        {current < this.state.steps.length - 1 && (
                                            <Button type="primary" onClick={() => this.next()}>
                                                {this.state.res.installNextStep}
                                            </Button>
                                        )}
                                        {current === this.state.steps.length - 1 && (
                                            <Button type="primary"
                                                    onClick={() => message.success(this.state.res.installSuccess)}>
                                                {this.state.res.installDone}
                                            </Button>
                                        )}
                                        {current > 0 && (
                                            <Button style={{margin: '0 8px'}} onClick={() => this.prev()}>
                                                {this.state.res.installPreviousStep}
                                            </Button>
                                        )}
                                    </div>
                                </div>
                            )}
                            <Divider/>
                            <Title level={4} style={{textAlign: "center", marginTop: '20px'}}>
                                <div dangerouslySetInnerHTML={{__html: this.state.res.installFeedback}}/>
                            </Title>
                        </Card>
                    </Content>
                    <Footer style={{textAlign: 'center'}}>{this.state.res.copyrightTips} . All Rights Reserved.</Footer>
                </Layout>
            </Spin>
        );
    }
}

export default App;
