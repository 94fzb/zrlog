import * as serviceWorker from "./serviceWorker";
import zh_CN from "antd/es/locale/zh_CN";
import {ConfigProvider, theme} from "antd";
import AppBase from "./AppBase";
import {useEffect, useState} from "react";
import EnvUtils from "./utils/env-utils";
import {BrowserRouter} from "react-router-dom";
import {legacyLogicalPropertiesTransformer, StyleProvider} from "@ant-design/cssinjs";
import {createRoot} from "react-dom/client";

const {darkAlgorithm, defaultAlgorithm} = theme;


const Index = () => {
    const [dark, setDark] = useState<boolean>(EnvUtils.isDarkMode);

    useEffect(() => {
        const mediaQuery = window.matchMedia('(prefers-color-scheme: dark)');
        const changeHandler = () => setDark(EnvUtils.isDarkMode());

        mediaQuery.addEventListener('change', changeHandler);

        // 在组件卸载时移除事件监听器
        return () => mediaQuery.removeEventListener('change', changeHandler);
    }, []);

    return (
        <ConfigProvider
            locale={zh_CN}
            theme={{
                algorithm: dark ? darkAlgorithm : defaultAlgorithm,
            }}
        >
            <BrowserRouter>
                <StyleProvider transformers={[legacyLogicalPropertiesTransformer]}>
                    <AppBase/>
                </StyleProvider>
            </BrowserRouter>
        </ConfigProvider>
    );
};

const container = document.getElementById("app");
const root = createRoot(container!); // createRoot(container!) if you use TypeScript
root.render(<Index/>);
// If you want your app to work offline and load faster, you can change
// unregister() to register() below. Note this comes with some pitfalls.
// Learn more about service workers: https://bit.ly/CRA-PWA
serviceWorker.unregister();
