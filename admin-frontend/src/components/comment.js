import React from "react";
import {Table} from "antd";
import {BaseTableComponent} from "./base-table-component";
import Spin from "antd/lib/spin";
import Title from "antd/es/typography/Title";
import Divider from "antd/es/divider";

const columns = [
    {
        title: 'ID',
        dataIndex: 'id',
        key: 'id'
    },
    {
        title: '内容',
        dataIndex: 'userComment',
        key: 'userComment',
        width: "200px"
    },
    {
        title: '昵称',
        dataIndex: 'userName',
        key: 'userName',
    },
    {
        title: '评论者主页',
        key: 'userHome',
        dataIndex: 'userHome'
    },
    {
        title: 'IP',
        key: 'ip',
        dataIndex: 'ip'
    },
    {
        title: '邮箱',
        key: 'userMail',
        dataIndex: 'userMail'
    },
    {
        title: '评论时间',
        key: 'commTime',
        dataIndex: 'commTime'
    },
    {
        title: '浏览',
        key: 'view',
        dataIndex: 'view'
    },
];

export class Comment extends BaseTableComponent {

    getDataApiUri() {
        return "/api/admin/comment"
    }

    getSecondTitle() {
        return this.state.res['admin.comment.manage'];
    }

    render() {

        const {rows, pagination, tableLoading} = this.state;


        return (
            <Spin spinning={tableLoading}>
                <Title className='page-header' level={3}>{this.getSecondTitle()}</Title>
                <Divider/>
                <Table onChange={this.onShowSizeChange} columns={columns} pagination={pagination} dataSource={rows}/>
            </Spin>
        )
    }
}