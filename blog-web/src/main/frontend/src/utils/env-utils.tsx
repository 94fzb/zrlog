class EnvUtils {

    static getPreferredColorScheme() {
        if (window.matchMedia) {
            if (window.matchMedia('(prefers-color-scheme: dark)').matches) {
                return 'dark';
            }
        }
        return 'light';
    }


    static isDarkMode() {
        return EnvUtils.getPreferredColorScheme() === "dark";
    }
}

export default EnvUtils;
