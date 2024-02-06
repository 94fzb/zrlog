import { Route, Routes } from "react-router-dom";
import { lazy, Suspense, useEffect, useState } from "react";
import { useLocation } from "react-router";
import { getCsrData } from "./api";
import MyLoadingComponent from "./components/my-loading-component";
import { ssData } from "./index";

const AsyncArticleEdit = lazy(() => import("components/articleEdit"));

const AsyncComment = lazy(() => import("components/comment"));

const AsyncPlugin = lazy(() => import("components/plugin"));

const AsyncIndex = lazy(() => import("components/index"));

const AsyncWebSite = lazy(() => import("components/website"));

const AsyncType = lazy(() => import("components/type"));

const AsyncLink = lazy(() => import("components/link"));

const AsyncNav = lazy(() => import("components/nav"));

const AsyncNotFoundPage = lazy(() => import("components/not-found-page"));

const AsyncUpgrade = lazy(() => import("components/upgrade"));

const AsyncTemplateCenter = lazy(() => import("components/template/template-center"));

const AsyncTemplateConfig = lazy(() => import("components/template/template-config"));

const AsyncUserUpdatePassword = lazy(() => import("components/user-update-password"));

const AsyncArticle = lazy(() => import("components/article"));

const AsyncUser = lazy(() => import("components/user"));

const AdminManageLayout = lazy(() => import("layout/index"));

let ssRendered: boolean = false;

const AdminDashboardRouter = () => {
    const location = useLocation();

    let defaultData = {};

    // 仅客户端渲染时，第一次渲染时生效
    if (!ssRendered && ssData && ssData.pageData) {
        ssRendered = true;
        const uri = location.pathname + location.search;
        defaultData = { [uri]: ssData.pageData };
    }

    const [data, setData] = useState<Record<string, any>>(defaultData);

    const getDataFromState = () => {
        const uri = location.pathname + location.search;
        return data[uri] !== undefined && data[uri] !== null ? data[uri] : undefined;
    };

    useEffect(() => {
        if (getDataFromState()) {
            return;
        }
        setData({});
        const uri = location.pathname + location.search;
        getCsrData(uri).then((e) => {
            const newData = { [uri]: e };
            setData({ ...newData });
        });
    }, [location.pathname, location.search]);

    //console.info(data);

    return (
        <Routes>
            <Route
                path="index"
                element={
                    <AdminManageLayout>
                        {getDataFromState() && (
                            <Suspense fallback={<MyLoadingComponent />}>
                                <AsyncIndex data={getDataFromState()} />
                            </Suspense>
                        )}
                    </AdminManageLayout>
                }
            />
            <Route
                path="comment"
                element={
                    <AdminManageLayout>
                        {getDataFromState() && (
                            <Suspense fallback={<MyLoadingComponent />}>
                                <AsyncComment data={getDataFromState()} />
                            </Suspense>
                        )}
                    </AdminManageLayout>
                }
            />
            <Route
                path="plugin"
                element={
                    <AdminManageLayout>
                        <Suspense fallback={<MyLoadingComponent />}>
                            <AsyncPlugin />
                        </Suspense>
                    </AdminManageLayout>
                }
            />
            <Route
                path="website"
                element={
                    <AdminManageLayout>
                        {getDataFromState() && (
                            <Suspense fallback={<MyLoadingComponent />}>
                                <AsyncWebSite data={getDataFromState()} />
                            </Suspense>
                        )}
                    </AdminManageLayout>
                }
            />
            <Route
                path="article-type"
                element={
                    <AdminManageLayout>
                        {getDataFromState() && (
                            <Suspense fallback={<MyLoadingComponent />}>
                                <AsyncType data={getDataFromState()} />
                            </Suspense>
                        )}
                    </AdminManageLayout>
                }
            />
            <Route
                path="link"
                element={
                    <AdminManageLayout>
                        {getDataFromState() && (
                            <Suspense fallback={<MyLoadingComponent />}>
                                <AsyncLink data={getDataFromState()} />
                            </Suspense>
                        )}
                    </AdminManageLayout>
                }
            />
            <Route
                path="nav"
                element={
                    <AdminManageLayout>
                        {getDataFromState() && (
                            <Suspense fallback={<MyLoadingComponent />}>
                                <AsyncNav data={getDataFromState()} />
                            </Suspense>
                        )}
                    </AdminManageLayout>
                }
            />
            <Route
                path="article"
                element={
                    <AdminManageLayout>
                        {getDataFromState() && (
                            <Suspense fallback={<MyLoadingComponent />}>
                                <AsyncArticle data={getDataFromState()} />
                            </Suspense>
                        )}
                    </AdminManageLayout>
                }
            />
            <Route
                path="article-edit"
                element={
                    <AdminManageLayout>
                        {getDataFromState() && (
                            <Suspense fallback={<MyLoadingComponent />}>
                                <AsyncArticleEdit data={getDataFromState()} />
                            </Suspense>
                        )}
                    </AdminManageLayout>
                }
            />
            <Route
                path="user"
                element={
                    <AdminManageLayout>
                        {getDataFromState() && (
                            <Suspense fallback={<MyLoadingComponent />}>
                                <AsyncUser data={getDataFromState()} />
                            </Suspense>
                        )}
                    </AdminManageLayout>
                }
            />
            <Route
                path="template-center"
                element={
                    <AdminManageLayout>
                        <Suspense fallback={<MyLoadingComponent />}>
                            <AsyncTemplateCenter />
                        </Suspense>
                    </AdminManageLayout>
                }
            />
            <Route
                path="user-update-password"
                element={
                    <AdminManageLayout>
                        <Suspense fallback={<MyLoadingComponent />}>
                            <AsyncUserUpdatePassword />
                        </Suspense>
                    </AdminManageLayout>
                }
            />
            <Route
                path="upgrade"
                element={
                    <AdminManageLayout>
                        {getDataFromState() && (
                            <Suspense fallback={<MyLoadingComponent />}>
                                <AsyncUpgrade data={getDataFromState()} />
                            </Suspense>
                        )}
                    </AdminManageLayout>
                }
            />
            <Route
                path="template-config"
                element={
                    <AdminManageLayout>
                        {getDataFromState() && (
                            <Suspense fallback={<MyLoadingComponent />}>
                                <AsyncTemplateConfig data={getDataFromState()} />
                            </Suspense>
                        )}
                    </AdminManageLayout>
                }
            />
            <Route
                path={"*"}
                element={
                    <AdminManageLayout>
                        <Suspense fallback={<MyLoadingComponent />}>
                            <AsyncNotFoundPage />
                        </Suspense>
                    </AdminManageLayout>
                }
            />
        </Routes>
    );
};
export default AdminDashboardRouter;
