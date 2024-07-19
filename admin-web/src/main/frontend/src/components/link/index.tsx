import Title from "antd/es/typography/Title";
import Divider from "antd/es/divider";
import BaseTable, { PageDataSource } from "../../common/BaseTable";
import { getRes } from "../../utils/constants";
import EditLink from "./edit_link";
import AddLink from "./add_link";

const BLink = ({ data, offline }: { data: PageDataSource; offline: boolean }) => {
    const getColumns = () => {
        return [
            {
                title: getRes()["admin.link.manage"],
                dataIndex: "url",
                key: "url",
                width: 240,
                render: (url: string) => (
                    <a style={{ display: "inline" }} rel="noopener noreferrer" target={"_blank"} href={url}>
                        {url}
                    </a>
                ),
            },
            {
                title: "网站名称",
                key: "linkName",
                dataIndex: "linkName",
                width: 240,
            },
            {
                title: "描述",
                key: "alt",
                dataIndex: "alt",
                width: 240,
            },
            {
                title: getRes()["order"],
                key: "sort",
                dataIndex: "sort",
                width: 60,
            },
        ];
    };

    return (
        <>
            <Title className="page-header" level={3}>
                {getRes()["admin.link.manage"]}
            </Title>
            <Divider />
            <BaseTable
                offline={offline}
                hideId={true}
                columns={getColumns()}
                addBtnRender={(addSuccessCall) => {
                    return <AddLink offline={offline} addSuccessCall={addSuccessCall} />;
                }}
                datasource={data}
                editBtnRender={(_id, record, editSuccessCall) => (
                    <EditLink offline={offline} record={record} editSuccessCall={editSuccessCall} />
                )}
                deleteApi={"/api/admin/link/delete"}
            />
        </>
    );
};

export default BLink;
