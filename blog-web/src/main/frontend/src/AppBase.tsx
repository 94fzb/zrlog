import axios from "axios";
import {Spin, App} from "antd";
import {useEffect, useState} from "react";
import {getRes, resourceKey} from "./utils/constants";
import IndexLayout from "./components";

axios.defaults.baseURL = document.baseURI;

type AppState = {
    resLoaded: boolean;
};

const jsonStr = document.getElementById("resourceInfo")?.textContent;
let _resLoadedBySsr = false;

const setRes = (data: Record<string, unknown>) => {
    data.copyrightTips =
        data.copyright + ' <a target="_blank" href="https://blog.zrlog.com/about.html?footer">ZrLog</a>';
    //@ts-ignore
    window[resourceKey] = JSON.stringify(data);
}

if (jsonStr && jsonStr !== "") {
    setRes(JSON.parse(jsonStr));
    _resLoadedBySsr = true;
}

const AppBase = () => {
    const [appState, setAppState] = useState<AppState>({resLoaded: _resLoadedBySsr});

    const {modal} = App.useApp();

    const loadResourceFromServer = () => {
        const resourceApi = "/api/public/installResource";
        axios
            .get(resourceApi)
            .then(({data}: { data: Record<string, any> }) => {
                setRes(data.data)
                setAppState({resLoaded: true})
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

    const initRes = () => {
        const resourceData = getRes();
        if (Object.keys(resourceData).length !== 0) {
            return;
        }
        loadResourceFromServer();
    };


    useEffect(() => {
        initRes();
    }, []);

    if (!appState.resLoaded) {
        return <Spin fullscreen delay={1000}/>;
    }

    return (
        <IndexLayout/>
    );
};

export default AppBase;
