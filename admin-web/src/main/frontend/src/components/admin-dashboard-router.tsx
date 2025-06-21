import {Route, Routes} from "react-router-dom";
import {ComponentType, FunctionComponent, lazy, ReactElement, Suspense, useEffect, useRef, useState} from "react";
import {useLocation} from "react-router";
import {getCsrData} from "../api";
import MyLoadingComponent from "./my-loading-component";
import {ssData} from "../index";
import {
    addToCache,
    getCacheByKey,
    getLastOpenedPage,
    getPageDataCacheKey,
    getPageDataCacheKeyByPath,
    getPageFullState,
    removeCacheDataByKey,
    savePageFullState,
} from "../cache";
import {deepEqualWithSpecialJSON, getFullPath} from "../utils/helpers";
import Upgrade from "./upgrade";
import {getRes} from "../utils/constants";
import {isPWA} from "../utils/env-utils";
import * as H from "history";
import Plugin from "./plugin";
import WebSite, {WebSiteProps} from "./website";
import TemplateConfig from "./template/template-config";
import UserUpdatePassword from "./user-update-password";
import TemplateCenter from "./template/template-center";
import User from "./user";
import ArticleEdit from "./articleEdit";
import {ArticleEditProps} from "./articleEdit/index.types";
import System from "./system";
import Link from "./link";
import Nav from "./nav";
import Article from "./article";
import Type from "./type";
import UnknownErrorPage, {ErrorPageProps} from "./unknown-error-page";
import Offline from "../common/Offline";
import Index from "./index";
import Comment from "./comment";
import {useAxiosBaseInstance} from "../AppBase";
import {BasicUserInfo} from "../type";

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

const AdminManageLayout = lazy(() => import("layout"));

type AdminDashboardRouterState = {
    axiosRequesting: boolean;
    fullScreen: boolean;
    lastAxiosRequestedCacheKey: string;
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
    userInfo: BasicUserInfo;
};

