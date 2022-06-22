import {useEffect, useRef, useState} from 'react';
import '../App.css';
import {Alert, Button, Card, Divider, Form, FormInstance, Input, Layout, message, Steps, Typography} from 'antd';

import axios from "axios";
import Text from "antd/es/typography/Text";
import {getRes} from "../utils/constants";
import {mapToQueryString} from "../utils/helpers";

const FormItem = Form.Item;
const {Title} = Typography;

const {Footer} = Layout;

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

type AppState = {
    current: number;
    installed: false,
    dataBaseInfo: Record<string, unknown>,
    weblogInfo: Record<string, unknown>;
}


const App = () => {

    const [state, setState] = useState<AppState>({
        current: 0,
        installed: false,
        dataBaseInfo: {},
        weblogInfo: {},
    })

    const getSteps = () => {
        return [
            {
                title: getRes()['installDatabaseInfo'],
            }, {
                title: getRes()['installWebSiteInfo']
            }, {
                title: getRes()['installComplete']
            }
        ]
    }

    useEffect(() => {
        setState({
            ...state, installed: getRes()['installed'],
        })
    }, [])

    const formDataBaseInfoRef = useRef<FormInstance>(null);
    const formWeblogInfoRef = useRef<FormInstance>(null);

    const setDatabaseValue = (changedValues: Record<string, unknown>, allValues: Record<string, unknown>) => {
        if (formDataBaseInfoRef === undefined || formDataBaseInfoRef.current === undefined || formDataBaseInfoRef.current === null) {
            return;
        }
        formDataBaseInfoRef.current.setFieldsValue(changedValues);
        setState({
            ...state,
            dataBaseInfo: allValues,
        });
    }

    const setWeblogValue = (changedValues: Record<string, unknown>, allValues: Record<string, unknown>) => {
        if (formWeblogInfoRef === undefined || formWeblogInfoRef.current === undefined || formWeblogInfoRef.current === null) {
            return;
        }
        formWeblogInfoRef.current.setFieldsValue(changedValues);
        setState({
            ...state,
            weblogInfo: allValues,
        });
    }

    const next = () => {
        if (state.current === 0) {
            // @ts-ignore
            axios.get("/api/install/testDbConn?" + mapToQueryString(state.dataBaseInfo)).then(({data}) => {
                if (!data.error) {
                    const current = state.current + 1;
                    setState({...state, current: current});
                } else {
                    message.error(data.message);
                }
            })
        } else if (state.current === 1) {
            // @ts-ignore
            axios.post("/api/install/startInstall", mapToQueryString({
                ...state.dataBaseInfo,
                ...state.weblogInfo
            }), {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                }
            }).then(({data}) => {
                if (!data.error) {
                    const current = state.current + 1;
                    setState({...state, current: current});
                } else {
                    message.error(data.message);
                }
            })
        } else {

        }

    }

    const prev = () => {
        const current = state.current - 1;
        setState({...state, current: current});
    }


    return (
        <Layout style={{height: "100vh", display: "flex", alignItems: "center"}}>
            <Card title={getRes().installWizard} className='container'>
                {state.installed && (
                    <Title level={3} type={"danger"}
                           style={{textAlign: 'center'}}>{getRes().installedTips}</Title>
                )}
                {!state.installed && (
                    <div>
                        {/*utf tips for some window env*/}
                        <div hidden={getRes()['utfTips'] === ''}>
                            <Alert type='error'
                                   message={<div
                                       dangerouslySetInnerHTML={{__html: getRes()['utfTips']}}/>}
                                   showIcon/>
                            <Divider/>
                        </div>
                        <Steps current={state.current}>
                            {getSteps().map(item => (
                                <Step key={item.title + ""} title={item.title + ""}/>
                            ))}
                        </Steps>
                        <div className="steps-content" style={{marginTop: '20px'}}>
                            {state.current === 0 && (
                                <Form ref={formDataBaseInfoRef} {...formItemLayout}
                                      onValuesChange={(k, v) => setDatabaseValue(k, v)}>
                                    <div>
                                        <Title level={3}
                                               type="danger">{getRes().installPrompt}</Title>
                                        <ul>
                                            <li><Text type="danger">{getRes().installWarn1}</Text>
                                            </li>
                                            <li><Text type="danger">{getRes().installWarn2}</Text>
                                            </li>
                                        </ul>
                                    </div>
                                    <Title level={4}>{getRes().installInputDbInfo}</Title>
                                    <FormItem name='dbHost' label={getRes().installDbHost}
                                              rules={[{required: true}]}>
                                        <Input placeholder='127.0.0.1'/>
                                    </FormItem>
                                    <FormItem name='dbName' label={getRes().installDbName}
                                              rules={[{required: true}]}>
                                        <Input placeholder='ZrLog'/>
                                    </FormItem>
                                    <FormItem name='dbUserName' label={getRes().installDbUserName}
                                              rules={[{required: true}]}>
                                        <Input placeholder='root'/>
                                    </FormItem>
                                    <FormItem name='dbPassword' label={getRes().installDbPassword}>
                                        <Input type='password'/>
                                    </FormItem>
                                    <FormItem name='dbPort' label={getRes().installDbPort}
                                              rules={[{required: true}]}>
                                        <Input type='number' placeholder='3306'/>
                                    </FormItem>
                                </Form>
                            )}
                            {state.current === 1 && (
                                <Form ref={formWeblogInfoRef} {...formItemLayout}
                                      onValuesChange={(k, v) => setWeblogValue(k, v)}>
                                    <Title level={3}>{getRes().installInputWebSiteInfo}</Title>
                                    <FormItem name='username' label={getRes().installAdmin}
                                              rules={[{required: true}]}>
                                        <Input placeholder='admin'/>
                                    </FormItem>
                                    <FormItem name='password' label={getRes().installAdminPassword}
                                              rules={[{required: true}]}>
                                        <Input type='password'/>
                                    </FormItem>
                                    <FormItem name='email' label={getRes().installAdminEmail}>
                                        <Input type='email'/>
                                    </FormItem>
                                    <FormItem name='title' label={getRes().installWebSiteTitle}
                                              rules={[{required: true}]}>
                                        <Input placeholder={getRes().installWebSiteTitleTip}/>
                                    </FormItem>
                                    <FormItem name='second_title'
                                              label={getRes().installWebSiteSecond}>
                                        <Input/>
                                    </FormItem>
                                </Form>
                            )}
                            {state.current === 2 && (
                                <div style={{textAlign: 'center'}}>
                                    <Title level={3} type='success'>{getRes().installSuccess}</Title>
                                    <a href={document.baseURI}>{getRes().installSuccessView}</a>
                                </div>
                            )}
                        </div>
                        <div className="steps-action" style={{paddingTop: '20px'}}>
                            {state.current < getSteps().length - 1 && (
                                <Button type="primary" onClick={() => next()}>
                                    {getRes().installNextStep}
                                </Button>
                            )}
                            {state.current === getSteps().length - 1 && (
                                <Button type="primary"
                                        onClick={() => message.success(getRes().installSuccess)}>
                                    {getRes().installDone}
                                </Button>
                            )}
                            {state.current > 0 && (
                                <Button style={{margin: '0 8px'}} onClick={() => prev()}>
                                    {getRes().installPreviousStep}
                                </Button>
                            )}
                        </div>
                    </div>
                )}
                <Divider/>
                <Title level={4} style={{textAlign: "center", marginTop: '20px'}}>
                    <div dangerouslySetInnerHTML={{__html: getRes().installFeedback}}/>
                </Title>
            </Card>
            <Footer style={{textAlign: 'center'}}><span
                dangerouslySetInnerHTML={{__html: getRes().copyrightTips}}/> .
                All Rights Reserved.</Footer>
        </Layout>
    );
}

export default App;
