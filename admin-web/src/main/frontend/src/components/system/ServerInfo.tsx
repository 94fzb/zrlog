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
import { Grid } from "antd";

const { useBreakpoint } = Grid;

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
    const screens = useBreakpoint();

    const render = (e: string, r: ServerInfoEntry) => {
        if (r.key === "runtime") {
            if (dockerMode || nativeImageMode) {
                return (
                    <>
                        {dockerMode && <DockerOutlined />}
                        {nativeImageMode && screens.sm && <GraalVmOutlined />}
                        <Typography.Text style={{ fontSize: 18, lineHeight: "28px" }} ellipsis={true}>
                            {e}
                        </Typography.Text>
                    </>
                );
            }
        }
        if (r.key === "system") {
            if (r.value.startsWith("Linux")) {
                return (
                    <>
                        <LinuxOutlined />
                        <Typography.Text style={{ fontSize: 18, lineHeight: "28px" }} ellipsis={true}>
                            {e}
                        </Typography.Text>
                    </>
                );
            } else if (r.value.startsWith("Darwin")) {
                return (
                    <>
                        <AppleOutlined />
                        <Typography.Text style={{ fontSize: 18, lineHeight: "28px" }} ellipsis={true}>
                            {e}
                        </Typography.Text>{" "}
                    </>
                );
            } else {
                return (
                    <>
                        <WindowsOutlined />
                        <Typography.Text style={{ fontSize: 18, lineHeight: "28px" }} ellipsis={true}>
                            {e}
                        </Typography.Text>
                    </>
                );
            }
        }
        if (r.key === "usedCacheSpace" || r.key === "usedDiskSpace") {
            return (
                <>
                    <HddOutlined />
                    <Typography.Text style={{ fontSize: 18, lineHeight: "28px" }} ellipsis={true}>
                        {e}
                    </Typography.Text>
                </>
            );
        }
        if (r.key === "usedMemorySpace" || r.key === "totalMemorySpace") {
            return (
                <>
                    <MemoryIcon />
                    <Typography.Text style={{ fontSize: 18, lineHeight: "28px" }} ellipsis={true}>
                        {e}
                    </Typography.Text>
                </>
            );
        }
        if (r.key === "dbInfo") {
            return (
                <>
                    <DatabaseOutlined />
                    <Typography.Text style={{ fontSize: 18, lineHeight: "28px" }} ellipsis={true}>
                        {e}
                    </Typography.Text>
                </>
            );
        }
        if (r.key === "cpuInfo") {
            return (
                <>
                    <CPUIcon />
                    <Typography.Text style={{ fontSize: 18, lineHeight: "28px" }} ellipsis={true}>
                        {e}
                    </Typography.Text>
                </>
            );
        }
        return (
            <>
                <Typography.Text style={{ fontSize: 18, lineHeight: "28px" }} ellipsis={true}>
                    {e}
                </Typography.Text>
            </>
        );
    };

    const externalUrl = (key: string, value: string) => {
        if (key === "programInfo") {
            return "https://www.zrlog.com/changelog/?ref=systemInfo";
        } else if (key === "webServer") {
            if (value.toLowerCase().startsWith("simplewebserver/")) {
                return "https://github.com/94fzb/simplewebserver";
            } else if (value.startsWith("Apache Tomcat/")) {
                return "https://tomcat.apache.org/";
            } else if (value.startsWith("Lambda")) {
                return "https://aws.amazon.com/lambda/";
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
                    valueRender={() => {
                        return (
                            <div
                                style={{
                                    display: "flex",
                                    gap: 3,
                                    flexFlow: "row",
                                    alignItems: "center",
                                    fontSize: 24,
                                    overflow: "hidden",
                                    textOverflow: "ellipsis",
                                    whiteSpace: "nowrap",
                                }}
                            >
                                {render(item.value, item) as React.ReactNode}
                            </div>
                        );
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
