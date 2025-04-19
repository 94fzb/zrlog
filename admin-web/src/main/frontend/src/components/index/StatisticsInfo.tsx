import {Card, Statistic, Typography} from "antd";
import {getRes} from "../../utils/constants";
import Row from "antd/es/grid/row";
import Col from "antd/es/grid/col";
import {CommentOutlined, ContainerOutlined, InfoCircleOutlined} from "@ant-design/icons";
import {Link} from "react-router-dom";
import {StatisticsInfoState} from "../../type";

const StatisticsInfo = ({data, versionInfo}: { data: StatisticsInfoState, versionInfo: string }) => {
    return (
        <Card size={"small"} title={getRes()["admin.index.outline"]}>
            <Row gutter={[8, 16]}>
                <Col xs={24} md={12}>
                    <Card styles={{body: {padding: 16}}}>
                        <Statistic
                            title={getRes()["todayComment"]}
                            value={data.toDayCommCount}
                            prefix={<CommentOutlined/>}
                        />
                    </Card>
                </Col>
                <Col xs={24} md={12}>
                    <Card styles={{body: {padding: 16}}}>
                        <Link to={"/comment"}>
                            <Statistic
                                title={getRes()["totalComment"]}
                                value={data.commCount}
                                prefix={<CommentOutlined/>}
                            />
                        </Link>
                    </Card>
                </Col>
            </Row>
            <Row gutter={[8, 8]} style={{paddingTop: 8}}>
                <Col xs={24} md={12}>
                    <Card styles={{body: {padding: 16}}}>
                        <Link to={"/article"}>
                            <Statistic
                                title={getRes()["totalArticle"]}
                                value={data.articleCount}
                                prefix={<ContainerOutlined/>}
                            />
                        </Link>
                    </Card>
                </Col>
                <Col xs={24} md={12}>
                    <Card styles={{body: {padding: 16}}}>
                        <Statistic
                            title={getRes()["totalArticleView"]}
                            value={data.clickCount}
                            prefix={<ContainerOutlined/>}
                        />
                    </Card>
                </Col>
                <Col xs={24} md={12}>
                    <Card styles={{body: {padding: 16}}}>
                        <Link to={"/system"}>
                            <Statistic
                                title={getRes()["systemInfo"]}
                                value={versionInfo}
                                prefix={<InfoCircleOutlined/>}
                                valueStyle={{
                                    lineHeight: "38px",
                                    overflow: "hidden",
                                    display: "flex",
                                    textOverflow: "ellipsis",
                                    whiteSpace: "nowrap",
                                    fontSize: 18
                                }}
                                valueRender={(v) => {
                                    return <Row
                                        style={{
                                            display: "flex",
                                            gap: 3,
                                            flexFlow: "row",
                                            alignItems: "center",
                                            fontSize: 18,
                                        }}
                                    >
                                        <Typography.Text
                                            style={{fontSize: 18}}
                                            ellipsis={true}
                                        >
                                            {v}
                                        </Typography.Text>
                                    </Row>
                                }}
                            />
                        </Link>
                    </Card>
                </Col>
            </Row>
        </Card>
    );
};

export default StatisticsInfo;
