import { Route, Routes } from "react-router-dom";
import { lazy } from "react";
import { Suspense } from "react";
import { App, Spin } from "antd";
import axios, { AxiosError } from "axios";
import { API_VERSION_PATH } from "./components/upgrade";
import ErrorBoundary from "./common/ErrorBoundary";

const AsyncLogin = lazy(() => import("components/login"));
const AsyncAdminDashboardRouter = lazy(() => import("AdminDashboardRouter"));

const AppBase = () => {
    const { modal } = App.useApp();

    const initAxios = () => {
        //@ts-ignore
        if (window.axiosConfiged) {
            return;
        }
        //@ts-ignore
        window.axiosConfiged = true;
        axios.interceptors.response.use(
            (response) => {
                if (response.data.error === 9001) {
                    modal.error({
                        title: response.data.error,
                        content: response.data.message,
                    });
                    return Promise.reject(response.data);
                }
                return response;
            },
            (error) => {
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
                            content: (
                                <div
                                    style={{ paddingTop: 20 }}
                                    dangerouslySetInnerHTML={{ __html: error.response.data }}
                                />
                            ),
                        });
                        return Promise.reject(error.response);
                    }
                } else {
                    if ((error as AxiosError) && error.config) {
                        modal.error({
                            title: "请求 " + error.config.url + " 错误",
                            content: error.toString(),
                        });
                    }
                }
                return Promise.reject(error);
            }
        );
    };

    initAxios();

    return (
        <>
            <Routes>
                <Route
                    path={"login"}
                    element={
                        <ErrorBoundary>
                            <Suspense fallback={<Spin spinning={true} fullscreen delay={1000} />}>
                                <AsyncLogin />
                            </Suspense>
                        </ErrorBoundary>
                    }
                />
                <Route
                    path={"*"}
                    element={
                        <ErrorBoundary>
                            <Suspense fallback={<Spin spinning={true} fullscreen delay={1000} />}>
                                <AsyncAdminDashboardRouter />
                            </Suspense>
                        </ErrorBoundary>
                    }
                />
            </Routes>
        </>
    );
};

export default AppBase;
