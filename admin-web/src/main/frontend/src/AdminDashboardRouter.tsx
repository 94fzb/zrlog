import { Route, Routes } from "react-router-dom";
import { lazy, Suspense } from "react";

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
    return (
        <Routes>
            <Route
                path=""
                element={
                    <AdminManageLayout>
                        <Suspense>
                            <AsyncIndex />
                        </Suspense>
                    </AdminManageLayout>
                }
            />
            <Route
                path="index"
                element={
                    <AdminManageLayout>
                        <Suspense>
                            <AsyncIndex />
                        </Suspense>
                    </AdminManageLayout>
                }
            />
            <Route
                path="article-edit"
                element={
                    <AdminManageLayout>
                        <Suspense>
                            <AsyncArticleEdit />
                        </Suspense>
                    </AdminManageLayout>
                }
            />
            <Route
                path="comment"
                element={
                    <AdminManageLayout>
                        <Suspense>
                            <AsyncComment />
                        </Suspense>
                    </AdminManageLayout>
                }
            />
            <Route
                path="plugin"
                element={
                    <AdminManageLayout>
                        <Suspense>
                            <AsyncPlugin />
                        </Suspense>
                    </AdminManageLayout>
                }
            />
            <Route
                path="website"
                element={
                    <AdminManageLayout>
                        <Suspense>
                            <AsyncWebSite />
                        </Suspense>
                    </AdminManageLayout>
                }
            />
            <Route
                path="article-type"
                element={
                    <AdminManageLayout>
                        <Suspense>
                            <AsyncType />
                        </Suspense>
                    </AdminManageLayout>
                }
            />
            <Route
                path="link"
                element={
                    <AdminManageLayout>
                        <Suspense>
                            <AsyncLink />
                        </Suspense>
                    </AdminManageLayout>
                }
            />
            <Route
                path="nav"
                element={
                    <AdminManageLayout>
                        <Suspense>
                            <AsyncNav />
                        </Suspense>
                    </AdminManageLayout>
                }
            />
            <Route
                path="article"
                element={
                    <AdminManageLayout>
                        <Suspense>
                            <AsyncArticle />
                        </Suspense>
                    </AdminManageLayout>
                }
            />
            <Route
                path="user"
                element={
                    <AdminManageLayout>
                        <Suspense>
                            <AsyncUser />
                        </Suspense>
                    </AdminManageLayout>
                }
            />
            <Route
                path="template-center"
                element={
                    <AdminManageLayout>
                        <Suspense>
                            <AsyncTemplateCenter />
                        </Suspense>
                    </AdminManageLayout>
                }
            />
            <Route
                path="user-update-password"
                element={
                    <AdminManageLayout>
                        <Suspense>
                            <AsyncUserUpdatePassword />
                        </Suspense>
                    </AdminManageLayout>
                }
            />
            <Route
                path="upgrade"
                element={
                    <AdminManageLayout>
                        <Suspense>
                            <AsyncUpgrade />
                        </Suspense>
                    </AdminManageLayout>
                }
            />
            <Route
                path="template-config"
                element={
                    <AdminManageLayout>
                        <Suspense>
                            <AsyncTemplateConfig />
                        </Suspense>
                    </AdminManageLayout>
                }
            />
            <Route
                path={"*"}
                element={
                    <AdminManageLayout>
                        <Suspense>
                            <AsyncNotFoundPage />
                        </Suspense>
                    </AdminManageLayout>
                }
            />
        </Routes>
    );
};
export default AdminDashboardRouter;
