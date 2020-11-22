import React from "react";
import {DeleteOutlined, EditOutlined, EyeOutlined} from '@ant-design/icons';

import {Col, Row, Table} from "antd";
import Search from "antd/lib/input/Search";
import {BaseTableComponent} from "./base-table-component";
import Spin from "antd/es/spin";
import Title from "antd/es/typography/Title";
import Divider from "antd/es/divider";
import Popconfirm from "antd/es/popconfirm";

export class Article extends BaseTableComponent {

    initState() {
        return {
            columns: [
                {
                    title: 'ID',
                    dataIndex: 'id',
                    key: 'id'
                },
                {
                    title: '',
                    dataIndex: 'id',
                    key: 'delete',
                    render: (text, record) =>
                        this.state.rows.length >= 1 ? (
                            <div style={{color: "red"}}>
                                <Popconfirm title="Sure to delete?"
                                            onConfirm={() => this.handleDelete(record.id)}>
                                    <DeleteOutlined/>
                                </Popconfirm>
                            </div>
                        ) : null,
                },
                {
                    title: '标题',
                    dataIndex: 'title',
                    key: 'title',
                    render: e => <div dangerouslySetInnerHTML={{__html: e}}/>,
                    width: "400px"
                },
                {
                    title: '关键字',
                    dataIndex: 'keywords',
                    key: 'keywords',
                },
                {
                    title: '作者',
                    key: 'userName',
                    dataIndex: 'userName'
                },
                {
                    title: '分类',
                    key: 'typeName',
                    dataIndex: 'typeName'
                },
                {
                    title: '浏览量',
                    key: 'click',
                    dataIndex: 'click'
                },
                {
                    title: '草稿',
                    key: 'rubbish',
                    dataIndex: 'rubbish',
                    render: text => text ? "是" : "否"
                },
                {
                    title: '公开',
                    key: 'privacy',
                    dataIndex: 'privacy',
                    render: text => text ? "否" : "是"
                },
                {
                    title: '创建时间',
                    key: 'releaseTime',
                    dataIndex: 'releaseTime'
                },
                {
                    title: '最后更新时间',
                    key: 'lastUpdateDate',
                    dataIndex: 'lastUpdateDate'
                },
                {
                    title: '编辑',
                    key: 'edit',
                    dataIndex: 'id',
                    render: (text) =>
                        this.state.rows.length >= 1 ? (
                            <div style={{color: "red"}}>
                                <a href={"/admin/article-edit?id=" + text}>
                                    <EditOutlined/>
                                </a>
                            </div>
                        ) : null,

                },
                {
                    title: '浏览',
                    key: 'view',
                    dataIndex: 'url',
                    render: (url) =>
                        this.state.rows.length >= 1 ? (
                            <div style={{color: "red"}}>
                                <a rel="noopener noreferrer" target={"_blank"} href={url}>
                                    <EyeOutlined/>
                                </a>
                            </div>
                        ) : null,
                },
            ]
        }
    }

    onErrSearch = (key) => {
        this.fetchData({current: 1, pageSize: 10, key: key})
    }

    getDataApiUri() {
        return "/api/admin/article";
    }

    getSecondTitle() {
        return this.state.res['blogManage'];
    }

    getDeleteApiUri() {
        return "/api/admin/article/delete";
    }

    render() {

        const {rows, pagination, tableLoading} = this.state;

        return (
            <Spin delay={this.getSpinDelayTime()} spinning={tableLoading}>
                <Title className='page-header' level={3}>{this.getSecondTitle()}</Title>
                <Divider/>
                <Row style={{paddingBottom: "10px"}}>
                    <Col span={24}>
                        <Search placeholder={this.state.res.searchTip} onSearch={this.onErrSearch} enterButton
                                style={{maxWidth: "240px", float: "right"}}/>
                    </Col>
                </Row>

                <Table bordered style={{minWidth: "1200px"}} onChange={this.onShowSizeChange} columns={this.state.columns}
                       pagination={pagination} dataSource={rows}/>
            </Spin>
        )
    }
}