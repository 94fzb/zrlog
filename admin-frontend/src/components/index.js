import React from 'react'
import {Card, Statistic} from "antd";
import Table from "antd/es/table";
import {BaseResourceComponent} from "./base-resource-component";
import Row from "antd/es/grid/row";
import Col from "antd/es/grid/col";
import Alert from "antd/es/alert";
import {CommentOutlined, ContainerOutlined} from "@ant-design/icons";
import Spin from "antd/lib/spin";
import Title from "antd/es/typography/Title";
import Divider from "antd/es/divider";

const system = [
    {
        "name": "运行环境",
        "value": ""
    },
    {
        "name": "JavaEE 容器信息",
        "value": ""
    }
    ,
    {
        "name": "运行路径",
        "value": ""
    },
    {
        "name": "操作系统",
        "value": ""
    },
    {
        "name": "系统时区 - 地域/语言",
        "value": ""
    },
    {
        "name": "数据库版本",
        "value": ""
    },
    {
        "name": "系统编码",
        "value": ""
    },
    {
        "name": "程序版本",
        "value": ""
    }
];

class Index extends BaseResourceComponent {

    initState() {
        return {
            statisticsInfo: {},
            system: system
        };
    }

    componentDidMount() {
        super.componentDidMount();
        this.getAxios().get("/api/admin/statisticsInfo").then(({data}) => {
            this.setState({
                statisticsInfo: data.data,
            })
        });
        this.getAxios().get("/api/admin/serverInfo").then(({data}) => {
            system[0].value = data.data['java.vm.name'] + " - " + data.data['java.runtime.version'];
            system[1].value = data.data['server.info'];
            system[2].value = data.data['zrlog.runtime.path'];
            system[3].value = data.data['os.name'] + " - " + data.data['os.arch'] + " - " + data.data['os.version'];
            system[4].value = data.data['user.timezone'] + " - " + data.data['user.country'] + "/" + data.data['user.language'];
            system[5].value = data.data['dbServer.version'];
            system[6].value = data.data['file.encoding'];
            system[7].value = data.data['zrlog.version'] + " - " + data.data['zrlog.buildId'] + " (" + data.data['zrlog.buildTime'] + ")";
            this.setState({
                system: system
            })
        })

    }

    getFixedColumns() {
        return [
            {
                title: this.state.res.key,
                dataIndex: 'name',
                key: 'name',
                fixed: true,
                width: 180,
            },
            {
                title: this.state.res.value,
                key: 'value',
                dataIndex: 'value',
            },
        ];
    }

    getSecondTitle() {
        return this.state.res.dashboard;
    }

    render() {

        const {statisticsInfo} = this.state;

        return (
            <Spin delay={this.getSpinDelayTime()}
                  spinning={this.state.resLoading}>
                <Title className='page-header' level={3}>{this.getSecondTitle()}</Title>
                <Divider/>
                <Alert message={this.state.res['admin.index.welcomeTips']} type="info" showIcon/>
                <Row gutter={[8, 8]} style={{paddingTop: "20px"}}>
                    <Col xs={24} md={14}>
                        <Card size={"small"} title={this.state.res['serverInfo']}>
                            <Table
                                style={{wordBreak: "break-all"}}
                                size='small'
                                columns={this.getFixedColumns()}
                                dataSource={this.state.system}
                                pagination={false}
                                bordered
                            />
                        </Card>
                    </Col>
                    <Col xs={24} md={10}>
                        <Card size={"small"} title={this.state.res['admin.index.outline']}>
                            <Row>
                                <Col xs={24} md={12}>
                                    <Card>
                                        <Statistic title="今天评论" value={statisticsInfo.toDayCommCount}
                                                   prefix={<CommentOutlined/>}/></Card>

                                </Col>
                                <Col xs={24} md={12}>
                                    <Card>
                                        <Statistic title="评论总数" value={statisticsInfo.commCount}
                                                   prefix={<CommentOutlined/>}/>
                                    </Card>
                                </Col>
                            </Row>
                            <Row>
                                <Col xs={24} md={12}>
                                    <Card>
                                        <Statistic title="文章总数" value={JSON.stringify(statisticsInfo.articleCount)}
                                                   prefix={<ContainerOutlined/>}/>
                                    </Card>
                                </Col>
                                <Col xs={24} md={12}>
                                    <Card>
                                        <Statistic title="文章浏览总数" value={statisticsInfo.clickCount}
                                                   prefix={<ContainerOutlined/>}/>
                                    </Card>
                                </Col>
                            </Row>
                        </Card>
                    </Col>
                </Row>
            </Spin>
        )
    }
}

export default Index;