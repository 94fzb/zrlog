import { FunctionComponent } from "react";
import Card from "antd/es/card";
import BaseTitle from "../../base/BaseTitle";

type VersionProps = {
    data: VersionResponse;
};

type VersionResponse = {
    version: string;
    changelog: string;
};

const Version: FunctionComponent<VersionProps> = ({ data }) => {
    return (
        <>
            <BaseTitle title={data.version} />
            <Card title={""} size={"small"}>
                <div dangerouslySetInnerHTML={{ __html: data.changelog }}></div>
            </Card>
        </>
    );
};
export default Version;
