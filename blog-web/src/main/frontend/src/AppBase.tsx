import axios from "axios";
import {Modal, Spin} from "antd";
import EnvUtils from "./utils/env-utils";
import {useEffect, useState} from "react";
import {getRes, resourceKey} from "./utils/constants";
import App from "./components";

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
                        <div style={{paddingTop: 20}} dangerouslySetInnerHTML={{__html: error.response.data}}/>
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
};

const AppBase = () => {
    const [appState, setAppState] = useState<AppState>({resLoaded: false});

    const handleRes = (data: Record<string, any>) => {
        EnvUtils.setTheme(EnvUtils.getPreferredColorScheme());
        data.copyrightTips =
            data.copyright + ' <a target="_blank" href="https://blog.zrlog.com/about?footer">ZrLog</a>';
        //@ts-ignore
        window[resourceKey] = JSON.stringify(data);
        setAppState({resLoaded: true});
    };

    const initRes = () => {
        const resourceData = getRes();
        if (Object.keys(resourceData).length === 0) {
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

    const loadResourceFromServer = () => {
        const resourceApi = "/api/public/installResource";
        axios
            .get(resourceApi)
            .then(({data}: { data: Record<string, any> }) => {
                handleRes(data.data);
            })
            .catch((error: any) => {
                Modal.error({
                    title: "Load error",
                    content: (
                        <div style={{paddingTop: 20}} dangerouslySetInnerHTML={{__html: error.response.data}}/>
                    ),
                    okText: "确认",
                });
            });
    };

    useEffect(() => {
        initRes();
    }, []);

    if (!appState.resLoaded) {
        return <Spin delay={1000}/>;
    }

    return (
        <App/>
    );
};

export default AppBase;
