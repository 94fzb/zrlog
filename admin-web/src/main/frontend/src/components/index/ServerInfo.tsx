import {Card} from "antd";
import {getRes} from "../../utils/constants";
import Table from "antd/es/table";
import DockerOutlined from "../../icons/DockerOutlined";
import {useEffect, useState} from "react";
import axios from "axios";

type ServerInfoState = {
    system: Record<string, any>[],
    docker: boolean,
}


const ServerInfo = () => {

    const [indexState, setIndexState] = useState<ServerInfoState>({
        docker: false,
        system: []
    })


    const getFixedColumns = () => {
        return [
            {
                title: getRes().key,
                dataIndex: 'name',
                key: 'name',
                fixed: true,
                width: 180,
            },
            {
                title: getRes().value,
                key: 'value',
                dataIndex: 'value',
                render: (e: string, r: any) => {
                    if (r.name !== '运行环境') {
                        return e;
                    } else {
                        if (indexState.docker) {
                            return (<><DockerOutlined/><span style={{marginLeft: 3}}>{e}</span></>);
                        } else {
                            return e;
                        }
                    }
                }
            },
        ];
    }

    useEffect(() => {
        axios.get("/api/admin/serverInfo").then(({data}) => {
            const system = [
                {
                    "name": "运行环境",
                    "value": ""
                },
                {
                    "name": "JavaEE 容器信息",
                    "value": ""
                }
                ,
                {
                    "name": "运行路径",
                    "value": ""
                },
                {
                    "name": "操作系统",
                    "value": ""
                },
                {
                    "name": "系统时区 - 地域/语言",
                    "value": ""
                },
                {
                    "name": "数据库版本",
                    "value": ""
                },
                {
                    "name": "系统编码",
                    "value": ""
                },
                {
                    "name": "程序版本",
                    "value": ""
                }
            ];
            system[0].value = data.data['java.vm.name'] + " - " + data.data['java.runtime.version'];
            system[1].value = data.data['server.info'];
            system[2].value = data.data['zrlog.runtime.path'];
            system[3].value = data.data['os.name'] + " - " + data.data['os.arch'] + " - " + data.data['os.version'];
            system[4].value = data.data['user.timezone'] + " - " + data.data['user.country'] + "/" + data.data['user.language'];
            system[5].value = data.data['dbServer.version'];
            system[6].value = data.data['file.encoding'];
            system[7].value = data.data['zrlog.version'] + " - " + data.data['zrlog.buildId'] + " (" + data.data['zrlog.buildTime'] + ")";
            setIndexState({
                ...indexState,
                system: system,
                docker: data.data['docker'] === 'docker'
            })
        });
    },[])

    return <Card size={"small"} title={getRes()['serverInfo']}>
        <Table
            style={{wordBreak: "break-all"}}
            size='small'
            loading={indexState.system.length === 0}
            columns={getFixedColumns()}
            //@ts-ignore
            dataSource={indexState.system}
            pagination={false}
            bordered
        />
    </Card>

}

export default ServerInfo;