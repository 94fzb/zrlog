import React from "react";
import {DeleteOutlined, EditOutlined, TagOutlined} from '@ant-design/icons';

import {Col, Row, Space, Table, Tag} from "antd";
import Search from "antd/lib/input/Search";
import {BaseTableComponent} from "../base-table-component";
import Spin from "antd/es/spin";
import Title from "antd/es/typography/Title";
import Divider from "antd/es/divider";
import Popconfirm from "antd/es/popconfirm";
import {Link} from "react-router-dom";
import './article.css'

class Article extends BaseTableComponent {

    tagForMap = tag => {
        const tagElem = (
            <Tag icon={<TagOutlined/>} closable={false} color="#108ee9">
                {tag}
            </Tag>
        );
        return (
            <span key={"all-" + tag} style={{display: 'inline-block'}}>
        {tagElem}
      </span>
        );
    };

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
                    key: 'action',
                    render: (text, record) =>
                        this.state.rows.length >= 1 ? (
                            <Space size={16}>
                                <Popconfirm title={this.state.res['deleteTips']}
                                            onConfirm={() => this.handleDelete(record.id)}>
                                    <DeleteOutlined style={{color: "red"}}/>
                                </Popconfirm>
                                <Link to={"/admin/article-edit?id=" + text}>
                                    <EditOutlined/>
                                </Link>
                            </Space>
                        ) : null,
                },
                {
                    title: '标题',
                    dataIndex: 'title',
                    key: 'title',
                    render: (text, record) =>
                        this.state.rows.length >= 1 ? (
                            <a style={{display: "inline"}} rel="noopener noreferrer" target={"_blank"}
                               href={record.url}>
                                <div style={{display: "inline"}} dangerouslySetInnerHTML={{__html: text}}/>
                            </a>
                        ) : null,
                    width: "400px"
                },
                {
                    title: '标签',
                    dataIndex: 'keywords',
                    key: 'keywords',
                    render: (text) =>
                        this.state.rows.length >= 1 && text ? (text.split(',').map(this.tagForMap)) : null,
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
                }
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
            <Spin delay={this.getSpinDelayTime()} spinning={this.state.resLoading}>
                <Title className='page-header' level={3}>{this.getSecondTitle()}</Title>
                <Divider/>
                <Row style={{paddingBottom: "10px"}}>
                    <Col span={24}>
                        <Search placeholder={this.state.res.searchTip} onSearch={this.onErrSearch} enterButton={this.state.res['search']}
                                style={{maxWidth: "240px", float: "right"}}/>
                    </Col>
                </Row>

                <Table loading={tableLoading} bordered
                       onChange={this.onShowSizeChange} columns={this.state.columns}
                       pagination={pagination} dataSource={rows}
                       scroll={{x: '90vw'}}/>
            </Spin>
        )
    }
}

export default Article;
