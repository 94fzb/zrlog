import React from "react";
import axios from "axios";
import {BaseResourceComponent} from "./base-resource-component";

export class BaseTableComponent extends BaseResourceComponent {


    onShowSizeChange = (pagination) => {
        console.info(pagination);
        this.fetchData(pagination);
    }

    fetchData(pagination) {
        const query = {
            page: pagination.current,
            size: pagination.pageSize,
            key: pagination.key
        }
        axios.get(this.getDataApiUri() + this.mapToQueryString(query)).then(({data}) => {
            const tableData = {
                tableLoading: false,
                pagination: {
                    total: data.data.totalElements,
                },
                rows: data.data.rows
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
}