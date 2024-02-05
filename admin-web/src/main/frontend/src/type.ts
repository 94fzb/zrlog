export type StatisticsInfoState = {
    clickCount: number;
    articleCount: number;
    commCount: number;
    toDayCommCount: number;
    loading: boolean;
};

export type LastVersion = {
    upgrade:boolean;
    version:Record<string, never>;
}

export type BasicUserInfo = {
    userName: string;
    header: string;
    lastVersion?:LastVersion
};

export type UserInfoState = {
    basicInfoLoading: boolean;
    basicInfo: BasicUserInfo;
    upgrade: boolean;
    newVersion: string;
    versionType: string;
};

export type IndexData = {
    serverInfo: Record<string, any>[],
    statisticsInfo: StatisticsInfoState
}