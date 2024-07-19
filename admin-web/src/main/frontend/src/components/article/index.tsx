import { EditOutlined, TagOutlined } from "@ant-design/icons";

import { Col, Row, Space, Tag, Tooltip } from "antd";
import Search from "antd/es/input/Search";
import Title from "antd/es/typography/Title";
import Divider from "antd/es/divider";
import { getColorPrimary, getRes } from "../../utils/constants";
import { ReactElement, useState } from "react";
import BaseTable, { PageDataSource } from "../../common/BaseTable";
import { Link } from "react-router-dom";
import { deleteCacheDataByKey } from "../../cache";
import { LockOutlined } from "@ant-design/icons";

const Index = ({ data, offline }: { data: PageDataSource; offline: boolean }) => {
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

    const getColumns = () => {
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
            },
            {
                title: getRes()["createTime"],
                key: "releaseTime",
                dataIndex: "releaseTime",
                width: 120,
            },
            {
                title: getRes()["lastUpdateDate"],
                key: "lastUpdateDate",
                dataIndex: "lastUpdateDate",
                width: 120,
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
