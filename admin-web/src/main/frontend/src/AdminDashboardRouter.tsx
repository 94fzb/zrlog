import { Route, Routes } from "react-router-dom";
import { FunctionComponent, lazy, Suspense, useEffect, useState } from "react";
import { useLocation } from "react-router";
import { getCsrData } from "./api";
import MyLoadingComponent from "./components/my-loading-component";
import { ssData } from "./index";
import { getCachedData, getLastOpenedPage, getPageFullState, putCache } from "./cache";
import { deepEqual, getFullPath, removeQueryParam } from "./utils/helpers";
import { UpgradeData } from "./components/upgrade";
import { cacheIgnoreReloadKey, getRes } from "./utils/constants";
import { isPWA } from "./utils/env-utils";
import * as H from "history";

const AsyncArticleEdit = lazy(() => import("components/articleEdit"));
const AsyncOffline = lazy(() => import("common/Offline"));

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
const AsyncError = lazy(() => import("components/unknown-error-page"));

const AdminManageLayout = lazy(() => import("layout/index"));

type AdminDashboardRouterState = {
    firstRender: boolean;
    currentUri: string;
    axiosRequesting: boolean;
    fullScreen: boolean;
    offline: boolean;
    data: Record<string, any>;
};

const updateDocumentTitle = (newDocumentTitle: string) => {
    const baseTitle = getRes()["websiteTitle"] + " - " + getRes()["admin.management"];
    if (newDocumentTitle) {
        if (isPWA()) {
            window.document.title = newDocumentTitle.replace(" - " + baseTitle, "");
        } else {
            window.document.title = newDocumentTitle;
        }
    } else {
        window.document.title = baseTitle;
    }
};

type AdminDashboardRouterProps = {
    offline: boolean;
};

