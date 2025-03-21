import Title from "antd/es/typography/Title";
import Divider from "antd/es/divider";
import { getRes } from "../utils/constants";
import BaseTable, { PageDataSource } from "../common/BaseTable";
import TextArea from "antd/es/input/TextArea";

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
            <Title className="page-header" level={3}>
                {getRes()["admin.comment.manage"]}
            </Title>
            <Divider />
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
