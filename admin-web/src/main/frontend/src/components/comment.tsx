import Title from "antd/es/typography/Title";
import Divider from "antd/es/divider";
import { getRes } from "../utils/constants";
import BaseTable, { PageDataSource } from "../common/BaseTable";
import TextArea from "antd/es/input/TextArea";

const Comment = ({ data }: { data: PageDataSource }) => {
    const getColumns = () => {
        return [
            {
                title: getRes()["content"],
                dataIndex: "userComment",
                key: "userComment",
                width: 600,
                render: (text: string) => (
                    <TextArea
                        autoSize={{ minRows: 1, maxRows: 6 }}
                        style={{ border: "none", minWidth: 300 }}
                        readOnly={true}
                        value={text}
                    />
                ),
            },
            {
                title: getRes()["nickName"],
                dataIndex: "userName",
                key: "userName",
            },
            {
                title: "评论者主页",
                key: "userHome",
                dataIndex: "userHome",
            },
            {
                title: "IP",
                key: "userIp",
                dataIndex: "userIp",
            },
            {
                title: getRes()["email"],
                key: "userMail",
                dataIndex: "userMail",
            },
            {
                title: "评论时间",
                key: "commTime",
                dataIndex: "commTime",
            },
        ];
    };

    const getDeleteApiUri = () => {
        return "/api/admin/comment/delete";
    };

    return (
        <>
            <Title className="page-header" level={3}>
                {getRes()["admin.comment.manage"]}
            </Title>
            <Divider />
            <BaseTable datasource={data} columns={getColumns()} deleteApi={getDeleteApiUri()} />
        </>
    );
};

export default Comment;
