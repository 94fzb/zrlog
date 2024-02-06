import { Route, Routes } from "react-router-dom";
import { lazy } from "react";
import { Suspense } from "react";
import { App, Spin } from "antd";
import axios, { AxiosError } from "axios";

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
                    if (error.response.status) {
                        modal.error({
                            title: "服务异常[" + error.response.status + "]",
                            content: (
                                <div
                                    style={{ paddingTop: 20 }}
                                    dangerouslySetInnerHTML={{ __html: error.response.data }}
                                />
                            ),
                            okText: "确认",
                        });
                        return Promise.reject(error.response);
                    }
                } else {
                    if ((error as AxiosError) && error.config) {
                        modal.error({
                            title: "请求 " + error.config.url + " 错误",
                            content: error.toString(),
                            okText: "确认",
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
                        <Suspense fallback={<Spin spinning={true} fullscreen delay={1000} />}>
                            <AsyncLogin />
                        </Suspense>
                    }
                />
                <Route
                    path={"*"}
                    element={
                        <Suspense fallback={<Spin spinning={true} fullscreen delay={1000} />}>
                            <AsyncAdminDashboardRouter />
                        </Suspense>
                    }
                />
            </Routes>
        </>
    );
};

export default AppBase;