type AdminPageProps<P> = {
    LazyComponent: ComponentType<P>;
    FallbackComponent: ComponentType<P>;
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

export type AdminCommonProps = {
    data: any,
    offlineData: boolean,
    offline: boolean,
    fullScreen?: boolean,
    userInfo: BasicUserInfo;
}

export function AdminPage(props: AdminPageProps<any>): ReactElement<AdminPageProps<AdminCommonProps>> {
    const {
        FallbackComponent,
        LazyComponent,
        props: componentProps,
    } = props;

    return <AdminManageLayout basicUserInfo={props.props.userInfo} offline={props.props.offline}
                              loading={props.props.offlineData && !props.props.offline}
                              fullScreen={props.props.fullScreen}>
        {props.props.data ? (
            <LazyWithFallbackElement LazyComponent={LazyComponent} FallbackComponent={FallbackComponent}
                                     props={componentProps}/>
        ) : <MyLoadingComponent/>}
    </AdminManageLayout>
}

const AdminDashboardRouter: FunctionComponent<AdminDashboardRouterProps> = ({offline, userInfo}) => {
    const location = useLocation();
    const pwaLastOpenedPage = isPWA() ? getLastOpenedPage() : null;
    const defaultFullScreen = getPageFullState(pwaLastOpenedPage ? pwaLastOpenedPage : getFullPath(location));
    const initCurrentPageDataKey = getPageDataCacheKey(location);
    const serverSideData = useRef<boolean>(ssData && ssData.data);

    const [state, setState] = useState<AdminDashboardRouterState>({
        axiosRequesting: false,
        lastAxiosRequestedCacheKey: serverSideData.current ? initCurrentPageDataKey : "",
        fullScreen: defaultFullScreen,
    });

    const getDataFromCache = () => {
        const pageDataCacheKey = getPageDataCacheKey(location);
        return getCacheByKey(pageDataCacheKey);
    };

    const deleteThisPageStateCache = () => {
        removeCacheDataByKey(getPageDataCacheKey(location));
    };

    const axiosBaseInstance = useAxiosBaseInstance();

    const loadData = async (currentPageDataKey: string, cacheData: any, location: H.Location) => {
        const responseData = await getCsrData(currentPageDataKey, axiosBaseInstance);
        const {data, documentTitle} = responseData;
        updateDocumentTitle(documentTitle);
        ssData.data = data;
        //如果请求回来的和请求回来的一致的情况
        if (deepEqualWithSpecialJSON(cacheData, data)) {
            console.info(currentPageDataKey + " cache hits");
            setState((prevState) => {
                return {
                    ...prevState,
                    axiosRequesting: false,
                    lastAxiosRequestedCacheKey: currentPageDataKey,
                };
            });
            return;
        }
        addToCache(currentPageDataKey, data);
        setState(() => {
            return {
                axiosRequesting: false,
                lastAxiosRequestedCacheKey: currentPageDataKey,
                fullScreen: getPageFullState(getFullPath(location)),
            };
        });
    };

    useEffect(() => {
        const currentPageDataKey = getPageDataCacheKeyByPath(location.pathname, location.search);
        if (serverSideData.current) {
            addToCache(currentPageDataKey, ssData.data)
            serverSideData.current = false;
            return;
        }
        //使用缓存先显示
        const cacheData = getCacheByKey(currentPageDataKey);
        //console.info(currentPageDataKey + "=> " + JSON.stringify(cacheData))
        setState((prevState) => {
            return {
                ...prevState,
                axiosRequesting: !offline,
                fullScreen: getPageFullState(getFullPath(location)),
            };
        });
        if (offline) {
            return;
        }
        loadData(currentPageDataKey, cacheData, location)
            .then(() => {
                //ignore
            })
            .catch(() => {
                //标记未没有请求了
                setState((prevState) => {
                    return {
                        ...prevState,
                        axiosRequesting: false,
                        fullScreen: getPageFullState(getFullPath(location)),
                    };
                });
            });
    }, [location.pathname, location.search]);

    const routes = [
        {
            paths: ["index", "index.html", ".html", ""],
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
            props: {activeKey: "basic"} as WebSiteProps
        },
        {
            paths: ["website/admin", "website/admin.html"],
            lazy: AsyncWebSite,
            fallback: WebSite,
            props: {activeKey: "admin"} as WebSiteProps
        },
        {
            paths: ["website/template", "website/template.html"],
            lazy: AsyncWebSite,
            fallback: WebSite,
            props: {activeKey: "template"} as WebSiteProps
        },
        {
            paths: ["website/other", "website/other.html"],
            lazy: AsyncWebSite,
            fallback: WebSite,
            props: {activeKey: "other"} as WebSiteProps
        },
        {
            paths: ["website/blog", "website/blog.html"],
            lazy: AsyncWebSite,
            fallback: WebSite,
            props: {activeKey: "blog"} as WebSiteProps
        },
        {
            paths: ["website/upgrade", "website/upgrade.html"],
            lazy: AsyncWebSite,
            fallback: WebSite,
            props: {activeKey: "upgrade"} as WebSiteProps
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
            props: {
                deleteCacheOnDestroy: () => deleteThisPageStateCache(),
                onFullScreen: () => {
                    setState((prevState) => {
                        savePageFullState(getFullPath(location), true);
                        return {...prevState, fullScreen: true};
                    });
                },
                onExitFullScreen: () => {
                    if (state.fullScreen) {
                        setState((prevState) => {
                            savePageFullState(getFullPath(location), false);
                            return {...prevState, fullScreen: false};
                        });
                    }
                }
            } as ArticleEditProps
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
            } as ErrorPageProps
        },
        {
            paths: ["500", "500.html"],
            lazy: AsyncError,
            fallback: UnknownErrorPage,
            props: {
                code: 500,
            } as ErrorPageProps
        },
        {
            paths: ["offline", "offline.html"],
            lazy: AsyncOffline,
            fallback: Offline,
        },
        {
            paths: ["system", "system.html"],
            lazy: AsyncSystem,
            fallback: System
        }
    ];

    const isOfflineData = () => {
        if (serverSideData.current) {
            return false;
        }
        if (state.axiosRequesting) {
            return true;
        }
        return state.lastAxiosRequestedCacheKey !== getPageDataCacheKey(location);
    }

    //console.info(location.pathname + "," + JSON.stringify(state));

    return (
        <Routes>
            {routes.flatMap(({paths, lazy, fallback, props = {}}, i) =>
                paths.map((path, j) => (
                    <Route
                        key={`${i}-${j}`}
                        path={path}
                        element={
                            <AdminPage
                                LazyComponent={lazy}
                                FallbackComponent={fallback}
                                props={{
                                    ...props,
                                    userInfo: userInfo,
                                    fullScreen: state.fullScreen,
                                    data: getDataFromCache(),
                                    offline: offline,
                                    offlineData: isOfflineData(),
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
