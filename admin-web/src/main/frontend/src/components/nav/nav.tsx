import Title from "antd/es/typography/Title";
import Divider from "antd/es/divider";
import BaseTable from "../../common/BaseTable";
import {getRes} from "../../utils/constants";
import AddNav from "./add_nav";
import EditNav from "./edit_nav";

const Nav = () => {

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
                title: '导航名称',
                dataIndex: 'navName',
                key: 'navName',
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
            <Title className='page-header' level={3}>{getRes()['admin.nav.manage']}</Title>
            <Divider/>
            <BaseTable columns={getColumns()}
                       addBtnRender={(addSuccessCall) => {
                           return <AddNav addSuccessCall={addSuccessCall}/>
                       }}
                       editBtnRender={(_id, record, editSuccessCall) => <EditNav record={record}
                                                                                 editSuccessCall={editSuccessCall}/>}
                       deleteApi={'/api/admin/nav/delete'}
                       searchKey={""}
                       dataApi={"/api/admin/nav"}/>
        </>
    )
}

export default Nav;
