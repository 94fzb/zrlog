import BaseTable, { PageDataSource } from "../../common/BaseTable";
import { getRes } from "../../utils/constants";
import EditLink from "./edit_link";
import AddLink from "./add_link";
import BaseTitle from "../../base/BaseTitle";

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
                title: getRes()["admin.link.name"],
                key: "linkName",
                dataIndex: "linkName",
                width: 240,
                render: (e: string) => {
                    return <span dangerouslySetInnerHTML={{ __html: e }} />;
                },
            },
            {
                title: getRes()["introduction"],
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
            <BaseTitle title={getRes()["admin.link.manage"]} />
            <BaseTable
                defaultPageSize={10}
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
