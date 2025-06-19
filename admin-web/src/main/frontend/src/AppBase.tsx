import {Route, Routes, useNavigate} from "react-router-dom";
import {lazy, Suspense} from "react";
import {App, Spin} from "antd";
import axios, {AxiosError, AxiosInstance} from "axios";
import {API_VERSION_PATH} from "./components/upgrade";
import ErrorBoundary from "./common/ErrorBoundary";
import {getBackendServerUrl, getRealRouteUrl, isStaticPage} from "./utils/constants";

const AsyncLogin = lazy(() => import("components/login"));
const AsyncAdminDashboardRouter = lazy(() => import("AdminDashboardRouter"));

const errorCountMap = new Map<number, number>();

export const useAxiosBaseInstance = (getContainer?: () => HTMLElement): AxiosInstance => {
    const {modal, message} = App.useApp();

    const navigate = useNavigate();

    const axiosInstance = axios.create();
    if (isStaticPage()) {
        axiosInstance.defaults.withCredentials = true;
    }

    const commonAxiosErrorHandle = (
        error: any,
    ): Promise<any> => {
        //ignore upgrade api error
        if ((error as AxiosError) && error.config && error.config.url) {
            if (error.config.url.includes(API_VERSION_PATH)) {
                return Promise.reject(error.message);
            }
        }
        if (error && error.response) {
            if (error.response.status) {

                modal.error({
                    title: "服务异常[" + error.response.status + "]",
                    content: <div style={{paddingTop: 20, overflow: "auto"}}
                                  dangerouslySetInnerHTML={{__html: error.response.data}}/>,
                    getContainer: getContainer ? getContainer() : undefined,
                });
                return Promise.reject(error.response);
            }
        } else {
            if ((error as AxiosError) && error.config && error.config.url) {
                if (navigator.onLine) {
                    modal.error({
                        title: "请求 " + error.config.url + " 错误",
                        content: JSON.stringify(error),
                        getContainer: getContainer ? getContainer() : undefined,
                    });
                } else {
                    message.error("请求 " + error.config.url + " " + error.toString() + " network offline");
                }
            }
        }
        return Promise.reject(error);
    };

    axiosInstance.defaults.baseURL = getBackendServerUrl();

    axiosInstance.interceptors.response.use(
        (response) => {
            const errorCode = response.data.error;
            if (errorCode === 9001) {
                let count = errorCountMap.get(errorCode);
                if (count === null || count === undefined) {
                    count = 0;
                }
                if (count === 0) {
                    errorCountMap.set(errorCode, count + 1);
                    modal.error({
                        title: response.data.error,
                        content: response.data.message,
                        getContainer: getContainer ? getContainer() : undefined,
                        onOk: () => {
                            errorCountMap.set(errorCode, 0);
                        }
                    });
                }

                if (isStaticPage()) {
                    if (!window.location.search.includes("redirectFrom")) {
                        navigate(getRealRouteUrl(`/login`) + `?redirectFrom=${encodeURI(window.location.pathname.split(".html")[0])}${encodeURI(window.location.search)}`, {replace: true});
                    }
                }
                return Promise.reject(response.data);
            }
            return response;
        },
        (error) => {
            return commonAxiosErrorHandle(error);
        }
    );
    return axiosInstance;
};

const AppBase = ({offline}: { offline: boolean }) => {

    return (
        <>
            <Routes>
                {
                    ["login", "login.html"].map(e => {
                        return <Route
                            key={e}
                            path={e}
                            element={
                                <ErrorBoundary>
                                    <Suspense fallback={<Spin spinning={true} fullscreen delay={1000}/>}>
                                        <AsyncLogin offline={offline}/>
                                    </Suspense>
                                </ErrorBoundary>
                            }
                        />
                    })
                }

                <Route
                    path={"logout"}
                    element={
                        <ErrorBoundary>
                            <Suspense fallback={<Spin spinning={true} fullscreen delay={1000}/>}>
                                <AsyncLogin offline={offline}/>
                            </Suspense>
                        </ErrorBoundary>
                    }
                />
                <Route
                    path={"*"}
                    element={
                        <ErrorBoundary>
                            <Suspense fallback={<Spin spinning={true} fullscreen delay={1000}/>}>
                                <AsyncAdminDashboardRouter offline={offline}/>
                            </Suspense>
                        </ErrorBoundary>
                    }
                />
            </Routes>
        </>
    );
};

export default AppBase;
