import Title from "antd/es/typography/Title";
import Divider from "antd/es/divider";
import { getRes } from "../../utils/constants";
import BaseTable, { PageDataSource } from "../../common/BaseTable";
import AddType from "./add_type";
import EditType from "./edit_type";

const Type = ({ data, offline }: { data: PageDataSource; offline: boolean }) => {
    const getColumns = () => {
        return [
            {
                title: getRes()["admin.type.manage"],
                dataIndex: "typeName",
                key: "typeName",
                width: 240,
                render: (e: string, r: Record<string, string>) => {
                    return (
                        <a rel="noopener noreferrer" target={"_blank"} href={r.url}>
                            {e}
                        </a>
                    );
                },
            },
            {
                title: getRes()["alias"],
                dataIndex: "alias",
                key: "alias",
                width: 120,
            },
            {
                title: "简介",
                key: "remark",
                dataIndex: "remark",
                width: 240,
                render: (e: string) => {
                    return <span dangerouslySetInnerHTML={{ __html: e }} />;
                },
            },
            {
                title: "文章数量",
                dataIndex: "amount",
                key: "amount",
                width: 80,
            },
        ];
    };

    return (
        <>
            <Title className="page-header" level={3}>
                {getRes()["admin.type.manage"]}
            </Title>
            <Divider />
            <BaseTable
                offline={offline}
                hideId={true}
                columns={getColumns()}
                addBtnRender={(addSuccessCall) => {
                    return <AddType offline={offline} addSuccessCall={addSuccessCall} />;
                }}
                editBtnRender={(_id, record, editSuccessCall) => (
                    <EditType offline={offline} record={record} editSuccessCall={editSuccessCall} />
                )}
                datasource={data}
                deleteApi={"/api/admin/type/delete"}
            />
        </>
    );
};

export default Type;
