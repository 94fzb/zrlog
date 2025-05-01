import { FunctionComponent, useEffect, useRef, useState } from "react";
import { App, Button, Col, message, Progress, Row, Steps } from "antd";
import Title from "antd/es/typography/Title";
import { getRealRouteUrl, getRes } from "../utils/constants";
import { AxiosError } from "axios";
import { getContextPath } from "../utils/helpers";
import { useAxiosBaseInstance } from "../base/AppBase";
import { getCsrData, getVersion } from "../api";
import BaseTitle from "../base/BaseTitle";
import { UpgradeData } from "../type";
import UpgradeContent from "./upgrade-content";

const { Step } = Steps;
export const API_VERSION_PATH = "/api/public/version";
export const API_DO_UPGRADE_PATH = "/api/admin/upgrade/doUpgrade";

type UpgradeState = {
    current: number;
    downloadProcess: number;
    upgradeMessage: string;
};

type StepInfo = {
    title: string;
    alias: "changeLog" | "downloadProcess" | "doUpgrade";
};

export type UpgradeProps = {
    data: UpgradeData;
    offline: boolean;
    offlineData: boolean;
};

const Upgrade: FunctionComponent<UpgradeProps> = ({ data, offline, offlineData }) => {
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

    const upgradeTimer = useRef<NodeJS.Timeout | null>(null);

    const [state, setState] = useState<UpgradeState>({
        current: 0,
        downloadProcess: 0,
        upgradeMessage: "",
    });

    const { modal } = App.useApp();

    const [messageApi, contextHolder] = message.useMessage({ maxCount: 3 });

    const checkRestartSuccess = (newBuildId: string) => {
        getVersion(newBuildId, axiosInstance)
            .then(({ data }) => {
                if (newBuildId === data.buildId) {
                    modal.success({
                        title: data.message,
                        content: "",
                        onOk: function () {
                            window.location.href =
                                getRealRouteUrl(getContextPath() + "admin/index") + "?buildId=" + newBuildId;
                        },
                    });
                    return;
                }
                upgradeTimer.current = setTimeout(() => {
                    checkRestartSuccess(newBuildId);
                }, 500);
            })
            .catch(() => {
                upgradeTimer.current = setTimeout(() => {
                    checkRestartSuccess(newBuildId);
                }, 500);
            });
    };

    const axiosInstance = useAxiosBaseInstance();

    const downloadProcess = async () => {
        const current = 1;
        setState((prevState) => {
            return {
                ...prevState,
                current: current,
            };
        });
        try {
            const data = await getCsrData("/upgrade/download?preUpgradeKey=" + preUpgradeKey, axiosInstance);
            if (data.error) {
                messageApi.error(data.message);
                return;
            }
            setState((prevState) => {
                return {
                    ...prevState,
                    downloadProcess: data.data.process,
                    current: current,
                };
            });
            if (data.data.process < 100) {
                upgradeTimer.current = setTimeout(downloadProcess, 500);
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
        try {
            const { data } = await getCsrData("/upgrade/doUpgrade?preUpgradeKey=" + preUpgradeKey, axiosInstance);
            if (data && data.message) {
                setState((prevState) => {
                    return {
                        ...prevState,
                        upgradeMessage: data.message,
                        current: current,
                    };
                });
            }
            if (data && !data.finish) {
                upgradeTimer.current = setTimeout(upgrade, 500);
                return;
            }
            checkRestartSuccess(newBuildId);
        } catch (e) {
            console.error(e);
            //need restart check
            checkRestartSuccess(newBuildId);
        }
    };

    const next = async () => {
        if (state.current === 0) {
            if (isDisabledDownload()) {
                await upgrade();
            } else {
                await downloadProcess();
            }
        } else if (state.current === 1) {
            await upgrade();
        }
    };

    const nextDisabled = (): boolean => {
        if (offlineData) {
            return true;
        }
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

    const isDisabledDownload = () => {
        return !data.onlineUpgradable;
    };

    useEffect(() => {
        return () => {
            if (upgradeTimer && upgradeTimer.current) {
                clearTimeout(upgradeTimer.current);
            }
        };
    }, []);

    return (
        <Row key={data.preUpgradeKey}>
            {contextHolder}
            <Col style={{ maxWidth: 600 }} xs={24}>
                <BaseTitle title={getRes()["upgradeWizard"]} />
                <Steps current={state.current} style={{ paddingTop: 16 }}>
                    {steps.map((item) => {
                        if (item.alias === "downloadProcess") {
                            if (isDisabledDownload()) {
                                return <></>;
                            }
                        }
                        return <Step key={item.alias} title={item.title} />;
                    })}
                </Steps>
                <div className="steps-content" style={{ marginTop: "20px" }}>
                    {state.current === 0 && (
                        <>
                            <Title level={4}>{getRes().changeLog}</Title>
                            <UpgradeContent data={data} />
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
                        <Button type="primary" loading={offlineData} disabled={nextDisabled()} onClick={() => next()}>
                            {getRes().nextStep}
                        </Button>
                    )}
                </div>
            </Col>
        </Row>
    );
};

export default Upgrade;
