import Row from "antd/es/grid/row";
import Col from "antd/es/grid/col";
import Title from "antd/es/typography/Title";
import Divider from "antd/es/divider";
import {getColorPrimary, getRes} from "../../utils/constants";

import {FunctionComponent} from "react";
import {IndexData} from "../../type";
import ActivityGraph, {generateCompleteData} from "./ActivityGraph";
import Card from "antd/es/card";
import IndexTipBg from "./IndexTipBg";
import EnvUtils from "../../utils/env-utils";
import StatisticsInfo from "./StatisticsInfo";
import {Link} from "react-router-dom";
import {DatabaseOutlined, FolderAddFilled, PlusCircleOutlined} from "@ant-design/icons";
import {Typography} from "antd";
import styled from "styled-components";

type IndexProps = {
    data: IndexData;
};

const PREFIX = "index";

const classes = {
    card: `${PREFIX}-card`,
}

const StyledIndex = styled(`div`)({
    [`& .${PREFIX}-card`]: {
        background: EnvUtils.isDarkMode() ? "#141414" : getColorPrimary(),
        padding: 0,
        marginBottom: 8,
        color: "white",
    }
})

const Index: FunctionComponent<IndexProps> = ({data}) => {
    if (data.statisticsInfo === null) {
        return <></>;
    }
    return (
        <StyledIndex>
            <Title className="page-header" level={3}>
                {getRes().dashboard}
            </Title>
            <Divider/>
            <Row gutter={[8, 8]}>
                <Col xs={24} md={12}>
                    <Card
                        title={""}
                        styles={{
                            body: {
                                padding: 0,
                            },
                        }}
                        className={classes.card}
                    >
                        <IndexTipBg style={{position: "absolute", height: "100%", width: "100%", zIndex: 2}}/>
                        <div style={{padding: 12}}>
                            <div style={{fontSize: 20, fontWeight: 500,textOverflow: "ellipsis", whiteSpace: "nowrap",overflow:"hidden"}}>{data.welcomeTip}</div>
                            <div style={{overflow: "hidden", textOverflow: "ellipsis", whiteSpace: "nowrap"}}>
                                {data.tips.map((e) => {
                                    return e;
                                })}
                            </div>
                        </div>
                    </Card>
                    <StatisticsInfo data={data.statisticsInfo} versionInfo={data.versionInfo}/>
                </Col>
                <Col xs={24} md={12}>
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
                    >
                        <ActivityGraph data={generateCompleteData(data.activityData)}/>
                    </Card>
                    <Card title={"快捷操作"} size={"small"}
                          styles={{
                              body: {
                                  overflow: "auto",
                                  marginTop: 8,
                                  marginRight: 8,
                              },
                          }} style={{marginTop: 8}}
                    >
                        <Row gutter={[8, 8]}>
                            <Col xs={12} md={12}>
                                <Link to={"/article-edit"}>
                                    <div style={{
                                        display: "flex",
                                        flexFlow: "column",
                                        alignItems: "center",
                                        justifyContent: "center",
                                        padding: 8,
                                    }}>
                                        <PlusCircleOutlined style={{fontSize: 28, color: getColorPrimary()}}/>
                                        <Typography.Text style={{lineHeight: 2, paddingTop: 8}}>写文章</Typography.Text>
                                    </div>
                                </Link>
                            </Col>
                            <Col xs={12} md={12}>
                                <Link to={"/article-type"}>
                                    <div style={{
                                        display: "flex",
                                        flexFlow: "column",
                                        alignItems: "center",
                                        justifyContent: "center",
                                        padding: 8,
                                    }}>
                                        <FolderAddFilled style={{fontSize: 28, color: getColorPrimary()}}/>
                                        <Typography.Text style={{lineHeight: 2, paddingTop: 8}}>分类</Typography.Text>
                                    </div>
                                </Link>
                            </Col>
                            <Col xs={12} md={12}>
                                <Link to={"/plugin?page=backup-sql-file/files"}>
                                    <div style={{
                                        display: "flex",
                                        flexFlow: "column",
                                        alignItems: "center",
                                        justifyContent: "center",
                                        padding: 8,
                                    }}>
                                        <DatabaseOutlined style={{fontSize: 28, color: getColorPrimary()}}/>
                                        <Typography.Text
                                            style={{lineHeight: 2, paddingTop: 8}}>备份数据</Typography.Text>
                                    </div>
                                </Link>
                            </Col>
                        </Row>
                    </Card>
                </Col>
            </Row>
        </StyledIndex>
    );
};

export default Index;
