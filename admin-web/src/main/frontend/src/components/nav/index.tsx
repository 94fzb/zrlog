import Title from "antd/es/typography/Title";
import Divider from "antd/es/divider";
import BaseTable, { PageDataSource } from "../../common/BaseTable";
import { getRes } from "../../utils/constants";
import AddNav from "./add_nav";
import EditNav from "./edit_nav";
import { Tooltip } from "antd";

const Nav = ({ data }: { data: PageDataSource }) => {
    const getColumns = () => {
        return [
            {
                title: "链接",
                dataIndex: "url",
                width: 240,
                key: "url",
                render: (url: string) => (
                    <a style={{ display: "inline" }} rel="noopener noreferrer" target={"_blank"} href={url}>
                        <Tooltip placement="topLeft" title={url}>
                            <div style={{ display: "inline" }} dangerouslySetInnerHTML={{ __html: url }} />
                        </Tooltip>
                    </a>
                ),
            },
            {
                title: "导航名称",
                dataIndex: "navName",
                key: "navName",
                width: 240,
            },
            {
                title: "排序",
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
                columns={getColumns()}
                addBtnRender={(addSuccessCall) => {
                    return <AddNav addSuccessCall={addSuccessCall} />;
                }}
                editBtnRender={(_id, record, editSuccessCall) => (
                    <EditNav record={record} editSuccessCall={editSuccessCall} />
                )}
                datasource={data}
                deleteApi={"/api/admin/nav/delete"}
                dataApi={"/api/admin/nav"}
            />
        </>
    );
};

export default Nav;
