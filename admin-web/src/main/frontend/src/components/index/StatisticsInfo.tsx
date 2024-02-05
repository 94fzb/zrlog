import { Card, Statistic } from "antd";
import { getRes } from "../../utils/constants";
import Row from "antd/es/grid/row";
import Col from "antd/es/grid/col";
import { CommentOutlined, ContainerOutlined } from "@ant-design/icons";
import { StatisticsInfoState } from "../../type";

const StatisticsInfo = ({ data }: { data: StatisticsInfoState }) => {
    return (
        <Card size={"small"} title={getRes()["admin.index.outline"]}>
            <Row gutter={[8, 8]}>
                <Col xs={24} md={12}>
                    <Card>
                        <Statistic title="今天评论" value={data.toDayCommCount} prefix={<CommentOutlined />} />
                    </Card>
                </Col>
                <Col xs={24} md={12}>
                    <Card>
                        <Statistic title="评论总数" value={data.commCount} prefix={<CommentOutlined />} />
                    </Card>
                </Col>
            </Row>
            <Row gutter={[8, 8]} style={{ paddingTop: 8 }}>
                <Col xs={24} md={12}>
                    <Card>
                        <Statistic title="文章总数" value={data.articleCount} prefix={<ContainerOutlined />} />
                    </Card>
                </Col>
                <Col xs={24} md={12}>
                    <Card>
                        <Statistic title="文章浏览总数" value={data.clickCount} prefix={<ContainerOutlined />} />
                    </Card>
                </Col>
            </Row>
        </Card>
    );
};

export default StatisticsInfo;
