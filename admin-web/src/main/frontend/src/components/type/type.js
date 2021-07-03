import React from "react";
import {Space, Table} from "antd";
import Spin from "antd/es/spin";
import Title from "antd/es/typography/Title";
import Divider from "antd/es/divider";
import Popconfirm from "antd/es/popconfirm";
import {DeleteOutlined} from "@ant-design/icons";
import {BaseTableComponent} from "../base-table-component";
import {AddType} from "./add_type";
import {EditType} from "./edit_type";

class Type extends BaseTableComponent {

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
                            <Space size={16}>
                                <Popconfirm title={this.state.res['deleteTips']}
                                            onConfirm={() => this.handleDelete(record.id)}>
                                    <DeleteOutlined style={{color: "red"}}/>
                                </Popconfirm>
                                <EditType col={record} tableComponent={this}/>
                            </Space>
                        ) : null,
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
            ]
        }
    }


    getDataApiUri() {
        return "/api/admin/type"
    }

    getSecondTitle() {
        return this.state.res['admin.type.manage'];
    }

    getDeleteApiUri() {
        return "/api/admin/type/delete";
    }

    render() {

        const {rows, pagination, tableLoading} = this.state;


        return (
            <>
                <Spin delay={this.getSpinDelayTime()} spinning={this.state.resLoading}>
                    <Title className='page-header' level={3}>{this.getSecondTitle()}</Title>
                    <Divider/>
                    <AddType tableComponent={this}/>
                    <Table loading={tableLoading} bordered onChange={this.onShowSizeChange} columns={this.state.columns}
                           pagination={pagination}
                           dataSource={rows}
                           scroll={{x: '90vw'}}/>
                </Spin>
            </>
        )
    }
}

export default Type;
