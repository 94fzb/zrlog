import {Route, Switch} from "react-router-dom";
import React from "react";
import Loadable from "react-loadable";
import {MyLoadingComponent} from "../components/my-loading-component";

const AsyncArticleEdit = Loadable({
    loader: () => import("../components/article/article-edit"),
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
    loader: () => import("../components/type/type"),
    loading: MyLoadingComponent
});


const AsyncLink = Loadable({
    loader: () => import("../components/link/link"),
    loading: MyLoadingComponent
});

const AsyncNav = Loadable({
    loader: () => import("../components/nav/nav"),
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
    loader: () => import("../components/template/template-center"),
    loading: MyLoadingComponent
});

const AsyncTemplateConfig = Loadable({
    loader: () => import("../components/template/template-config"),
    loading: MyLoadingComponent
})

const AsyncUserUpdatePassword = Loadable({
    loader: () => import("../components/user-update-password"),
    loading: MyLoadingComponent
});

const AsyncArticle = Loadable({
    loader: () => import("../components/article/article"),
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
                <Route path="/admin/template-config" component={AsyncTemplateConfig}/>
                <Route component={AsyncNotFoundPage}/>
            </Switch>
        );
    }
}

export default AdminLoginedRouter;
