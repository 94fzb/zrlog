import { Route, Routes } from "react-router-dom";
import { lazy, useEffect, useState } from "react";
import { Suspense } from "react";
import { App } from "antd";
import axios from "axios";
import { useLocation } from "react-router";
import { getCsrData } from "./api";

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
                    if (error.response.status === 502) {
                        modal.error({
                            title: "服务未启动",
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
                }
                return Promise.reject(error);
            }
        );
    };

    const [data, setData] = useState<any>(null);

    const location = useLocation();

    useEffect(() => {
        initAxios();
        setData(getCsrData(location.pathname.replace("/admin/", "")));
    }, []);

    return (
        <>
            <Routes>
                <Route
                    path={"login"}
                    element={
                        <Suspense fallback={<div />}>
                            <AsyncLogin />
                        </Suspense>
                    }
                />
                <Route
                    path={"*"}
                    element={
                        <Suspense fallback={<div />}>
                            <AsyncAdminDashboardRouter data={data} />
                        </Suspense>
                    }
                />
            </Routes>
        </>
    );
};

export default AppBase;
