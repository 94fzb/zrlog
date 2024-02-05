import Row from "antd/es/grid/row";
import Col from "antd/es/grid/col";
import Alert from "antd/es/alert";
import Title from "antd/es/typography/Title";
import Divider from "antd/es/divider";
import { getRes } from "../../utils/constants";

import { FunctionComponent } from "react";
import { IndexData } from "../../type";
import ServerInfo from "./ServerInfo";
import StatisticsInfo from "./StatisticsInfo";

type IndexProps = {
    data: IndexData;
};

const Index: FunctionComponent<IndexProps> = ({ data }) => {
    if (data.serverInfo === undefined || data.statisticsInfo === null) {
        return <></>;
    }
    return (
        <>
            <Title className="page-header" level={3}>
                {getRes().dashboard}{" "}
            </Title>
            <Divider />
            <Alert message={getRes()["admin.index.welcomeTips"]} type="info" showIcon />
            <Row gutter={[8, 8]} style={{ paddingTop: "12px" }}>
                <Col xs={24} md={14}>
                    <ServerInfo data={data.serverInfo} />
                </Col>
                <Col xs={24} md={10}>
                    <StatisticsInfo data={data.statisticsInfo} />
                </Col>
            </Row>
        </>
    );
};

export default Index;
