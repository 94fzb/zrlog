import {EditOutlined, TagOutlined} from '@ant-design/icons';

import {Col, Row, Tag, Tooltip} from "antd";
import Search from "antd/lib/input/Search";
import Title from "antd/es/typography/Title";
import Divider from "antd/es/divider";
import './article.css'
import {getRes} from "../../utils/constants";
import {useState} from "react";
import BaseTable from "../../common/BaseTable";
import {Link} from "react-router-dom";

const Article = () => {


    const dataApi = '/api/admin/article';

    const tagForMap = (tag: string) => {
        const tagElem = (
            <Tag icon={<TagOutlined/>} closable={false} color="#108ee9">
                {tag}
            </Tag>
        );
        return (
            <span key={"all-" + tag} style={{display: 'inline-block'}}>
        {tagElem}
      </span>
        );
    };


    const getColumns = () => {
        return [
            {
                title: '标题',
                dataIndex: 'title',
                key: 'title',
                ellipsis: {
                    showTitle: false,
                },
                width: 300,
                render: (text: string, record: any) =>
                    text ? (
                        <a style={{display: "inline"}} rel="noopener noreferrer" target={"_blank"}
                           href={record.url}>
                            <Tooltip placement="topLeft" title={text}>
                                <div style={{display: "inline"}} dangerouslySetInnerHTML={{__html: text}}/>
                            </Tooltip>
                        </a>
                    ) : null,
            },
            {
                title: '标签',
                dataIndex: 'keywords',
                key: 'keywords',
                width: 150,
                render: (text: string) =>
                    text ? (text.split(',').map(tagForMap)) : null,
            },
            {
                title: '作者',
                key: 'userName',
                dataIndex: 'userName',
                width: 80,
            },
            {
                title: '分类',
                key: 'typeName',
                dataIndex: 'typeName',
                width: 100,
            },
            {
                title: '浏览量',
                key: 'click',
                dataIndex: 'click',
                width: 80,
            },
            {
                title: '草稿',
                key: 'rubbish',
                dataIndex: 'rubbish',
                render: (v: boolean) => v ? "是" : "否",
                width: 80,
            },
            {
                title: '公开',
                key: 'privacy',
                dataIndex: 'privacy',
                render: (v: boolean) => v ? "否" : "是",
                width: 80,
            },
            {
                title: '创建时间',
                key: 'releaseTime',
                dataIndex: 'releaseTime',
                width: 120,
            },
            {
                title: '最后更新时间',
                key: 'lastUpdateDate',
                dataIndex: 'lastUpdateDate',
                width: 120,
            }
        ]
    }

    const onSearch = (key: string) => {
        setSearchKey(key);
    }


    const getDeleteApiUri = () => {
        return "/api/admin/article/delete";
    }

    const [searchKey, setSearchKey] = useState<string>()


    return (
        <>
            <Title className='page-header' level={3}>{getRes()['blogManage']}</Title>
            <Divider/>
            <Row style={{paddingBottom: "10px"}}>
                <Col span={24}>
                    <Search placeholder={getRes().searchTip} onSearch={onSearch}
                            enterButton={getRes()['search']}
                            style={{maxWidth: "240px", float: "right"}}/>
                </Col>
            </Row>
            <BaseTable columns={getColumns()}
                       editBtnRender={(id) => <Link to={"./article-edit?id=" + id}>
                           <EditOutlined/>
                       </Link>}
                       deleteApi={getDeleteApiUri()}
                       searchKey={searchKey}
                       dataApi={dataApi}/>
        </>
    )
}

export default Article;
