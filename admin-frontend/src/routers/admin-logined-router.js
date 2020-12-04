import {Route, Switch} from "react-router-dom";
import React from "react";
import Loadable from "react-loadable";
import {MyLoadingComponent} from "../App";

const AsyncArticleEdit = Loadable({
    loader: () => import("../components/article-edit"),
    loading: MyLoadingComponent
});

const AsyncComment = Loadable({
    loader: () => import("../components/comment"),
    loading: MyLoadingComponent
});


const AsyncPlugin = Loadable({
    loader: () => import("../components/plugin"),
    loading: MyLoadingComponent
});

const AsyncIndex = Loadable({
    loader: () => import("../components/index"),
    loading: MyLoadingComponent
});

const AsyncWebSite = Loadable({
    loader: () => import("../components/website"),
    loading: MyLoadingComponent
});

const AsyncType = Loadable({
    loader: () => import("../components/type"),
    loading: MyLoadingComponent
});


const AsyncLink = Loadable({
    loader: () => import("../components/link"),
    loading: MyLoadingComponent
});

const AsyncNav = Loadable({
    loader: () => import("../components/nav"),
    loading: MyLoadingComponent
});


const AsyncNotFoundPage = Loadable({
    loader: () => import("../components/not-found-page"),
    loading: MyLoadingComponent
});

const AsyncUpgrade = Loadable({
    loader: () => import("../components/upgrade"),
    loading: MyLoadingComponent
});

const AsyncTemplateCenter = Loadable({
    loader: () => import("../components/template-center"),
    loading: MyLoadingComponent
});

const AsyncUserUpdatePassword = Loadable({
    loader: () => import("../components/user-update-password"),
    loading: MyLoadingComponent
});

const AsyncArticle = Loadable({
    loader: () => import("../components/article"),
    loading: MyLoadingComponent
});

const AsyncUser = Loadable({
    loader: () => import("../components/user"),
    loading: MyLoadingComponent
});


class AdminLoginedRouter extends React.Component {

    render() {
        return (
            <Switch>
                <Switch>
                    <Route path="/admin/index" component={AsyncIndex}/>
                    <Route path="/admin/article-edit" component={AsyncArticleEdit}/>
                    <Route path="/admin/comment" component={AsyncComment}/>
                    <Route path="/admin/plugin" component={AsyncPlugin}/>
                    <Route path="/admin/website" component={AsyncWebSite}/>
                    <Route path="/admin/article-type" component={AsyncType}/>
                    <Route path="/admin/link" component={AsyncLink}/>
                    <Route path="/admin/nav" component={AsyncNav}/>
                    <Route path="/admin/article" component={AsyncArticle}/>
                    <Route path="/admin/user" component={AsyncUser}/>
                    <Route path="/admin/template-center" component={AsyncTemplateCenter}/>
                    <Route path="/admin/user-update-password" component={AsyncUserUpdatePassword}/>
                    <Route path="/admin/upgrade" component={AsyncUpgrade}/>
                    <Route component={AsyncNotFoundPage}/>
                </Switch>
            </Switch>
        );
    }
}

export default AdminLoginedRouter;