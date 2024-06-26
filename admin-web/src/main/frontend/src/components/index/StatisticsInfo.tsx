import { Card, Statistic } from "antd";
import { getRes } from "../../utils/constants";
import Row from "antd/es/grid/row";
import Col from "antd/es/grid/col";
import { CommentOutlined, ContainerOutlined, HddOutlined } from "@ant-design/icons";
import { StatisticsInfoState } from "../../type";

const StatisticsInfo = ({ data }: { data: StatisticsInfoState }) => {
    return (
        <Card size={"small"} title={getRes()["admin.index.outline"]}>
            <Row gutter={[8, 8]}>
                <Col xs={24} md={12}>
                    <Card styles={{ body: { padding: 16 } }}>
                        <Statistic
                            title={getRes()["todayComment"]}
                            value={data.toDayCommCount}
                            prefix={<CommentOutlined />}
                        />
                    </Card>
                </Col>
                <Col xs={24} md={12}>
                    <Card styles={{ body: { padding: 16 } }}>
                        <Statistic
                            title={getRes()["totalComment"]}
                            value={data.commCount}
                            prefix={<CommentOutlined />}
                        />
                    </Card>
                </Col>
            </Row>
            <Row gutter={[8, 8]} style={{ paddingTop: 8 }}>
                <Col xs={24} md={12}>
                    <Card styles={{ body: { padding: 16 } }}>
                        <Statistic
                            title={getRes()["totalArticle"]}
                            value={data.articleCount}
                            prefix={<ContainerOutlined />}
                        />
                    </Card>
                </Col>
                <Col xs={24} md={12}>
                    <Card styles={{ body: { padding: 16 } }}>
                        <Statistic
                            title={getRes()["totalArticleView"]}
                            value={data.clickCount}
                            prefix={<ContainerOutlined />}
                        />
                    </Card>
                </Col>
                <Col xs={24} md={12}>
                    <Card styles={{ body: { padding: 16 } }}>
                        <Statistic
                            title={getRes()["usedCacheSpace"]}
                            value={data.usedCacheSpace > 0 ? (data.usedCacheSpace / 1024 / 1024).toFixed(2) + "M" : "-"}
                            prefix={<HddOutlined />}
                        />
                    </Card>
                </Col>
                <Col xs={24} md={12}>
                    <Card styles={{ body: { padding: 16 } }}>
                        <Statistic
                            title={getRes()["usedDiskSpace"]}
                            value={data.usedDiskSpace > 0 ? (data.usedDiskSpace / 1024 / 1024).toFixed(2) + "M" : "-"}
                            prefix={<HddOutlined />}
                        />
                    </Card>
                </Col>
            </Row>
        </Card>
    );
};

export default StatisticsInfo;
