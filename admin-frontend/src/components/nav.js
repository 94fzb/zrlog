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
        title: '链接',
        dataIndex: 'url',
        key: 'url',
    },
    {
        title: '导航名称',
        dataIndex: 'navName',
        key: 'navName',
    },
    {
        title: '排序',
        key: 'sort',
        dataIndex: 'sort'
    }
];

export class Nav extends BaseTableComponent {

    getDataApiUri() {
        return "/api/admin/nav"
    }

    getSecondTitle() {
        return this.state.res['admin.nav.manage'];
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