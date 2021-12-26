import EnvUtils from "./utils/env-utils";
import AsyncDarkApp from "./DarkApp";
import AsyncLightApp from "./LightApp";

const ThemeIndex = () => {
    return EnvUtils.isDarkMode() ? <AsyncDarkApp/> : <AsyncLightApp/>;
}

export default ThemeIndex;
