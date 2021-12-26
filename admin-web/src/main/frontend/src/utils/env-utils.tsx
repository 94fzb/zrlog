class EnvUtils {

    static getPreferredColorScheme() {
        if (window.matchMedia) {
            if (window.matchMedia('(prefers-color-scheme: dark)').matches) {
                return 'dark';
            } else {
                return 'dark';
            }
        }
        return 'light';
    }

    static setTheme(theme: string) {
        if (theme === 'light') {
            document.body.classList.remove('dark');
            document.body.classList.add('light');
        } else {
            document.body.classList.remove('light');
            document.body.classList.add('dark');
        }
    }

    static isDarkMode() {
        return document.body.classList.contains("dark");
    }
}

export default EnvUtils;
