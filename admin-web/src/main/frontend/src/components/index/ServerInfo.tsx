import { Card } from "antd";
import { getRes } from "../../utils/constants";
import Table from "antd/es/table";
import DockerOutlined from "../../icons/DockerOutlined";

const ServerInfo = ({ data }: { data: Record<string, any> }) => {
    const system = [
        {
            name: "运行环境",
            value: "",
        },
        {
            name: "容器信息",
            value: "",
        },
        {
            name: "运行路径",
            value: "",
        },
        {
            name: "操作系统",
            value: "",
        },
        {
            name: "系统时区 - 地域/语言",
            value: "",
        },
        {
            name: "数据库版本",
            value: "",
        },
        {
            name: "系统编码",
            value: "",
        },
        {
            name: "程序版本",
            value: "",
        },
    ];

    const getFixedColumns = () => {
        return [
            {
                title: getRes().key,
                dataIndex: "name",
                key: "name",
                fixed: true,
                width: 180,
            },
            {
                title: getRes().value,
                key: "value",
                dataIndex: "value",
                render: (e: string, r: any) => {
                    if (r.name !== "运行环境") {
                        return e;
                    } else {
                        if (data["docker"] === "docker") {
                            return (
                                <>
                                    <DockerOutlined />
                                    <span style={{ marginLeft: 3 }}>{e}</span>
                                </>
                            );
                        } else {
                            return e;
                        }
                    }
                },
            },
        ];
    };

    system[0].value = data["java.vm.name"] + " - " + data["java.runtime.version"];
    system[1].value = data["server.info"];
    system[2].value = data["zrlog.runtime.path"];
    system[3].value = data["os.name"] + " - " + data["os.arch"] + " - " + data["os.version"];
    system[4].value = data["user.timezone"] + " - " + data["user.country"] + "/" + data["user.language"];
    system[5].value = data["dbServer.version"];
    system[6].value = data["file.encoding"];
    system[7].value = data["zrlog.version"] + " - " + data["zrlog.buildId"] + " (" + data["zrlog.buildTime"] + ")";

    return (
        <Card size={"small"} title={getRes()["serverInfo"]}>
            <Table
                style={{ wordBreak: "break-all", whiteSpace: "unset" }}
                size="small"
                columns={getFixedColumns()}
                dataSource={system}
                pagination={false}
                bordered
            />
        </Card>
    );
};

export default ServerInfo;
