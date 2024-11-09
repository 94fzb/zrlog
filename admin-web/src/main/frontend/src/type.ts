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

export type ServerInfoEntry = {
    name:string;
    key :string;
    value:string;
}

export type IndexData = {
    serverInfos: ServerInfoEntry[],
    statisticsInfo: StatisticsInfoState,
    dockerMode: boolean;
    nativeImageMode: boolean;
    activityData: ActivityDay[]
    tips: string[];
    welcomeTip: string;
}