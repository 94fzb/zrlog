import { getBackendServerUrl } from "../utils/constants";
import { FunctionComponent } from "react";
import { AdminCommonProps } from "../type";
import Offline from "../common/Offline";

type PluginData = {
    includePagePath: string;
};

const Plugin: FunctionComponent<AdminCommonProps<PluginData>> = ({ data, offline }) => {
    const pluginUrl = getBackendServerUrl() + data.includePagePath;
    if (offline) {
        return <Offline />;
    }
    return <iframe width="100%" style={{ border: 0 }} height={1200} src={pluginUrl} />;
};

export default Plugin;
