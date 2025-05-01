import { AxiosInstance } from "axios";
import { getSsDate, getWindowPageBuildId, setWindowPageBuildId } from "./base/SsData";

export const getCsrData = async (uri: string, axiosInstance: AxiosInstance) => {
    const { data } = await axiosInstance.get("/api/admin" + uri.replace(".html", ""));
    if (data.pageBuildId !== undefined) {
        getSsDate().pageBuildId = data.pageBuildId as string as never;
        if (getWindowPageBuildId() === "" || getWindowPageBuildId() === null || getWindowPageBuildId() === undefined) {
            setWindowPageBuildId(data.pageBuildId);
        }
    }
    return data;
};

export const getVersion = async (buildId: string, axiosInstance: AxiosInstance) => {
    const { data } = await axiosInstance.get("/api/public/version?buildId=" + buildId);
    return data;
};
