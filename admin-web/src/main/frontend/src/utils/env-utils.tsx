import { getRes } from "./constants";

class EnvUtils {
    static getPreferredColorScheme() {
        if (window.matchMedia) {
            if (window.matchMedia("(prefers-color-scheme: dark)").matches) {
                return "dark";
            } else {
                return "dark";
            }
        }
        return "light";
    }

    static isDarkMode() {
        const configDarkMode = getRes()["admin_darkMode"];
        if (configDarkMode !== undefined) {
            return configDarkMode;
        }
        return this.getPreferredColorScheme() === "dark";
    }
}

export default EnvUtils;
