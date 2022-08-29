import { Route, Routes } from "react-router-dom";
import Loadable from "react-loadable";
import { MyLoadingComponent } from "../components/my-loading-component";

const AsyncArticleEdit = Loadable({
    loader: () => import("../components/articleEdit/article-edit"),
    loading: MyLoadingComponent,
});

const AsyncComment = Loadable({
    loader: () => import("../components/comment"),
    loading: MyLoadingComponent,
});

const AsyncPlugin = Loadable({
    loader: () => import("../components/plugin"),
    loading: MyLoadingComponent,
});

const AsyncIndex = Loadable({
    loader: () => import("../components/index"),
    loading: MyLoadingComponent,
});

const AsyncWebSite = Loadable({
    loader: () => import("../components/website"),
    loading: MyLoadingComponent,
});

const AsyncType = Loadable({
    loader: () => import("../components/type/type"),
    loading: MyLoadingComponent,
});

const AsyncLink = Loadable({
    loader: () => import("../components/link/link"),
    loading: MyLoadingComponent,
});

const AsyncNav = Loadable({
    loader: () => import("../components/nav/nav"),
    loading: MyLoadingComponent,
});

const AsyncNotFoundPage = Loadable({
    loader: () => import("../components/not-found-page"),
    loading: MyLoadingComponent,
});

const AsyncUpgrade = Loadable({
    loader: () => import("../components/upgrade"),
    loading: MyLoadingComponent,
});

const AsyncTemplateCenter = Loadable({
    loader: () => import("../components/template/template-center"),
    loading: MyLoadingComponent,
});

const AsyncTemplateConfig = Loadable({
    loader: () => import("../components/template/template-config"),
    loading: MyLoadingComponent,
});

const AsyncUserUpdatePassword = Loadable({
    loader: () => import("../components/user-update-password"),
    loading: MyLoadingComponent,
});

const AsyncArticle = Loadable({
    loader: () => import("../components/article/article"),
    loading: MyLoadingComponent,
});

const AsyncUser = Loadable({
    loader: () => import("../components/user"),
    loading: MyLoadingComponent,
});

const AdminLoginedRouter = () => {
    return (
        <Routes>
            <Route path="index" element={<AsyncIndex />} />
            <Route path="article-edit" element={<AsyncArticleEdit />} />
            <Route path="comment" element={<AsyncComment />} />
            <Route path="plugin" element={<AsyncPlugin />} />
            <Route path="website" element={<AsyncWebSite />} />
            <Route path="article-type" element={<AsyncType />} />
            <Route path="link" element={<AsyncLink />} />
            <Route path="nav" element={<AsyncNav />} />
            <Route path="article" element={<AsyncArticle />} />
            <Route path="user" element={<AsyncUser />} />
            <Route path="template-center" element={<AsyncTemplateCenter />} />
            <Route path="user-update-password" element={<AsyncUserUpdatePassword />} />
            <Route path="upgrade" element={<AsyncUpgrade />} />
            <Route path="template-config" element={<AsyncTemplateConfig />} />
            <Route element={<AsyncNotFoundPage />} />
        </Routes>
    );
};

export default AdminLoginedRouter;
