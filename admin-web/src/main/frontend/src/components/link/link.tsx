import Title from "antd/es/typography/Title";
import Divider from "antd/es/divider";
import BaseTable from "../../common/BaseTable";
import {getRes} from "../../utils/constants";
import EditLink from "./edit_link";
import AddLink from "./add_link";

const BLink = () => {

    const getColumns = () => {
        return [
            {
                title: '链接',
                dataIndex: 'url',
                key: 'url',
                render: (url: string) => (
                    <a style={{display: "inline"}} rel="noopener noreferrer" target={"_blank"} href={url}>
                        <div style={{display: "inline"}} dangerouslySetInnerHTML={{__html: url}}/>
                    </a>
                )
            },
            {
                title: '网站名称',
                key: 'linkName',
                dataIndex: 'linkName'
            },
            {
                title: '描述',
                key: 'alt',
                dataIndex: 'alt'
            },
            {
                title: '排序',
                key: 'sort',
                dataIndex: 'sort'
            }
        ]
    }

    return (
        <>
            <Title className='page-header' level={3}>{getRes()['admin.link.manage']}</Title>
            <Divider/>
            <BaseTable columns={getColumns()}
                       addBtnRender={(addSuccessCall) => {
                           return <AddLink addSuccessCall={addSuccessCall}/>
                       }}
                       editBtnRender={(_id, record, editSuccessCall) => <EditLink record={record}
                                                                                  editSuccessCall={editSuccessCall}/>}
                       deleteApi={'/api/admin/link/delete'}
                       searchKey={""}
                       dataApi={"/api/admin/link"}/>
        </>
    )
}

export default BLink;
