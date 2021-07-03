import React from "react";

import {BaseResourceComponent} from "./base-resource-component";
import {Button, Card, Col, Modal, Progress, Row, Spin, Steps} from 'antd';
import Title from "antd/es/typography/Title";
import Divider from "antd/es/divider";
import axios from "axios";

const {Step} = Steps;


let timer;
let upgradeTimer;
let checkRestartTimer;

class Upgrade extends BaseResourceComponent {


    initState() {
        return {
            current: 0,
            restartSuccess: false,
            downloadProcess: 0,
            changeLogLoading: true,
            steps: [{
                title: '变更日志'
            }, {
                title: '下载更新'
            }, {
                title: '执行更新'
            }],
            disabled: true
        }
    }

    componentDidMount() {
        super.componentDidMount();
        axios.get("/api/admin/upgrade/checkNewVersion").then(async ({data}) => {
            if (data.data.upgrade) {
                this.setState({
                    changeLog: data.data.version.changeLog,
                    disabled: false,
                    changeLogLoading: false
                })
            }
        });
    }

    checkRestartSuccess = () => {
        if (this.state.restartSuccess) {
            clearInterval(checkRestartTimer);
            Modal.info({
                title: "更新成功，跳转到管理首页？",
                content: '',
                okText: '确认',
                cancelText: '取消',
                onOk: function () {
                    window.location.href = "/admin/"
                }
            });
            return;
        }
        axios.get('/api/admin/website/version').then(({data}) => {
            this.setState({
                restartSuccess: data.data.buildId === this.state.newBuildId,
            })
            checkRestartTimer = setTimeout(this.checkRestartSuccess, 500);
        }).catch(() => {
            checkRestartTimer = setTimeout(this.checkRestartSuccess, 500);
        });
    }

    downloadProcess = () => {
        if (this.state.downloadProcess === 100) {
            clearInterval(timer);
            return;
        }
        axios.get('/api/admin/upgrade/download').then(({data}) => {
            this.setState({
                downloadProcess: data.data.process
            })
            timer = setTimeout(this.downloadProcess, 500);
        });
    }

    upgrade = () => {
        if (this.state.finish) {
            clearInterval(upgradeTimer);
            this.checkRestartSuccess();
            return;
        }
        axios.get('/api/admin/upgrade/doUpgrade').then(({data}) => {
            this.setState({
                finish: data.data.finish,
                upgradeMessage: data.data.message,
                newBuildId: data.data.buildId
            })
            upgradeTimer = setTimeout(this.upgrade, 500);
        }).catch(() => {
            //重启中，可能存在404，可以直接进行下一步了
            this.checkRestartSuccess();
        });
    }

    getSecondTitle() {
        return this.state.res['upgradeWizard'];
    }

    next = () => {
        if (this.state.current === 0) {
            this.downloadProcess();
            const current = this.state.current + 1;
            this.setState({current});
        } else if (this.state.current === 1) {
            this.upgrade();
            const current = this.state.current + 1;
            this.setState({current});
        }
    }

    render() {
        const {current} = this.state;

        return (<Row>
                <Col md={18} xs={24}>
                    <Spin spinning={this.state.resLoading || this.state.changeLogLoading}>
                        <Title className='page-header' level={3}>{this.getSecondTitle()}</Title>
                        <Divider/>

                        <Card>
                            <Steps current={current}>
                                {this.state.steps.map(item => (
                                    <Step key={item.title} title={item.title}/>
                                ))}
                            </Steps>
                            <div className="steps-content" style={{marginTop: '20px'}}>
                                {current === 0 && (
                                    <>
                                        <Title level={4}>{this.state.res.changeLog}</Title>
                                        <div dangerouslySetInnerHTML={{__html: this.state.changeLog}}/>
                                    </>
                                )}
                                {current === 1 && (
                                    <div>
                                        <Title level={4}>下载更新包</Title>
                                        <Progress strokeLinecap="square" percent={this.state.downloadProcess}/>
                                    </div>
                                )}
                                {current === 2 && (
                                    <div>
                                        <Title level={4}>正在执行更新...</Title>
                                        <div dangerouslySetInnerHTML={{__html: this.state.upgradeMessage}}/>
                                    </div>
                                )}
                            </div>
                            <div className="steps-action" style={{paddingTop: '20px'}}>
                                {current < this.state.steps.length - 1 && (
                                    <Button type="primary" disabled={this.state.disabled}
                                            onClick={() => this.next()}>
                                        {this.state.res.nextStep}
                                    </Button>
                                )}
                            </div>
                        </Card>
                    </Spin>
                </Col>
            </Row>
        )
    }
}

export default Upgrade;
