import { useState } from "react";
import { App, Button, Card, Col, Progress, Row, Steps } from "antd";
import Title from "antd/es/typography/Title";
import Divider from "antd/es/divider";
import { getRes } from "../utils/constants";
import axios from "axios";

const { Step } = Steps;
export const API_VERSION_PATH = "/api/admin/website/version";

type UpgradeState = {
    current: number;
    downloadProcess: number;
    upgradeMessage: string;
};
type UpgradeData = {
    upgrade: boolean;
    dockerMode: boolean;
    version: UpgradeVersion;
};

type UpgradeVersion = {
    changeLog: string;
    buildId: string;
};

type StepInfo = {
    title: string;
    alias: "changeLog" | "downloadProcess" | "doUpgrade";
};

const Upgrade = ({ data }: { data: UpgradeData }) => {
    const steps: StepInfo[] = [
        {
            title: getRes()["changeLog"],
            alias: "changeLog",
        },
        {
            title: "下载更新",
            alias: "downloadProcess",
        },
        {
            title: "执行更新",
            alias: "doUpgrade",
        },
    ];

    const [state, setState] = useState<UpgradeState>({
        current: 0,
        downloadProcess: 0,
        upgradeMessage: "",
    });

    const { modal } = App.useApp();

    const checkRestartSuccess = (newBuildId: string) => {
        axios
            .get(API_VERSION_PATH + "?buildId=" + newBuildId)
            .then(({ data }) => {
                if (newBuildId === data.data.buildId) {
                    modal.info({
                        title: "更新成功，跳转到管理首页？",
                        content: "",
                        okText: "确认",
                        cancelText: "取消",
                        onOk: function () {
                            window.location.href = "/admin/index?buildId=" + newBuildId;
                        },
                    });
                    return;
                }
                setTimeout(() => {
                    checkRestartSuccess(newBuildId);
                }, 500);
            })
            .catch(() => {
                setTimeout(() => {
                    checkRestartSuccess(newBuildId);
                }, 500);
            });
    };

    const downloadProcess = async () => {
        const current = 1;
        setState((prevState) => {
            return {
                ...prevState,
                current: current,
            };
        });
        const { data } = await axios.get("/api/admin/upgrade/download");
        setState((prevState) => {
            return {
                ...prevState,
                downloadProcess: data.data.process,
                current: current,
            };
        });
        if (data.data.process < 100) {
            setTimeout(downloadProcess, 500);
        }
    };

    const newBuildId = data.version.buildId;

    const upgrade = async () => {
        const current = 2;
        setState((prevState) => {
            return {
                ...prevState,
                current: current,
            };
        });
        const { data } = await axios.get("/api/admin/upgrade/doUpgrade");
        setState((prevState) => {
            return {
                ...prevState,
                upgradeMessage: data.data.message,
                current: current,
            };
        });
        if (data.data.finish) {
            checkRestartSuccess(newBuildId);
            return;
        }
        setTimeout(upgrade, 500);
    };

    const next = async () => {
        if (state.current === 0) {
            if (data.dockerMode) {
                await upgrade();
            } else {
                await downloadProcess();
            }
        } else if (state.current === 1) {
            await upgrade();
        }
    };

    const nextDisabled = (): boolean => {
        if (!data.upgrade) {
            return true;
        }
        if (state.current === 1) {
            return state.downloadProcess < 100;
        }
        return false;
    };

    return (
        <Row>
            <Col md={18} xs={24}>
                <Title className="page-header" level={3}>
                    {getRes()["upgradeWizard"]}
                </Title>
                <Divider />

                <Card>
                    <Steps current={state.current}>
                        {steps.map((item) => {
                            if (item.alias === "downloadProcess" && data.dockerMode) {
                                return <></>;
                            }
                            return <Step key={item.alias} title={item.title} />;
                        })}
                    </Steps>
                    <div className="steps-content" style={{ marginTop: "20px" }}>
                        {state.current === 0 && (
                            <>
                                <Title level={4}>{getRes().changeLog}</Title>
                                <div
                                    style={{ overflowWrap: "break-word" }}
                                    dangerouslySetInnerHTML={{ __html: data.version ? data.version.changeLog : "" }}
                                />
                            </>
                        )}
                        {state.current === 1 && (
                            <>
                                <Title level={4}>下载更新包</Title>
                                <Progress strokeLinecap="round" percent={state.downloadProcess} />
                            </>
                        )}
                        {state.current === 2 && (
                            <>
                                <Title level={4}>正在执行更新...</Title>
                                <div
                                    style={{ overflowWrap: "break-word" }}
                                    dangerouslySetInnerHTML={{ __html: state.upgradeMessage }}
                                />
                            </>
                        )}
                    </div>
                    <div className="steps-action" style={{ paddingTop: "20px" }}>
                        {state.current < steps.length - 1 && (
                            <Button type="primary" disabled={nextDisabled()} onClick={() => next()}>
                                {getRes().nextStep}
                            </Button>
                        )}
                    </div>
                </Card>
            </Col>
        </Row>
    );
};

export default Upgrade;
