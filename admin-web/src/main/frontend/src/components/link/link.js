import React from "react";
import {Space, Spin, Table} from "antd";
import {BaseTableComponent} from "../base-table-component";
import Title from "antd/es/typography/Title";
import Divider from "antd/es/divider";
import Popconfirm from "antd/es/popconfirm";
import {DeleteOutlined} from "@ant-design/icons";
import {AddLink} from "./add_link";
import {EditLink} from "./edit_link";

class BLink extends BaseTableComponent {

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
                                <EditLink col={record} tableComponent={this}/>
                            </Space>
                        ) : null,
                },
                {
                    title: '链接',
                    dataIndex: 'url',
                    key: 'url',
                    render: url => (
                        <a style={{display: "inline"}} rel="noopener noreferrer" target={"_blank"} href={url}>
                            <div style={{display: "inline"}} dangerouslySetInnerHTML={{__html: url}}/>
                        </a>
                    )
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
                },
                {
                    title: '排序',
                    key: 'sort',
                    dataIndex: 'sort'
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
            <Spin delay={this.getSpinDelayTime()} spinning={this.state.resLoading}>
                <Title className='page-header' level={3}>{this.getSecondTitle()}</Title>
                <Divider/>
                <AddLink tableComponent={this}/>
                <Table loading={tableLoading} bordered onChange={this.onShowSizeChange} columns={this.state.columns}
                       pagination={pagination}
                       dataSource={rows}
                       scroll={{x: '90vw'}}/>
            </Spin>
        )
    }
}

export default BLink;
