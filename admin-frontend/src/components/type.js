import React from "react";
import {Table} from "antd";
import {BaseTableComponent} from "./base-table-component";
import Spin from "antd/es/spin";
import Title from "antd/es/typography/Title";
import Divider from "antd/es/divider";

const columns = [
    {
        title: 'ID',
        dataIndex: 'id',
        key: 'id'
    },
    {
        title: '分类名称',
        dataIndex: 'typeName',
        key: 'typeName',
    },
    {
        title: '别名',
        dataIndex: 'alias',
        key: 'alias',
    },
    {
        title: '介绍',
        key: 'remark',
        dataIndex: 'remark'
    }
];

export class Type extends BaseTableComponent {

    getDataApiUri() {
        return "/api/admin/type"
    }

    getSecondTitle() {
        return this.state.res['admin.type.manage'];
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