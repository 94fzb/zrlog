import { EditOutlined, TagOutlined } from "@ant-design/icons";

import { Col, Row, Space, Tag, Tooltip } from "antd";
import Search from "antd/es/input/Search";
import Title from "antd/es/typography/Title";
import Divider from "antd/es/divider";
import { getColorPrimary, getRes } from "../../utils/constants";
import { useState } from "react";
import BaseTable, { PageDataSource } from "../../common/BaseTable";
import { Link } from "react-router-dom";
import { deleteCacheDataByKey } from "../../cache";

const Index = ({ data }: { data: PageDataSource }) => {
    const dataApi = "/api/admin/article";

    const tagForMap = (tag: string) => {
        const tagElem = (
            <Tag icon={<TagOutlined />} closable={false} color={getColorPrimary()}>
                {tag}
            </Tag>
        );
        return (
            <span key={"all-" + tag} style={{ display: "inline-block" }}>
                {tagElem}
            </span>
        );
    };

    const getColumns = () => {
        return [
            {
                title: "标题",
                dataIndex: "title",
                key: "title",
                ellipsis: {
                    showTitle: false,
                },
                width: 300,
                render: (text: string, record: any) =>
                    text ? (
                        <a rel="noopener noreferrer" target={"_blank"} href={record.url}>
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
                        </a>
                    ) : null,
            },
            {
                title: "标签",
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
            {
                title: "作者",
                key: "userName",
                dataIndex: "userName",
                width: 80,
            },
            {
                title: "分类",
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
                title: "草稿",
                key: "rubbish",
                dataIndex: "rubbish",
                render: (v: boolean) => (v ? "是" : "否"),
                width: 80,
            },
            {
                title: "公开",
                key: "privacy",
                dataIndex: "privacy",
                render: (v: boolean) => (v ? "否" : "是"),
                width: 80,
            },
            {
                title: "创建时间",
                key: "releaseTime",
                dataIndex: "releaseTime",
                width: 120,
            },
            {
                title: "最后更新时间",
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

    const [searchKey, setSearchKey] = useState<string>();

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
                        placeholder={getRes().searchTip}
                        onSearch={onSearch}
                        enterButton={getRes()["search"]}
                        style={{ maxWidth: "240px", float: "right" }}
                    />
                </Col>
            </Row>

            <Divider />
            <BaseTable
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
                dataApi={dataApi}
            />
        </>
    );
};

export default Index;
