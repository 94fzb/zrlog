import axios from "axios";
import { Spin,App} from "antd";
import {useEffect, useState} from "react";
import {getRes, resourceKey} from "./utils/constants";
import IndexLayout from "./components";

axios.defaults.baseURL = document.baseURI;

type AppState = {
    resLoaded: boolean;
};

const AppBase = () => {
    const [appState, setAppState] = useState<AppState>({resLoaded: false});

    const {modal} = App.useApp();

    const handleRes = (data: Record<string, any>) => {
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
                modal.error({
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
        <IndexLayout/>
    );
};

export default AppBase;
