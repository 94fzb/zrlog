import ServerInfo from "./ServerInfo";
import { SystemData } from "../../type";
import { FunctionComponent, useEffect, useState } from "react";
import { getRes } from "../../utils/constants";
import Row from "antd/es/grid/row";
import { Col } from "antd";
import { getCsrData } from "../../api";
import { useAxiosBaseInstance } from "../../base/AppBase";
import BaseTitle from "../../base/BaseTitle";

type SystemProps = {
    data: SystemData;
    offline: boolean;
};

let timer: NodeJS.Timeout;

const System: FunctionComponent<SystemProps> = ({ data }) => {
    const [state, setState] = useState<SystemData>(data);

    const axiosInstance = useAxiosBaseInstance();

    const cycleDuration = 5000;

    const fetchSystemInfo = () => {
        if (document.visibilityState === "visible") {
            getCsrData("/system", axiosInstance).then(({ data }) => {
                setState(data);
                timer = setTimeout(fetchSystemInfo, cycleDuration);
            });
        } else {
            timer = setTimeout(fetchSystemInfo, cycleDuration);
        }
    };

    useEffect(() => {
        timer = setTimeout(fetchSystemInfo, cycleDuration);
        return () => {
            if (timer) {
                clearTimeout(timer);
            }
        };
    }, []);

    useEffect(() => {
        setState(data);
    }, [data]);

    return (
        <>
            <BaseTitle title={getRes()["systemInfo"]} />
            <Row gutter={[8, 8]}>
                <Col xs={24} md={12}>
                    <ServerInfo
                        title={getRes()["serverInfo"]}
                        data={state.serverInfos}
                        dockerMode={state.dockerMode}
                        nativeImageMode={state.nativeImageMode}
                    />
                </Col>
                <Col xs={24} md={12}>
                    <ServerInfo
                        title={getRes()["admin.index.outline"]}
                        data={state.serverInfos2}
                        dockerMode={state.dockerMode}
                        nativeImageMode={state.nativeImageMode}
                    />
                </Col>
            </Row>
        </>
    );
};

export default System;
