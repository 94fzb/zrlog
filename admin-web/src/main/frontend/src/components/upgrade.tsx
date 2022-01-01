import {useEffect, useState} from "react";
import {Button, Card, Col, Modal, Progress, Row, Steps} from 'antd';
import Title from "antd/es/typography/Title";
import Divider from "antd/es/divider";
import {getRes} from "../utils/constants";
import axios from "axios";

const {Step} = Steps;


let timer: NodeJS.Timeout;
let upgradeTimer: NodeJS.Timeout;
let checkRestartTimer: NodeJS.Timeout;

type UpgradeState = {
    current: number,
    restartSuccess: boolean,
    downloadProcess: number,
    changeLogLoading: boolean,
    changeLog: string,
    disabled: boolean,
    newBuildId?: string,
    finish?: boolean,
    upgradeMessage: string,
}

const Upgrade = () => {

    const steps = [
        {
            title: '变更日志'
        }, {
            title: '下载更新'
        }, {
            title: '执行更新'
        }
    ]

    const [state, setState] = useState<UpgradeState>({
            current: 0,
            restartSuccess: false,
            downloadProcess: 0,
            changeLogLoading: true,
            disabled: true,
            changeLog: "",
            upgradeMessage: ""
        }
    );

    useEffect(() => {
        axios.get("/api/admin/upgrade/checkNewVersion").then(async ({data}) => {
            if (data.data.upgrade) {
                setState({
                    ...state,
                    changeLog: data.data.version.changeLog,
                    disabled: false,
                    changeLogLoading: false
                })
            }
        });
    }, [])

    const checkRestartSuccess = () => {
        if (state.restartSuccess) {
            clearInterval(checkRestartTimer);
            Modal.info({
                title: "更新成功，跳转到管理首页？",
                content: '',
                okText: '确认',
                cancelText: '取消',
                onOk: function () {
                    window.location.href = "../"
                }
            });
            return;
        }
        axios.get('/api/admin/website/version').then(({data}) => {
            setState({
                ...state,
                restartSuccess: data.data.buildId === state.newBuildId,
            })
            checkRestartTimer = setTimeout(checkRestartSuccess, 500);
        }).catch(() => {
            checkRestartTimer = setTimeout(checkRestartSuccess, 500);
        });
    }

    const downloadProcess = async () => {
        if (state.downloadProcess === 100) {
            clearInterval(timer);
            return;
        }
        const current = 1;
        setState({...state, current: current});
        await axios.get('/api/admin/upgrade/download').then(({data}) => {
            setState({
                ...state,
                downloadProcess: data.data.process
            })
            timer = setTimeout(downloadProcess, 500);
        });
    }

    const upgrade = async () => {
        if (state.finish) {
            clearInterval(upgradeTimer);
            checkRestartSuccess();
            return;
        }
        const current = 2;
        setState({...state, current: current});
        await axios.get('/api/admin/upgrade/doUpgrade').then(({data}) => {
            setState({
                ...state,
                finish: data.data.finish,
                upgradeMessage: data.data.message,
                newBuildId: data.data.buildId
            })
            upgradeTimer = setTimeout(upgrade, 500);
        }).catch(() => {
            //重启中，可能存在404，可以直接进行下一步了
            checkRestartSuccess();
        });
    }


    const next = async () => {
        if (state.current === 0) {
            await downloadProcess();
        } else if (state.current === 1) {
            await upgrade();
        }
    }


    return (<Row>
            <Col md={18} xs={24}>
                <Title className='page-header' level={3}>{getRes()['upgradeWizard']}</Title>
                <Divider/>

                <Card>
                    <Steps current={state.current}>
                        {steps.map(item => (
                            <Step key={item.title} title={item.title}/>
                        ))}
                    </Steps>
                    <div className="steps-content" style={{marginTop: '20px'}}>
                        {state.current === 0 && (
                            <>
                                <Title level={4}>{getRes().changeLog}</Title>
                                <div dangerouslySetInnerHTML={{__html: state.changeLog}}/>
                            </>
                        )}
                        {state.current === 1 && (
                            <div>
                                <Title level={4}>下载更新包</Title>
                                <Progress strokeLinecap="square" percent={state.downloadProcess}/>
                            </div>
                        )}
                        {state.current === 2 && (
                            <div>
                                <Title level={4}>正在执行更新...</Title>
                                <div dangerouslySetInnerHTML={{__html: state.upgradeMessage}}/>
                            </div>
                        )}
                    </div>
                    <div className="steps-action" style={{paddingTop: '20px'}}>
                        {state.current < steps.length - 1 && (
                            <Button type="primary" disabled={state.disabled}
                                    onClick={() => next()}>
                                {getRes().nextStep}
                            </Button>
                        )}
                    </div>
                </Card>
            </Col>
        </Row>
    )
}

export default Upgrade;
