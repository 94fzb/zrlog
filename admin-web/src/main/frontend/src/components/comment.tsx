import Title from "antd/es/typography/Title";
import Divider from "antd/es/divider";
import {getRes} from "../utils/constants";
import BaseTable from "../common/BaseTable";


const Comment = () => {

    const getColumns = () => {
        return [
            {
                title: '内容',
                dataIndex: 'userComment',
                key: 'userComment',
                width: "200px"
            },
            {
                title: '昵称',
                dataIndex: 'userName',
                key: 'userName',
            },
            {
                title: '评论者主页',
                key: 'userHome',
                dataIndex: 'userHome'
            },
            {
                title: 'IP',
                key: 'ip',
                dataIndex: 'ip'
            },
            {
                title: '邮箱',
                key: 'userMail',
                dataIndex: 'userMail'
            },
            {
                title: '评论时间',
                key: 'commTime',
                dataIndex: 'commTime'
            }
        ]
    }

    const getDeleteApiUri = () => {
        return "/api/admin/comment/delete"
    }

    return (
        <>
            <Title className='page-header' level={3}>{getRes()['admin.comment.manage']}</Title>
            <Divider/>
            <BaseTable columns={getColumns()}
                       deleteApi={getDeleteApiUri()}
                       searchKey={""}
                       dataApi={"/api/admin/comment"}/>
        </>
    )
}

export default Comment;
