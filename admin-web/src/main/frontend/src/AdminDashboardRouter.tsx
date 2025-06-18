import {Route, Routes} from "react-router-dom";
import {ComponentType, FunctionComponent, lazy, ReactElement, Suspense, useEffect, useRef, useState} from "react";
import {useLocation} from "react-router";
import {getCsrData} from "./api";
import MyLoadingComponent from "./components/my-loading-component";
import {ssData} from "./index";
import {
    getCachedData,
    getLastOpenedPage,
    getPageDataCacheKey,
    getPageFullState,
    putCache,
    savePageFullState,
} from "./cache";
import {deepEqualWithSpecialJSON, getFullPath} from "./utils/helpers";
import Upgrade, {UpgradeData} from "./components/upgrade";
import {getRes} from "./utils/constants";
import {isPWA} from "./utils/env-utils";
import * as H from "history";
import Index from "./components/index";
import Comment from "./components/comment";
import Plugin from "./components/plugin";
import WebSite from "./components/website";
import TemplateConfig from "./components/template/template-config";
import UserUpdatePassword from "./components/user-update-password";
import TemplateCenter from "./components/template/template-center";
import User from "./components/user";
import ArticleEdit from "./components/articleEdit";
import {ArticleEditProps} from "./components/articleEdit/index.types";
import System from "./components/system";
import Link from "./components/link";
import Nav from "./components/nav";
import Article from "./components/article";
import Type from "./components/type";

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
const AsyncSystem = lazy(() => import("components/system"));

const AdminManageLayout = lazy(() => import("layout/index"));

