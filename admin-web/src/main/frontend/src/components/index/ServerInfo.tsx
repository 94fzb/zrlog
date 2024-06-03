import { Card, Col, Row } from "antd";
import { getRes } from "../../utils/constants";
import Table from "antd/es/table";
import DockerOutlined from "../../icons/DockerOutlined";
import { ServerInfoEntry } from "../../type";
import { LinuxOutlined } from "@ant-design/icons";
import GraalVmOutlined from "../../icons/GraalVMOutlined";

const ServerInfo = ({
    data,
    dockerMode,
    nativeImageMode,
}: {
    data: ServerInfoEntry[];
    dockerMode: boolean;
    nativeImageMode: boolean;
}) => {
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
                        if (dockerMode || nativeImageMode) {
                            return (
                                <Row style={{ display: "flex", gap: 3, flexFlow: "row", alignItems: "center" }}>
                                    {dockerMode && <DockerOutlined />}
                                    {nativeImageMode && (
                                        <Col xs={0} md={12} style={{ maxWidth: 92, height: 30 }}>
                                            <GraalVmOutlined />
                                        </Col>
                                    )}
                                    <span>{e}</span>
                                </Row>
                            );
                        }
                    }
                    if (r.name === "操作系统" || r.name === "System Info") {
                        if (r.value.startsWith("Linux")) {
                            return (
                                <>
                                    <LinuxOutlined />
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
