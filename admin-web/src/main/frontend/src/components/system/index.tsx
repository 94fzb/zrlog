import ServerInfo from "./ServerInfo";
import {SystemData} from "../../type";
import {FunctionComponent, useEffect, useState} from "react";
import Title from "antd/es/typography/Title";
import {getRes} from "../../utils/constants";
import SystemDiskInfo from "./SystemDiskInfo";
import Row from "antd/es/grid/row";
import {Col} from "antd";
import {getCsrData} from "../../api";

type SystemProps = {
    data: SystemData;
    offline: boolean;
};

let timer: NodeJS.Timeout;

const System: FunctionComponent<SystemProps> = ({data}) => {

    const [state, setState] = useState<SystemData>(data);

    const fetchSystemInfo = () => {
        if (document.visibilityState === "visible") {
            getCsrData("/system").then(({data}) => {
                setState(data)
                timer = setTimeout(fetchSystemInfo, 5000);
            });
        } else {
            timer = setTimeout(fetchSystemInfo, 5000);
        }
    }

    useEffect(() => {
        fetchSystemInfo()
        return () => {
            if (timer) {
                clearTimeout(timer);
            }
        }
    }, [])

    useEffect(() => {
        setState(data);
    }, [data])

    return <>
        <Title className="page-header" level={3}>
            {getRes()["systemInfo"]}
        </Title>
        <Row gutter={[8, 8]}>
            <Col xs={24} md={12}>
                <ServerInfo data={state.serverInfos} dockerMode={state.dockerMode}
                            nativeImageMode={state.nativeImageMode}/>
            </Col>
            <Col xs={24} md={12}>
                <SystemDiskInfo data={state.systemIOInfo}/>
            </Col>
        </Row>
    </>

}

export default System