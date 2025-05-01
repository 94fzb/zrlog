import { FunctionComponent } from "react";
import Divider from "antd/es/divider";
import { UpgradeData } from "../type";

export type UpgradeContentProps = {
    data: UpgradeData;
};

const UpgradeContent: FunctionComponent<UpgradeContentProps> = ({ data }) => {
    return (
        <>
            <div
                style={{ overflowWrap: "break-word" }}
                dangerouslySetInnerHTML={{ __html: data.version ? data.version.changeLog : "" }}
            />
            {!data.onlineUpgradable && (
                <>
                    <Divider />
                    <div
                        style={{ overflowWrap: "break-word" }}
                        dangerouslySetInnerHTML={{ __html: data.disableUpgradeReason }}
                    />
                </>
            )}
        </>
    );
};

export default UpgradeContent;