const AdminDashboardRouter: FunctionComponent<AdminDashboardRouterProps> = ({ offline }) => {
    const location = useLocation();
    const pwaLastOpenedPage = isPWA() ? getLastOpenedPage() : null;
    const defaultFullScreen = getPageFullState(pwaLastOpenedPage ? pwaLastOpenedPage : getFullPath(location));
    //console.info(pwaLastOpenedPage + " => full screen " + defaultFullScreen);

    const [state, setState] = useState<AdminDashboardRouterState>({
        firstRender: ssData && ssData.pageData,
        currentUri: location.pathname + location.search,
        axiosRequesting: false,
        offline: offline,
        fullScreen: defaultFullScreen,
        data: { ...getCachedData(), [location.pathname + location.search]: ssData?.pageData },
    });

    const getDataFromState = () => {
        const uri = location.pathname + removeQueryParam(location.search, cacheIgnoreReloadKey);
        return state.data[uri] !== undefined && state.data[uri] !== null ? state.data[uri] : undefined;
    };

    const loadData = (uri: string, location: H.Location) => {
        getCsrData(uri)
            .then((e) => {
                const { data, documentTitle } = e;
                const mergeData = state.data;
                updateDocumentTitle(documentTitle);
                //如果请求回来的和请求回来的一致的情况就跳过 setState
                if (deepEqual(mergeData[uri], data)) {
                    console.debug(uri + " cache hits");
                    return;
                }
                mergeData[uri] = data;
                setState((prevState) => {
                    return {
                        offline: prevState.offline,
                        firstRender: false,
                        axiosRequesting: false,
                        currentUri: uri,
                        data: mergeData,
                        fullScreen: getPageFullState(getFullPath(location)),
                    };
                });
                putCache(mergeData);
            })
            .finally(() => {
                setState((prevState) => {
                    return {
                        offline: prevState.offline,
                        currentUri: uri,
                        firstRender: false,
                        axiosRequesting: false,
                        data: prevState.data,
                        fullScreen: getPageFullState(getFullPath(location)),
                    };
                });
            });
    };

    useEffect(() => {
        const uri = location.pathname + removeQueryParam(location.search, cacheIgnoreReloadKey);
        if (getDataFromState()) {
            if (state.firstRender) {
                setState((prevState) => {
                    return {
                        currentUri: uri,
                        fullScreen: getPageFullState(getFullPath(location)),
                        firstRender: false,
                        axiosRequesting: false,
                        offline: prevState.offline,
                        data: prevState.data,
                    };
                });
                return;
            } else {
                setState((prevState) => {
                    return {
                        currentUri: uri,
                        axiosRequesting: true,
                        firstRender: false,
                        fullScreen: getPageFullState(getFullPath(location)),
                        data: prevState.data,
                        offline: prevState.offline,
                    };
                });
            }
        }
        loadData(uri, location);
    }, [location.pathname, location.search]);

    useEffect(() => {
        setState((prevState) => {
            return {
                ...prevState,
                offline: offline,
            };
        });
    }, [offline]);

    //console.info(location.pathname + "," + JSON.stringify(state));

    return (
        <Routes>
            <Route
                path="index"
                element={
                    <AdminManageLayout offline={state.offline} loading={state.axiosRequesting}>
                        {getDataFromState() && (
                            <Suspense fallback={<MyLoadingComponent />}>
                                <AsyncIndex data={getDataFromState()} />
                            </Suspense>
                        )}
                    </AdminManageLayout>
                }
            />
            <Route
                path=""
                element={
                    <AdminManageLayout offline={state.offline} loading={state.axiosRequesting}>
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
                    <AdminManageLayout offline={state.offline} loading={state.axiosRequesting}>
                        {getDataFromState() && (
                            <Suspense fallback={<MyLoadingComponent />}>
                                <AsyncComment offline={offline} data={getDataFromState()} />
                            </Suspense>
                        )}
                    </AdminManageLayout>
                }
            />
            <Route
                path="plugin"
                element={
                    <AdminManageLayout offline={state.offline} loading={state.axiosRequesting}>
                        <Suspense fallback={<MyLoadingComponent />}>
                            <AsyncPlugin />
                        </Suspense>
                    </AdminManageLayout>
                }
            />
            <Route
                path={"website"}
                element={
                    <AdminManageLayout offline={state.offline} loading={state.axiosRequesting}>
                        {getDataFromState() && (
                            <Suspense fallback={<MyLoadingComponent />}>
                                <AsyncWebSite offline={state.offline} data={getDataFromState()} />
                            </Suspense>
                        )}
                    </AdminManageLayout>
                }
            />
            <Route
                path={"website/admin"}
                element={
                    <AdminManageLayout offline={state.offline} loading={state.axiosRequesting}>
                        {getDataFromState() && (
                            <Suspense fallback={<MyLoadingComponent />}>
                                <AsyncWebSite offline={state.offline} data={getDataFromState()} />
                            </Suspense>
                        )}
                    </AdminManageLayout>
                }
            />
            <Route
                path={"website/template"}
                element={
                    <AdminManageLayout offline={state.offline} loading={state.axiosRequesting}>
                        {getDataFromState() && (
                            <Suspense fallback={<MyLoadingComponent />}>
                                <AsyncWebSite offline={state.offline} data={getDataFromState()} />
                            </Suspense>
                        )}
                    </AdminManageLayout>
                }
            />
            <Route
                path={"website/other"}
                element={
                    <AdminManageLayout offline={state.offline} loading={state.axiosRequesting}>
                        {getDataFromState() && (
                            <Suspense fallback={<MyLoadingComponent />}>
                                <AsyncWebSite offline={state.offline} data={getDataFromState()} />
                            </Suspense>
                        )}
                    </AdminManageLayout>
                }
            />
            <Route
                path={"website/blog"}
                element={
                    <AdminManageLayout offline={state.offline} loading={state.axiosRequesting}>
                        {getDataFromState() && (
                            <Suspense fallback={<MyLoadingComponent />}>
                                <AsyncWebSite offline={state.offline} data={getDataFromState()} />
                            </Suspense>
                        )}
                    </AdminManageLayout>
                }
            />
            <Route
                path={"website/upgrade"}
                element={
                    <AdminManageLayout offline={state.offline} loading={state.axiosRequesting}>
                        {getDataFromState() && (
                            <Suspense fallback={<MyLoadingComponent />}>
                                <AsyncWebSite offline={state.offline} data={getDataFromState()} />
                            </Suspense>
                        )}
                    </AdminManageLayout>
                }
            />
            <Route
                path="article-type"
                element={
                    <AdminManageLayout offline={state.offline} loading={state.axiosRequesting}>
                        {getDataFromState() && (
                            <Suspense fallback={<MyLoadingComponent />}>
                                <AsyncType offline={state.offline} data={getDataFromState()} />
                            </Suspense>
                        )}
                    </AdminManageLayout>
                }
            />
            <Route
                path="link"
                element={
                    <AdminManageLayout offline={state.offline} loading={state.axiosRequesting}>
                        {getDataFromState() && (
                            <Suspense fallback={<MyLoadingComponent />}>
                                <AsyncLink offline={state.offline} data={getDataFromState()} />
                            </Suspense>
                        )}
                    </AdminManageLayout>
                }
            />
            <Route
                path="nav"
                element={
                    <AdminManageLayout offline={state.offline} loading={state.axiosRequesting}>
                        {getDataFromState() && (
                            <Suspense fallback={<MyLoadingComponent />}>
                                <AsyncNav offline={state.offline} data={getDataFromState()} />
                            </Suspense>
                        )}
                    </AdminManageLayout>
                }
            />
            <Route
                path="article"
                element={
                    <AdminManageLayout offline={state.offline} loading={state.axiosRequesting}>
                        {getDataFromState() && (
                            <Suspense fallback={<MyLoadingComponent />}>
                                <AsyncArticle offline={state.offline} data={getDataFromState()} />
                            </Suspense>
                        )}
                    </AdminManageLayout>
                }
            />
            <Route
                path="article-edit"
                element={
                    <AdminManageLayout
                        offline={state.offline}
                        loading={state.axiosRequesting}
                        fullScreen={state.fullScreen}
                    >
                        {getDataFromState() && (
                            <Suspense fallback={<MyLoadingComponent />}>
                                <AsyncArticleEdit
                                    offline={state.offline}
                                    onFullScreen={() => {
                                        setState((prevState) => {
                                            return {
                                                ...prevState,
                                                fullScreen: true,
                                            };
                                        });
                                    }}
                                    data={getDataFromState()}
                                    onExitFullScreen={() =>
                                        setState((prevState) => {
                                            return {
                                                ...prevState,
                                                fullScreen: false,
                                            };
                                        })
                                    }
                                />
                            </Suspense>
                        )}
                    </AdminManageLayout>
                }
            />
            <Route
                path="user"
                element={
                    <AdminManageLayout offline={state.offline} loading={state.axiosRequesting}>
                        {getDataFromState() && (
                            <Suspense fallback={<MyLoadingComponent />}>
                                <AsyncUser offline={state.offline} data={getDataFromState()} />
                            </Suspense>
                        )}
                    </AdminManageLayout>
                }
            />
            <Route
                path="template-center"
                element={
                    <AdminManageLayout offline={state.offline} loading={state.axiosRequesting}>
                        {getDataFromState() && (
                            <Suspense fallback={<MyLoadingComponent />}>
                                <AsyncTemplateCenter data={getDataFromState()} />
                            </Suspense>
                        )}
                    </AdminManageLayout>
                }
            />
            <Route
                path="user-update-password"
                element={
                    <AdminManageLayout offline={state.offline} loading={state.axiosRequesting}>
                        <Suspense fallback={<MyLoadingComponent />}>
                            <AsyncUserUpdatePassword offline={state.offline} />
                        </Suspense>
                    </AdminManageLayout>
                }
            />
            <Route
                path="upgrade"
                element={
                    <AdminManageLayout offline={state.offline} loading={state.axiosRequesting}>
                        {getDataFromState() && (
                            <Suspense fallback={<MyLoadingComponent />}>
                                <AsyncUpgrade
                                    offline={state.offline}
                                    key={(getDataFromState() as UpgradeData).preUpgradeKey}
                                    data={getDataFromState()}
                                />
                            </Suspense>
                        )}
                    </AdminManageLayout>
                }
            />
            <Route
                path="template-config"
                element={
                    <AdminManageLayout offline={state.offline} loading={state.axiosRequesting}>
                        {getDataFromState() && (
                            <Suspense fallback={<MyLoadingComponent />}>
                                <AsyncTemplateConfig offline={state.offline} data={getDataFromState()} />
                            </Suspense>
                        )}
                    </AdminManageLayout>
                }
            />
            <Route
                path="403"
                element={
                    getDataFromState() && (
                        <Suspense fallback={<MyLoadingComponent />}>
                            <AsyncError data={getDataFromState()} code={403} />
                        </Suspense>
                    )
                }
            />
            <Route
                path="500"
                element={
                    getDataFromState() && (
                        <Suspense fallback={<MyLoadingComponent />}>
                            <AsyncError data={getDataFromState()} code={500} />
                        </Suspense>
                    )
                }
            />
            <Route
                path={"offline"}
                element={
                    <Suspense fallback={<MyLoadingComponent />}>
                        <AsyncOffline />
                    </Suspense>
                }
            />
            <Route
                path={"*"}
                element={
                    <Suspense fallback={<MyLoadingComponent />}>
                        <AsyncNotFoundPage />
                    </Suspense>
                }
            />
        </Routes>
    );
};
export default AdminDashboardRouter;
