export type StatisticsInfoState = {
    clickCount: number;
    articleCount: number;
    commCount: number;
    toDayCommCount: number;
    loading: boolean;
};


export type IndexData = {
    serverInfo: Record<string, any>[],
    statisticsInfo: StatisticsInfoState
}