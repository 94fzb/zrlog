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
                    <Link to={"/comment"}>
                        <Card styles={{body: {padding: 16}}}>

                            <Statistic
                                title={getRes()["totalComment"]}
                                value={data.commCount}
                                prefix={<CommentOutlined/>}
                            />
                        </Card>
                    </Link>
                </Col>
            </Row>
            <Row gutter={[8, 8]} style={{paddingTop: 8}}>
                <Col xs={24} md={12}>
                    <Link to={"/article"}>
                        <Card styles={{body: {padding: 16}}}>
                            <Statistic
                                title={getRes()["totalArticle"]}
                                value={data.articleCount}
                                prefix={<ContainerOutlined/>}
                            />
                        </Card>
                    </Link>
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
                    <Link to={"/system"}>
                        <Card styles={{body: {padding: 16}}}>
                            <Statistic
                                title={getRes()["systemInfo"]}
                                prefix={<InfoCircleOutlined/>}
                                valueStyle={{
                                    lineHeight: "38px",
                                    display: "flex",
                                    fontSize: 18
                                }}
                                valueRender={() => {
                                    return <Row
                                        style={{
                                            display: "flex",
                                            gap: 3,
                                            flexFlow: "row",
                                            alignItems: "center",
                                        }}
                                    >
                                        <Typography.Text
                                            style={{fontSize: 18}}
                                            ellipsis={true}>
                                            {versionInfo}
                                        </Typography.Text>
                                    </Row>
                                }}
                            />
                        </Card>
                    </Link>
                </Col>
            </Row>
        </Card>
    );
};

export default StatisticsInfo;
