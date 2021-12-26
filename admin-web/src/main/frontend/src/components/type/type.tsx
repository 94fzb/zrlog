import Title from "antd/es/typography/Title";
import Divider from "antd/es/divider";
import {getRes} from "../../utils/constants";
import BaseTable from "../../common/BaseTable";
import AddType from "./add_type";
import EditType from "./edit_type";

const Type = () => {

    const getColumns = () => {
        return [
            {
                title: '分类名称',
                dataIndex: 'typeName',
                key: 'typeName',
            },
            {
                title: '别名',
                dataIndex: 'alias',
                key: 'alias',
            },
            {
                title: '介绍',
                key: 'remark',
                dataIndex: 'remark'
            }
        ]
    }

    return (
        <>
            <Title className='page-header' level={3}>{getRes()['admin.type.manage']}</Title>
            <Divider/>
            <BaseTable columns={getColumns()}
                       addBtnRender={(addSuccessCall) => {
                           return <AddType addSuccessCall={addSuccessCall}/>
                       }}
                       editBtnRender={(_id, record, editSuccessCall) => <EditType record={record}
                                                                                  editSuccessCall={editSuccessCall}/>}
                       deleteApi={'/api/admin/type/delete'}
                       searchKey={""}
                       dataApi={"/api/admin/type"}/>
        </>)
}

export default Type;
