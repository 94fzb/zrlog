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
import UnknownErrorPage from "./components/unknown-error-page";
import Offline from "./common/Offline";
import Index from "./components/index";
import Comment from "./components/comment";

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

    const routes = [
        {
            paths: ["index", "index.html",""],
            lazy: AsyncIndex,
            fallback: Index
        },
        {
            paths: ["comment", "comment.html"],
            lazy: AsyncComment,
            fallback: Comment
        },
        {
            paths: ["plugin", "plugin.html"],
            lazy: AsyncPlugin,
            fallback: Plugin
        },
        {
            paths: ["website", "website.html"],
            lazy: AsyncWebSite,
            fallback: WebSite,
            props: {activeKey: "basic"}
        },
        {
            paths: ["website/admin", "website/admin.html"],
            lazy: AsyncWebSite,
            fallback: WebSite,
            props: {activeKey: "admin"}
        },
        {
            paths: ["website/template", "website/template.html"],
            lazy: AsyncWebSite,
            fallback: WebSite,
            props: {activeKey: "template"}
        },
        {
            paths: ["website/other", "website/other.html"],
            lazy: AsyncWebSite,
            fallback: WebSite,
            props: {activeKey: "other"}
        },
        {
            paths: ["website/blog", "website/blog.html"],
            lazy: AsyncWebSite,
            fallback: WebSite,
            props: {activeKey: "blog"}
        },
        {
            paths: ["website/upgrade", "website/upgrade.html"],
            lazy: AsyncWebSite,
            fallback: WebSite,
            props: {activeKey: "upgrade"}
        },
        {
            paths: ["article-type", "article-type.html"],
            lazy: AsyncType,
            fallback: Type
        },
        {
            paths: ["link", "link.html"],
            lazy: AsyncLink,
            fallback: Link
        },
        {
            paths: ["nav", "nav.html"],
            lazy: AsyncNav,
            fallback: Nav
        },
        {
            paths: ["article", "article.html"],
            lazy: AsyncArticle,
            fallback: Article
        },
        {
            paths: ["article-edit", "article-edit.html"],
            lazy: AsyncArticleEdit,
            fallback: ArticleEdit,
            fullScreen: true,
            props: {
                deleteStateCacheOnDestroy: () => deleteThisPageStateCache(),
                onFullScreen: () => {
                    setState((prevState) => {
                        savePageFullState(getFullPath(location), true);
                        return {...prevState, fullScreen: true};
                    });
                },
                onExitFullScreen: () => {
                    setState((prevState) => {
                        savePageFullState(getFullPath(location), false);
                        return {...prevState, fullScreen: false};
                    });
                }
            }
        },
        {
            paths: ["user", "user.html"],
            lazy: AsyncUser,
            fallback: User
        },
        {
            paths: ["template-center", "template-center.html"],
            lazy: AsyncTemplateCenter,
            fallback: TemplateCenter
        },
        {
            paths: ["user-update-password", "user-update-password.html"],
            lazy: AsyncUserUpdatePassword,
            fallback: UserUpdatePassword
        },
        {
            paths: ["upgrade", "upgrade.html"],
            lazy: AsyncUpgrade,
            fallback: Upgrade,
            props: {
                key: (getDataFromState() as UpgradeData)?.preUpgradeKey || "",
                axiosRequesting: state.axiosRequesting
            }
        },
        {
            paths: ["template-config", "template-config.html"],
            lazy: AsyncTemplateConfig,
            fallback: TemplateConfig
        },
        {
            paths: ["403", "403.html"],
            lazy: AsyncError,
            fallback: UnknownErrorPage,
            props: {
                code: 403,
            }
        },
        {
            paths: ["500", "500.html"],
            lazy: AsyncError,
            fallback: UnknownErrorPage,
            props: {
                code: 500,
            }
        },
        {
            paths: ["offline", "offline.html"],
            lazy: AsyncOffline,
            fallback: Offline,
            props: {}
        },
        {
            paths: ["system", "system.html"],
            lazy: AsyncSystem,
            fallback: System
        }
    ];

    //console.info(location.pathname + "," + JSON.stringify(state));

    return (
        <Routes>
            {routes.flatMap(({paths, lazy, fallback, props = {}, fullScreen}, i) =>
                paths.map((path, j) => (
                    <Route
                        key={`${i}-${j}`}
                        path={path}
                        element={
                            <AdminPage
                                data={getDataFromState()}
                                axiosRequesting={state.axiosRequesting}
                                offline={state.offline}
                                fullScreen={fullScreen ? state.fullScreen : undefined}
                                LazyComponent={lazy}
                                FallbackComponent={fallback}
                                props={{
                                    ...props,
                                    data: getDataFromState(),
                                    offline: state.offline
                                }}
                            />
                        }
                    />
                ))
            )}
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
