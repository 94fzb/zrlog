import * as serviceWorker from "./serviceWorker";
import zh_CN from "antd/es/locale/zh_CN";
import en_US from "antd/es/locale/en_US";
import {App, ConfigProvider, Spin, theme} from "antd";
import {BrowserRouter} from "react-router-dom";
import EnvUtils, {isOffline} from "./utils/env-utils";
import AppBase from "./AppBase";
import {legacyLogicalPropertiesTransformer, StyleProvider} from "@ant-design/cssinjs";
import {useEffect, useState} from "react";
import {createRoot} from "react-dom/client";
import {getColorPrimary, getRes, setRes} from "./utils/constants";

import axios from "axios";
import {BasicUserInfo} from "./type";
import UnknownErrorPage from "./components/unknown-error-page";

const url = new URL(document.baseURI);
export const basePath = url.pathname + "admin/";
export const apiBasePath = url.pathname + "api/admin/";

axios.defaults.baseURL = document.baseURI;
const {darkAlgorithm, defaultAlgorithm} = theme;

type AppState = {
    resLoaded: boolean;
    resLoadErrorMsg: string;
    dark: boolean;
    offline: boolean;
    lang: string;
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

export const getItems_per_page = () => {
    if (getRes()["lang"] === "zh_CN") {
        // @ts-ignore
        return zh_CN.Pagination.items_per_page;
    }
    // @ts-ignore
    return en_US.Pagination.items_per_page;
};

const Index = () => {
    const [appState, setAppState] = useState<AppState>({
        resLoaded: false,
        resLoadErrorMsg: "",
        lang: "zh_CN",
        offline: isOffline(),
        dark: EnvUtils.isDarkMode(),
        colorPrimary: getColorPrimary(),
    });

    const loadResourceFromServer = () => {
        const resourceApi = "/api/public/adminResource";
        axios
            .get(resourceApi)
            .then(({data}: { data: Record<string, any> }) => {
                handleRes(data.data);
            })
            .catch((e) => {
                setAppState((prevState) => {
                    return {
                        dark: document.body.className.includes("dark"),
                        resLoadErrorMsg: "Request " + resourceApi + " error -> " + e.message,
                        resLoaded: false,
                        lang: "zh_CN",
                        offline: prevState.offline,
                        colorPrimary: getColorPrimary(),
                    };
                });
            });
    };
    const handleRes = (data: Record<string, never>) => {
        // @ts-ignore
        data.copyrightTips =
            data.copyright + ' <a target="_blank" href="https://blog.zrlog.com/about.html?footer">ZrLog</a>';
        setRes(data);
        setAppState((prevState) => {
            return {
                lang: data.lang,
                offline: prevState.offline,
                dark: EnvUtils.isDarkMode(),
                resLoadErrorMsg: "",
                resLoaded: true,
                colorPrimary: getColorPrimary(),
            };
        });
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

    const updateOnlineStatus = () => {
        setAppState((prevState) => {
            return {
                ...prevState,
                offline: isOffline(),
            };
        });
    };

    useEffect(() => {
        window.addEventListener("online", updateOnlineStatus);
        window.addEventListener("offline", updateOnlineStatus);
        initRes();
        // Cleanup event listeners on component unmount
        return () => {
            window.removeEventListener("online", updateOnlineStatus);
            window.removeEventListener("offline", updateOnlineStatus);
        };
    }, []);

    const getBody = () => {
        if (appState.resLoaded) {
            return <AppBase offline={appState.offline}/>
        } else if (appState.resLoadErrorMsg.length === 0) {
            return <Spin delay={1000} fullscreen={true}/>;
        }
        return <UnknownErrorPage
            code={500}
            data={{message: appState.resLoadErrorMsg}}
            style={{width: "100vw", height: "100vh"}}
        />
    }

    return (
        <ConfigProvider
            locale={appState.lang.startsWith("zh") ? zh_CN : en_US}
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
            card={{
                styles: {
                    body: {
                        padding: "8px",
                    },
                },
            }}
        >
            <App>
                <StyleProvider transformers={[legacyLogicalPropertiesTransformer]}>
                    <BrowserRouter
                        basename={basePath}
                        future={{
                            v7_relativeSplatPath: true,
                            v7_startTransition: true,
                        }}
                    >
                        {getBody()}
                    </BrowserRouter>
                </StyleProvider>
            </App>
        </ConfigProvider>
    );
};

const container = document.getElementById("app");
const root = createRoot(container!); // createRoot(container!) if you use TypeScript
root.render(<Index/>);
// If you want your app to work offline and load faster, you can change
// unregister() to register() below. Note this comes with some pitfalls.
// Learn more about service workers: https://bit.ly/CRA-PWA
serviceWorker.register();
