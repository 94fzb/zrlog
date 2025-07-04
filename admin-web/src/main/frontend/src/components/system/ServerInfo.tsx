import { Card, Col, Row, Statistic, Typography } from "antd";
import { ServerInfoEntry } from "../../type";
import GraalVmOutlined from "icons/GraalVMOutlined";
import LinuxOutlined from "@ant-design/icons/lib/icons/LinuxOutlined";
import DockerOutlined from "@ant-design/icons/lib/icons/DockerOutlined";
import * as React from "react";
import { Link } from "react-router-dom";
import { AppleOutlined, DatabaseOutlined, HddOutlined, WindowsOutlined } from "@ant-design/icons";
import MemoryIcon from "../../icons/MemoryIcon";
import CPUIcon from "../../icons/CPUIcon";

const ServerInfo = ({
    data,
    dockerMode,
    nativeImageMode,
    title,
}: {
    data: ServerInfoEntry[];
    dockerMode: boolean;
    nativeImageMode: boolean;
    title: string;
}) => {
    const render = (e: string, r: ServerInfoEntry) => {
        if (r.key === "runtime") {
            if (dockerMode || nativeImageMode) {
                return (
                    <div
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
                        <Typography.Text style={{ fontSize: 18 }} ellipsis={true}>
                            {e}
                        </Typography.Text>
                    </div>
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
            } else if (r.value.startsWith("Darwin")) {
                return (
                    <>
                        <AppleOutlined />
                        <span style={{ marginLeft: 3 }}>{e}</span>
                    </>
                );
            } else {
                return (
                    <>
                        <WindowsOutlined />
                        <span style={{ marginLeft: 3 }}>{e}</span>
                    </>
                );
            }
        }
        if (r.key === "usedCacheSpace" || r.key === "usedDiskSpace") {
            return (
                <>
                    <HddOutlined />
                    <span style={{ marginLeft: 3 }}>{e}</span>
                </>
            );
        }
        if (r.key === "usedMemorySpace" || r.key === "totalMemorySpace") {
            return (
                <>
                    <MemoryIcon />
                    <span style={{ marginLeft: 3 }}>{e}</span>
                </>
            );
        }
        if (r.key === "dbInfo") {
            return (
                <>
                    <DatabaseOutlined />
                    <span style={{ marginLeft: 3 }}>{e}</span>
                </>
            );
        }
        if (r.key === "cpuInfo") {
            return (
                <>
                    <CPUIcon />
                    <span style={{ marginLeft: 3 }}>{e}</span>
                </>
            );
        }
        return <>{e}</>;
    };

    const externalUrl = (key: string, value: string) => {
        if (key === "programInfo") {
            return "https://www.zrlog.com/changelog/?ref=systemInfo";
        } else if (key === "webServer") {
            if (value.toLowerCase().startsWith("simplewebserver/")) {
                return "https://github.com/94fzb/simplewebserver";
            } else if (value.startsWith("Apache Tomcat/")) {
                return "https://tomcat.apache.org/";
            }
        } else if (key === "runtime") {
            if (nativeImageMode) {
                return "https://www.graalvm.org";
            }
            return "https://www.oracle.com/java";
        }
        return "";
    };

    const buildItem = (item: ServerInfoEntry) => {
        return (
            <Card styles={{ body: { padding: 16 } }}>
                <Statistic
                    title={item.name}
                    valueRender={() => render(item.value, item) as React.ReactNode}
                    valueStyle={{
                        fontSize: 18,
                        lineHeight: "38px",
                        overflow: "hidden",
                        textOverflow: "ellipsis",
                        whiteSpace: "nowrap",
                    }}
                />
            </Card>
        );
    };

    if (data === undefined || data.length === 0) {
        return <></>;
    }

    return (
        <Card size={"small"} title={title} styles={{ body: { padding: 8, overflow: "hidden" } }}>
            <Row gutter={[8, 8]}>
                {data.map((e) => {
                    const url = externalUrl(e.key, e.value);
                    return (
                        <Col xs={24} md={12} key={e.key}>
                            {url.length > 0 ? (
                                <Link target={"_blank"} to={externalUrl(e.key, e.value)}>
                                    {buildItem(e)}
                                </Link>
                            ) : (
                                buildItem(e)
                            )}
                        </Col>
                    );
                })}
            </Row>
        </Card>
    );
};

export default ServerInfo;
