import { Route, Routes } from "react-router-dom";
import { lazy, Suspense, useEffect, useState } from "react";
import { useLocation } from "react-router";
import { getCsrData } from "./api";
import MyLoadingComponent from "./components/my-loading-component";

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

const AdminDashboardRouter = () => {
    const location = useLocation();

    const [data, setData] = useState<Record<string, any>>({});

    useEffect(() => {
        setData({});
        const uri = location.pathname + location.search;
        getCsrData(uri).then((e) => {
            const newData = { [uri]: e };
            setData({ ...newData });
        });
    }, [location.pathname, location.search]);

    const getData = () => {
        const uri = location.pathname + location.search;
        return data[uri] !== undefined && data[uri] !== null ? data[uri] : undefined;
    };

    //console.info(data);

    return (
        <Routes>
            <Route
                path="index"
                element={
                    <AdminManageLayout>
                        {getData() && (
                            <Suspense fallback={<MyLoadingComponent />}>
                                <AsyncIndex data={getData()} />
                            </Suspense>
                        )}
                    </AdminManageLayout>
                }
            />
            <Route
                path="comment"
                element={
                    <AdminManageLayout>
                        {getData() && (
                            <Suspense fallback={<MyLoadingComponent />}>
                                <AsyncComment data={getData()} />
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
                        {getData() && (
                            <Suspense fallback={<MyLoadingComponent />}>
                                <AsyncWebSite data={getData()} />
                            </Suspense>
                        )}
                    </AdminManageLayout>
                }
            />
            <Route
                path="article-type"
                element={
                    <AdminManageLayout>
                        {getData() && (
                            <Suspense fallback={<MyLoadingComponent />}>
                                <AsyncType data={getData()} />
                            </Suspense>
                        )}
                    </AdminManageLayout>
                }
            />
            <Route
                path="link"
                element={
                    <AdminManageLayout>
                        {getData() && (
                            <Suspense fallback={<MyLoadingComponent />}>
                                <AsyncLink data={getData()} />
                            </Suspense>
                        )}
                    </AdminManageLayout>
                }
            />
            <Route
                path="nav"
                element={
                    <AdminManageLayout>
                        {getData() && (
                            <Suspense fallback={<MyLoadingComponent />}>
                                <AsyncNav data={getData()} />
                            </Suspense>
                        )}
                    </AdminManageLayout>
                }
            />
            <Route
                path="article"
                element={
                    <AdminManageLayout>
                        {getData() && (
                            <Suspense fallback={<MyLoadingComponent />}>
                                <AsyncArticle data={getData()} />
                            </Suspense>
                        )}
                    </AdminManageLayout>
                }
            />
            <Route
                path="article-edit"
                element={
                    <AdminManageLayout>
                        {getData() && (
                            <Suspense fallback={<MyLoadingComponent />}>
                                <AsyncArticleEdit data={getData()} />
                            </Suspense>
                        )}
                    </AdminManageLayout>
                }
            />
            <Route
                path="user"
                element={
                    <AdminManageLayout>
                        {getData() && (
                            <Suspense fallback={<MyLoadingComponent />}>
                                <AsyncUser data={getData()} />
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
                        {getData() && (
                            <Suspense fallback={<MyLoadingComponent />}>
                                <AsyncUpgrade data={getData()} />
                            </Suspense>
                        )}
                    </AdminManageLayout>
                }
            />
            <Route
                path="template-config"
                element={
                    <AdminManageLayout>
                        {getData() && (
                            <Suspense fallback={<MyLoadingComponent />}>
                                <AsyncTemplateConfig data={getData()} />
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
