import {Card, Col, Row, Statistic} from "antd";
import {getRes} from "../../utils/constants";
import {SystemIOInfo} from "../../type";
import {HddOutlined} from "@ant-design/icons";
import {FunctionComponent} from "react";
import MemoryIcon from "../../icons/MemoryIcon";

type DiskInfoProps = {
    data: SystemIOInfo
}

const SystemDiskInfo: FunctionComponent<DiskInfoProps> = ({data}) => {
    return (
        <Card size={"small"} title={getRes()["admin.index.outline"]}>
            <Row gutter={[8, 8]}>
                <Col xs={24} md={12}>
                    <Card styles={{body: {padding: 16}}}>
                        <Statistic
                            title={getRes()["usedCacheSpace"]}
                            value={data.usedCacheSpace > 0 ? (data.usedCacheSpace / 1024 / 1024).toFixed(2) + "M" : "-"}
                            prefix={<HddOutlined/>}
                        />
                    </Card>
                </Col>
                <Col xs={24} md={12}>
                    <Card styles={{body: {padding: 16}}}>
                        <Statistic
                            title={getRes()["usedDiskSpace"]}
                            value={data.usedDiskSpace > 0 ? (data.usedDiskSpace / 1024 / 1024).toFixed(2) + "M" : "-"}
                            prefix={<HddOutlined/>}
                        />
                    </Card>
                </Col>
                <Col xs={24} md={12}>
                    <Card styles={{body: {padding: 16}}}>
                        <Statistic
                            title={getRes()["usedMemorySpace"]}
                            value={data.usedMemorySpace > 0 ? (data.usedMemorySpace / 1024 / 1024).toFixed(2) + "M" : "-"}
                            prefix={<MemoryIcon/>}
                        />
                    </Card>
                </Col>
                <Col xs={24} md={12}>
                    <Card styles={{body: {padding: 16}}}>
                        <Statistic
                            title={getRes()["totalMemorySpace"]}
                            value={data.totalMemorySpace > 0 ? (data.totalMemorySpace / 1024 / 1024 / 1024).toFixed(2) + "G" : "-"}
                            prefix={<MemoryIcon/>}
                        />
                    </Card>
                </Col>
            </Row>
        </Card>
    );
}

export default SystemDiskInfo;