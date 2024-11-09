import { useEffect, useState } from "react";
import { App, Button, Col, message, Progress, Row, Steps } from "antd";
import Title from "antd/es/typography/Title";
import Divider from "antd/es/divider";
import { getRes } from "../utils/constants";
import axios, { AxiosError } from "axios";

const { Step } = Steps;
export const API_VERSION_PATH = "/api/admin/website/version";

type UpgradeState = {
    current: number;
    downloadProcess: number;
    upgradeMessage: string;
};
export type UpgradeData = {
    upgrade: boolean;
    dockerMode: boolean;
    preUpgradeKey: string;
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

let upgradeTimer: NodeJS.Timeout;

const Upgrade = ({ data, offline }: { data: UpgradeData; offline: boolean }) => {
    const preUpgradeKey = data.preUpgradeKey;
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

    const [messageApi, contextHolder] = message.useMessage({ maxCount: 3 });

    const checkRestartSuccess = (newBuildId: string) => {
        axios
            .get(API_VERSION_PATH + "?buildId=" + newBuildId)
            .then(({ data }) => {
                if (newBuildId === data.data.buildId) {
                    modal.success({
                        title: data.data.message,
                        content: "",
                        onOk: function () {
                            window.location.href = "/admin/index?buildId=" + newBuildId;
                        },
                    });
                    return;
                }
                upgradeTimer = setTimeout(() => {
                    checkRestartSuccess(newBuildId);
                }, 500);
            })
            .catch(() => {
                upgradeTimer = setTimeout(() => {
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
        try {
            const { data } = await axios.get("/api/admin/upgrade/download?preUpgradeKey=" + preUpgradeKey);
            setState((prevState) => {
                return {
                    ...prevState,
                    downloadProcess: data.data.process,
                    current: current,
                };
            });
            if (data.data.process < 100) {
                upgradeTimer = setTimeout(downloadProcess, 500);
            }
        } catch (e) {
            if (e instanceof AxiosError) {
                if (e.response && e.response.data) {
                    messageApi.error(e.response.data.message);
                }
            }
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
        const { data } = await axios.get("/api/admin/upgrade/doUpgrade?preUpgradeKey=" + preUpgradeKey);
        if (data.data) {
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
        } else {
            //没有 data 也认为在重启中了
            checkRestartSuccess(newBuildId);
            return;
        }
        upgradeTimer = setTimeout(upgrade, 500);
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
        if (offline) {
            return true;
        }
        if (!data.upgrade) {
            return true;
        }
        if (state.current === 1) {
            return state.downloadProcess < 100;
        }
        return false;
    };

    useEffect(() => {
        return () => {
            if (upgradeTimer) {
                clearTimeout(upgradeTimer);
            }
        };
    }, []);

    return (
        <Row>
            {contextHolder}
            <Col style={{ maxWidth: 600 }} xs={24}>
                <Title className="page-header" level={3}>
                    {getRes()["upgradeWizard"]}
                </Title>
                <Divider />

                <Steps current={state.current} style={{ paddingTop: 16 }}>
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
            </Col>
        </Row>
    );
};

export default Upgrade;
