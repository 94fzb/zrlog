import { message, PaginationProps, Space, Table } from "antd";
import { FunctionComponent, useEffect, useState } from "react";
import axios from "axios";
import { mapToQueryString } from "../utils/helpers";
import Popconfirm from "antd/es/popconfirm";
import { cacheIgnoreReloadKey, getRes } from "../utils/constants";
import { DeleteOutlined } from "@ant-design/icons";
import { Link, useNavigate } from "react-router-dom";
import { useLocation } from "react-router";

type BaseTableProps = {
    deleteApi: string;
    deleteSuccessCallback?: (id: number) => void;
    columns: any[];
    datasource?: PageDataSource;
    searchKey?: string;
    addBtnRender?: (addSuccessCall: () => void) => any;
    editBtnRender?: (id: number, record: any, editSuccessCall: () => void) => any;
};

export type PageDataSource = {
    rows: [];
    page: number;
    key?: string;
    size: number;
    totalElements: number;
};

export type TableData = {
    tableLoaded: boolean;
    pagination: MyPagination;
    tablePagination?: PaginationProps;
    rows: [];
    query: string | undefined;
};

export type MyPagination = {
    page: number;
    size: number;
    key?: string;
};

const BaseTable: FunctionComponent<BaseTableProps> = ({
    deleteApi,
    editBtnRender,
    addBtnRender,
    columns,
    datasource,
    searchKey,
    deleteSuccessCallback,
}) => {
    const navigate = useNavigate();
    const location = useLocation();

    const buildJumpUrl = (page: number, size: number, searchKey: string | undefined) => {
        return buildJumpUrlFull(page, size, searchKey, -1);
    };

    const buildJumpUrlFull = (page: number, size: number, searchKey: string | undefined, t: number) => {
        const queryParam: Record<string, number | string> = {};
        if (page > 1) {
            queryParam.page = page;
        }
        //默认10， 1000000，为不分页时候返回的值
        if (size != 10 && size != 1000000) {
            queryParam.size = size;
        }
        if (searchKey && searchKey.trim().length > 0) {
            queryParam.key = searchKey.trim();
        }
        if (t > 0) {
            queryParam[cacheIgnoreReloadKey] = t;
        }
        const queryStr = mapToQueryString(queryParam);
        if (queryStr.length === 0) {
            return location.pathname;
        }
        return location.pathname + "?" + queryStr;
    };

    const fetchData = (page: number, size: number, searchKey: string | undefined) => {
        navigate(buildJumpUrl(page, size, searchKey));
    };

    const fetchDataWithReload = (page: number, size: number, searchKey: string | undefined) => {
        navigate(buildJumpUrlFull(page, size, searchKey, new Date().getTime()));
    };

    const [tableDataState, setTableDataState] = useState<TableData>({
        pagination: {
            page: datasource?.page ? datasource.page : 1,
            key: datasource?.key,
            size: datasource?.size ? datasource?.size : 10,
        },
        query: datasource?.key,
        tableLoaded: true,
        rows: datasource ? datasource.rows : [],
        tablePagination: {
            total: datasource?.totalElements,
            current: datasource?.page,
            pageSize: datasource?.size,
            onChange: (page: number, size: number) => {
                fetchData(page, size, tableDataState.query);
            },
        },
    });

    const [messageApi, contextHolder] = message.useMessage();
    const handleDelete = async (pagination: MyPagination, deleteApiUri: string, key: string) => {
        const response = await axios.post(deleteApiUri + "?id=" + key);
        if (response.data.error) {
            messageApi.error(response.data.message);
            return;
        }
        messageApi.info("删除成功");
        fetchDataWithReload(pagination.page, pagination.size, tableDataState.query);
    };

    useEffect(() => {
        if (searchKey === tableDataState.query) {
            return;
        }
        setTableDataState({ ...tableDataState, query: searchKey });
        fetchData(1, tableDataState.pagination.size, searchKey);
    }, [searchKey]);

    useEffect(() => {
        setTableDataState((prevState) => {
            return {
                ...prevState,
                rows: datasource ? datasource.rows : [],
                pagination: {
                    page: datasource?.page ? datasource.page : 1,
                    size: datasource?.size ? datasource.size : 10,
                },
                tablePagination: {
                    current: datasource?.page,
                    pageSize: datasource?.size,
                    total: datasource?.totalElements,
                },
            };
        });
    }, [datasource]);

    const getActionedColumns = () => {
        const c = [];
        c.push({
            title: "ID",
            dataIndex: "id",
            key: "id",
            width: 80,
        });
        c.push({
            title: "",
            dataIndex: "id",
            key: "action",
            width: 100,
            render: (text: any, record: any) =>
                text ? (
                    <Space size={16}>
                        <Popconfirm
                            title={getRes()["deleteTips"]}
                            onConfirm={() =>
                                handleDelete(tableDataState.pagination, deleteApi, record.id).then(() => {
                                    if (deleteSuccessCallback) {
                                        deleteSuccessCallback(record.id);
                                    }
                                })
                            }
                        >
                            <DeleteOutlined style={{ color: "red" }} />
                        </Popconfirm>
                        {editBtnRender
                            ? editBtnRender(text, record, () => {
                                  fetchDataWithReload(
                                      tableDataState.pagination.page,
                                      tableDataState.pagination.size,
                                      tableDataState.query
                                  );
                              })
                            : null}
                    </Space>
                ) : null,
        });
        columns.forEach((e) => {
            c.push(e);
        });
        return c;
    };

    return (
        <>
            {contextHolder}
            {addBtnRender
                ? addBtnRender(() => {
                      fetchDataWithReload(
                          tableDataState.pagination.page,
                          tableDataState.pagination.size,
                          tableDataState.query
                      );
                  })
                : undefined}
            <Table
                onChange={(pagination) => {
                    fetchData(
                        pagination.current ? pagination.current : 1,
                        pagination.pageSize ? pagination.pageSize : 10,
                        tableDataState.query
                    );
                }}
                style={{ minHeight: 512 }}
                columns={getActionedColumns()}
                pagination={{
                    hideOnSinglePage: true,
                    ...tableDataState.tablePagination,
                    itemRender: (page, _type, e) => {
                        return (
                            <Link
                                key={page}
                                to={buildJumpUrl(page, datasource?.size ? datasource.size : 10, tableDataState.query)}
                            >
                                {e}
                            </Link>
                        );
                    },
                }}
                dataSource={tableDataState.rows}
                scroll={{ x: "90vw" }}
            ></Table>
        </>
    );
};

export default BaseTable;
