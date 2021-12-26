import Row from "antd/es/grid/row";
import Col from "antd/es/grid/col";
import Alert from "antd/es/alert";
import Title from "antd/es/typography/Title";
import Divider from "antd/es/divider";
import {getRes} from "../../utils/constants";
import ServerInfo from "./ServerInfo";
import StatisticsInfo from "./StatisticsInfo";

const Index = () => {

    return (
        <>
            <Title className='page-header' level={3}>{getRes().dashboard} </Title>
            <Divider/>
            <Alert message={getRes()['admin.index.welcomeTips']} type="info" showIcon/>
            <Row gutter={[8, 8]} style={{paddingTop: "20px"}}>
                <Col xs={24} md={14}>
                    <ServerInfo/>
                </Col>
                <Col xs={24} md={10}>
                    <StatisticsInfo/>
                </Col>
            </Row>
        </>
    )
}

export default Index;
