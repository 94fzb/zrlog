import React from "react";
import {Spin, Table} from "antd";
import {BaseTableComponent} from "./base-table-component";
import Title from "antd/es/typography/Title";
import Divider from "antd/es/divider";

const columns = [
    {
        title: 'ID',
        dataIndex: 'id',
        key: 'id'
    },
    {
        title: '链接',
        dataIndex: 'url',
        key: 'url',
    },
    {
        title: '链接',
        dataIndex: 'linkName',
        key: 'linkName',
    },
    {
        title: '网站名称',
        key: 'linkName',
        dataIndex: 'linkName'
    },
    {
        title: '描述',
        key: 'alt',
        dataIndex: 'alt'
    }
];

export class BLink extends BaseTableComponent {

    getDataApiUri() {
        return "/api/admin/link"
    }

    getSecondTitle() {
        return this.state.res['admin.link.manage'];
    }

    render() {
        const {rows, pagination, tableLoading} = this.state;


        return (
            <Spin spinning={tableLoading}>
                <Title className='page-header' level={3}>{this.getSecondTitle()}</Title>
                <Divider/>
                <Table onChange={this.onShowSizeChange} columns={columns} pagination={pagination}
                       dataSource={rows}/>
            </Spin>
        )
    }
}