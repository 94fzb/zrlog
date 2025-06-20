import AppBase, {useAxiosBaseInstance} from "AppBase";
import {AppState, refreshPathInfo, ssData} from "index";
import {FunctionComponent, useEffect, useState} from "react";
import {
    getBackendServerUrl,
    getColorPrimary,
    getRes,
    isStaticPage,
    setBackendServerUrl,
    setRes
} from "./utils/constants";
import EnvUtils, {isOffline} from "./utils/env-utils";
import Init from "./components/init";
import Spin from "antd/es/spin";
import UnknownErrorPage from "./components/unknown-error-page";

type AppInitProps = {
    offline: boolean;
    lang: string;
    onInit: (
        data: AppState
    ) => void;
}

type AppInitState = {
    resLoaded: boolean;
    resLoadErrorMsg: string;
    requiredBackendServerUrl: boolean;
};


const AppInit: FunctionComponent<AppInitProps> = ({onInit, lang, offline}) => {

    const [appState, setAppState] = useState<AppInitState>({
        resLoaded: false,
        resLoadErrorMsg: "",
        requiredBackendServerUrl: isStaticPage() && getBackendServerUrl() === "/",
    });


    const axiosInstance = useAxiosBaseInstance();

    const loadResourceFromServer = (baseUrl: string) => {
        const resourceApi = "/api/public/adminResource";
        if (baseUrl.length > 0) {
            axiosInstance.defaults.baseURL = baseUrl;
        }
        axiosInstance.get(resourceApi)
            .then(({data}: { data: Record<string, any> }) => {
                if (baseUrl.length > 0) {
                    setBackendServerUrl(baseUrl);
                }
                handleRes(data.data);
            })
            .catch((e) => {
                const errorMsg = "Request " + axiosInstance.defaults.baseURL + resourceApi.substring(1) + " error -> " + e.message;
                //console.info(errorMsg);
                if (isStaticPage()) {
                    setAppState((prevState) => {
                        return {
                            ...prevState,
                            resLoaded: true,
                            requiredBackendServerUrl: true,
                        };
                    });
                    return;
                }
                setAppState((prevState) => {
                    return {
                        ...prevState,
                        resLoadErrorMsg: errorMsg,
                        resLoaded: false,
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
            offline: isOffline(),
            lang: data.lang,
            dark: EnvUtils.isDarkMode(),
            colorPrimary: getColorPrimary(),
        })
        setAppState((prevState) => {
            return {
                ...prevState,
                resLoadErrorMsg: "",
                resLoaded: true,
                requiredBackendServerUrl: false,
            };
        });
    };

    const initRes = () => {
        const resourceData = getRes();
        if (resourceData === null || Object.keys(resourceData).length === 0) {
            if (ssData && ssData.resourceInfo) {
                handleRes(ssData.resourceInfo);
            } else {
                loadResourceFromServer("");
            }
        } else {
            handleRes(resourceData);
        }
    };

    useEffect(() => {
        refreshPathInfo();
        initRes();
    }, []);


    if (appState.requiredBackendServerUrl) {
        return <Init lang={lang} onSubmit={(backendServerUrl) => {
            loadResourceFromServer(backendServerUrl);
        }}/>
    } else if (appState.resLoaded) {
        return <AppBase offline={offline}/>
    } else if (appState.resLoadErrorMsg.length === 0) {
        return <Spin delay={1000} fullscreen={true}/>;
    }
    return <UnknownErrorPage
        code={500}
        data={{message: appState.resLoadErrorMsg}}
        style={{width: "100vw", height: "100vh"}}
    />

}

export default AppInit;