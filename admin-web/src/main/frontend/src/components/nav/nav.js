import React from "react";
import {Space, Table} from "antd";
import {BaseTableComponent} from "../base-table-component";
import Spin from "antd/lib/spin";
import Title from "antd/es/typography/Title";
import Divider from "antd/es/divider";
import Popconfirm from "antd/es/popconfirm";
import {DeleteOutlined} from "@ant-design/icons";
import {AddNav} from "./add_nav";
import {EditNav} from "./edit_nav";

class Nav extends BaseTableComponent {

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
                    key: 'op',
                    render: (text, record) =>
                        this.state.rows.length >= 1 ? (
                            <Space size={16}>
                                <Popconfirm title={this.state.res['deleteTips']}
                                            onConfirm={() => this.handleDelete(record.id)}>
                                    <DeleteOutlined style={{color: "red"}}/>
                                </Popconfirm>
                                <EditNav col={record} tableComponent={this}/>
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
                    title: '导航名称',
                    dataIndex: 'navName',
                    key: 'navName',
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
        return "/api/admin/nav"
    }

    getDeleteApiUri() {
        return "/api/admin/nav/delete";
    }

    getSecondTitle() {
        return this.state.res['admin.nav.manage'];
    }

    render() {

        const {rows, pagination, tableLoading} = this.state;


        return (
            <Spin delay={this.getSpinDelayTime()} spinning={this.state.resLoading}>
                <Title className='page-header' level={3}>{this.getSecondTitle()}</Title>
                <Divider/>
                <AddNav tableComponent={this}/>
                <Table loading={tableLoading} bordered onChange={this.onShowSizeChange} columns={this.state.columns}
                       pagination={pagination}
                       dataSource={rows}
                       scroll={{x: '90vw'}}/>
            </Spin>
        )
    }
}

export default Nav;
