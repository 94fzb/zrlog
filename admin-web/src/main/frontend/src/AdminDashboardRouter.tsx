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

type AdminDashboardRouterState = {
    firstRender: boolean;
    data: Record<string, any>;
};

const AdminDashboardRouter = () => {
    const location = useLocation();

    const [state, setState] = useState<AdminDashboardRouterState>({
        firstRender: ssData && ssData.pageData,
        data: ssData && ssData.pageData ? { [location.pathname + location.search]: ssData.pageData } : {},
    });

    const getDataFromState = () => {
        const uri = location.pathname + location.search;
        return state.data[uri] !== undefined && state.data[uri] !== null ? state.data[uri] : undefined;
    };

    useEffect(() => {
        if (getDataFromState()) {
            return;
        }
        setState({ firstRender: false, data: {} });
        const uri = location.pathname + location.search;
        getCsrData(uri).then((e) => {
            const newData = { [uri]: e };
            setState({ firstRender: false, data: newData });
        });
    }, [location.pathname, location.search]);

    //console.info(location.pathname + "," + JSON.stringify(state));

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
