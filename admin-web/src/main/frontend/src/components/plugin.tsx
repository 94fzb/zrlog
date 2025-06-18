import {useLocation} from "react-router";
import {parseQueryParamsToMap} from "../utils/helpers";
import {getBackendServerUrl} from "../utils/constants";

const Plugin = () => {
    const location = useLocation();
    const queryParamMap: Map<string, string> = parseQueryParamsToMap(location.search);
    const pluginUrl = getBackendServerUrl() + "admin/plugins/" + (queryParamMap.get('page') ? queryParamMap.get('page') : "");
    return <iframe width="100%" style={{border: 0}} height={1200} src={pluginUrl}/>;
};

export default Plugin;
