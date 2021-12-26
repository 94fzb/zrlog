import {useEffect, useState} from "react";
import axios from "axios";
import {Card, Statistic} from "antd";
import {getRes} from "../../utils/constants";
import Row from "antd/es/grid/row";
import Col from "antd/es/grid/col";
import {CommentOutlined, ContainerOutlined} from "@ant-design/icons";

type StatisticsInfoState = {
    clickCount: number,
    articleCount: number,
    commCount: number,
    toDayCommCount: number,
}

const StatisticsInfo = () => {
    const [statisticsInfoState, setStatisticsInfoState] = useState<StatisticsInfoState>({
        clickCount: 0,
        articleCount: 0,
        commCount: 0,
        toDayCommCount: 0
    })

    useEffect(() => {
        axios.get("/api/admin/statisticsInfo").then(({data}) => {
            setStatisticsInfoState(data)
        });
    }, [])


    return <Card size={"small"} title={getRes()['admin.index.outline']}>
        <Row>
            <Col xs={24} md={12}>
                <Card>
                    <Statistic title="今天评论" value={statisticsInfoState.toDayCommCount}
                               prefix={<CommentOutlined/>}/></Card>

            </Col>
            <Col xs={24} md={12}>
                <Card>
                    <Statistic title="评论总数" value={statisticsInfoState.commCount}
                               prefix={<CommentOutlined/>}/>
                </Card>
            </Col>
        </Row>
        <Row>
            <Col xs={24} md={12}>
                <Card>
                    <Statistic title="文章总数" value={JSON.stringify(statisticsInfoState.articleCount)}
                               prefix={<ContainerOutlined/>}/>
                </Card>
            </Col>
            <Col xs={24} md={12}>
                <Card>
                    <Statistic title="文章浏览总数" value={statisticsInfoState.clickCount}
                               prefix={<ContainerOutlined/>}/>
                </Card>
            </Col>
        </Row>
    </Card>
}

export default StatisticsInfo