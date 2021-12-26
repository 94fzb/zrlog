import {Route, Switch} from "react-router-dom";
import Loadable from "react-loadable";
import {MyLoadingComponent} from "../components/my-loading-component";

const AsyncArticleEdit = Loadable({
    loader: () => import("../components/articleEdit/article-edit"),
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


const AdminLoginedRouter = () => {

    return (
        <Switch>
            <Route path="*/index" component={AsyncIndex}/>
            <Route path="*/article-edit" component={AsyncArticleEdit}/>
            <Route path="*/comment" component={AsyncComment}/>
            <Route path="*/plugin" component={AsyncPlugin}/>
            <Route path="*/website" component={AsyncWebSite}/>
            <Route path="*/article-type" component={AsyncType}/>
            <Route path="*/link" component={AsyncLink}/>
            <Route path="*/nav" component={AsyncNav}/>
            <Route path="*/article" component={AsyncArticle}/>
            <Route path="*/user" component={AsyncUser}/>
            <Route path="*/template-center" component={AsyncTemplateCenter}/>
            <Route path="*/user-update-password" component={AsyncUserUpdatePassword}/>
            <Route path="*/upgrade" component={AsyncUpgrade}/>
            <Route path="*/template-config" component={AsyncTemplateConfig}/>
            <Route component={AsyncNotFoundPage}/>
        </Switch>
    );
}

export default AdminLoginedRouter;
