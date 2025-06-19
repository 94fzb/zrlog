import AppBase, {useAxiosBaseInstance} from "AppBase";
import {AppInitState, AppState, refreshPathInfo, ssData} from "index";
import {FunctionComponent, useEffect, useState} from "react";
import {getColorPrimary, getRes, isStaticPage, setRes} from "./utils/constants";
import {message} from "antd";
import EnvUtils, {isOffline} from "./utils/env-utils";
import Init from "./components/init";
import Spin from "antd/es/spin";
import UnknownErrorPage from "./components/unknown-error-page";

type AppInitProps = {
    onInit: (
        data: AppState
    ) => void;
}

const AppInit: FunctionComponent<AppInitProps> = ({onInit}) => {

    const [appState, setAppState] = useState<AppInitState>({
        resLoaded: false,
        resLoadErrorMsg: "",
        offline: isOffline(),
        requiredBackendServerUrl: false,
    });

    const [messageApi, contextHolder] = message.useMessage({maxCount: 3});

    const axiosInstance = useAxiosBaseInstance();

    const loadResourceFromServer = (first: boolean) => {
        const resourceApi = "/api/public/adminResource";
        axiosInstance.get(resourceApi)
            .then(({data}: { data: Record<string, any> }) => {
                handleRes(data.data);
            })
            .catch((e) => {
                const errorMsg = "Request " + axiosInstance.defaults.baseURL + resourceApi.substring(1) + " error -> " + e.message;
                if (!first) {
                    messageApi.error(errorMsg)
                }
                if (isStaticPage()) {
                    setAppState((prevState) => {
                        return {
                            ...prevState,
                            dark: document.body.className.includes("dark"),
                            resLoaded: true,
                            lang: "zh_CN",
                            requiredBackendServerUrl: true,
                            offline: prevState.offline,
                            colorPrimary: getColorPrimary(),
                        };
                    });
                    return;
                }
                setAppState((prevState) => {
                    return {
                        ...prevState,
                        dark: document.body.className.includes("dark"),
                        resLoadErrorMsg: errorMsg,
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
        onInit({
            lang: data.lang,
            dark: EnvUtils.isDarkMode(),
            colorPrimary: getColorPrimary(),
        })
        setAppState((prevState) => {
            return {
                ...prevState,
                lang: data.lang,
                offline: prevState.offline,
                dark: EnvUtils.isDarkMode(),
                resLoadErrorMsg: "",
                resLoaded: true,
                requiredBackendServerUrl: false,
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
                loadResourceFromServer(true);
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
        refreshPathInfo();
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
        if (appState.requiredBackendServerUrl) {
            return <Init onSubmit={() => {
                loadResourceFromServer(false);
            }}/>
        }
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
    return <>
        {contextHolder}
        {getBody()}
    </>
}

export default AppInit;