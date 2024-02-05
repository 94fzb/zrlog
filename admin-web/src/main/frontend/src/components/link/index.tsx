import Title from "antd/es/typography/Title";
import Divider from "antd/es/divider";
import BaseTable, { PageDataSource } from "../../common/BaseTable";
import { getRes } from "../../utils/constants";
import EditLink from "./edit_link";
import AddLink from "./add_link";
import { Tooltip } from "antd";

const BLink = ({ data }: { data: PageDataSource }) => {
    const getColumns = () => {
        return [
            {
                title: "链接",
                dataIndex: "url",
                key: "url",
                width: 240,
                render: (url: string) => (
                    <a style={{ display: "inline" }} rel="noopener noreferrer" target={"_blank"} href={url}>
                        <Tooltip placement="topLeft" title={url}>
                            <div style={{ display: "inline" }} dangerouslySetInnerHTML={{ __html: url }} />
                        </Tooltip>
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
                {getRes()["admin.link.manage"]}
            </Title>
            <Divider />
            <BaseTable
                columns={getColumns()}
                addBtnRender={(addSuccessCall) => {
                    return <AddLink addSuccessCall={addSuccessCall} />;
                }}
                datasource={data}
                editBtnRender={(_id, record, editSuccessCall) => (
                    <EditLink record={record} editSuccessCall={editSuccessCall} />
                )}
                deleteApi={"/api/admin/link/delete"}
                dataApi={"/api/admin/link"}
            />
        </>
    );
};

export default BLink;
