import { ArticleEditInfo, ArticleEditState, ArticleEntry } from "../components/articleEdit/index.types";
import { addToCache, deleteCacheDataByKey, getCacheByKey, removePageCacheByLocation } from "../cache";
import * as H from "history";

const buildCacheKey = (newArticle: ArticleEntry) => {
    return "local-article-info-" + (newArticle && newArticle.logId && newArticle.logId > 0 ? newArticle.logId : -1);
};

export const articleDataToState = (data: ArticleEditInfo): ArticleEditState => {
    const article: ArticleEntry =
        data.article.logId && data.article.logId > 0
            ? data.article
            : {
                  version: -1,
                  title: "",
                  keywords: "",
              };
    const cachedArticle = getCacheByKey(buildCacheKey(article)) as ArticleEntry;
    let realArticle;
    //本地缓存版本是没有被服务器再次修改的情况下才使用缓存数据
    if (cachedArticle && cachedArticle.version >= data.article.version) {
        realArticle = cachedArticle;
    } else {
        realArticle = data.article;
    }

    return {
        typeOptions: data.types
            ? data.types.map((x) => {
                  return { value: x.id, label: x.typeName };
              })
            : [],
        editorInitSuccess: false,
        editorVersion: realArticle.version,
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

export const deleteArticleCacheWithPageCache = (article: ArticleEntry, location: H.Location) => {
    deleteCacheDataByKey(buildCacheKey(article));
    removePageCacheByLocation(location);
};
