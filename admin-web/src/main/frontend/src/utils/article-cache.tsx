import { ArticleEditInfo, ArticleEditState, ArticleEntry } from "../components/articleEdit/index.types";
import { addToCache, getCacheByKey } from "../cache";

const buildCacheKey = (newArticle: ArticleEntry) => {
    return "local-article-info-" + (newArticle && newArticle.logId && newArticle.logId > 0 ? newArticle.logId : -1);
};

export const articleDataToState = (data: ArticleEditInfo, fullScreen: boolean, offline: boolean): ArticleEditState => {
    const article: ArticleEntry =
        data.article.logId && data.article.logId > 0
            ? data.article
            : {
                  version: -1,
                  title: "",
                  keywords: "",
              };
    const cachedArticle = getCacheByKey(buildCacheKey(article)) as ArticleEntry;
    const realArticle = {
        ...article,
        ...cachedArticle,
        //use input version
        version: data.article.version,
    };
    return {
        offline: offline,
        typeOptions: data.types
            ? data.types.map((x) => {
                  return { value: x.id, label: x.typeName };
              })
            : [],
        editorInitSuccess: false,
        editorVersion: realArticle.version,
        fullScreen: fullScreen,
        tags: data.tags ? data.tags : [],
        rubbish: data.article && data.article.rubbish ? data.article.rubbish : false,
        article: realArticle,
        saving: {
            previewIng: false,
            releaseSaving: false,
            rubbishSaving: false,
            autoSaving: false,
        },
    };
};

export const articleSaveToCache = (article: ArticleEntry) => {
    addToCache(buildCacheKey(article), article);
};

export const deleteArticleCache = (article: ArticleEntry) => {
    addToCache(buildCacheKey(article), article);
};
