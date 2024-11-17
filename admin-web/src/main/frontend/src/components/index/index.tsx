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
import ActivityGraph, { generateCompleteData } from "./ActivityGraph";
import Card from "antd/es/card";

type IndexProps = {
    data: IndexData;
};

const Index: FunctionComponent<IndexProps> = ({ data }) => {
    if (data.serverInfos === undefined || data.statisticsInfo === null) {
        return <></>;
    }
    return (
        <>
            <Title className="page-header" level={3}>
                {getRes().dashboard}{" "}
            </Title>
            <Divider />
            {data.tips.map((e) => {
                return <Alert message={e} type="info" showIcon />;
            })}
            <Row gutter={[8, 8]} style={{ paddingTop: "12px" }}>
                <Col xs={24} md={12}>
                    <ServerInfo
                        data={data.serverInfos}
                        nativeImageMode={data.nativeImageMode}
                        dockerMode={data.dockerMode}
                    />
                </Col>
                <Col xs={24} md={12}>
                    <StatisticsInfo data={data.statisticsInfo} />
                    <Card
                        title={getRes()["admin.index.activity"]}
                        size={"small"}
                        styles={{
                            body: {
                                overflow: "auto",
                                marginTop: 8,
                            },
                        }}
                        style={{ marginTop: 8 }}
                    >
                        <ActivityGraph data={generateCompleteData(data.activityData)} />
                    </Card>
                </Col>
            </Row>
        </>
    );
};

export default Index;
