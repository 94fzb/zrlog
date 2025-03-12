import Title from "antd/es/typography/Title";
import Divider from "antd/es/divider";
import BaseTable, { PageDataSource } from "../../common/BaseTable";
import { getRes } from "../../utils/constants";
import AddNav from "./add_nav";
import EditNav from "./edit_nav";

const Nav = ({ data, offline }: { data: PageDataSource; offline: boolean }) => {
    const getColumns = () => {
        return [
            {
                title: getRes()["admin.nav.manage"],
                dataIndex: "url",
                width: 240,
                key: "url",
                render: (url: string, r: Record<string, any>) => (
                    <a style={{ display: "inline" }} rel="noopener noreferrer" target={"_blank"} href={r["jumpUrl"]}>
                        {url}
                    </a>
                ),
            },
            {
                title: getRes()["admin.nav.name"],
                dataIndex: "navName",
                key: "navName",
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
                {getRes()["admin.nav.manage"]}
            </Title>
            <Divider />
            <BaseTable
                defaultPageSize={10}
                offline={offline}
                hideId={true}
                columns={getColumns()}
                addBtnRender={(addSuccessCall) => {
                    return <AddNav offline={offline} addSuccessCall={addSuccessCall} />;
                }}
                editBtnRender={(_id, record, editSuccessCall) => (
                    <EditNav offline={offline} record={record} editSuccessCall={editSuccessCall} />
                )}
                datasource={data}
                deleteApi={"/api/admin/nav/delete"}
            />
        </>
    );
};

export default Nav;
