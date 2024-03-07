import * as serviceWorker from "./serviceWorker";
import zh_CN from "antd/es/locale/zh_CN";
import { App, ConfigProvider, Spin, theme } from "antd";
import { BrowserRouter } from "react-router-dom";
import EnvUtils from "./utils/env-utils";
import AppBase from "./AppBase";
import { legacyLogicalPropertiesTransformer, StyleProvider } from "@ant-design/cssinjs";
import { useEffect, useState } from "react";
import { createRoot } from "react-dom/client";
import { getColorPrimary, getRes, setRes } from "./utils/constants";

import axios from "axios";
import { BasicUserInfo } from "./type";

const url = new URL(document.baseURI);
export const basePath = url.pathname + "admin/";
export const apiBasePath = url.pathname + "api/admin/";

axios.defaults.baseURL = document.baseURI;
const { darkAlgorithm, defaultAlgorithm } = theme;

type AppState = {
    resLoaded: boolean;
    dark: boolean;
    colorPrimary: string;
};

type SsDate = {
    pageData?: any;
    resourceInfo?: Record<string, never>;
    user?: BasicUserInfo;
    key?: string;
};

export let ssData: SsDate | undefined;
const ssDataStr = document.getElementById("__SS_DATA__")?.innerText;
// @ts-ignore
if (ssDataStr?.length > 0) {
    ssData = JSON.parse(ssDataStr as string);
} else {
    ssData = {};
}
const Index = () => {
    const [appState, setAppState] = useState<AppState>({
        resLoaded: false,
        dark: EnvUtils.isDarkMode(),
        colorPrimary: getColorPrimary(),
    });

    const loadResourceFromServer = () => {
        const resourceApi = "/api/public/blogResource";
        axios.get(resourceApi).then(({ data }: { data: Record<string, any> }) => {
            handleRes(data.data);
        });
    };
    const handleRes = (data: Record<string, never>) => {
        // @ts-ignore
        data.copyrightTips =
            data.copyright + ' <a target="_blank" href="https://blog.zrlog.com/about?footer">ZrLog</a>';
        setRes(data);
        setAppState({ dark: EnvUtils.isDarkMode(), resLoaded: true, colorPrimary: getColorPrimary() });
    };

    const initRes = () => {
        const resourceData = getRes();
        if (resourceData === null || Object.keys(resourceData).length === 0) {
            if (ssData && ssData.resourceInfo) {
                handleRes(ssData.resourceInfo);
            } else {
                loadResourceFromServer();
            }
        } else {
            handleRes(resourceData);
        }
    };

    useEffect(() => {
        initRes();
    }, []);

    return (
        <ConfigProvider
            locale={zh_CN}
            theme={{
                algorithm: appState.dark ? darkAlgorithm : defaultAlgorithm,
                token: {
                    colorPrimary: appState.colorPrimary,
                },
            }}
            table={{
                style: {
                    whiteSpace: "nowrap",
                },
            }}
            divider={{
                style: {
                    margin: "16px 0",
                },
            }}
        >
            <App>
                <StyleProvider transformers={[legacyLogicalPropertiesTransformer]}>
                    <BrowserRouter basename={basePath}>
                        {appState.resLoaded ? (
                            <AppBase />
                        ) : (
                            <Spin delay={1000} style={{ maxHeight: "100vh" }}>
                                <div style={{ height: "100vh", width: "100vw" }}></div>
                            </Spin>
                        )}
                    </BrowserRouter>
                </StyleProvider>
            </App>
        </ConfigProvider>
    );
};

const container = document.getElementById("app");
const root = createRoot(container!); // createRoot(container!) if you use TypeScript
root.render(<Index />);
// If you want your app to work offline and load faster, you can change
// unregister() to register() below. Note this comes with some pitfalls.
// Learn more about service workers: https://bit.ly/CRA-PWA
serviceWorker.unregister();
