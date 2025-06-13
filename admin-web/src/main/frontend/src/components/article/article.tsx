import { EditOutlined, TagOutlined } from "@ant-design/icons";

import { Col, Row, Tag, Tooltip } from "antd";
import Search from "antd/lib/input/Search";
import Title from "antd/es/typography/Title";
import Divider from "antd/es/divider";
import { getColorPrimary, getRes } from "../../utils/constants";
import { useState } from "react";
import BaseTable from "../../common/BaseTable";
import { Link } from "react-router-dom";

const Article = () => {
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
                            <Tooltip placement="top" title={text}>
                                <div dangerouslySetInnerHTML={{ __html: text }} />
                            </Tooltip>
                        </a>
                    ) : null,
            },
            {
                title: "标签",
                dataIndex: "keywords",
                key: "keywords",
                width: 150,
                render: (text: string) => (text ? text.split(",").map(tagForMap) : null),
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
                columns={getColumns()}
                editBtnRender={(id) => (
                    <Link to={"/article-edit?id=" + id}>
                        <EditOutlined style={{ color: getColorPrimary() }} />
                    </Link>
                )}
                deleteApi={getDeleteApiUri()}
                searchKey={searchKey}
                dataApi={dataApi}
            />
        </>
    );
};

export default Article;
