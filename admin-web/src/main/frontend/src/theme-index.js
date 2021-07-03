import {BaseResourceComponent} from "./components/base-resource-component";
import EnvUtils from "./utils/env-utils";
import AsyncDarkApp from "./DarkApp";
import AsyncLightApp from "./LightApp";
import React from "react";

class ThemeIndex extends BaseResourceComponent {
    render() {
        return EnvUtils.isDarkMode() ? <AsyncDarkApp/> : <AsyncLightApp/>;
    }
}

export default ThemeIndex;
