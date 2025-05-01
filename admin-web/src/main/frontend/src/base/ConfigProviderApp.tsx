import { App, ConfigProvider, theme } from "antd";
import { useEffect, useState } from "react";
import EnvUtils, { isOffline } from "../utils/env-utils";
import { getColorPrimary } from "../utils/constants";
import { getContextPath } from "../utils/helpers";
import zh_CN from "antd/es/locale/zh_CN";
import en_US from "antd/es/locale/en_US";
import { legacyLogicalPropertiesTransformer, StyleProvider } from "@ant-design/cssinjs";
import { BrowserRouter, Route, Routes } from "react-router-dom";
import AppInit from "./AppInit";
import { AppState } from "../type";

const { darkAlgorithm, defaultAlgorithm } = theme;

const ConfigProviderApp = () => {
    const [appState, setState] = useState<AppState>({
        lang: document.documentElement.lang ? document.documentElement.lang : "zh_CN",
        dark: EnvUtils.isDarkMode(),
        colorPrimary: getColorPrimary(),
        offline: isOffline(),
    });

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

    const basePath = getContextPath() + "admin";

    useEffect(() => {
        window.document.body.setAttribute("class", appState.dark ? "dark" : "light");
    }, [appState.dark]);

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
                        padding: 8,
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
                            <Route
                                path={"/*"}
                                element={
                                    <AppInit
                                        lang={appState.lang}
                                        offline={appState.offline}
                                        onInit={(newState) => {
                                            setState((prevState) => {
                                                return {
                                                    ...prevState,
                                                    ...newState,
                                                };
                                            });
                                        }}
                                    />
                                }
                            />
                        </Routes>
                    </BrowserRouter>
                </StyleProvider>
            </App>
        </ConfigProvider>
    );
};

export default ConfigProviderApp;
