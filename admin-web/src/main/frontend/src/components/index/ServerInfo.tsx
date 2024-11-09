import { Card, Col, Row, Statistic } from "antd";
import { getRes } from "../../utils/constants";
import { ServerInfoEntry } from "../../type";
import GraalVmOutlined from "icons/GraalVMOutlined";
import LinuxOutlined from "@ant-design/icons/lib/icons/LinuxOutlined";
import DockerOutlined from "@ant-design/icons/lib/icons/DockerOutlined";
import * as React from "react";
import { Link } from "react-router-dom";

const ServerInfo = ({
    data,
    dockerMode,
    nativeImageMode,
}: {
    data: ServerInfoEntry[];
    dockerMode: boolean;
    nativeImageMode: boolean;
}) => {
    const render = (e: string, r: ServerInfoEntry) => {
        if (r.key === "runtime") {
            if (dockerMode || nativeImageMode) {
                return (
                    <Row
                        style={{
                            display: "flex",
                            gap: 3,
                            flexFlow: "row",
                            alignItems: "center",
                            fontSize: 18,
                        }}
                    >
                        {dockerMode && <DockerOutlined />}
                        {nativeImageMode && (
                            <Col xs={0} md={12} style={{ maxWidth: 92, height: 30 }}>
                                <GraalVmOutlined />
                            </Col>
                        )}
                        <span
                            style={{
                                overflow: "hidden",
                                textOverflow: "ellipsis",
                            }}
                        >
                            {e}
                        </span>
                    </Row>
                );
            }
        }
        if (r.key === "system") {
            if (r.value.startsWith("Linux")) {
                return (
                    <>
                        <LinuxOutlined />
                        <span style={{ marginLeft: 3 }}>{e}</span>
                    </>
                );
            }
        }
        return <>{e}</>;
    };

    const externalUrl = (key: string) => {
        if (key === "programInfo") {
            return "https://www.zrlog.com/changelog/?ref=dashboard";
        }
        if (key === "webServer") {
            return "https://github.com/94fzb/simplewebserver";
        }
        return "";
    };

    return (
        <Card size={"small"} title={getRes()["serverInfo"]} styles={{ body: { padding: 8, overflow: "hidden" } }}>
            <Row gutter={[8, 8]}>
                {data.map((e) => {
                    return (
                        <Col xs={24} md={12} key={e.key}>
                            <Link target={externalUrl(e.key).length > 0 ? "_blank" : ""} to={externalUrl(e.key)}>
                                <Card styles={{ body: { padding: 16 } }}>
                                    <Statistic
                                        title={e.name}
                                        valueRender={() => render(e.value, e) as React.ReactNode}
                                        valueStyle={{
                                            fontSize: 18,
                                            lineHeight: "38px",
                                            overflow: "hidden",
                                            textOverflow: "ellipsis",
                                            whiteSpace: "nowrap",
                                        }}
                                    />
                                </Card>
                            </Link>
                        </Col>
                    );
                })}
            </Row>
        </Card>
    );
};

export default ServerInfo;
