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


export type ArticleEditInfo = {
    tags: any[];
    types: any[];
    article: ArticleEntry;
};


export type FullScreenProps = {
    onExitFullScreen: () => void;
    onFullScreen: () => void;
}

export type ArticleEditProps = FullScreenProps & {
    data: ArticleEditInfo;
    offline: boolean;
};

export type ArticleEditState = {
    typeOptions: any[];
    tags: any[];
    rubbish: boolean;
    fullScreen: boolean;
    editorVersion: number;
    editorInitSuccess: boolean;
    article: ArticleEntry;
    saving: ArticleSavingState;
    offline: boolean;
};

export type ArticleSavingState = {
    rubbishSaving: boolean;
    previewIng: boolean;
    autoSaving: boolean;
    releaseSaving: boolean;
};