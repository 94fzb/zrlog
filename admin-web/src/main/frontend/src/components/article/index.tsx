import { EditOutlined, LockOutlined, TagOutlined } from "@ant-design/icons";

import { Col, Row, Space, Tag, Tooltip } from "antd";
import Search from "antd/es/input/Search";
import Title from "antd/es/typography/Title";
import Divider from "antd/es/divider";
import { getColorPrimary, getRes } from "../../utils/constants";
import { ReactElement, useEffect, useRef, useState } from "react";
import BaseTable, { ArticlePageDataSource } from "../../common/BaseTable";
import { Link } from "react-router-dom";
import { deleteCacheDataByKey } from "../../cache";
import { useLocation } from "react-router";

const genTypes = (d: ArticlePageDataSource, search: string) => {
    const types = new URLSearchParams(search).get("types") as unknown as string;

    return d.types
        ? d.types.map((e) => {
              return { text: e.typeName, value: e.alias, selected: types ? types.split(",").includes(e.alias) : false };
          })
        : [];
};

const Index = ({ data, offline }: { data: ArticlePageDataSource; offline: boolean }) => {
    const location = useLocation();
    const ds = genTypes(data, location.search);

    const [filters, setFilters] = useState<Record<string, any>[]>(ds); // 用于存储选中的筛选项
    const jumped = useRef(false);

    const tagForMap = (tag: string) => {
        const tagElem = (
            <Tag icon={<TagOutlined />} closable={false} color={getColorPrimary()} style={{ userSelect: "none" }}>
                {tag}
            </Tag>
        );
        return (
            <span key={"all-" + tag} style={{ display: "inline-block" }}>
                {tagElem}
            </span>
        );
    };

    const wrapperArticleStateInfo = (record: any, children: ReactElement) => {
        return (
            <span style={{ display: "flex", gap: 4, whiteSpace: "normal" }}>
                {record.privacy && <LockOutlined style={{ color: getColorPrimary() }} />}
                {record.rubbish && <span>[{getRes()["rubbish"]}]</span>}
                {children}
            </span>
        );
    };

    const handleNavigation = () => {
        if (jumped.current) {
            return;
        }
        jumped.current = true;
        const sortArgs: string[] = [];
        data.sort.forEach((e) => {
            sortArgs.push("sort=" + encodeURIComponent(e));
        });
        //FIXME jump append sort
        const endTag = location.search.includes("sort=") ? "&" : "";
        location.pathname =
            location.pathname +
            "?types=" +
            encodeURIComponent(
                filters
                    .filter((e) => e.selected)
                    .map((e) => e.value)
                    .join(",")
            ) +
            "&" +
            sortArgs.join("&") +
            endTag;
    };

    useEffect(() => {
        jumped.current = false;
        setFilters(genTypes(data, location.search));
    }, [data]);

    const handleFilterChange = (value: string, checked: boolean) => {
        console.log(value);
        filters.forEach((filter) => {
            if (filter.value === value) {
                filter.selected = checked;
            } else {
                filter.selected = false;
            }
        });
        setFilters(filters);
    };

    const getColumns = () => {
        const sorterMap: Record<string, string> = {};

        data.sort &&
            data.sort.map((e) => {
                const arr: string[] = e.split(",");
                sorterMap[arr[0]] = arr[1] === "ASC" ? "ascend" : "descend";
            });

        return [
            {
                title: getRes()["title"],
                dataIndex: "title",
                key: "title",
                ellipsis: {
                    showTitle: false,
                },
                width: 300,
                render: (text: string, record: any) => {
                    const t = (
                        <Tooltip
                            placement="top"
                            title={
                                <div>
                                    点击查看《<span dangerouslySetInnerHTML={{ __html: text }}></span>》
                                </div>
                            }
                        >
                            <div
                                style={{ overflow: "hidden", textOverflow: "ellipsis" }}
                                dangerouslySetInnerHTML={{ __html: text }}
                            />
                        </Tooltip>
                    );
                    if (record["url"].includes("previewMode")) {
                        return wrapperArticleStateInfo(record, <Link to={record["url"]}>{t}</Link>);
                    }
                    return wrapperArticleStateInfo(
                        record,
                        <a rel="noopener noreferrer" target={"_blank"} href={record.url}>
                            {t}
                        </a>
                    );
                },
            },
            {
                title: getRes().tag,
                dataIndex: "keywords",
                key: "keywords",
                width: 150,
                render: (text: string) =>
                    text ? (
                        <Space size={[0, 8]} wrap>
                            {text.split(",").map(tagForMap)}
                        </Space>
                    ) : null,
            },
            /*{
                title: "作者",
                key: "userName",
                dataIndex: "userName",
                width: 80,
            },*/
            {
                title: getRes()["type"],
                key: "typeName",
                dataIndex: "typeName",
                width: 100,
                filters: filters,
                filterMultiple: false,
                filteredValue: filters.filter((e) => e.selected).map((e) => e.value), // 动态绑定当前选中值
                onFilter: (value: string) => {
                    // 更新选中状态
                    if (
                        filters
                            .filter((e) => e.selected)
                            .map((e) => e.value)
                            .includes(value)
                    ) {
                        return true;
                    }
                    handleFilterChange(value, true);
                    handleNavigation();
                    return true; // 保留默认筛选功能
                },
            },
            {
                title: "浏览量",
                key: "click",
                dataIndex: "click",
                width: 80,
            },
            {
                title: getRes()["commentAble"],
                key: "canComment",
                dataIndex: "canComment",
                render: (v: boolean) => (v ? "是" : "否"),
                width: 80,
            },
            {
                title: "评论量",
                key: "commentSize",
                dataIndex: "commentSize",
                width: 80,
                sorter: true,
                sortOrder: sorterMap["commentSize"],
            },
            {
                title: getRes()["createTime"],
                key: "releaseTime",
                dataIndex: "releaseTime",
                width: 120,
                sorter: true,
                sortOrder: sorterMap["releaseTime"],
            },
            {
                title: getRes()["lastUpdateDate"],
                key: "lastUpdateDate",
                dataIndex: "lastUpdateDate",
                width: 120,
                sorter: true,
                sortOrder: sorterMap["lastUpdateDate"],
            },
        ];
    };

    const onSearch = (key: string) => {
        setSearchKey(key);
    };

    const getDeleteApiUri = () => {
        return "/api/admin/article/delete";
    };

    const [searchKey, setSearchKey] = useState<string>(data.key ? data.key : "");

    return (
        <>
            <Row gutter={[8, 8]} style={{ paddingTop: 20 }}>
                <Col md={14} xxl={18} sm={6} span={24}>
                    <Title className="page-header" style={{ marginTop: 0, marginBottom: 0 }} level={3}>
                        {getRes()["blogManage"]}
                    </Title>
                </Col>
                <Col md={10} xxl={6} sm={18}>
                    <Search
                        disabled={offline}
                        placeholder={getRes().searchTip}
                        onSearch={onSearch}
                        defaultValue={data.key}
                        enterButton={getRes()["search"]}
                        style={{ maxWidth: "240px", float: "right" }}
                    />
                </Col>
            </Row>

            <Divider />
            <BaseTable
                defaultPageSize={data.defaultPageSize}
                offline={offline}
                datasource={data}
                columns={getColumns()}
                editBtnRender={(id) => (
                    <Link to={"/article-edit?id=" + id}>
                        <EditOutlined style={{ color: getColorPrimary() }} />
                    </Link>
                )}
                deleteSuccessCallback={(id) => {
                    deleteCacheDataByKey("/article-edit?id=" + id);
                }}
                deleteApi={getDeleteApiUri()}
                searchKey={searchKey}
            />
        </>
    );
};

export default Index;
