import {ActivityDay} from "./components/index/ActivityGraph";

export type StatisticsInfoState = {
    clickCount: number;
    articleCount: number;
    commCount: number;
    toDayCommCount: number;
    loading: boolean;
    usedCacheSpace: number;
    usedDiskSpace: number;
};

export type Version = {
    type: string;
    version: string;
}

export type LastVersion = {
    upgrade: boolean;
    version: Version;
}

export type BasicUserInfo = {
    userName: string;
    header: string;
    key: string;
    lastVersion?: LastVersion
    cacheableApiUris?: string[]
};

export type AdminCommonProps<P> = {
    data: P,
    offlineData: boolean,
    offline: boolean,
    fullScreen?: boolean,
    userInfo?: BasicUserInfo;
}

export type ServerInfoEntry = {
    name: string;
    key: string;
    value: string;
}

export type IndexData = {
    statisticsInfo: StatisticsInfoState,
    activityData: ActivityDay[],
    tips: string[];
    welcomeTip: string;
    versionInfo: string;
}

export type SystemData = {
    serverInfos: ServerInfoEntry[],
    serverInfos2: ServerInfoEntry[],
    dockerMode: boolean;
    nativeImageMode: boolean;
}