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

// Function to check if the page is running as a PWA
export const isPWA = (): boolean => {
    //@ts-ignore
    if (window.navigator.standalone) {
        return true;
    }
    return window.matchMedia("(display-mode: standalone)").matches;
};

export const isOffline = () => {
    return !navigator.onLine;
};

export default EnvUtils;
