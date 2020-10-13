import React from 'react'
import {Card, Statistic} from "antd";
import Table from "antd/es/table";
import {BaseResourceComponent} from "./base-resource-component";
import Row from "antd/es/grid/row";
import Col from "antd/es/grid/col";
import Alert from "antd/es/alert";
import * as axios from "axios";
import {CommentOutlined, ContainerOutlined} from "@ant-design/icons";
import Spin from "antd/lib/spin";
import Title from "antd/es/typography/Title";
import Divider from "antd/es/divider";

export class Index extends BaseResourceComponent {

    initState() {
        return {
            statisticsInfo: {}
        };
    }

    componentDidMount() {
        super.componentDidMount();
        axios.get("/api/admin/statisticsInfo").then(({data}) => {
            this.setState({
                statisticsInfo: data.data,
                statisticsInfoLoading: false,
            })
        });
        axios.get("/api/admin/serverInfo").then(({data}) => {
            this.setState({
                serverInfoLoading: false,
                system: [
                    {
                        "name": "运行环境",
                        "value": data.data['java.vm.name'] + " - " + data.data['java.runtime.version']
                    },
                    {
                        "name": "JavaEE 容器信息",
                        "value": data.data['server.info']
                    }
                    ,
                    {
                        "name": "运行路径",
                        "value": data.data['zrlog.runtime.path']
                    },
                    {
                        "name": "操作系统",
                        "value": data.data['os.name'] + " - " + data.data['os.arch'] + " - " + data.data['os.version']
                    },
                    {
                        "name": "系统时区 - 地域/语言",
                        "value": data.data['user.timezone'] + " - " + data.data['user.country'] + "/" + data.data['user.language'],
                    },
                    {
                        "name": "数据库版本",
                        "value": data.data['dbServer.version']
                    },
                    {
                        "name": "系统编码",
                        "value": data.data['file.encoding']
                    },
                    {
                        "name": "程序版本",
                        "value": data.data['zrlog.version'] + " - " + data.data['zrlog.buildId'] + " (" + data.data['zrlog.buildTime'] + ")"
                    }
                ]
            })
        })

    }

    getFixedColumns() {
        return [
            {
                title: this.state.res.key,
                dataIndex: 'name',
                fixed: true,
                width: 180,
            },
            {
                title: this.state.res.value,
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
            <Spin spinning={this.state.serverInfoLoading && this.state.resLoading && this.state.statisticsInfoLoading}>
                <Title className='page-header' level={3}>{this.getSecondTitle()}</Title>
                <Divider/>
                <Alert message={this.state.res['admin.index.welcomeTips']} type="info" showIcon/>
                <Row style={{paddingTop: "20px"}}>
                    <Col xs={24} md={14}>
                        <Card size={"small"} title={this.state.res['serverInfo']}>
                            <Table
                                size='middle'
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