import { Card } from "antd";
import { getRes } from "../../utils/constants";
import Table from "antd/es/table";
import DockerOutlined from "../../icons/DockerOutlined";
import { ServerInfoEntry } from "../../type";

const ServerInfo = ({ data, dockerMode }: { data: ServerInfoEntry[]; dockerMode: boolean }) => {
    const getFixedColumns = () => {
        return [
            {
                title: getRes().key,
                dataIndex: "name",
                key: "name",
                fixed: true,
                width: 120,
            },
            {
                title: getRes().value,
                key: "value",
                dataIndex: "value",
                render: (e: string, r: ServerInfoEntry) => {
                    if (r.name === "运行环境" || r.name == "Runtime") {
                        if (dockerMode) {
                            return (
                                <>
                                    <DockerOutlined />
                                    <span style={{ marginLeft: 3 }}>{e}</span>
                                </>
                            );
                        }
                    }
                    return e;
                },
            },
        ];
    };

    return (
        <Card size={"small"} title={getRes()["serverInfo"]} styles={{ body: { padding: 0, overflow: "hidden" } }}>
            <Table
                style={{ wordBreak: "break-all", whiteSpace: "unset", borderRadius: 0 }}
                size="small"
                columns={getFixedColumns()}
                dataSource={data}
                showHeader={false}
                pagination={false}
                bordered
            />
        </Card>
    );
};

export default ServerInfo;
