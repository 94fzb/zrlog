import { getRes } from "../utils/constants";
import BaseTable, { PageDataSource } from "../common/BaseTable";
import TextArea from "antd/es/input/TextArea";
import BaseTitle from "../base/BaseTitle";

const Comment = ({ data, offline }: { data: PageDataSource; offline: boolean }) => {
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
                title: getRes()["commentUserHome"],
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
                title: getRes()["commentDate"],
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
            <BaseTitle title={getRes()["admin.comment.manage"]} />
            <BaseTable
                defaultPageSize={10}
                offline={offline}
                datasource={data}
                columns={getColumns()}
                deleteApi={getDeleteApiUri()}
            />
        </>
    );
};

export default Comment;
