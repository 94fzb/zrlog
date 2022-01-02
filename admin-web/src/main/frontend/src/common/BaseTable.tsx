import {message, PaginationProps, Space, Table} from "antd";
import {FunctionComponent, useEffect, useState} from "react";
import axios from "axios";
import {mapToQueryString} from "../utils/helpers";
import Popconfirm from "antd/es/popconfirm";
import {getRes} from "../utils/constants";
import {DeleteOutlined} from "@ant-design/icons";

type BaseTableProps = {
    dataApi: string,
    deleteApi: string,
    columns: any[],
    searchKey?: string,
    addBtnRender?: (addSuccessCall: () => void) => any,
    editBtnRender?: (id: number, record: any, editSuccessCall: () => void) => any
}

export type TableData = {
    tableLoaded: boolean,
    pagination: MyPagination,
    tablePagination?: PaginationProps,
    rows: [],
    query: string | undefined,
}

export type MyPagination = {
    page?: number,
    size?: number,
    key?: string,
}

export const fetchData = async (pagination: MyPagination, dataApiUri: string): Promise<TableData> => {
    const query = {
        page: pagination.page,
        size: pagination.size,
        key: pagination.key
    }
    const {data} = await axios.get(dataApiUri + "?" + mapToQueryString(query));
    return {
        tableLoaded: true,
        pagination: pagination,
        rows: data.data.rows,
        query: pagination.key,
        tablePagination: {
            total: data.data.totalElements
        }
    };
}

export const handleDelete = async (pagination: MyPagination, dataApiUri: string, deleteApiUri: string, key: string): Promise<TableData> => {
    await axios.post(deleteApiUri + "?id=" + key).then(({data}) => {
        if (data.error) {
            message.error(data.message);
            return;
        }
        message.info("删除成功");
    });
    return fetchData(pagination, dataApiUri);
}

const BaseTable: FunctionComponent<BaseTableProps> = ({
                                                          dataApi,
                                                          deleteApi,
                                                          editBtnRender,
                                                          addBtnRender,
                                                          columns,
                                                          searchKey
                                                      }) => {

    const [tableDataState, setTableDataState] = useState<TableData>({
        pagination: {page: 1, key: searchKey, size: 10},
        query: searchKey,
        tableLoaded: false,
        rows: []
    })

    useEffect(() => {
        fetchData(tableDataState.pagination, dataApi).then((r) => {
            setTableDataState(r)
        })
    }, [])

    useEffect(() => {
        if (searchKey === undefined) {
            return;
        }
        fetchData({...tableDataState.pagination, key: searchKey}, dataApi).then((r) => {
            setTableDataState(r)
        })
    }, [searchKey]);

    const getActionedColumns = () => {
        const c = [];
        c.push({
            title: 'ID',
            dataIndex: 'id',
            key: 'id',
            width: 80,
        },)
        c.push({
            title: '',
            dataIndex: 'id',
            key: 'action',
            width: 100,
            render: (text: any, record: any) =>
                text ? (
                    <Space size={16}>
                        <Popconfirm title={getRes()['deleteTips']}
                                    onConfirm={() => handleDelete(tableDataState.pagination, dataApi, deleteApi, record.id).then(r => {
                                        setTableDataState(r);
                                    })}>
                            <DeleteOutlined style={{color: "red"}}/>
                        </Popconfirm>
                        {editBtnRender ? editBtnRender(text, record, () => {
                            fetchData(tableDataState.pagination, dataApi).then((r) => {
                                setTableDataState(r)
                            })
                        }) : null}
                    </Space>
                ) : null,
        })
        columns.forEach(e => {
            c.push(e);
        })
        return c;
    }

    return (
        <>
            {addBtnRender ? addBtnRender(() => {
                fetchData(tableDataState.pagination, dataApi).then((r) => {
                    setTableDataState(r)
                })
            }) : undefined}
            <Table loading={!tableDataState.tableLoaded} bordered
                   onChange={(pagination) => {
                       fetchData({
                           page: pagination.current,
                           size: pagination.pageSize,
                           key: tableDataState.pagination?.key
                       }, dataApi).then((r) => {
                           setTableDataState(r)
                       })
                   }} columns={getActionedColumns()}
                   pagination={tableDataState.tablePagination} dataSource={tableDataState.rows}
                   scroll={{x: '90vw'}}/>
        </>
    )
}

export default BaseTable;