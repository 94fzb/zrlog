export type ArticleEntry = ChangedContent &
    ThumbnailChanged &
    TitleChanged &
    AliasChanged &
    DigestChanged &
    KeywordsChanged &
    PrivacyChanged &
    CanCommentChanged &
    TypeChanged & {
    rubbish?: boolean;
    logId?: number;
    lastUpdateDate?: number;
    version: number;
};

export type ChangedContent = {
    content?: string;
    markdown?: string;
};

export type ThumbnailChanged = {
    thumbnail?: string;
};

export type TitleChanged = {
    title: string;
};

export type AliasChanged = {
    alias?: string;
};

export type DigestChanged = {
    digest?: string;
};

export type KeywordsChanged = {
    keywords?: string;
};

export type TypeChanged = {
    typeId?: number;
};

export type CanCommentChanged = {
    canComment?: boolean;
};
export type PrivacyChanged = {
    privacy?: boolean;
};

export type ArticleChangeableValue =
    | ArticleEntry
    | PrivacyChanged
    | CanCommentChanged
    | AliasChanged
    | TypeChanged
    | TitleChanged
    | KeywordsChanged
    | ChangedContent
    | ThumbnailChanged
    | DigestChanged;