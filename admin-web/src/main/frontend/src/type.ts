export type StatisticsInfoState = {
    clickCount: number;
    articleCount: number;
    commCount: number;
    toDayCommCount: number;
    loading: boolean;
};

export type Version= {
    type:string;
    version:string;
}

export type LastVersion = {
    upgrade:boolean;
    version: Version;
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
    version: string;
    type: string;
};

export type IndexData = {
    serverInfo: Record<string, any>[],
    statisticsInfo: StatisticsInfoState
}