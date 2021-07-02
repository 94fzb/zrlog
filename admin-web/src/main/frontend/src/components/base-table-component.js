// eslint-disable-next-line no-unused-vars
import React from "react";
import {BaseResourceComponent} from "./base-resource-component";
import {message} from "antd";

export class BaseTableComponent extends BaseResourceComponent {


    onShowSizeChange = (pagination) => {
        console.info(pagination);
        this.fetchData(pagination);
    }

    fetchData(pagination) {
        let query;
        if (pagination) {
            query = {
                page: pagination.current,
                size: pagination.pageSize,
                key: pagination.key
            }
        } else {
            query = this.state.query;
        }
        this.getAxios().get(this.getDataApiUri() + this.mapToQueryString(query)).then(({data}) => {
            const tableData = {
                tableLoading: false,
                pagination: {
                    total: data.data.totalElements,
                    key: query.key,
                    current: query.page
                },
                rows: data.data.rows,
                query: query,
            };
            this.setState(tableData);
        })
    }

    componentDidMount() {
        super.componentDidMount();
        this.setState({
            tableLoading: true,
        })
        this.fetchData({current: 1, pageSize: 10});
    }

    getDataApiUri() {
        return null;
    }


    getDeleteApiUri() {
        return null;
    }

    handleDelete = async (key) => {
        await this.getAxios().post(this.getDeleteApiUri() + "?id=" + key).then(({data}) => {
            if (data.error) {
                message.error(this.state.res['deleteError']);
                return;
            }
            message.info("删除成功");
            this.fetchData(this.state.pagination);
        });
    }
}
