import Row from "antd/es/grid/row";
import Col from "antd/es/grid/col";
import Title from "antd/es/typography/Title";
import Divider from "antd/es/divider";
import { getColorPrimary, getRes } from "../../utils/constants";

import { FunctionComponent } from "react";
import { IndexData } from "../../type";
import ServerInfo from "./ServerInfo";
import StatisticsInfo from "./StatisticsInfo";
import ActivityGraph, { generateCompleteData } from "./ActivityGraph";
import Card from "antd/es/card";
import IndexTipBg from "./IndexTipBg";
import EnvUtils from "../../utils/env-utils";

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
            <Row gutter={[8, 8]}>
                <Col xs={24} md={12}>
                    <Card
                        title={""}
                        styles={{
                            body: {
                                padding: 0,
                            },
                        }}
                        style={{
                            background: EnvUtils.isDarkMode() ? "#000000" : getColorPrimary(),
                            padding: 0,
                            marginBottom: 8,
                            color: "white",
                        }}
                    >
                        <IndexTipBg style={{ position: "absolute", height: "100%", width: "100%", zIndex: 2 }} />
                        <div style={{ padding: 12 }}>
                            <div style={{ fontSize: 20, fontWeight: 500 }}>{data.welcomeTip}</div>
                            <div style={{ overflow: "hidden", textOverflow: "ellipsis", whiteSpace: "nowrap" }}>
                                {data.tips.map((e) => {
                                    return e;
                                })}
                            </div>
                        </div>
                    </Card>
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
                                marginRight: 8,
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