type AdminDashboardRouterState = {
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

type AdminPageProps<P> = {
    offline: boolean,
    axiosRequesting: boolean,
    data: any,
    LazyComponent: ComponentType<P>;
    FallbackComponent: ComponentType<P>;
    fullScreen?: boolean,
    props: P;
}

interface LazyWithFallbackElementProps<P> {
    LazyComponent: ComponentType<P>;
    FallbackComponent: ComponentType<P>;
    props: P;
}

export function LazyWithFallbackElement<P>({
                                               LazyComponent,
                                               FallbackComponent,
                                               props,
                                           }: LazyWithFallbackElementProps<P>) {

    return (
        <Suspense fallback={<FallbackComponent {...props} />}>
            <LazyComponent {...props} />
        </Suspense>
    );
}


export function AdminPage(props: AdminPageProps<ArticleEditProps | any>): ReactElement<AdminPageProps<ArticleEditProps | any>> {
    const {
        offline,
        axiosRequesting,
        data,
        FallbackComponent,
        LazyComponent,
        fullScreen,
        props: componentProps,
    } = props;

    return <AdminManageLayout offline={offline} loading={axiosRequesting} fullScreen={fullScreen}>
        {data && (
            <LazyWithFallbackElement LazyComponent={LazyComponent} FallbackComponent={FallbackComponent}
                                     props={componentProps}/>
        )}
    </AdminManageLayout>
}

const AdminDashboardRouter: FunctionComponent<AdminDashboardRouterProps> = ({offline}) => {
    const location = useLocation();
    const pwaLastOpenedPage = isPWA() ? getLastOpenedPage() : null;
    const defaultFullScreen = getPageFullState(pwaLastOpenedPage ? pwaLastOpenedPage : getFullPath(location));
    const initUri = location.pathname + location.search;
    const defaultData = {...getCachedData(), [initUri]: ssData?.pageData};

    const firstRender = useRef<boolean>(ssData && ssData.pageData);
    const [state, setState] = useState<AdminDashboardRouterState>({
        currentUri: initUri,
        axiosRequesting: false,
        offline: offline,
        fullScreen: defaultFullScreen,
        data: defaultData,
    });

    const getDataFromState = () => {
        const uri = getPageDataCacheKey(location);
        return state.data[uri] !== undefined && state.data[uri] !== null ? state.data[uri] : undefined;
    };

    const deleteThisPageStateCache = () => {
        const mergeData = state.data;
        mergeData[getPageDataCacheKey(location)] = undefined;
        setState((prevState) => {
            return {
                ...prevState,
                data: mergeData,
            };
        });
    };

    const loadData = async (uri: string, location: H.Location) => {
        const responseData = await getCsrData(uri);
        const {data, documentTitle} = responseData;
        const mergeData = state.data;
        updateDocumentTitle(documentTitle);
        //如果请求回来的和请求回来的一致的情况就跳过 setState
        if (deepEqualWithSpecialJSON(mergeData[uri], data)) {
            console.info(uri + " cache hits");
            setState((prevState) => {
                return {
                    ...prevState,
                    axiosRequesting: false,
                };
            });
            return;
        }
        mergeData[uri] = data;
        putCache(mergeData);
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
    };

    useEffect(() => {
        const uri = getPageDataCacheKey(location);
        if (getDataFromState()) {
            if (firstRender.current) {
                firstRender.current = false;
                return;
            }
            //使用缓存显示一次
            setState((prevState) => {
                return {
                    currentUri: uri,
                    axiosRequesting: true,
                    fullScreen: getPageFullState(getFullPath(location)),
                    data: prevState.data,
                    offline: prevState.offline,
                };
            });
        }
        loadData(uri, location)
            .then(() => {
                //ignore
            })
            .catch(() => {
                //标记未没有请求了
                setState((prevState) => {
                    return {
                        offline: prevState.offline,
                        currentUri: uri,
                        axiosRequesting: false,
                        data: prevState.data,
                        fullScreen: getPageFullState(getFullPath(location)),
                    };
                });
            });
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
            {
                ["index", "index.html", ""].map(e => {
                    return <Route
                        path={e}
                        key={e}
                        element={
                            <AdminPage data={getDataFromState()} axiosRequesting={state.axiosRequesting}
                                       offline={state.offline}
                                       LazyComponent={AsyncIndex}
                                       FallbackComponent={Index}
                                       props={{
                                           data: getDataFromState()
                                       }}/>
                        }
                    />
                })
            }
            {
                [
                    "comment", "comment.html"
                ].map(e => {
                    return <Route
                        key={e}
                        path={e}
                        element={
                            <AdminPage data={getDataFromState()} axiosRequesting={state.axiosRequesting}
                                       offline={state.offline}
                                       LazyComponent={AsyncComment}
                                       FallbackComponent={Comment}
                                       props={{
                                           offline: state.offline,
                                           data: getDataFromState()
                                       }}
                            />
                        }
                    />
                })
            }

            <Route
                path="plugin"
                element={
                    <AdminPage data={getDataFromState()} axiosRequesting={state.axiosRequesting} offline={state.offline}
                               LazyComponent={AsyncPlugin}
                               FallbackComponent={Plugin}
                               props={{}}
                    />
                }
            />
            <Route
                path={"website"}
                element={
                    <AdminPage data={getDataFromState()} axiosRequesting={state.axiosRequesting} offline={state.offline}
                               LazyComponent={AsyncWebSite}
                               FallbackComponent={WebSite}
                               props={{
                                   activeKey: "basic",
                                   offline: state.offline,
                                   data: getDataFromState(),
                               }}
                    />

                }
            />
            <Route
                path={"website/admin"}
                element={
                    <AdminPage data={getDataFromState()} axiosRequesting={state.axiosRequesting} offline={state.offline}
                               LazyComponent={AsyncWebSite}
                               FallbackComponent={WebSite}
                               props={{
                                   activeKey: "admin",
                                   offline: state.offline,
                                   data: getDataFromState(),
                               }}
                    />
                }
            />
            <Route
                path={"website/template"}
                element={
                    <AdminPage data={getDataFromState()} axiosRequesting={state.axiosRequesting} offline={state.offline}
                               LazyComponent={AsyncWebSite}
                               FallbackComponent={WebSite}
                               props={{
                                   activeKey: "template",
                                   offline: state.offline,
                                   data: getDataFromState(),
                               }}
                    />
                }
            />
            <Route
                path={"website/other"}
                element={
                    <AdminPage data={getDataFromState()} axiosRequesting={state.axiosRequesting} offline={state.offline}
                               LazyComponent={AsyncWebSite}
                               FallbackComponent={WebSite}
                               props={{
                                   activeKey: "other",
                                   offline: state.offline,
                                   data: getDataFromState(),
                               }}
                    />
                }
            />
            <Route
                path={"website/blog"}
                element={
                    <AdminPage data={getDataFromState()} axiosRequesting={state.axiosRequesting} offline={state.offline}
                               LazyComponent={AsyncWebSite}
                               FallbackComponent={WebSite}
                               props={{
                                   activeKey: "blog",
                                   offline: state.offline,
                                   data: getDataFromState(),
                               }}
                    />
                }
            />
            <Route
                path={"website/upgrade"}
                element={
                    <AdminPage data={getDataFromState()} axiosRequesting={state.axiosRequesting} offline={state.offline}
                               LazyComponent={AsyncWebSite}
                               FallbackComponent={WebSite}
                               props={{
                                   activeKey: "upgrade",
                                   offline: state.offline,
                                   data: getDataFromState(),
                               }}
                    />
                }
            />
            <Route
                path="article-type"
                element={
                    <AdminPage data={getDataFromState()} axiosRequesting={state.axiosRequesting} offline={state.offline}
                               LazyComponent={AsyncType}
                               FallbackComponent={Type}
                               props={{
                                   offline: state.offline,
                                   data: getDataFromState()
                               }}
                    />
                }
            />
            <Route
                path="link"
                element={
                    <AdminPage data={getDataFromState()} axiosRequesting={state.axiosRequesting} offline={state.offline}
                               LazyComponent={AsyncLink}
                               FallbackComponent={Link}
                               props={{
                                   offline: state.offline,
                                   data: getDataFromState()
                               }}
                    />
                }
            />
            <Route
                path="nav"
                element={
                    <AdminPage data={getDataFromState()} axiosRequesting={state.axiosRequesting} offline={state.offline}
                               LazyComponent={AsyncNav}
                               FallbackComponent={Nav}
                               props={{
                                   offline: state.offline,
                                   data: getDataFromState()
                               }}
                    />
                }
            />
            <Route
                path="article"
                element={
                    <AdminPage data={getDataFromState()} axiosRequesting={state.axiosRequesting} offline={state.offline}
                               LazyComponent={AsyncArticle}
                               FallbackComponent={Article}
                               props={{
                                   offline: state.offline,
                                   data: getDataFromState()
                               }}
                    />
                }
            />
            <Route
                path="article-edit"
                element={

                    <AdminPage data={getDataFromState()} axiosRequesting={state.axiosRequesting} offline={state.offline}
                               LazyComponent={AsyncArticleEdit}
                               fullScreen={state.fullScreen}
                               FallbackComponent={ArticleEdit}
                               props={{
                                   fullScreen: state.fullScreen,
                                   offline: state.offline,
                                   deleteStateCacheOnDestroy: () => {
                                       deleteThisPageStateCache();
                                   },
                                   data: getDataFromState(),
                                   onFullScreen: () => {
                                       setState((prevState) => {
                                           savePageFullState(getFullPath(location), true);
                                           return {
                                               ...prevState,
                                               fullScreen: true,
                                           };
                                       })
                                   },
                                   onExitFullScreen: () => {
                                       setState((prevState) => {
                                           savePageFullState(getFullPath(location), false);
                                           return {
                                               ...prevState,
                                               fullScreen: false,
                                           };
                                       });
                                   }

                               }}
                    />
                }
            />
            <Route
                path="user"
                element={
                    <AdminPage data={getDataFromState()} axiosRequesting={state.axiosRequesting} offline={state.offline}
                               LazyComponent={AsyncUser}
                               FallbackComponent={User}
                               props={{
                                   offline: state.offline,
                                   data: getDataFromState(),
                               }}
                    />
                }
            />
            <Route
                path="template-center"
                element={
                    <AdminPage data={getDataFromState()} axiosRequesting={state.axiosRequesting} offline={state.offline}
                               LazyComponent={AsyncTemplateCenter}
                               FallbackComponent={TemplateCenter}
                               props={{
                                   offline: state.offline,
                                   data: getDataFromState(),
                               }}
                    />
                }
            />
            <Route
                path="user-update-password"
                element={
                    <AdminPage data={getDataFromState()} axiosRequesting={state.axiosRequesting} offline={state.offline}
                               LazyComponent={AsyncUserUpdatePassword}
                               FallbackComponent={UserUpdatePassword}
                               props={{
                                   offline: state.offline,
                                   data: getDataFromState(),
                               }}
                    />
                }
            />
            <Route
                path="upgrade"
                element={
                    <AdminPage data={getDataFromState()} axiosRequesting={state.axiosRequesting} offline={state.offline}
                               LazyComponent={AsyncUpgrade}
                               FallbackComponent={Upgrade}
                               props={{
                                   offline: state.offline,
                                   key: (getDataFromState() as UpgradeData) ? getDataFromState().preUpgradeKey : "",
                                   data: getDataFromState(),
                                   axiosRequesting: state.axiosRequesting
                               }}
                    />
                }
            />
            <Route
                path="template-config"
                element={
                    <AdminPage data={getDataFromState()} axiosRequesting={state.axiosRequesting} offline={state.offline}
                               LazyComponent={AsyncTemplateConfig}
                               FallbackComponent={TemplateConfig}
                               props={{
                                   offline: state.offline,
                                   data: getDataFromState(),
                               }}
                    />
                }
            />
            <Route
                path="403"
                element={
                    getDataFromState() && (
                        <Suspense fallback={<MyLoadingComponent/>}>
                            <AsyncError data={getDataFromState()} code={403}/>
                        </Suspense>
                    )
                }
            />
            <Route
                path="500"
                element={
                    getDataFromState() && (
                        <Suspense fallback={<MyLoadingComponent/>}>
                            <AsyncError data={getDataFromState()} code={500}/>
                        </Suspense>
                    )
                }
            />
            <Route
                path={"offline"}
                element={
                    <Suspense fallback={<MyLoadingComponent/>}>
                        <AsyncOffline/>
                    </Suspense>
                }
            />
            <Route
                path={"system"}
                element={
                    <AdminPage data={getDataFromState()} axiosRequesting={state.axiosRequesting} offline={state.offline}
                               LazyComponent={AsyncSystem}
                               FallbackComponent={System}
                               props={{
                                   offline: state.offline,
                                   data: getDataFromState(),
                               }}
                    />
                }
            />
            <Route
                path={"*"}
                element={
                    <Suspense fallback={<MyLoadingComponent/>}>
                        <AsyncNotFoundPage/>
                    </Suspense>
                }
            />
        </Routes>
    );
};
export default AdminDashboardRouter;
