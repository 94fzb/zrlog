import {useLocation} from "react-router";
import {parseQueryParamsToMap} from "../utils/helpers";

const Plugin = () => {
    const location = useLocation();
    const queryParamMap: Map<string, string> = parseQueryParamsToMap(location.search);
    const pluginUrl = "admin/plugins/" + (queryParamMap.get('page') ? queryParamMap.get('page') : "");
    return <iframe width="100%" style={{border: 0}} height={1200} src={document.baseURI + pluginUrl}/>;
};

export default Plugin;
