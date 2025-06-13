import * as serviceWorker from "./serviceWorker";
import zh_CN from "antd/es/locale/zh_CN";
import { ConfigProvider, Spin, theme } from "antd";
import { BrowserRouter } from "react-router-dom";
import EnvUtils from "./utils/env-utils";
import AppBase from "./AppBase";

const url = new URL(document.baseURI);
export const basePath = url.pathname + "admin/";
export const apiBasePath = url.pathname + "api/admin/";
const { darkAlgorithm, defaultAlgorithm } = theme;
import { legacyLogicalPropertiesTransformer, StyleProvider } from "@ant-design/cssinjs";
import { useEffect, useState } from "react";
import { createRoot } from "react-dom/client";
import { getColorPrimary } from "./utils/constants";
import { getRes, setRes } from "./utils/constants";

import axios from "axios";
import { App, Modal } from "antd";

axios.defaults.baseURL = document.baseURI;
axios.interceptors.response.use(
    (response) => {
        if (response.data.error === 9001) {
            Modal.warn({
                title: "会话过期",
                content: response.data.message,
                okText: "确认",
            });
            return Promise.reject(response.data);
        }
        return response;
    },
    (error) => {
        if (error && error.response) {
            if (error.response.status === 502) {
                Modal.error({
                    title: "服务未启动",
                    content: (
                        <div style={{ paddingTop: 20 }} dangerouslySetInnerHTML={{ __html: error.response.data }} />
                    ),
                    okText: "确认",
                });
                return Promise.reject(error.response);
            }
        }
        return Promise.reject(error);
    }
);

type AppState = {
    resLoaded: boolean;
    dark: boolean;
    colorPrimary: string;
};

const Index = () => {
    const [appState, setAppState] = useState<AppState>({
        resLoaded: false,
        dark: EnvUtils.isDarkMode(),
        colorPrimary: getColorPrimary(),
    });

    const loadResourceFromServer = () => {
        const resourceApi = "/api/public/blogResource";
        axios
            .get(resourceApi)
            .then(({ data }: { data: Record<string, any> }) => {
                handleRes(data.data);
            })
            .catch((error: any) => {
                Modal.error({
                    title: "加载 " + resourceApi + " 错误",
                    content: (
                        <div style={{ paddingTop: 20 }} dangerouslySetInnerHTML={{ __html: error.response.data }} />
                    ),
                    okText: "确认",
                });
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
            const jsonStr = document.getElementById("resourceInfo")?.textContent;
            if (jsonStr && jsonStr !== "") {
                handleRes(JSON.parse(jsonStr));
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
        >
            <BrowserRouter basename={basePath}>
                <App>
                    <StyleProvider transformers={[legacyLogicalPropertiesTransformer]}>
                        {appState.resLoaded ? (
                            <AppBase />
                        ) : (
                            <Spin delay={1000} style={{ maxHeight: "100vh" }}>
                                <div style={{ height: "100vh", width: "100vw" }}></div>
                            </Spin>
                        )}
                    </StyleProvider>
                </App>
            </BrowserRouter>
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
