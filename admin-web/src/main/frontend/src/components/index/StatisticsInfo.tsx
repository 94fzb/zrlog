import { useEffect, useState } from "react";
import axios from "axios";
import { Card, Spin, Statistic } from "antd";
import { getRes } from "../../utils/constants";
import Row from "antd/es/grid/row";
import Col from "antd/es/grid/col";
import { CommentOutlined, ContainerOutlined } from "@ant-design/icons";

type StatisticsInfoState = {
    clickCount: number;
    articleCount: number;
    commCount: number;
    toDayCommCount: number;
    loading: boolean;
};

const StatisticsInfo = () => {
    const [statisticsInfoState, setStatisticsInfoState] = useState<StatisticsInfoState>({
        clickCount: 0,
        articleCount: 0,
        commCount: 0,
        toDayCommCount: 0,
        loading: true,
    });

    useEffect(() => {
        axios.get("/api/admin/statisticsInfo").then(({ data }) => {
            setStatisticsInfoState({ ...data.data, loading: false });
        });
    }, []);

    return (
        <Card size={"small"} title={getRes()["admin.index.outline"]}>
            <Spin spinning={statisticsInfoState.loading}>
                <Row gutter={[8, 8]}>
                    <Col xs={24} md={12}>
                        <Card>
                            <Statistic
                                title="今天评论"
                                valueStyle={{ display: statisticsInfoState.loading ? "none" : "block" }}
                                value={statisticsInfoState.toDayCommCount}
                                prefix={<CommentOutlined />}
                            />
                        </Card>
                    </Col>
                    <Col xs={24} md={12}>
                        <Card>
                            <Statistic
                                title="评论总数"
                                valueStyle={{ display: statisticsInfoState.loading ? "none" : "block" }}
                                value={statisticsInfoState.commCount}
                                prefix={<CommentOutlined />}
                            />
                        </Card>
                    </Col>
                </Row>
                <Row gutter={[8, 8]} style={{ paddingTop: 8 }}>
                    <Col xs={24} md={12}>
                        <Card>
                            <Statistic
                                title="文章总数"
                                valueStyle={{ display: statisticsInfoState.loading ? "none" : "block" }}
                                value={statisticsInfoState.articleCount}
                                prefix={<ContainerOutlined />}
                            />
                        </Card>
                    </Col>
                    <Col xs={24} md={12}>
                        <Card>
                            <Statistic
                                title="文章浏览总数"
                                valueStyle={{ display: statisticsInfoState.loading ? "none" : "block" }}
                                value={statisticsInfoState.clickCount}
                                prefix={<ContainerOutlined />}
                            />
                        </Card>
                    </Col>
                </Row>
            </Spin>
        </Card>
    );
};

export default StatisticsInfo;
