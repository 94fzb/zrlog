import React from "react";
import {Spin, Table} from "antd";
import {BaseTableComponent} from "./base-table-component";
import Title from "antd/es/typography/Title";
import Divider from "antd/es/divider";
import Popconfirm from "antd/es/popconfirm";
import {DeleteOutlined} from "@ant-design/icons";

export class BLink extends BaseTableComponent {

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
            ]
        }
    }

    getDataApiUri() {
        return "/api/admin/link"
    }

    getDeleteApiUri() {
        return "/api/admin/link/delete";
    }

    getSecondTitle() {
        return this.state.res['admin.link.manage'];
    }

    render() {
        const {rows, pagination, tableLoading} = this.state;


        return (
            <Spin delay={this.getSpinDelayTime()} spinning={tableLoading}>
                <Title className='page-header' level={3}>{this.getSecondTitle()}</Title>
                <Divider/>
                <Table onChange={this.onShowSizeChange} columns={this.state.columns} pagination={pagination}
                       dataSource={rows}/>
            </Spin>
        )
    }
}