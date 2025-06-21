import {AxiosInstance} from "axios";
import {ssData} from "./index";

export const getCsrData = async (uri: string, axiosInstance: AxiosInstance) => {
    const {data} = await axiosInstance.get("/api/admin" + uri.replace(".html", ""));
    if (data.pageBuildId !== undefined) {
        ssData.pageBuildId = data.pageBuildId as string as never;
    }
    return data;
}