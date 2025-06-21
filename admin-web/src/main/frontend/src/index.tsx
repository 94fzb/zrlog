import * as serviceWorker from "./serviceWorker";
import zh_CN from "antd/es/locale/zh_CN";
import en_US from "antd/es/locale/en_US";
import {App, ConfigProvider, theme} from "antd";
import {BrowserRouter, Route, Routes} from "react-router-dom";
import {legacyLogicalPropertiesTransformer, StyleProvider} from "@ant-design/cssinjs";
import {useEffect, useState} from "react";
import {createRoot} from "react-dom/client";
import {getColorPrimary, getRes} from "./utils/constants";

import {BasicUserInfo} from "./type";
import AppInit from "./AppInit";
import EnvUtils, {isOffline} from "./utils/env-utils";
import {getCacheByKey} from "./cache";
import {getContextPath} from "./utils/helpers";

const {darkAlgorithm, defaultAlgorithm} = theme;

export type AppState = {
    dark: boolean;
    lang: string;
    colorPrimary: string;
    offline: boolean;
};

type SsDate = {
    data?: any;
    resourceInfo?: Record<string, never>;
    user: BasicUserInfo | null;
    key: string;
    pageBuildId: string;
};

export let ssData: SsDate = {key: "", data: undefined, resourceInfo: undefined, user: null, pageBuildId: ""};
//@ts-ignore
window['__SS_DATA'] = ssData;
export const ssKeyStorageKey = "ss_key";


export const getItems_per_page = () => {
    if (getRes()["lang"] === "zh_CN") {
        // @ts-ignore
        return zh_CN.Pagination.items_per_page;
    }
    // @ts-ignore
    return en_US.Pagination.items_per_page;
};

const Index = () => {
    const [appState, setState] = useState<AppState>({
        lang: document.documentElement.lang ? document.documentElement.lang : "zh_CN",
        dark: EnvUtils.isDarkMode(),
        colorPrimary: getColorPrimary(),
        offline: isOffline(),
    });

    const initSsData = () => {
        const ssDataStr = document.getElementById("__SS_DATA__")?.innerText;
        // @ts-ignore
        if (ssDataStr?.length > 0) {
            ssData = JSON.parse(ssDataStr as string);
        }
        const ssKey = localStorage.getItem(ssKeyStorageKey);
        if (ssKey) {
            ssData.key = ssKey;
            if (ssData.user === undefined || ssData.user === null) {
                ssData.user = getCacheByKey("/user");
            }
        }
    }

    initSsData();

    const updateOnlineStatus = () => {
        setState((prevState) => {
            return {
                ...prevState,
                offline: isOffline(),
            };
        });
    };


    useEffect(() => {
        window.addEventListener("online", updateOnlineStatus);
        window.addEventListener("offline", updateOnlineStatus);
        // Cleanup event listeners on component unmount
        return () => {
            window.removeEventListener("online", updateOnlineStatus);
            window.removeEventListener("offline", updateOnlineStatus);
        };
    }, []);

    const basePath = getContextPath() + "admin/"

    return (
        <ConfigProvider
            key={appState.lang + "_" + appState.dark + "_" + appState.colorPrimary}
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
                        <Routes>
                            <Route path={"/*"} element={<AppInit lang={appState.lang} offline={appState.offline}
                                                                 onInit={(newState) => {
                                                                     setState((prevState) => {
                                                                         return {
                                                                             ...prevState,
                                                                             ...newState
                                                                         }
                                                                     })
                                                                 }}/>}/>
                        </Routes>
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
