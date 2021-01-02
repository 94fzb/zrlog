class EnvUtils {

    static getPreferredColorScheme() {
        if (window.matchMedia) {
            if (window.matchMedia('(prefers-color-scheme: dark)').matches) {
                return 'dark';
            } else {
                return 'light';
            }
        }
        return 'light';
    }
}

export default EnvUtils;