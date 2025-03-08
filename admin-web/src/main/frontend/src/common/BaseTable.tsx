import { message, PaginationProps, Space, Table } from "antd";
import { FunctionComponent, useEffect, useState } from "react";
import axios from "axios";
import { mapToQueryString } from "../utils/helpers";
import Popconfirm from "antd/es/popconfirm";
import { cacheIgnoreReloadKey, getRes } from "../utils/constants";
import { DeleteOutlined } from "@ant-design/icons";
import { Link, useNavigate } from "react-router-dom";
import { useLocation } from "react-router";
import { SorterResult } from "antd/es/table/interface";

type BaseTableProps = {
    deleteApi: string;
    deleteSuccessCallback?: (id: number) => void;
    columns: any[];
    datasource?: PageDataSource;
    searchKey?: string;
    hideId?: boolean;
    offline: boolean;
    defaultPageSize: number;
    addBtnRender?: (addSuccessCall: () => void) => any;
    editBtnRender?: (id: number, record: any, editSuccessCall: () => void) => any;
};

export type PageDataSource = {
    rows: [];
    page: number;
    key?: string;
    sort: string[];
    size: number;
    defaultPageSize: number;
    totalElements: number;
};
export type ArticlePageDataSource = PageDataSource & {
    types: Record<string, any>[];
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
    sort: string[];
};

const BaseTable: FunctionComponent<BaseTableProps> = ({
    deleteApi,
    editBtnRender,
    addBtnRender,
    columns,
    datasource,
    defaultPageSize,
    searchKey,
    deleteSuccessCallback,
    hideId,
    offline,
}) => {
    const navigate = useNavigate();
    const location = useLocation();

    const buildJumpUrl = (page: number, size: number, searchKey: string | undefined, sorter: string[]) => {
        return buildJumpUrlFull(page, size, searchKey, -1, sorter);
    };

    const buildJumpUrlFull = (page: number, size: number, searchKey: string | undefined, t: number, sort: string[]) => {
        const queryParam: Record<string, number | string | string[]> = {};
        if (page > 1) {
            queryParam.page = page;
        }
        //默认分页的， 1000000，为不分页时候返回的值
        if (size != defaultPageSize && size != 1000000) {
            queryParam.size = size;
        }
        if (searchKey && searchKey.trim().length > 0) {
            queryParam.key = searchKey.trim();
        }
        if (t > 0) {
            queryParam[cacheIgnoreReloadKey] = t;
        }
        if (sort.length > 0) {
            //暂时支持一个属性进行排序
            queryParam["sort"] = sort[0];
        }
        const queryStr = mapToQueryString(queryParam);
        console.info(queryStr + "===<");
        if (queryStr.length === 0) {
            return location.pathname;
        }
        return location.pathname + "?" + queryStr;
    };

    const fetchData = (page: number, size: number, searchKey: string | undefined, sorter: string[]) => {
        navigate(buildJumpUrl(page, size, searchKey, sorter));
    };

    const fetchDataWithReload = (page: number, size: number, searchKey: string | undefined, sorter: string[]) => {
        navigate(buildJumpUrlFull(page, size, searchKey, new Date().getTime(), sorter));
    };

    const [tableDataState, setTableDataState] = useState<TableData>({
        pagination: {
            page: datasource?.page ? datasource.page : 1,
            key: datasource?.key,
            sort: datasource?.sort ? datasource?.sort : [],
            size: datasource?.size ? datasource?.size : defaultPageSize,
        },
        query: datasource?.key,
        tableLoaded: true,
        rows: datasource ? datasource.rows : [],
        tablePagination: {
            total: datasource?.totalElements,
            current: datasource?.page,
            pageSize: datasource?.size,
            onChange: (page: number, size: number) => {
                fetchData(page, size, tableDataState.query, []);
            },
        },
    });

    const [messageApi, contextHolder] = message.useMessage({ maxCount: 3 });
    const handleDelete = async (pagination: MyPagination, deleteApiUri: string, key: string): Promise<boolean> => {
        const response = await axios.post(deleteApiUri + "?id=" + key);
        if (response.data.error) {
            messageApi.error(response.data.message);
            return false;
        }
        if (response.data.error === 0) {
            messageApi.success(getRes()["deleteSuccess"]);
            fetchDataWithReload(pagination.page, pagination.size, tableDataState.query, tableDataState.pagination.sort);
            return true;
        }
        return false;
    };

    useEffect(() => {
        if (searchKey === tableDataState.query) {
            return;
        }
        setTableDataState({ ...tableDataState, query: searchKey });
        fetchData(1, tableDataState.pagination.size, searchKey, tableDataState.pagination.sort);
    }, [searchKey]);

    useEffect(() => {
        setTableDataState((prevState) => {
            return {
                ...prevState,
                rows: datasource ? datasource.rows : [],
                pagination: {
                    page: datasource?.page ? datasource.page : 1,
                    size: datasource?.size ? datasource.size : 10,
                    sort: datasource?.sort ? datasource.sort : [],
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
        if (hideId === null || hideId === undefined || !hideId) {
            c.push({
                title: "ID",
                dataIndex: "id",
                key: "id",
                fixed: true,
                width: 64,
                render: (text: string) => {
                    return <span style={{ maxWidth: 64 }}>{text}</span>;
                },
            });
        }
        c.push({
            title: "",
            dataIndex: "id",
            key: "action",
            fixed: true,
            width: 80,
            render: (text: any, record: any) =>
                text ? (
                    <Space size={16}>
                        <Link
                            to={"#delete-" + record.id}
                            onClick={(e) => {
                                e.preventDefault();
                                e.stopPropagation();
                            }}
                        >
                            <Popconfirm
                                disabled={offline}
                                title={getRes()["deleteTips"]}
                                onConfirm={async () => {
                                    const success = await handleDelete(tableDataState.pagination, deleteApi, record.id);
                                    if (success) {
                                        if (deleteSuccessCallback) {
                                            deleteSuccessCallback(record.id);
                                        }
                                    }
                                }}
                            >
                                <DeleteOutlined style={{ color: "red" }} />
                            </Popconfirm>
                        </Link>
                        {editBtnRender
                            ? editBtnRender(text, record, () => {
                                  fetchDataWithReload(
                                      tableDataState.pagination.page,
                                      tableDataState.pagination.size,
                                      tableDataState.query,
                                      tableDataState.pagination.sort
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
                          tableDataState.query,
                          tableDataState.pagination.sort
                      );
                  })
                : undefined}
            <Table
                onChange={(pagination, _filter, sorter) => {
                    const sort =
                        (sorter as SorterResult) && (sorter as SorterResult).field
                            ? [
                                  (sorter as SorterResult).field +
                                      "," +
                                      ((sorter as SorterResult).order === "descend" ? "DESC" : "ASC"),
                              ]
                            : [];
                    if (sort.length > 0) {
                        setTableDataState({
                            ...tableDataState,
                            pagination: {
                                ...tableDataState.pagination,
                                sort: sort,
                            },
                        });
                    }
                    fetchData(
                        pagination.current ? pagination.current : 1,
                        pagination.pageSize ? pagination.pageSize : 10,
                        tableDataState.query,
                        sort
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
                                to={buildJumpUrl(
                                    page,
                                    datasource?.size ? datasource.size : 10,
                                    tableDataState.query,
                                    datasource?.sort ? datasource.sort : []
                                )}
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
